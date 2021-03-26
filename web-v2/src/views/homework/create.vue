<template>
  <v-container>
    <v-stepper v-model="e1">
      <v-stepper-header>
        <v-stepper-step :complete="e1 > 1" step="1">创建作业</v-stepper-step>

        <v-divider />

        <v-stepper-step :complete="e1 > 2" step="2">导入题目</v-stepper-step>

        <v-divider />

        <v-stepper-step step="3">提交数据</v-stepper-step>
      </v-stepper-header>

      <v-stepper-items>
        <!-- 基本信息 -->
        <v-stepper-content step="1">
          <InfoForm @homework="getHomeworkInfo" />

        </v-stepper-content>

        <!-- 导入或新建问题 -->
        <v-stepper-content step="2">
          <ImportQuestion @questions="getQuestionsList" @back="backToOne" />
        </v-stepper-content>

        <!-- 提交数据 -->
        <v-stepper-content step="3">
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-divider />
              </v-col>
            </v-row>
            <v-row justify="center">
              <h2>
                {{ homework.title }}
                <v-chip
                  class="ma-2"
                  small
                  :color="Constant.HOMEWORK__TYPE_COLOR[homework.type]"
                >
                  {{ Constant.HOMEWORK_TYPE[homework.type] }}
                </v-chip>
              </h2>
            </v-row>

            <v-row justify="center">
              试卷总分：<strong> {{ homework.totalScore }} </strong>
            </v-row>

            <v-row justify="center">
              开始时间： <strong>{{ homework.openTime }}</strong>
              <span v-html="`&nbsp;&nbsp;`" />
              结束时间： <strong>{{ homework.closeTime }}</strong>
            </v-row>
            <v-row justify="center">
              <v-col cols="10">
                简介：(为了处理简单，此处简介并非最终显示效果)
              </v-col>
            </v-row>
            <v-row justify="center">
              <v-col cols="10">
                {{ homework.content }}
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                <v-divider />
              </v-col>
            </v-row>
            <v-row justify="center">
              <strong>注意：此处题目显示与之前相同，都是为了处理方便，此处展示效果并非最终结果</strong>
            </v-row>
            <v-row v-for="(item, index) in homework.questionsModels" :key="index" justify="center">
              <v-col cols="10">
                {{ index + 1 }}. {{ item.question }} ( {{ Constant.QUESTION_TYPE[item.type] }}  {{ item.score }} 分)
              </v-col>
            </v-row>
          </v-container>
          <v-col />
          <v-row justify="space-around">
            <v-btn depressed @click="e1 = 2">上一步</v-btn>
            <v-btn
              depressed
              color="primary"
              @click="dialog = true"
            >
              提交
            </v-btn>

          </v-row>
          <v-col />
        </v-stepper-content>
      </v-stepper-items>
    </v-stepper>
    <v-dialog
      v-model="dialog"
      width="500"
    >
      <v-card outlined>
        <v-card-title class="headline grey lighten-2">
          确认提交
        </v-card-title>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="primary"

            @click="submit"
          >
            提交
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
import InfoForm from '@/components/homework/info-form.vue'
import ImportQuestion from '@/components/homework/question/import-question.vue'
import Constant from '@/utils/constant.vue'
export default {
  name: 'CreateHomwork',
  components: {
    InfoForm,
    ImportQuestion
  },
  props: {
    course: {
      type: Object,
      default: null
    },
    role: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      Constant,
      id: 0,
      e1: 1,
      homework: {},
      dialog: false,
      message: '',
      showMessage: false
    }
  },
  created() {
    this.id = this.$route.params.id
    // 页面拦截
    if (this.role.role !== 'ROLE_TEACHER') {
      this.$router.go(-1)
    }
    this.$vuetify.goTo(0)
    document.title = '创建新的作业 - ' + this.course.curriculumName
  },
  methods: {
    submit() {
      this.homework.classNumber = this.id
      this.httpPost('/homework/save', this.homework, (json) => {
        if (json.status === 200) {
          this.message = '创建成功'
          this.showMessage = true
          this.dialog = false
          this.$router.push(`/course/learn/${this.id}/exam`)
        } else {
          this.message = json.message
          this.showMessage = true
          this.dialog = false
        }
      })
    },
    getHomeworkInfo(info) {
      this.homework = info
      this.e1 = 2
    },
    getQuestionsList(value, score) {
      this.homework.totalScore = score
      this.homework.questionsModels = value
      this.e1 = 3
    },
    backToOne(value) {
      this.e1 = 1
    }
  }
}
</script>

<style>

</style>
