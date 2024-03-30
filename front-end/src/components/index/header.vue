<template>
    <el-header class="index-top-navigation">
      <el-menu mode="horizontal" class="top-menu">
        <router-link to="/index" v-slot="{ navigate }">
          <el-menu-item index="1" class="top-menu-element" @click="navigate">首页</el-menu-item>
        </router-link>
        <router-link to="/discover" v-slot="{ navigate }">
          <el-menu-item index="2" class="top-menu-element" @click="navigate">发现</el-menu-item>
        </router-link>
        <router-link to="/forum" v-slot="{ navigate }">
          <el-menu-item index="3" class="top-menu-element" @click="navigate">问答</el-menu-item>
        </router-link>
        <router-link to="/video" v-slot="{ navigate }">
          <el-menu-item index="4" class="top-menu-element" @click="navigate">视频</el-menu-item>
        </router-link>
        <router-link to="/about" v-slot="{ navigate }">
          <el-menu-item index="5" class="top-menu-element" @click="navigate">关于</el-menu-item>
        </router-link>
        <el-menu-item index="6" class="top-menu-element" @click="navigateToProfile">个人中心</el-menu-item>
        <el-menu-item index="7" class="top-menu-element" @click="navigateToSettings">
          <img src="../../../public/image/icon/setting.svg" alt="Settings" class="settings-icon">
        </el-menu-item>
      </el-menu>
    </el-header>
</template>

<script>
import Cookies from 'js-cookie';

export default {
  methods: {
    navigateToProfile() {
      const username = Cookies.get('username');
      if (username) {
        this.$router.push({ path: `/user/${username}` });
      } else {
        this.$router.push('/login');
      }
    },
    navigateToSettings() {
      this.$router.push('/setting');
    },
  }
}

</script>

<style scoped>
.settings-icon {
  width: 20px;  /* 调整图标的宽度 */
  height: 20px;  /* 调整图标的高度 */
}

.index-top-navigation {
  background-color: black;
}

el-menu {
  background-color: black;
}

el-menu-item {
  color: white;
}

.search-input {
  color: white;
  background-color: black;
  font-size: 16px;
}

.top-menu{
  background-color: black;
  font-size: 16px;
  width: 100%;
}

.top-menu-element{
  color: white;
}
</style>