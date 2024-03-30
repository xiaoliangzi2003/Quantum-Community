<template>
  <el-container direction="vertical" class="manage-container">
    <el-header class="index-top-navigation">
      <el-menu mode="horizontal" class="horizontal-navigation">
        <el-menu-item index="1" class="navigation-item">量子论坛</el-menu-item>
        <el-menu-item index="2" class="navigation-item">首页</el-menu-item>
        <el-menu-item index="3" class="navigation-item">发现</el-menu-item>
        <el-menu-item index="4" class="navigation-item">问答</el-menu-item>
        <el-menu-item index="5" class="navigation-item">视频</el-menu-item>
        <el-menu-item index="6" class="navigation-item">关于</el-menu-item>
        <el-menu-item index="7" class="navigation-item">个人中心</el-menu-item>
        <el-input class="search-input" placeholder="搜索"></el-input>
      </el-menu>
    </el-header>
    <el-header class="admin-header">后台管理系统</el-header>
    <el-container>
      <el-aside width="20%">
        <el-menu class="admin-menu">
          <el-menu-item @click="currentComponent = 'UserManage'">用户管理</el-menu-item>
          <el-menu-item @click="currentComponent = 'QuestionManage'">问答管理</el-menu-item>
          <el-menu-item @click="currentComponent = 'ArticleManage'">文章管理</el-menu-item>
          <el-menu-item @click="currentComponent = 'DataVisualize'">数据可视化</el-menu-item>
          <el-menu-item @click="currentComponent = 'CommentManage'">评论管理</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="admin-main">
        <component :is="currentComponent"></component>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import UserManage from '../../components/admin/user-manage.vue';
import QuestionManage from '../../components/admin/question-manage.vue';
import ArticleManage from '../../components/admin/articlemanage.vue';
import DataVisualize from '../../components/admin/data-visualize.vue';
import CommentManage from '../../components/admin/comment-manage.vue';

export default {
  name: 'Admin',
  components: {
    UserManage,
    QuestionManage,
    ArticleManage,
    DataVisualize,
    CommentManage
  },
  data() {
    return {
      currentComponent: 'UserManage'
    }
  }
}
</script>

<style scoped>
.admin-header {
  background-color: #3498db; /* 更改为深蓝色 */
  color: white; /* 更改为白色 */
  text-align: center;
  line-height: 60px;
  margin-top:0;
  font-size: 24px;
  font-weight: bold;
}


.admin-menu {
  height: 100%;
  background-color: #ecf0f1; /* 更改为浅灰色 */
  color: #2c3e50; /* 更改为深蓝色 */
}

.admin-main {
  background-color: white; /* 更改为白色 */
  color: #2c3e50; /* 更改为深蓝色 */
}

.index-container {
  height: 100vh;
}

.index-top-navigation {
  background-color: black;
}

.search-input {
  color: white;
  background-color: black;
  font-size: 16px;
}

.horizontal-navigation{
  background-color: black;
  margin-bottom: 0;
  color: white;

}

.navigation-item{
  color: white;
}
</style>