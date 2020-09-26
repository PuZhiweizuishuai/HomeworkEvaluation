<template>
  <v-container>
    <!-- 试卷头部内容 -->
    <v-btn
      v-if="homeworkInfo.type === 1 || homeworkInfo.type === 2"
      fixed
      right
      text
      large
    >
      <v-chip
        class="ma-2"
        small
        color="red"
        text-color="white"
      >
        倒计时
      </v-chip>
      <span v-if="countdownTextShow == false">{{ countDownNum.minute }} 分 {{ countDownNum.second }} 秒 </span>
      <span v-if="countdownTextShow">
        测验结束或你已提交
      </span>
    </v-btn>
    <v-row justify="center">
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <v-row justify="center">
      <h2>
        {{ homeworkInfo.title }}
        <v-chip
          class="ma-2"
          small
          :color="Constant.HOMEWORK__TYPE_COLOR[homeworkInfo.type]"
        >
          {{ Constant.HOMEWORK_TYPE[homeworkInfo.type] }}
        </v-chip>
      </h2>
    </v-row>
    <v-row justify="center">
      试卷总分：<strong> {{ homeworkInfo.totalScore }} </strong>
      <span v-if="homeworkInfo.showTeacherComment">你的得分：<span style="color:red;font-weight: bold;"> {{ homeworkInfo.score }} </span></span>
    </v-row>
    <v-row justify="center">
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <!-- 详细介绍 -->
    <v-row justify="center">
      开始时间：<strong>{{ homeworkInfo.openTime }} </strong>
      <span v-html="`&nbsp;&nbsp;`" />
      结束时间：<strong>{{ homeworkInfo.closeTime }} </strong>
    </v-row>
    <v-row justify="center">
      <v-col cols="12">
        <ShowMarkdown v-if="showHomeworkContent" :markdown="homeworkInfo.content" />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <!-- 试卷题目部分 -->
    <v-row v-for="(item, index) in homeworkInfo.questionsModels" :key="item.id">
      <v-col cols="12">
        <Choice v-if="item.type == 1 || item.type == 0 || item.type == 4" :index="index + 1" :question="item" :disabled="!homeworkInfo.submit" :answer="homeworkInfo.showTeacherComment" @answer="getAnswer" />
        <Discourses v-if="item.type == 2 || item.type == 3" :index="index + 1" :question="item" :disabled="!homeworkInfo.submit" :answer="homeworkInfo.showTeacherComment" @answer="getAnswer" />
        <v-divider />
      </v-col>
    </v-row>
    <!-- 试卷提交部分 -->
    <v-row justify="center">
      <v-btn depressed color="success" :disabled="!homeworkInfo.submit" @click="dialog = true">提交</v-btn>
      <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />
      <v-btn depressed color="primary" :disabled="!homeworkInfo.submit" @click="submitHomework(1)">暂时保存</v-btn>
    </v-row>

    <v-row v-if="homeworkInfo.showTeacherComment" justify="center">
      <v-col cols="11">
        <v-card outlined>
          <v-card-title>教师评价：</v-card-title>
          <v-row>
            <v-col cols="12">
              <ShowMarkdown :markdown="homeworkInfo.teacherComment" />
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-if="homeworkInfo.submit == false" justify="center" align="center" style="height: 150px">
      <strong>已经提交，无法再次提交</strong>
    </v-row>

    <!-- 支撑底部宽度部分 -->
    <v-row justify="center" align="center" style="height: 150px">

      我是有底线的😁

    </v-row>
    <!-- 提示框 -->
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
    <!-- 提示框 -->
    <v-dialog
      v-model="dialog"
      width="500"
    >

      <v-card>
        <v-card-title>
          确认提交
        </v-card-title>

        <v-card-text>
          请检查无误后再提交，你只有一次提交机会，提交后将无法再次提交！
        </v-card-text>

        <v-divider />

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="primary"
            text
            @click="submitHomework(2)"
          >
            提交
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import Constant from '@/utils/constant.vue'
import TimeUtil from '@/utils/time-util.vue'
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
import Choice from '@/views/homework/question/choice-judge.vue'
import Discourses from '@/views/homework/question/discourses.vue'

/**
 * 显示作业内容
 */
export default {
  name: 'Homework',
  components: {
    ShowMarkdown,
    Choice,
    Discourses
  },
  data() {
    return {
      TimeUtil,
      Constant,
      homeworkId: 0,
      homeworkInfo: {},
      message: '',
      showMessage: false,
      showHomeworkContent: false,
      // 如果是测验，处理倒计时
      countDown: null,
      countDownNum: {
        hour: 0,
        minute: 0,
        second: 0
      },
      countdownTextShow: false,
      // 需要提交的数据
      submitData: {
        homeworkId: 0,
        type: 0,
        answers: []
      },
      dialog: false,
      submitDataMap: {
        Set: function(key, value) { this[key] = value },
        Get: function(key) { return this[key] },
        Contains: function(key) { return this.Get(key) != null },
        Remove: function(key) { delete this[key] }
      }
    }
  },
  created() {
    this.homeworkId = this.$route.params.homeworkId
    this.getHomework()
  },
  methods: {
    submitHomework(value) {
      // console.log(this.submitData)
      this.submitData.type = value
      this.submitData.homeworkId = this.homeworkId
      this.httpPost(`/homework/submit`, this.submitData, (json) => {
        if (json.status === 200) {
          if (value === 2) {
            this.message = '提交成功！'
          } else {
            this.message = '保存成功！'
          }
          this.showMessage = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
        this.dialog = false
      })
    },
    getAnswer(ans, id, type) {
      const answer = {
        questionId: 0,
        otherAnswer: '',
        answer: []
      }

      answer.questionId = id
      if (type === 1 || type === 0) {
        answer.answer = ans
      } else {
        answer.otherAnswer = ans
      }
      this.unique(answer)
    },
    unique(answer) {
      for (let i = 0; i < this.submitData.answers.length; i++) {
        if (this.submitData.answers[i].questionId === answer.questionId) {
          this.submitData.answers[i] = answer
          return
        }
      }
      this.submitData.answers.push(answer)
    },
    getHomework() {
      this.httpGet(`/homework/info/${this.homeworkId}`, (json) => {
        if (json.status === 200) {
          this.homeworkInfo = json.data
          this.showHomeworkContent = true
          this.autoSubmit()
          for (let i = 0; i < json.data.questionsModels.length; i++) {
            let sysAns = []
            let othAns = ''
            if (json.data.questionsModels[i].type === 0 || json.data.questionsModels[i].type === 1) {
              sysAns = json.data.questionsModels[i].answer
            } else {
              othAns = json.data.questionsModels[i].otherAnswer
            }
            const ans = {
              questionId: json.data.questionsModels[i].id,
              otherAnswer: othAns,
              answer: sysAns
            }
            this.submitData.answers.push(ans)
          }
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    autoSubmit() {
      // && this.homeworkInfo.submit
      if ((this.homeworkInfo.type === 1 || this.homeworkInfo.type === 2) && this.homeworkInfo.submit) {
        this.countDown = window.setInterval(() => {
          // + 10000，留出10秒的富余，避免无法提交
          const date = new Date().getTime() + 10000
          let endTime = 0
          if (this.homeworkInfo.type === 1) {
            endTime = this.homeworkInfo.intoTime + this.homeworkInfo.time * 60000
            // 结束时间不能超过作业的结束时间
            if (endTime > this.homeworkInfo.closeTime) {
              endTime = this.homeworkInfo.closeTime
            }
          } else {
            endTime = this.homeworkInfo.closeTime
          }

          const seconds = parseInt((endTime - date) / 1000)

          this.countDownNum.minute = parseInt(seconds / 60)
          this.countDownNum.second = parseInt(seconds % 60)

          // 如果倒计时到头
          if (endTime < date) {
            this.countdownTextShow = true
            if (this.homeworkInfo.submit === true) {
              this.homeworkInfo.submit = false
              window.clearInterval(this.countDown)
              this.submitHomework(2)
            }
          }
        }, 1000)
      } else {
        this.countdownTextShow = true
      }
    }
  }
}
</script>

<style>

</style>
