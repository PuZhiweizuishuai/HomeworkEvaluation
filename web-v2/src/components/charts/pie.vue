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

      option && myChart.setOption(option)
    }

  }
}
</script>

<style>

</style>
