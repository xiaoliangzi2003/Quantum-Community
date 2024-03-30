<template>
  <el-container direction="vertical">
    <Header/>
    <el-container>
      <el-main>
        <el-button type="danger" class="logout-button" @click="logout">退出登录</el-button>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import Header from '../../components/index/header.vue'
import axios from 'axios';
import Cookies from 'js-cookie';

export default {
  components: {
    Header,
  },
  methods: {
    // 退出登录
    logout() {
      const username = Cookies.get('username');
      axios.post('http://localhost:8080/auth/logout', username)
          .then(() => {
            Cookies.remove('token');
            Cookies.remove('username');
            // 你可能还需要将用户重定向到登录页面
            this.$router.push('/login');
          })
          .catch(error => {
            console.error('Logout failed:', error);
          });
    },
    //绑定邮箱

    //绑定手机号
  },
}


</script>

<style scoped>
.logout-button {
  color: white;  /* 设置字体颜色为白色 */
  font-weight: bold;  /* 设置字体为加粗 */
}

el-menu {
  background-color: black;
}

el-menu-item {
  color: black;
}


</style>