<template>
  <a-layout>
    <!-- <a-layout-header> -->
    <template>
      <a-page-header
        style="border: 1px solid rgb(235, 237, 240)"
        title="作业批改与评价系统"
        sub-title="当前已批改"
        @back="() => close()"
      />
    </template>
    <!-- </a-layout-header> -->

    <a-layout-content :style="{ padding: '0 50px' }">
      <!-- 基本信息 -->、
      <!-- 添加数据分析页面，成绩排名，正确率 -->
      <b-row>
        <b-col col="3">
          <ve-pie :data="submitWithNoSubmitChart" />
        </b-col>
      </b-row>
      <a-card>
        <a-descriptions title="当前作业基本情况">
          <a-descriptions-item label="提交数/未提交">

            {{ dashboardData.homework.submitCount }} / {{ dashboardData.studentCount }}
          </a-descriptions-item>
          <a-descriptions-item label="批改数/待批改">
            {{ commentComplete }} / {{ dashboardData.homework.submitCount - commentComplete }}
          </a-descriptions-item>
          <a-descriptions-item label="作业状态">
            {{ getHomeworkStatus(dashboardData.homework.status) }}
          </a-descriptions-item>
          <a-descriptions-item label="作业类型">
            {{ getHomeworkType(dashboardData.homework.type) }}
          </a-descriptions-item>
        </a-descriptions>
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

export default {
  name: '',
  components: {
    Footer, Userlist
  },
  data() {
    return {
      getHomeworkStatus,
      getHomeworkType,
      id: 0,
      commentComplete: 0,
      dashboardData: {},
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
      this.commentComplete = this.dashboardData.submitList.length
      for (let i = 0; i < this.commentComplete; i++) {
        if (this.dashboardData.submitList[i].status === 2) {
          this.userSubmitList.push(this.dashboardData.submitList[i])
        } else {
          this.completeList.push(this.dashboardData.submitList[i])
        }
      }
    },
    close() {
      window.opener = null
      window.open('', '_self')
      window.close()
    }
  }
}
</script>

<style>

</style>
