<template>
  <div class="comment-manage">
    <el-table :data="comments" style="width: 100%">
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="content" label="内容"></el-table-column>
      <el-table-column prop="author" label="作者"></el-table-column>
      <el-table-column prop="commentDate" label="评论日期"></el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'CommentManage',
  data() {
    return {
      comments: Array.from({ length: 50 }, (_, i) => ({
        id: i + 1,
        content: `评论${i + 1}`,
        author: `作者${i + 1}`,
        commentDate: `日期${i + 1}`
      }))
    }
  }
}
</script>

<style scoped>
.comment-manage {
  padding: 20px;
}
</style>