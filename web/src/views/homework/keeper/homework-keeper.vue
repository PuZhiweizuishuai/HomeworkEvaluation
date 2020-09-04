<template>
  <a-layout>
    <!-- <a-layout-header> -->
    <template>
      <a-page-header
        style="border: 1px solid rgb(235, 237, 240)"
        title="作业批改与评价系统"
        :sub-title="dashboardData.homework.title"
        @back="() => close()"
      />
    </template>
    <!-- </a-layout-header> -->

    <a-layout-content :style="{ padding: '0 50px' }">
      <!-- 基本信息 -->、
      <!-- 添加数据分析页面，成绩排名，正确率 -->
      <a-card title="当前作业基本情况">
        <b-row>
          <b-col cols="3">
            作业状态：
            {{ getHomeworkStatus(dashboardData.homework.status) }} <br><br>
            作业类型：
            {{ getHomeworkType(dashboardData.homework.type) }}<br><br>
            作业创建时间：
            {{ timeFormate(dashboardData.homework.createTime) }}<br><br>
            作业开始时间：
            {{ timeFormate(dashboardData.homework.openTime) }}<br><br>
            作业结束时间：
            {{ timeFormate(dashboardData.homework.closeTime) }}<br><br>
            是否打开互评：
            <a-switch v-model="isEvaluation" checked-children="开" un-checked-children="关" @click="setEvaluation" />
          </b-col>
          <b-col cols="4">
            <ve-pie :data="submitWithNoSubmitChart" :settings="chartSettings" />
          </b-col>
          <b-col cols="4">
            <ve-pie :data="commentChart" :settings="chartSettings" />
          </b-col>
        </b-row>
      </a-card>
      <!-- 基本操作，比如批改完成后开启互评 -->

      <!-- 待批改列表 -->
      <a-page-header
        style="border: 1px solid rgb(235, 237, 240)"
        title="待批改列表"
      />
      <Userlist :userlist="completeList" />
      <!-- 批改完成列表 -->
      <br>
      <a-page-header
        style="border: 1px solid rgb(235, 237, 240)"
        title="批改完成列表"
      />
      <Userlist :userlist="userSubmitList" />
      <!--  -->
    </a-layout-content>
    <a-layout-footer>
      <Footer />
    </a-layout-footer>
  </a-layout>
</template>

<script>
import Footer from '@/layout/components/Footer.vue'
import { getHomeworkStatus, getHomeworkType } from '@/utils/homework-utils.js'
import Userlist from '@/views/homework/keeper/keeper-user-list.vue'
import TimeUtil from '@/utils/time-util.vue'
export default {
  name: '',
  components: {
    Footer, Userlist
  },
  data() {
    return {
      isEvaluation: false,
      getHomeworkStatus,
      getHomeworkType,
      id: 0,
      commentComplete: 0,
      dashboardData: {
        homework: {
          submitCount: 0
        }
      },
      // 完成批改的列表
      completeList: [],
      // 待批改作业列表
      userSubmitList: [],
      submitWithNoSubmitChart: {
        columns: ['提交情况', '人数'],
        rows: [
          { '提交情况': '已提交', '人数': 0 },
          { '提交情况': '未提交', '人数': 0 }
        ]
      },
      commentChart: {
        columns: ['批改情况', '人数'],
        rows: [
          { '批改情况': '以批改', '人数': 0 },
          { '批改情况': '未批改', '人数': 0 }
        ]
      },
      chartSettings: {
        radius: 70
      }
    }
  },
  created() {
    this.id = this.$route.params.id
    this.getDashboardData()
  },
  methods: {
    // 检查是否有批改权限
    getDashboardData() {
      fetch(`${this.SERVER_API_URL}/homework/keeper/info/${this.id}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          if (json.status !== 200) {
            window.location.href = '/'
            return
          }
          if (json.data.isPower) {
            window.location.href = '/'
            return
          }
          this.dashboardData = json.data
          this.initSubmitCount()
        })
        .catch(e => {
          return null
        })
    },
    initSubmitCount() {
      // 设置学生提交数据
      this.submitWithNoSubmitChart.rows[0].人数 = this.dashboardData.homework.submitCount
      this.submitWithNoSubmitChart.rows[1].人数 = this.dashboardData.studentCount - this.dashboardData.homework.submitCount
      this.commentChart.rows[0].人数 = this.commentComplete
      this.commentChart.rows[1].人数 = this.dashboardData.homework.submitCount - this.commentComplete
      this.commentComplete = this.dashboardData.submitList.length
      // 设置互评按钮
      if (this.dashboardData.homework.evaluation === 0) {
        this.isEvaluation = false
      } else {
        this.isEvaluation = true
      }
      for (let i = 0; i < this.commentComplete; i++) {
        if (this.dashboardData.submitList[i].status === 2) {
          this.userSubmitList.push(this.dashboardData.submitList[i])
        } else {
          this.completeList.push(this.dashboardData.submitList[i])
        }
      }
    },
    setEvaluation(click) {
      console.log(click)
    },
    close() {
      window.opener = null
      window.open('', '_self')
      window.close()
    },
    timeFormate(date) {
      return TimeUtil.formateTimeToChinese(date)
    }
  }
}
</script>

<style>

</style>
