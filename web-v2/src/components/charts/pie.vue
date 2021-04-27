<template>
  <v-card outlined height="400">
    <div :id="id" style="height: 100%" />
  </v-card>
</template>

<script>
import * as echarts from 'echarts'

export default {
  props: {
    id: {
      type: String,
      default: 'charts-pie'
    },
    option: {
      type: Object,
      default: null
    }
  },
  data() {
    return {

    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      const chartDom = document.querySelector('#' + this.id)
      const myChart = echarts.init(chartDom)
      const option = this.option
      console.log(option)

      // option = {
      //   title: {
      //     text: '某站点用户访问来源',
      //     left: 'center'
      //   },
      //   tooltip: {
      //     trigger: 'item'
      //   },
      //   legend: {
      //     orient: 'vertical',
      //     left: 'left'
      //   },
      //   series: [
      //     {
      //       name: '访问来源',
      //       type: 'pie',
      //       radius: '50%',
      //       data: [
      //         { value: 1048, name: '搜索引擎' },
      //         { value: 735, name: '直接访问' },
      //         { value: 580, name: '邮件营销' },
      //         { value: 484, name: '联盟广告' },
      //         { value: 300, name: '视频广告' }
      //       ],
      //       emphasis: {
      //         itemStyle: {
      //           shadowBlur: 10,
      //           shadowOffsetX: 0,
      //           shadowColor: 'rgba(0, 0, 0, 0.5)'
      //         }
      //       }
      //     }
      //   ]
      // }

      option && myChart.setOption(option)
    }

  }
}
</script>

<style>

</style>
