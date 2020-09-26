<template>
  <v-container fluid>
    <!-- 头部数据 -->
    <v-row v-resize="onResize" justify="center">
      <v-col cols="12">
        <!-- 头部卡片 -->
        <v-card outlined>
          <v-card-title>
            <v-btn class="mx-2" fab dark small depressed color="primary" @click="back">
              <v-icon dark> mdi-arrow-left-thick </v-icon>
            </v-btn>

            作业批改与评价系统
          </v-card-title>
          <v-row justify="center">
            <!-- 作业信息修改与显示部分 -->
            <v-col :cols="colsLeft">
              <v-row>
                <v-col>
                  作业标题：{{ dashboardData.homework.title }}
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  作业状态：
                  <v-chip
                    class="ma-2"
                    small
                    :color="Constant.HOMEWORK_STATUS_COLOR[dashboardData.homework.status+1]"
                    text-color="white"
                  >
                    {{ Constant.HOMEWORK_STATUS[dashboardData.homework.status+1] }}
                  </v-chip></v-col>
              </v-row>
              <v-row>
                <v-col>
                  作业类型：
                  <v-chip
                    class="ma-2"
                    small
                    :color="Constant.HOMEWORK__TYPE_COLOR[dashboardData.homework.type]"
                    text-color="white"
                  >
                    {{ Constant.HOMEWORK_TYPE[dashboardData.homework.type] }}
                  </v-chip>
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  创建时间： {{ TimeUtil.renderTime(dashboardData.homework.createTime) }}
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  开始时间： {{ TimeUtil.renderTime(dashboardData.homework.openTime) }}
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  结束时间： {{ TimeUtil.renderTime(dashboardData.homework.closeTime) }}
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  <div style="float:left;">是否开启互评：</div>
                  <v-switch
                    v-model="dashboardData.homework.evaluation"
                    style="margin-top: 0px;"
                    :label="evaluationLable"
                    :false-value="0"
                    :true-value="1"
                    @click="setEvaluation()"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  修改结束时间：
                </v-col>
              </v-row>
              <TimeForm @time="getCloseTime" />
              <v-row justify="end">
                <v-btn depressed color="success" @click="editEndTime">
                  修改
                </v-btn>
              </v-row>

            </v-col>
            <!-- 显示当前作业基本数据 -->
            <v-col :cols="closRight">
              <!-- 基本数据 -->
              <v-row no-gutters>
                <v-col cols="6">
                  <ve-pie :data="submitWithNoSubmitChart" :settings="chartSettings" />
                </v-col>
                <v-col cols="6">
                  <ve-pie :data="commentChart" :settings="chartSettings" />
                </v-col>
              </v-row>
              <!-- 详细数据分析，统计信息 -->
              <v-row>
                <v-col cols="12">
                  详细数据分析，统计信息,正在施工中
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>

    <!-- 待批改列表 -->

    <v-card outlined>
      <v-card-title>待批改列表</v-card-title>
      <!-- <v-card-actions>
        <v-btn depressed color="success">
          一键批改
        </v-btn>
        在全是选择题或判断题时，你可以使用这个功能
      </v-card-actions> -->
      <UserList :user="userSubmitList" />
    </v-card>
    <v-row>
      <v-col cols="12">
        <span v-html="'&nbsp;'" />
      </v-col>
    </v-row>
    <!-- 提交列表 -->
    <v-card outlined>
      <v-card-title>批改完成列表</v-card-title>
      <UserList :user="completeList" :lable="`查看`" />
    </v-card>
    <v-snackbar
      v-model="showMessage"
      :top="true"
      :timeout="3000"
    >
      {{ message }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="pink"
          text
          v-bind="attrs"
          @click="showMessage = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import Constant from '@/utils/constant.vue'
import TimeForm from '@/components/form/time-form.vue'
import UserList from '@/views/keeper/user-list.vue'

export default {
  components: {
    TimeForm,
    UserList
  },
  data() {
    return {
      closRight: 7,
      colsLeft: 4,
      homeworkId: 0,
      windowSize: {
        x: 0,
        y: 0
      },
      TimeUtil,
      Constant,
      dashboardData: {},
      evaluationLable: '关闭',
      // 待批改作业列表
      userSubmitList: [],
      // 完成批改的列表
      completeList: [],
      teacherSubmitList: [],
      commentComplete: 0,
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
          { '批改情况': '已批改', '人数': 0 },
          { '批改情况': '未批改', '人数': 0 }
        ]
      },
      chartSettings: {
        radius: 70
      },
      closeTime: '',
      message: '',
      showMessage: false
    }
  },
  created() {
    this.homeworkId = this.$route.params.homeworkId
    this.getHomework()
  },
  methods: {
    getHomework() {
      this.httpGet(`/homework/keeper/info/${this.homeworkId}`, (json) => {
        if (json.status !== 200) {
          window.location.href = '/'
          return
        }
        if (json.data.showPower === false) {
          window.location.href = '/'
          return
        }
        this.dashboardData = json.data
        this.initSubmitCount()
        document.title = json.data.homework.title + '- 批改'
      })
    },
    initSubmitCount() {
      // 设置学生提交数据
      this.submitWithNoSubmitChart.rows[0].人数 = this.dashboardData.homework.submitCount
      this.submitWithNoSubmitChart.rows[1].人数 = this.dashboardData.studentCount - this.dashboardData.homework.submitCount
      this.commentComplete = this.dashboardData.homework.submitCount

      for (let i = 0; i < this.commentComplete; i++) {
        if (this.dashboardData.submitList[i].status === 2) {
          this.userSubmitList.push(this.dashboardData.submitList[i])
        } else {
          this.completeList.push(this.dashboardData.submitList[i])
        }
      }

      if (this.dashboardData.homework.evaluation === 0) {
        this.evaluationLable = '关闭'
      } else {
        this.evaluationLable = '开启'
      }
      for (let i = 0; i < this.dashboardData.teacherSubmitList.length; i++) {
        this.teacherSubmitList.push(this.dashboardData.teacherSubmitList[i])
      }
      this.commentChart.rows[0].人数 = this.completeList.length
      this.commentChart.rows[1].人数 = this.userSubmitList.length
    },
    setEvaluation() {
      if (this.dashboardData.homework.evaluation === 0) {
        this.evaluationLable = '关闭'
        this.editHomeWorkInfo(0)
      } else {
        this.evaluationLable = '开启'
        this.editHomeWorkInfo(0)
      }
    },
    /**
     * @param value 数字： 0 修改是否开启互评
     *                    1  修改结束时间
     */
    editHomeWorkInfo(value) {
      const homework = {
        id: this.homeworkId
      }
      if (value === 0) {
        homework.evaluation = this.dashboardData.homework.evaluation
      } else if (value === 1) {
        if (this.closeTime === '' || this.closeTime == null) {
          this.message = '请先输入要修改的时间'
          this.showMessage = true
          return
        }
        homework.closeTime = this.closeTime
      }
      this.httpPost('/homework/setting/update', homework, (json) => {
        if (json.status === 200) {
          this.message = '修改成功！'
          this.showMessage = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
      // /homework/setting/update
    },
    editEndTime() {
      this.editHomeWorkInfo(1)
    },
    getCloseTime(tiem) {
      this.closeTime = tiem
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.colsLeft = 12
        this.closRight = 12
      } else {
        this.colsLeft = 4
        this.closRight = 7
      }
    },
    back() {
      this.$router.push({ path: `/course/learn/${this.$route.params.id}/setting` })
    }
  }
}
</script>

<style>

</style>
