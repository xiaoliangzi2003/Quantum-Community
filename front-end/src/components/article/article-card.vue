<template>
  <el-card class="article-card">
    <div class="card-content">
      <div class="left-content">
        <div class="article-info">
          <div>
            <h2 class="article-title">{{ title }}</h2>
            <p class="publish-time">{{ publishTime }}</p>
          </div>
        </div>
        <div class="article-stats">
          <div class="icon-stat">
            <el-tooltip content="浏览量">
              <img src="../../../public/image/icon/view.svg" alt="view icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ views }}</span>
          </div>
          <div class="icon-stat">
            <el-tooltip content="点赞量">
              <img :src="isLiked ? '../../../public/image/icon/like_enable.svg' : '../../../public/image/icon/like_disable.svg'"
                   alt="thumb icon"
                   class="input-icon"
                   @click="toggleLike"/>
            </el-tooltip>
            <span>{{ likes }}</span>
          </div>
          <div class="icon-stat">
            <el-tooltip content="评论量">
              <img src="../../../public/image/icon/comment.svg" alt="comment icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ comments }}</span>
          </div>
          <div class="icon-stat">
            <el-tooltip content="收藏量">
              <img src="../../../public/image/icon/collect_disable.svg" alt="star icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ collects }}</span>
          </div>
          <div class="icon-stat">
            <el-tooltip content="转发量">
              <img src="../../../public/image/icon/share.svg" alt="star icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ forwards }}</span>
          </div>
        </div>
      </div>
      <div class="right-content">
        <img :src="'https://quantumblog.oss-cn-chengdu.aliyuncs.com/'+this.author+'_avatar.jpg'"
             class="avatar"
             alt=""
        @click="goToAuthorPage()">
        <p class="author-name">{{ author }}</p>
      </div>
    </div>
  </el-card>
</template>

<script>
import axios from 'axios'
import Cookies from 'js-cookie'


function debounce(func, wait) {
  let timeout;
  return function() {
    clearTimeout(timeout);
    timeout = setTimeout(() => {
      func.apply(this, arguments);
    }, wait);
  };
}

export default {
  name: 'ArticleCard',
  data() {
    return {
      title: '',
      author: '',
      publishTime: '',
      authorAvatar: '',
      views: '',
      likes: '',
      shares: '',
      comments: '',
      collects:'',
      forwards:'',
      isLiked: false,
    }
  },

  created() {
    let articleId = this.$route.params.id
    this.author=this.$route.params.author

    axios.get(`http://localhost:8080/blog/get-article-brief-info/${articleId}`
    ).then(response => {
      let article = response.data.data
      this.title = article.title
      this.author = article.author
      this.publishTime = article.publishTime
      this.authorAvatar = article.authorAvatar
      this.views = article.views
      this.likes = article.likes
      this.shares = article.shares
      this.comments = article.comments
      this.collects=article.collects
      this.forwards=article.forwards

      Cookies.set('article-title', this.title)
      Cookies.set('article-author', this.author)
      Cookies.set('article-id', articleId)
    }).catch(error => {
      console.log(error)
    })

    axios.post('http://localhost:8080/like/user-has-liked',{
      username:Cookies.get('username'),
      articleId:parseInt(articleId)
    }).then(response=>{
      if(response.data.statusCode===200){
        if(response.data.message==="0"){
          this.isLiked=false
        }
        if(response.data.message==="1"){
          this.isLiked=true
        }
      }
    }).catch(error=>{
      console.log(error)
    })
  },

  methods: {
    toggleLike: debounce(function() {
      let username = Cookies.get('username');
      // 根据用户当前的点赞状态，发送不同的请求到后端
      if (this.isLiked) {
        // 如果用户已经点赞了，那么发送取消点赞的请求
        this.isLiked = false; // 先更新点赞状态
        this.likes--; // 点赞数量减1
        axios.put(`http://localhost:8080/blog/update-article-likes`, {
          flag: 1,
          username: username,
          id: parseInt(this.$route.params.id)
        }).then(response => {
          if (response.data.statusCode !== 200) {
            this.isLiked = true; // 如果后端返回失败，恢复点赞状态
            this.likes++; // 点赞数量加1
          }
        }).catch(error => {
          console.log(error);
          this.isLiked = true; // 如果请求失败，恢复点赞状态
          this.likes++; // 点赞数量加1
        });
      } else {
        // 如果用户还没有点赞，那么发送点赞的请求
        this.isLiked = true; // 先更新点赞状态
        this.likes++; // 点赞数量加1
        axios.put(`http://localhost:8080/blog/update-article-likes`, {
          flag: 0,
          username: username,
          id: parseInt(this.$route.params.id)
        }).then(response => {
          if (response.data.statusCode !== 200) {
            this.isLiked = false; // 如果后端返回失败，恢复点赞状态
            this.likes--; // 点赞数量减1
          }
        }).catch(error => {
          console.log(error);
          this.isLiked = false; // 如果请求失败，恢复点赞状态
          this.likes--; // 点赞数量减1
        });
      }
    }, 500), // 设置延迟时间为500毫秒
    goToAuthorPage() {
      this.$router.push({ name: 'UserProfile', params: { username: this.author } });
    }
  }
}
</script>

<style scoped>
.avatar{
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-right: 20px;
}


.article-actions .el-button {
  color: #333; /* 按钮文字颜色 */
}

.article-actions .el-button:hover {
  color: #fff; /* 鼠标悬停时的文字颜色 */
  background-color: #333; /* 鼠标悬停时的背景颜色 */
}

.article-stats {
  display: flex;
  flex-direction: row;  /* 设置为水平排列 */
  justify-content: flex-start;  /* 从左开始排列 */
  align-items: center;
}

.input-icon {
  width: 15px;  /* 将图标大小调小 */
  height: 15px;  /* 将图标大小调小 */
  padding: 0 10px;
}

.icon-stat {
  margin-right: 10px;  /* 添加右边距 */
}

.card-content {
  display: flex;
  justify-content: space-between;
}

.left-content {
  display: flex;
  flex-direction: column;
}

.right-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.author-avatar,
.author-name {
  margin: auto;
}

</style>