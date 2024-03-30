<template>
  <div class="hot-list">
    <el-card class="article-card">
      <div class="card-header">
        文 章 热 榜
      </div>
      <p class="hot-articles" v-for="(article, index) in articles" :key="index">{{ article.title }}</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const articles = ref([
  { title: 'Linux 内核 vs Windows 内核' },
  { title: '图片上传功能测试' },
  { title: '执行一条select语句，会发生什么？' },
  { title: 'MySQL 单表不要超过 2000W 行，靠谱吗？' },
  { title: '解开Buffer Pool的面纱' },
  { title: '调度算法' },
  { title: 'MySQL一行记录是如何存储的？' },
  { title: '内存满了会发生什么'},
  { title: 'malloc是如何分配内存的？' },
  { title: '测试定时发布' },
]);
</script>

<style scoped>
，hot-articles{
  margin: 10px;
  font-size: 16px;

}
.hot-list {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.article-card {
  background-color: white;
  margin-bottom: 10px;
  width: 80%;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-top:30px;
}

.card-header {
  width: 100%; /* 设置宽度为 100% */
  text-align: center;
  font-size: 24px;
  padding: 10px 0; /* 设置上下的内边距为 10px */
  border-bottom: 1px solid rgba(211, 208, 208, 0.46);
  font-weight: bolder;
}
</style>