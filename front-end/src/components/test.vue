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
