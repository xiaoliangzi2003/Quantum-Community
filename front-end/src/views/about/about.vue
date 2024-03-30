<template>
  <el-container direction="vertical" class="discover-container">
    <Navigation/>
    <el-container>
      <el-aside width="20%">
      </el-aside>
      <el-main>
        <div class="construction-message">
          网站还在建设中...
        </div>
      </el-main>
      <el-aside width="20%">
      </el-aside>
    </el-container>
  </el-container>
</template>

<script>
import Navigation from "../../components/index/header.vue"
export default {
  name: 'Index',
  components: {Navigation},
  data() {
    return {
    }
  },
}
</script>

<style scoped>
.construction-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  font-size: 2em;
  color: #888;
}
</style>