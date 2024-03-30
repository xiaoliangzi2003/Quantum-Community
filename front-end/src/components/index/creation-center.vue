<template>
  <el-card class="box-card">
    <div class="user-info">
      <img class="avatar" :src="userAvatar" alt="User avatar">
      <div class="user-details">
        <h2 class="username">{{ userName }}</h2>
        <div class="user-data">
          <div class="follows-fans">
            <img src="../../../public/image/icon/VIP.svg" alt="关注徽章" CLASS="badage">
          </div>
        </div>
      </div>
    </div>
    <div class="follows-fans">
      <el-card class="follows">
        <h2 class="card-title">关注数</h2>
        <p class="card-data">{{ userFollows }}</p>
      </el-card>
      <el-card class="fans">
        <h2 class="card-title">粉丝数</h2>
        <p class="card-data">{{ userFans }}</p>
      </el-card>
    </div>
    <div class="creation-center">
      <button @click="answerQuestion">
        <img src="../../../public/image/icon/question.svg" alt="Icon for answer question" class="button-icon">
        写回答
      </button>
      <button @click="writeArticle">
        <img src="../../../public/image/icon/write.svg" alt="Icon for write article" class="button-icon">
        写文章
      </button>
      <button @click="postStatus">
        <img src="../../../public/image/icon/saying.svg" alt="Icon for post status" class="button-icon">
        发动态
      </button>
      <button @click="enterCreationCenter">
        <img src="../../../public/image/icon/creation_center.svg" alt="Icon for enter creation center" class="button-icon">
        创作中心
      </button>
    </div>
  </el-card>
</template>

<script>
import Cookies from 'js-cookie';

const username = Cookies.get('username');
export default {
  data() {
    return {
      userAvatar: 'https://quantumblog.oss-cn-chengdu.aliyuncs.com/'+username+'_avatar.jpg',
      userName: username, // 替换为实际的用户名
      userFollows: 0,
      userFans: 0,
    };
  },
  methods: {
    answerQuestion() {

    },
    writeArticle() {
      // 实现写文章的逻辑
    },
    postStatus() {
      // 实现发动态的逻辑
    },
    enterCreationCenter() {
      // 实现进入创作中心的逻辑
    },
  },
};
</script>

<style scoped>
.button-icon {
  width: 40px;
  height: 40px;
  margin-right: 5px;
}

.follows{
  width: 50%;
  background-color: white;
  margin-right: 10px;
  border: none;
  box-shadow: none;
}

.fans{
  width: 50%;
  background-color: white;
  border: none;
  box-shadow: none;
}
.follows-fans {
  display: flex;
  justify-content: space-between;
}

.card-title {
  color: black;
  font-weight: bold;
  text-align: center;
  margin-top: 0;
}
.card-data {
  text-align: center;
}

.username{
  font-weight: bold;
}
.badage{
  width: 20px;
  height: 20px;

}

.user-data {
  flex-direction: column;
}

.user-data p {
  font-weight: bold;
}


.box-card {
  background-color: white;
  margin-top:20px;
  margin-right: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-right: 20px;
}

.creation-center {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.creation-center button {
  width: 20%;
  background: transparent;
  border: none;
  flex-direction: column !important; /* 添加这一行 */
  align-items: center; /* 添加这一行 */
  justify-content: center; /* 添加这一行 */
}
</style>