<template>
  <form @submit.prevent="submitComment(0,0)">
    <textarea v-model="content" placeholder="请文明交流"></textarea>
    <button type="submit">发表</button>
  </form>
  <div v-if="comments.length > 0" class="comment-area">
    <div v-for="comment in comments" :key="comment.id" class="comment-card">
      <div class="parent-comment-part">
        <div class="comment-user">
          <img :src="'https://quantumblog.oss-cn-chengdu.aliyuncs.com/'+comment.commentator+'_avatar.jpg'" class="comment-avatar" alt="评论者头像">
        </div>
        <div class="comment-right">
          <div class="right-top">
            <p class="comment-username">{{ comment.commentator }}</p>
            <el-dropdown>
            <span class="el-dropdown-link">
              <img src="../../../public/image/icon/more.svg" class="icon"  alt=""/>
            </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="blockUser(comment.commentator)">屏蔽</el-dropdown-item>
                <el-dropdown-item @click.native="reportUser(comment.commentator)">举报</el-dropdown-item>
                <el-dropdown-item @click.native="copyComment(comment.content)">复制</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div class="comment-content">
            {{ comment.content }}
          </div>
          <div class="comment-bottom">
            <p class="comment-time">{{ new Date(comment.createTime).toLocaleString() }}</p>
            <div class="comment-actions">
              <div class="like-action">
                <img :src="comment.isLiked  ? '../../../public/image/icon/like_enable.svg' : '../../../public/image/icon/like_disable.svg'"
                     alt="like icon"
                     class="icon"
                     @click="likeComment(comment.id)">
                <span class="like-count">{{ comment.likes }}</span>
              </div>
              <div class="reply-action">
                <img src="../../../public/image/icon/comment.svg" alt="comment icon" class="icon" @click="showReplyInput(comment.id, comment.id,comment.content,comment.commentator)">
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-if=" repliedCommentId === comment.id" class="reply-input">
        <textarea v-model="replyContent" placeholder="请输入回复1内容"></textarea>
        <button @click="submitComment(comment.id,comment.id)">提交回复</button>
      </div>
      <div v-for="child in comment.children" :key="child.id" class="reply-contain">
        <div class="parent-comment-part">
          <div class="comment-user">
              <img :src="'https://quantumblog.oss-cn-chengdu.aliyuncs.com/'+child.commentator+'_avatar.jpg'" class="comment-avatar" alt="评论者头像">
            </div>
          <div class="comment-right">
            <div class="right-top">
              <p class="comment-username">{{ child.commentator }}</p>
            </div>
            <div class="comment-content">
              {{ child.content }}
              <span v-if="child.repliedContent">//
                <a class="reply-name" @click="goToUserPage(child.repliedCommentator)"> @{{child.repliedCommentator}}</a>
                :{{ child.repliedContent }}
              </span>
            </div>
            <div class="comment-bottom">
              <p class="comment-time">{{ new Date(child.createTime).toLocaleString() }}</p>
              <div class="comment-actions">
                <div class="like-action">
                  <img :src="child.isLiked  ? '../../../public/image/icon/like_enable.svg'
                  : '../../../public/image/icon/like_disable.svg'"
                       alt="like icon"
                       class="icon"
                       @click="likeComment(child.id)">
                  <span class="like-count">{{ child.likes }}</span>
                </div>
                <div class="reply-action">
                  <img src="../../../public/image/icon/comment.svg" alt="comment icon" class="icon" @click="showReplyInput(comment.id,child.id,child.content,child.commentator)">
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="repliedCommentId === child.id" class="reply-input">
          <textarea v-model="replyContent" placeholder="请输入回复2内容"></textarea>
          <button @click="submitComment(comment.id,child.id)">提交回复</button>
        </div>
      </div>
      <el-button type="text" @click="loadMoreReplies(comment.id)" class="load-more-reply">查看全部回复</el-button>
      <el-dialog v-model="comment.dialogVisible" :close-on-click-modal="true"
                 @close="closeDialog(comment)" class="reply-dialog" style="max-height: 650px;"
                 :before-close="handleClose">
        <div class="scrollable-content">
          <div class = "reply-parent-comment">
            <div class="comment-user">
              <img :src="'https://quantumblog.oss-cn-chengdu.aliyuncs.com/'+comment.commentator+'_avatar.jpg'" class="comment-avatar" alt="评论者头像">
            </div>
            <div class="comment-right">
              <div class="right-top">
                <p class="comment-username">{{ comment.commentator }}</p>
                <el-dropdown>
        <span class="el-dropdown-link">
          <img src="../../../public/image/icon/more.svg" class="icon"  alt=""/>
        </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item @click.native="blockUser(comment.commentator)">屏蔽</el-dropdown-item>
                    <el-dropdown-item @click.native="reportUser(comment.commentator)">举报</el-dropdown-item>
                    <el-dropdown-item @click.native="copyComment(comment.content)">复制</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
              <div class="comment-content">
                {{ comment.content }}
              </div>
              <div class="comment-bottom">
                <p class="comment-time">{{ new Date(comment.createTime).toLocaleString() }}</p>
                <div class="comment-actions">
                  <div class="like-action">
                    <img :src="comment.isLiked  ? '../../../public/image/icon/like_enable.svg' : '../../../public/image/icon/like_disable.svg'"
                         alt="like icon"
                         class="icon"
                         @click="likeComment(comment.id)">
                    <span class="like-count">{{ comment.likes }}</span>
                  </div>
                  <div class="reply-action">
                    <img src="../../../public/image/icon/comment.svg" alt="comment icon" class="icon" @click="showReplyInput(comment.id, comment.id,comment.content,comment.commentator)">
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="replies">
            <div v-for="reply in comment.replies" :key="reply.id" class="child-children-card">
              <div class="parent-comment-part">
                <div class="comment-user">
                  <img :src="'https://quantumblog.oss-cn-chengdu.aliyuncs.com/'+reply.commentator+'_avatar.jpg'" class="comment-avatar" alt="评论者头像">
                </div>
                <div class="comment-right">
                  <div class="right-top">
                    <p class="comment-username">{{ reply.commentator }}</p>
                  </div>
                  <div class="comment-content">
                    {{ reply.content }}
                    <span v-if="reply.repliedContent">//
            <a class="reply-name" @click="goToUserPage(reply.repliedCommentator)"> @{{reply.repliedCommentator}}</a>
            :{{ reply.repliedContent }}
          </span>
                  </div>
                  <div class="comment-bottom">
                    <p class="comment-time">{{ new Date(reply.createTime).toLocaleString() }}</p>
                    <div class="comment-actions">
                      <div class="like-action">
                        <img :src="reply.isLiked  ? '../../../public/image/icon/like_enable.svg'
              : '../../../public/image/icon/like_disable.svg'"
                             alt="like icon"
                             class="icon"
                             @click="likeComment(reply.id)">
                        <span class="like-count">{{ reply.likes }}</span>
                      </div>
                      <div class="reply-action">
                        <img src="../../../public/image/icon/comment.svg" alt="comment icon" class="icon" @click="showReplyInput(comment.id,reply.id,reply.content,reply.commentator)">
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="repliedCommentId === reply.id" class="reply-input">
                <textarea v-model="replyContent" placeholder="请输入回复2内容"></textarea>
                <button @click="submitComment(comment.id,reply.id)">提交回复</button>
              </div>
            </div>
            <el-button type="text" @click="loadMoreRepliesInDialog">加载更多</el-button>
          </div>
        </div>
        <div class="reply-input-two">
          <div class="reply-user">
            <img :src="'https://quantumblog.oss-cn-chengdu.aliyuncs.com/'+this.loginUser+'_avatar.jpg'" class="reply-avatar" alt="评论者头像">
          </div>
          <div class="reply-form">
            <textarea v-model="replyContent" placeholder="请输入回复内容" class="reply-input-form"></textarea>
            <button @click="submitComment(comment.id,comment.id)">提交回复</button>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
  <div>
    <el-button v-if="hasMore" type="text" @click="loadMoreComments">加载更多评论</el-button>
    <p v-if="allCommentsLoaded">已加载全部评论</p>
  </div>
</template>

<script>
import axios from 'axios';
import Cookies from "js-cookie";
import { ElDialog, ElButton } from 'element-plus';

export default {
  props: {
    articleId: {
      type: Number,
      required: true
    }
  },

  beforeMount() {
    this.resetComments();
  },


  data() {
    return {
      content: '',
      comments: [],
      pageNum: 1,
      pageSize: 10,
      hasMore: true,
      allCommentsLoaded: false,
      isLiked:false,
      repliedContent:'',
      replyContent:'',
      repliedCommentId:0,
      repliedCommentator: '',
      loginUser:Cookies.get('username'),
      dialogVisible: false,
      replies: [],
      replyPageNum: 1,
      replyPageSize: 10,
      hasMoreReplies: true,
      allRepliesLoaded: false,
    };
  },
  created() {
    this.loadComments();
  },
  methods: {
    handleClose(done) {
      this.dialogVisible = false;
      document.body.style.overflow = 'auto';
      done();
    },
    showDialog() {
      this.dialogVisible = true;
      document.body.style.overflow = 'hidden';
    },
    closeDialog(comment) {
      comment.dialogVisible = false;
      comment.replies = [];
    },
    loadMoreReplies(commentId) {
      const comment = this.comments.find(comment => comment.id === commentId);
      if (comment) {
        // 如果该评论的 replies 已经有内容，那么就不再加载
        if (comment.replies.length > 0) {
          return;
        }
        // 将该评论的 dialogVisible 属性设置为 true
        comment.dialogVisible = true;
      }
      this.replies = [];
      const request = {
        pageNum: this.replyPageNum,
        pageSize: this.replyPageSize,
        commentId: commentId,
        articleId: parseInt(this.articleId),
      };
      axios.post('http://localhost:8080/comment/load-more-sub-comments', request)
          .then(response => {
            if (response.data.statusCode === 200) {
              const replies = Object.values(response.data.data);
              comment.replies = comment.replies.concat(replies);
              this.replyPageNum++;
              if (replies.length < this.replyPageSize) {
                this.hasMoreReplies = false;
                this.allRepliesLoaded = true;
              }
              document.body.style.overflow = 'hidden';
            } else {
              console.error('Failed to load replies: ' + response.data.message);
            }
          })
          .catch(error => {
            console.error('Error loading replies:', error);
          });
    },
    loadMoreRepliesInDialog() {
      this.loadMoreReplies();
    },

    //重置评论
    resetComments() {
      this.comments = [];
      this.pageNum = 1;
      this.hasMore = true;
      this.allCommentsLoaded = false;
    },
    showReplyInput(parentCommentId,replyCommentId,repliedContent, repliedCommentator) {
      this.repliedCommentId = replyCommentId;
      this.repliedContent = (parentCommentId===replyCommentId?'':repliedContent)
      this.parentCommentId= parentCommentId;
      this.repliedCommentator = repliedCommentator;
    },
    submitComment(parentId,replyId) {
      const comment = {
        content: this.replyContent || this.content,
        articleId: parseInt(this.articleId),
        commentator: Cookies.get('username'),
        parentCommentId: parentId,
        repliedCommentId: replyId,
        repliedContent: this.repliedContent,
        repliedCommentator: this.repliedCommentator,
      };
      if(!comment.content){
        this.$message.error('评论内容不能为空');
        return;
      }
      if(this.articleId===undefined){
        this.$message.error('文章id不能为空');
        return;
      }
      axios.post('http://localhost:8080/comment/add-comment', comment)
          .then(response => {
            if (response.data.statusCode === 200) {
              console.log('Comment published successfully');
              this.content = '';
              this.replyContent = '';
              this.repliedCommentator='';
              this.repliedContent='';
              this.repliedCommentId = 0;
              let username = Cookies.get('username');
              let ws = new WebSocket(`ws://localhost:8080/websocket/${username}`);
              ws.onopen = function (event) {
                console.log('WebSocket connection opened');
              };
              ws.onmessage = function (event) {
                console.log('Received data from server:', event.data);
                try {
                  let responseComment = JSON.parse(event.data);
                  if (responseComment.parentCommentId === 0) {
                    this.comments.unshift(responseComment);
                  } else {
                    const parentComment = this.comments.find(c => c.id === responseComment.parentCommentId);
                    if (parentComment) {
                      if (!parentComment.children) {
                        parentComment.children = [];
                      }
                      parentComment.children.unshift(responseComment);
                    }
                  }
                  this.$nextTick(() => {
                    // 接收到评论信息后，关闭 WebSocket 连接
                    ws.close();
                  })
                } catch (error) {
                  console.error('Error parsing JSON:', error);
                  ws.close();
                }
              }.bind(this);

              ws.onclose = function(event) {
                console.log('WebSocket connection closed');
              };

              ws.onerror = function(error) {
                console.error('WebSocket error:', error);
              };
            } else {
              console.error('Failed to publish comment: ' + response.data.message);
            }
          })
          .catch(error => {
            console.error('Error publishing comment:', error);
          });
    },
    loadComments() {
      this.comments = [];
      const request = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        title: Cookies.get('article-title'),
        author: Cookies.get('article-author'),
        username:Cookies.get('username'),
      };
      axios.post('http://localhost:8080/comment/get-comment-list', request)
          .then(response => {
            if (response.data.statusCode === 200) {
              const comments = Object.values(response.data.data);
              comments.forEach(comment => {
                if (comment && 'secondLevelComment' in comment) {
                  comment.children = comment.secondLevelComment;
                } else {
                  console.error('Comment does not have secondLevelComment property:', comment);
                }
                if (!comment.children) {
                  comment.children = [];
                }
                comment.dialogVisible = false;
                comment.replies = [];
              });
              this.comments = this.comments.concat(comments);
              this.pageNum++;
              if (comments.length < this.pageSize) {
                this.hasMore = false;
                this.allCommentsLoaded = true;
              }
            } else {
              console.error('Failed to load comments: ' + response.data.message);
            }
          })
          .catch(error => {
            console.error('Error loading comments:', error);
          });
    },
    loadMoreComments() {
      this.loadComments();
    },
    likeComment(commentId) {
      let comment = this.comments.find(comment => comment.id === commentId);
      if (!comment) {
        // 如果在一级评论中没有找到，那么在二级评论中查找
        for (let parentComment of this.comments) {
          comment = parentComment.children.find(child => child.id === commentId);
          if (comment) {
            break;
          }
        }
      }
      if (!comment) {
        // 如果还是没有找到，那么直接返回
        return;
      }
      let username = Cookies.get('username');
      //取消点赞
      if (comment.isLiked) {
        comment.isLiked = false;
        comment.likes--;
        axios.post(`http://localhost:8080/comment/like-comment`, {
          flag: 1,
          commentator: username,
          commentId: commentId
        }).then(response => {
          if (response.data.statusCode !== 200) {
            comment.isLiked = true;
            comment.likes++;
          }
        }).catch(error => {
          console.log(error);
          comment.isLiked = true;
          comment.likes++;
        });
      } else {
        comment.isLiked = true;
        comment.likes++;
        axios.post(`http://localhost:8080/comment/like-comment`, {
          flag: 0,
          commentator: username,
          commentId: commentId
        }).then(response => {
          if (response.data.statusCode !== 200) {
            comment.isLiked = false;
            comment.likes--;
          }
        }).catch(error => {
          console.log(error);
          comment.isLiked = false;
          comment.likes--;
        });
      }
    },
  }
};
</script>

<style scoped>

.reply-form{
  width:82%;

}

.reply-input-form{
  margin-top:10px;
  min-height: 50px;
}
.reply-avatar{
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-right: 10px;
  margin-top:10px;
}

.reply-user{
    display: flex;
    align-items: flex-start !important;
    margin-right: 20px;
}

.reply-input-two {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  flex: none;
  margin-top: 20px;
  box-shadow: 0 -5px 10px rgba(0, 0, 0, 0.1); /* 只在上方添加阴影 */
  border: none; /* 移除左右的边界 */
  overflow: hidden;
}

.scrollable-content {
  flex: 1;
  overflow: auto; /* 当内容超出元素的大小时，就会出现滚动条 */
  max-height: 400px; /* 设置一个最大高度 */
  flex-direction: column;
}

.dialog-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;

}

.reply-input {
  position: sticky;
  bottom: 0;
  background-color: #fff; /* 设置背景颜色为白色，遮挡滚动条 */
}

.replies {
  /* 回复列表样式 */
  flex: 1;
  padding: 20px; /* 添加内边距 */
  background-color: #fff; /* 设置背景颜色为白色 */
}

.load-more-reply{
  margin-bottom: 20px;
  background-color: gray;
  color: white;
  margin-left: 100px;
  overflow: hidden;
}

.reply-contain{
  margin-left: 100px;
}
.comment-bottom{
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.right-top{
  display: flex;
  justify-content: space-between; /* 添加这一行 */
}

.reply-name {
  color: blue;
}

.reply-parent-comment {
  border: 1px solid #ccc; /* 添加边框 */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15); /* 添加阴影 */
  padding: 20px; /* 添加内边距 */
  background-color: #fff; /* 设置背景颜色为白色 */
  border-radius: 10px; /* 设置边框圆角 */
  margin-bottom: 20px; /* 添加下边距 */
  flex-direction: row !important;
  align-items: flex-start;
  justify-content: space-between;
}

.parent-comment-part{
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.comment-domain{
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  margin-left: 50px;
}
.like-count{
  margin-left: 5px;

}
.like-action{
  margin-right: 10px;
}

.icon {
  cursor: pointer;
  width: 20px;
  height: 20px;
}

.comment-card {
  display: flex;
  flex-direction: column;
  width: 60%;
  margin: 20px auto auto auto;
  border-bottom: 1px solid #ccc;
}
.comment-user {
  display: flex;
  flex-direction: column !important;
  align-items: flex-start !important; /* 添加这一行 */
  margin-right: 20px;
}

.comment-content {
  flex-grow: 1;
  margin-left: 30px;
  margin-top: 30px;
}

.comment-right {
  display: flex;
  flex-direction: column !important;
  width: 100%;
}


.comment-actions {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.comment-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-right: 10px;
}

.comment-username {
  font-weight: bold;
}

.comment-content {
  flex-grow: 1;
  margin: 0 10px;
}

.comment-actions {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.comment-time {
  font-size: 12px;
  color: #999;

}

form {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 20px auto auto auto;
  padding: 20px;
  background-color: #f8f8f8;
  border-radius: 10px;
  width: 60%; /* Set the width to 60% */
}

textarea {
  font-family: Arial, sans-serif;
  font-size: 16px;
  width: 100%;
  min-height: 100px;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  resize: vertical;
}

button {
  width: 100px;
  height: 30px;
  background-color: #1684ff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #126cd6;
}

.reply-action {
  margin-left: 10px;
}

.reply-input {
  width: 90%;
  margin-bottom: 30px;
  margin-left: auto;
}
</style>