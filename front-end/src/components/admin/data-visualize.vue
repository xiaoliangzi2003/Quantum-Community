<template>
  <div class="question-manage">
    <div ref="chart" style="width: 600px;height:400px;"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'QuestionManage',
  data() {
    return {
      questions: Array.from({ length: 50 }, (_, i) => ({
        id: i + 1,
        question: `问题${i + 1}`,
        author: `提问者${i + 1}`,
        questionDate: `日期${i + 1}`,
        answers: Math.floor(Math.random() * 100) // 随机生成回答数量
      })),
      chart: null
    }
  },
  mounted() {
    this.chart = echarts.init(this.$refs.chart)
    this.updateChart()
  },
  methods: {
    updateChart() {
      const option = {
        title: {
          text: '问题回答数量'
        },
        tooltip: {},
        xAxis: {
          data: this.questions.map(q => q.question)
        },
        yAxis: {},
        series: [{
          name: '回答数量',
          type: 'bar',
          data: this.questions.map(q => q.answers)
        }]
      }
      this.chart.setOption(option)
    }
  }
}
</script>

<style scoped>
.question-manage {
  padding: 20px;
}
</style>