<template>
  <el-container direction="vertical">
    <index-header></index-header>
    <el-container>
      <el-aside class="user-profile-area">
        <div v-if="isEditing && isCurrentUser" class="edit-profile">
          <el-upload class="avatar-uploader" ref="uploader" action="http://localhost:8080/user-profile/upload-avatar" :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeUpload">
            <vue-cropper
                ref="cropper"
                :src="form.avatar"
                :output-size="{width: 200, height: 200}"
                :output-type="'jpeg'"
                @crop-end="handleCrop"
            ></vue-cropper>
            <img v-if="form.avatar" :src="form.avatar" class="avatar-edit" alt="" @click="triggerUpload">
            <i v-else class="el-icon-plus avatar-uploader-icon" @click="triggerUpload"></i>
            <img src="../../../public/image/icon/edit-avatar.svg" class="avatar-edit-icon" @click="triggerUpload" alt="Edit Avatar">
          </el-upload>
          <div class="user-info-edit">
            <div class="name-sex-edit">

              <el-input v-model="form.nickname" class="name-input"></el-input>
              <el-select v-model="form.gender" class="sex-icon-select">
                <el-option label="男" value="male"></el-option>
                <el-option label="女" value="female"></el-option>
              </el-select>
            </div>
            <div class="birth-location-edit">
              <el-input v-model="form.location" class="location-input"></el-input>
            </div>
            <el-input v-model="form.signature" type="textarea" class="signature-input"></el-input>
            <el-input v-model="form.email" class="email-input"></el-input>
            <el-input v-model="form.phone" class="phone-input"></el-input>
            <el-input v-model="form.wechat" class="wechat-input"></el-input>
            <el-input v-model="form.github" class="github-input"></el-input>
            <el-button class="edit-button" @click="updateUserProfile">
              <strong>保存</strong>
            </el-button>
          </div>
        </div>
        <div v-else>
          <div class="user-profile">
            <img :src="'https://quantumblog.oss-cn-chengdu.aliyuncs.com/'+this.username+'_avatar.jpg'" class="avatar" alt="">
            <div class="user-info">
              <div class="name-sex">
                <h2>{{ userProfile.nickname }}</h2>
                <img :src="userProfile.gender === 'man' ? '../../../public/image/icon/man.svg' : '../../../public/image/icon/woman.svg'" alt="Gender" class="sex-icon">
              </div>
              <div class="birth-location">
                <div class="birth">
                  <img src="../../../public/image/icon/birth.svg" alt="Birthday Icon" class="birth-icon">
                  <p>{{ formattedBirthday }}</p>
                </div>
                <div class="location">
                  <img src="../../../public/image/icon/location.svg" alt="Address Icon" class="address-icon">
                  <p>{{ userProfile.location }}</p>
                </div>
              </div>
              <p class="signature">{{ userProfile.signature }}</p>
              <el-button class="edit-button"  v-if="isCurrentUser" @click="isEditing = true">
                <strong>编辑个人资料</strong>
              </el-button>
              <div class="email">
                <img src="../../../public/image/icon/email.svg" alt="Email Icon" class="email-icon">
                <p>{{ this.email }}</p>
              </div>
              <div class="phone">
                <img src="../../../public/image/icon/phone.svg" alt="phone Icon" class="phone-icon">
                <p>{{ this.phone }}</p>
              </div>
              <div class="wechat">
                <img src="../../../public/image/icon/wechat.svg" alt="wechat Icon" class="wechat-icon">
                <p>{{ userProfile.wechat }}</p>
              </div>
              <div class="github">
                <img src="../../../public/image/icon/github.svg" alt="github Icon" class="github-icon">
                <a :href="'https://github.com/' + userProfile.github" target="_blank">{{ userProfile.github }}</a>
              </div>
            </div>
            <div class="user-actions" v-if="isCurrentUser">
              <div class="action-item">
                <img src="../../../public/image/icon/write.svg" alt="写文章" @click="goToWriteArticle">
                <p class="action-label">写文章</p>
              </div>
              <div class="action-item">
                <img src="../../../public/image/icon/question.svg" alt="提问" @click="goToAskQuestion">
                <p class="action-label">提问</p>
              </div>
              <div class="action-item">
                <img src="../../../public/image/icon/essay.svg" alt="发动态" @click="goToPostStatus">
                <p class="action-label">发动态</p>
              </div>
              <div class="action-item">
                <img src="../../../public/image/icon/draft.svg" alt="草稿箱" @click="goToDraftBox">
                <p class="action-label">草稿箱</p>
              </div>
            </div>
          </div>
        </div>
      </el-aside>
      <el-main class="profile-artifact">
        <div style="height: 500px;">
          <Active/>
        </div>
        <el-tabs v-model="activeTab" @tab-click="handleClick" class="art-tabs">
          <el-tab-pane label="动态" name="activities">
            <el-input v-model="searchQuery" placeholder="请输入搜索内容" class="search-input"></el-input>
          </el-tab-pane>
          <el-tab-pane label="文章" name="articles">
            <el-input v-model="searchQuery" placeholder="请输入搜索内容" class="search-input"></el-input>
            <el-card class="article" v-for="article in articles" :key="article.id" @click="goToArticleDetail(article)">
              <el-popover
                  placement="bottom-end"
                  width="160"
                  v-model="popoverVisible[article.id]"
              >
                <p>
                  <img src="../../../public/image/icon/write.svg" alt="Edit Icon" @click.stop="goToEditArticle(article)"> 编辑
                </p>
                <p>
                  <el-switch v-model="article.isPublic" active-color="#13ce66" inactive-color="#ff4949" @click.stop></el-switch> 设置公开/私密
                </p>
                <p>
                  <img src="../../../public/image/icon/delete.svg" alt="Delete Icon" @click.stop="deleteArticle(article)"> 删除
                </p>
                <img slot="reference" src="../../../public/image/icon/more.svg" class="more-icon" @click.stop="togglePopover(article.id)" alt="">
              </el-popover>
              <h2 class="article-title">{{ article.title }}</h2>
              <p class="article-summary">{{ article.summary+'...' }}</p>
              <div class="article-stats">
                <div class="icon-stat">
                  <el-tooltip content="浏览量">
                    <img src="../../../public/image/icon/view.svg" alt="view icon" class="input-icon"/>
                  </el-tooltip>
                  <span>{{ article.views }}</span>
                </div>
                <div class="icon-stat">
                  <el-tooltip content="点赞量">
                    <img src="../../../public/image/icon/like_disable.svg" alt="thumb icon" class="input-icon"/>
                  </el-tooltip>
                  <span>{{ article.likes }}</span>
                </div>
                <div class="icon-stat">
                  <el-tooltip content="评论量">
                    <img src="../../../public/image/icon/comment.svg" alt="comment icon" class="input-icon"/>
                  </el-tooltip>
                  <span>{{ article.comments }}</span>
                </div>
                <div class="icon-stat">
                  <el-tooltip content="收藏量">
                    <img src="../../../public/image/icon/collect_disable.svg" alt="star icon" class="input-icon"/>
                  </el-tooltip>
                  <span>{{ article.collects }}</span>
                </div>
                <div class="icon-stat">
                  <el-tooltip content="转发量">
                    <img src="../../../public/image/icon/share.svg" alt="star icon" class="input-icon"/>
                  </el-tooltip>
                  <span>{{ article.forwards}}</span>
                </div>
              </div>
              <div class="article-info">
                <p class="article-author">作者: {{ article.author }}</p>
                <p class="article-category">分类: {{ article.category }}</p>
                <div class="article-tags">
                  <el-tag class="article-tag" v-for="tag in article.tags.split(',')" :key="tag">{{ tag }}</el-tag>
                </div>
              </div>
            </el-card>
            <div v-if="hasMore" @click="loadUserArticles" class="load-more">点击加载更多</div>
          </el-tab-pane>
          <el-tab-pane label="回答" name="answers">
            <el-input v-model="searchQuery" placeholder="请输入搜索内容" class="search-input"></el-input>
          </el-tab-pane>
          <el-tab-pane label="提问" name="questions">
            <el-input v-model="searchQuery" placeholder="请输入搜索内容" class="search-input"></el-input>
          </el-tab-pane>
          <el-tab-pane label="收藏夹" name="collections">
            <el-input v-model="searchQuery" placeholder="请输入搜索内容" class="search-input"></el-input>
          </el-tab-pane>
          <el-tab-pane label="历史记录" name="history" v-if="isCurrentUser">
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import IndexHeader from '@/components/index/header.vue';
import axios from 'axios';
import Cookies from 'js-cookie';
import Active from '../../components/user/active.vue';
import VueCropper from 'vue-cropper';
import moment from 'moment';

export default {
  components: {
    IndexHeader,
    Active,
    isCurrentUser: false,
    VueCropper,
  },
  data() {
    return {
      username: '',
      avatar: '',
      birthday: '',
      gender: '',
      signature: '',
      address: '',
      socialAccounts: '',
      activeTab: 'articles',
      articles: [],
      pageNum: 1,
      pageSize: 10,
      hasMore: true,
      isEditing: false,
      popoverVisible: {},

      form: {
        avatar: '',
        nickname: '',
        gender: '',
        birthday: '',
        signature: '',
        location:'',
        email: '',
        phone: '',
        wechat: '',
        github: ''
      },

      userProfile: {
        nickname: '',
        username: '',
        avatar: '',
        gender: '',
        birthday: '',
        signature: '',
        wechat: '',
        github: '',
        location:'',
        phone:'',
        email:'',
      },
    }
  },

  computed: {
    formattedBirthday() {
      return moment(this.userProfile.birthday).format('YYYY-MM-DD');
    },
  },

  created() {
    this.username = this.$route.params.username;
    this.loadUserArticles();
    this.getUserProfile();
    const loggedInUsername = Cookies.get('username');
    this.isCurrentUser = loggedInUsername === this.username;
  },
  methods: {
    handleCrop() {
      this.form.avatar = this.$refs.cropper.crop();
    },
    beforeUpload(file) {
      // 创建一个新的FileReader对象
      const reader = new FileReader();

      // 当文件读取完成后，将结果设置为头像的预览
      reader.onloadend = () => {
        this.form.avatar = reader.result;
      };

      // 开始读取文件
      if (file) {
        reader.readAsDataURL(file);
      } else {
        this.form.avatar = '';
      }

      const formData = new FormData();

      formData.append('file', file, this.username + '_avatar' + '.' + file.name.split('.').pop());

      // 使用axios发送POST请求，上传文件
      axios.post('http://localhost:8080/user-profile/upload-avatar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(response => {
        // 处理响应
      }).catch(error => {
        // 处理错误
      });

      // 返回false阻止文件上传
      return false;
    },
    togglePopover(articleId) {
      this.$set(this.popoverVisible, articleId, !this.popoverVisible[articleId]);
    },
    goToEditArticle(article) {
      this.$router.push(`/edit-article/${article.id}`);
    },
    deleteArticle(article) {
      axios.delete(`http://localhost:8080/blog/delete-article/${article.id}`)
          .then(response => {
            if (response.data.statusCode === 200) {
              this.$message.success('删除成功');
              this.articles = this.articles.filter(item => item.id !== article.id);
            } else {
              this.$message.error('删除失败');
            }
          })
          .catch(error => {
            console.error('Failed to delete article: ' + error);
            this.$message.error('删除失败');
          });
    },
    goToArticleEditor() {
      this.$router.push('/article-editor');
    },
    handleClick(tab, event) {
      console.log(tab, event);
    },
    loadUserArticles() {
      axios.post('http://localhost:8080/blog/article-list-by-author', {
        author: this.username,
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(response => {
        if (response.data.statusCode === 200) {
          this.articles = this.articles.concat(response.data.data.articleList);
          this.pageNum++;
          if (response.data.data.articleList.length < this.pageSize) {
            this.hasMore = false;
          }
        } else {
          console.error('Failed to load articles: ' + response.data.message);
        }
      }).catch(error => {
        console.error('Failed to load articles: ' + error);
      });
    },
    goToWriteArticle() {
      this.$router.push('/article-editor');
    },
    goToAskQuestion() {
      this.$router.push('/ask-question');
    },
    goToPostStatus() {
      this.$router.push('/post-status');
    },
    goToDraftBox() {
      this.$router.push('/draft-box');
    },
    goToArticleDetail(article) {
      this.$router.push(`/article-detail/${article.id}`);
    },
    onCrop(dataUrl) {
      this.form.avatar = dataUrl;
    },
    updateUserProfile() {
      // 创建一个包含用户信息的对象
      const userProfile = {
        username: this.form.username,
        nickname: this.form.nickname,
        gender: this.form.gender,
        birthday: new Date(this.form.birthday).getTime(), // 将日期转换为时间戳
        signature: this.form.signature,
        wechat: this.form.wechat,
        github: this.form.github,
        location: this.form.location
      };

      // 使用axios发送POST请求
      axios.post('http://localhost:8080/user-profile/update-profile', userProfile)
          .then(response => {
            if (response.data.statusCode === 200) {
              this.$message.success('更新用户信息成功');
              this.isEditing = false;
            } else {
              this.$message.error('更新用户信息失败');
              this.isEditing = false;
            }
          })
          .catch(error => {
            console.error(error);
            this.$message.error('更新用户信息失败');
            this.isEditing = false;
          });
    },
    getUserProfile() {
      axios.post('http://localhost:8080/user-profile/get-user-profile', {
        username: this.username
      }).then(response => {
        if (response.data.statusCode === 200) {
          this.userProfile = response.data.data.userProfile;
          this.form = { ...this.userProfile };
          this.email=response.data.data.email;
          this.form.email=response.data.data.email;
          this.phone=response.data.data.phone;
          this.form.phone=response.data.data.phone;
          this.form.avatar='https://quantumblog.oss-cn-chengdu.aliyuncs.com/'+this.username+'_avatar.jpg';
        } else {
          console.error('Failed to get user profile: ' + response.data.message);
        }
      }).catch(error => {
        console.error('Failed to get user profile: ' + error);
      });
    }
  }
}


</script>

<style scoped>
.more-icon {
  width: 20px;  /* 调整图片的大小 */
  height: 20px;  /* 调整图片的大小 */
  cursor: pointer;
  position: absolute;
  top: 0;
  right: 0;
}

.article-actions {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  align-items: center;
}

.article-actions img {
  width: 20px;  /* 调整图片的大小 */
  height: 20px;  /* 调整图片的大小 */
  cursor: pointer;
}

.article-actions el-switch {
  margin: 0 10px;  /* 调整开关的位置 */
}

.avatar-edit-icon {
  width: 50px;  /* 调整图标的宽度 */
  height: 50px;  /* 调整图标的高度 */
  cursor: pointer;  /* 将鼠标指针改为手形 */
  right: 0;
  top: 50%;
  position: absolute;
  transform: translate(-120%, -50%);
}

.search-input{
  width: 100%;
  margin-bottom: 20px;
  height: 50px;
  font-size: 20px;
}
.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-label {
  color: black;
  align-items: center;
  margin-right: 30px;
}

.user-actions img {
  width: 50px;
  height: 50px;
  margin-right: 30px;
  margin-top:50px;
}
.user-actions {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.location-input,
.signature-input,
.email-input,
.phone-input,
.wechat-input,
.github-input {
  margin-bottom: 20px;  /* 调整这个值来改变输入框之间的间距 */
}

.location-input{
  margin-left: 30px;
  align-items: center;
}
.sex-icon-select,
.name-input{
  margin-right: 30px;
  align-items: center;
}
.birth-icon-picker{
  margin-bottom: -5px;
}
.email,
.phone,
.wechat,
.github {
  display: flex;
  align-items: center;
  flex-direction: row;
  margin-top:20px;
}

.email-icon,
.phone-icon,
.wechat-icon,
.github-icon {
  width: 40px;
  height: 40px;
  margin-left: 10px;
  margin-right:10px;
}

.edit-button {
  width: 100%;
  margin: 0 auto;
  background-color: black;
  color: white;
}

.birth-location-edit{
  display: flex;
  align-items: center;
  flex-direction:row;
  justify-content: space-between;
}
.birth-location{
  display: flex;
  align-items: center;
  flex-direction:row;
  justify-content: space-between;
}
.location{
  display: flex;
  align-items: center;
  flex-direction:row;
}

.birth{
  display: flex;
  align-items: center;
  flex-direction:row;
}
.birth-icon{
  width: 60px;
  height: 60px;
  margin-left: 10px;
  margin-right:10px;
}

.address-icon{
  width: 40px;
  height: 40px;
  margin-left: 10px;
  margin-right:10px;
}
.sex-icon{
  width: 40px;
  height: 40px;
  margin-left: 10px;
  margin-right:10px;
}

.name-sex-edit{
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-direction: column;
}

.name-sex{
  display: flex;
  align-items: center;
  flex-direction:row;
  margin-left:80px;
}
.edit-profile{
  display: flex;
  align-items: center;
  flex-direction: column;
}
.avatar-uploader{
  align-items: center;
  position: relative;
  margin-bottom: 20px;
}
.avatar-edit{
  width: 200px;
  height: 200px;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  margin:auto;
}
.avatar{
  width: 200px;
  height: 200px;
  border-radius: 50%;
  margin-right: 20px;
  margin-top:50px;
}

.user-profile-area{
  width:25%;
  padding: 20px;
  box-sizing: border-box;
}

.profile-artifact{
  width:75%;
  margin: 0 auto;
}
.fixed-button {
  position: fixed;
  top: 150px;
  left: 50px;
  flex-direction: column;
}

.edit-icon {
  width: 100px;  /* 调整图标的宽度 */
  height: 100px;  /* 调整图标的高度 */
}

.article-summary{
  margin-left: 20px;
}

.art-tabs{
  margin-top: 20px;

}
.user-profile {
  display: flex;
  align-items: center;
  flex-direction: column;
}
.user-info-edit {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
}
.user-info {
  display: flex;
  flex-direction: column;
}

.article-author,
.article-category {
  display: block;  /* 修改为 block 或其他合适的值 */
}

.article-tag{
  margin-right: 10px;
}
.load-more {
  text-align: center;
  padding: 10px;
  cursor: pointer;
}

.icon-stat {
  display: flex;
  align-items: center;
}

.input-icon {
  width: 20px;
  height: 20px;
  padding: 0 10px;
}

.article-title {
  text-align:left;
  margin-top: 0;
  margin-left: 20px;
}

.article-info{
  text-align: right;

}
.article-stats {
  display: flex;
  justify-content: space-between;
  position: absolute;
  bottom: 10px;
  left: 10px;

}

.article-author,
.article-category {
  display: none;  /* 隐藏作者和分类 */
}

.article-tags {

  margin-top:20px;
}


.article {
  width: 100%;
  margin-bottom: 5px;
  position: relative;
  padding-top: 0;
  box-sizing: border-box;
}

el-menu {
  background-color: black;
}

el-menu-item {
  color: black;
}

.article-summary{
  margin-left: 20px;
}

.art-tabs{
  margin-top: 20px;
}
.user-info {
  display: flex;
  flex-direction: column;
}

.article-author,
.article-category {
  display: block;  /* 修改为 block 或其他合适的值 */
}


.article-tag{
  margin-right: 10px;
}
.load-more {
  text-align: center;
  padding: 10px;
  cursor: pointer;
}

.icon-stat {
  display: flex;
  align-items: center;
}

.input-icon {
  width: 20px;
  height: 20px;
  padding: 0 10px;
}

.article-title {
  text-align:left;
  margin-top: 0;
  margin-left: 20px;
}

.article-info{
  text-align: right;

}

.article-stats {
  display: flex;
  justify-content: space-between;
  position: absolute;
  bottom: 10px;
  left: 10px;

}

.article-author,
.article-category {
  display: none;  /* 隐藏作者和分类 */
}

.article-tags {

  margin-top:20px;
}


.article {
  width: 100%;
  margin-bottom: 5px;
  position: relative;
  padding-top: 0;
  box-sizing: border-box;
}

el-menu {
  background-color: black;
}

el-menu-item {
  color: black;
}

</style>