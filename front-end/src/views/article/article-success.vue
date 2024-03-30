<template>
  <div class="success-container">
    <h1 class="success-title">文章发布成功！</h1>
    <p class="success-description">你的文章已经成功发布，感谢你的贡献！</p>
    <router-link to="/index" class="success-link">返回首页</router-link>
  </div>
</template>

<script>
export default {
  name: 'ArticleSuccess'
}
</script>

<style scoped>
.success-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  text-align: center;
}

.success-title {
  font-size: 2em;
  color: #2c3e50;
}

.success-description {
  font-size: 1.2em;
  color: #34495e;
}

.success-link {
  margin-top: 20px;
  color: #3498db;
  text-decoration: none;
}
</style>