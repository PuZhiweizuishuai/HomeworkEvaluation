<template>
  <!-- 要有宽高大小的容器 -->
  <div id="vote" />

</template>

<script>
// import * as echarts from 'echarts'
import * as echarts from 'echarts'
export default {
  props: {
    vote: {
      type: Object,
      default: null
    }
  },
  mounted() {
    this.createVote()
  },
  methods: {
    createVote() {
      // -${vote.id.counter}
      const doc = document.getElementById(`vote`)
      const myChart = echarts.init(doc)
      console.log(myChart)
      // 绘制图表
      myChart.setOption({
        title: {
          text: 'ECharts 入门示例'
        },
        tooltip: {},
        xAxis: {
          data: ['衬衫', '羊毛衫', '雪纺衫', '裤子', '高跟鞋', '袜子']
        },
        yAxis: {},
        series: [{
          name: '销量',
          type: 'bar',
          data: [5, 20, 36, 10, 10, 20]
        }]
      })
    }
  }
}
</script>

<style>

</style>
