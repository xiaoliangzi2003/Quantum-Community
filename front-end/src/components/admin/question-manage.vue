<template>
  <div class="question-manage">
    <el-table :data="questions" style="width: 100%">
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="question" label="问题"></el-table-column>
      <el-table-column prop="author" label="提问者"></el-table-column>
      <el-table-column prop="questionDate" label="提问日期"></el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'QuestionManage',
  data() {
    return {
      questions: Array.from({ length: 50 }, (_, i) => ({
        id: i + 1,
        question: `问题${i + 1}`,
        author: `提问者${i + 1}`,
        questionDate: `日期${i + 1}`
      }))
    }
  }
}
</script>

<style scoped>
.question-manage {
  padding: 20px;
}
</style>