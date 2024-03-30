<template>
  <div>
    <v-chart :options="chartOptions" autoresize></v-chart>
  </div>
</template>

<script>
import { ref } from 'vue';
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { BarChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent
} from "echarts/components";
import VChart from 'vue-echarts';

use([
  CanvasRenderer,
  BarChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent
]);

export default {
  components: {
    VChart
  },
  setup() {
    const chartOptions = ref({
      title: {
        text: 'GitHub活跃图'
      },
      tooltip: {},
      legend: {
        data:['活跃度']
      },
      xAxis: {
        data: ['周一','周二','周三','周四','周五','周六','周日']
      },
      yAxis: {},
      series: [{
        name: '活跃度',
        type: 'bar',
        data: [5, 20, 36, 10, 10, 20, 30]  // 这里的数据应该是你从GitHub API获取的活跃度数据
      }]
    });

    return {
      chartOptions
    };
  }
};
</script>