<template>
  <v-container>
    <v-card outlined>
      <v-card-title>
        <v-btn class="mx-2" fab dark small depressed color="primary" @click="back">
          <v-icon dark> mdi-arrow-left-thick </v-icon>
        </v-btn>
        姓名： {{ homeworkData.studentName }} , 学号： {{ this.$route.query.studentId }}
      </v-card-title>
      <v-row justify="center">
        <h3>全卷总分： {{ homeworkData.totalScore }} 分, 当前实际得分： {{ homeworkData.score }} 分</h3>
      </v-row>
      <v-row justify="center">
        说明：选择判断题系统会自动进行判分，而选择填空因为答案可能不唯一，所以暂不提供自动判题
      </v-row>
      <v-row>
        <v-col cols="12">
          <v-divider />
        </v-col>
      </v-row>
      <!-- 显示题目 -->
      <v-row v-for="(item, index) in homeworkData.questionsModels" :key="item.id">
        <v-col cols="12">
          <Choice v-if="item.type == 1 || item.type == 0 || item.type == 4" :index="index + 1" :question="item" :disabled="true" :teacher="true" />
          <Discourses v-if="item.type == 2 || item.type == 3" :index="index + 1" :question="item" :disabled="true" :teacher="true" />
          <v-divider />
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="11">
          <h3>总评：</h3>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="11">
          <Vditor :height="400" :hide="false" :idname="`Vditor-teacher-comment`" :uploadurl="uploadurl" @vditor-input="getComment" />
        </v-col>
      </v-row>
      <v-row v-if="this.$route.query.type != 'see'" justify="space-around">

        <v-btn large depressed>
          打回
        </v-btn>
        <v-btn depressed large color="primary">
          保存
        </v-btn>

      </v-row>

      <v-row v-if="this.$route.query.type == 'see'" justify="space-around">
        <v-btn depressed large color="primary" @click="goToEdit">
          编辑
        </v-btn>
      </v-row>
      <v-row style="height: 100px" justify="center" align="center">
        tips: 点击打回或完成，会自动跳转到下一名学生，如果没有下一名学生，则会返回批改页面
      </v-row>
    </v-card>
  </v-container>
</template>

<script>
import Choice from '@/views/homework/question/choice-judge.vue'
import Discourses from '@/views/homework/question/discourses.vue'
import Vditor from '@/components/vditor/vditor.vue'

/**
 * 教师批改作业的页面
 */
export default {
  components: {
    Choice,
    Discourses,
    Vditor
  },
  data() {
    return {
      homeworkData: {},
      uploadurl: this.SERVER_API_URL + `/upload/file?type=homework&homework=${this.$route.params.homeworkId}`,
      commentMap: {

      },
      commentData: {
        studentId: this.$route.query.studentId,
        questionList: [],
        comment: '',
        status: 3,
        id: this.$route.params.homeworkId
      },
      isShowTeacherComment: false
    }
  },
  created() {
    this.$vuetify.goTo(0)
    this.getAnswer()
  },
  methods: {
    getAnswer() {
      this.httpGet(`/homework/keeper/correct/${this.$route.params.homeworkId}?studentId=${this.$route.query.studentId}`, (json) => {
        if (json.status === 200) {
          this.homeworkData = json.data
          this.isShowTeacherComment = true
        } else {
          this.$router.push({ path: `/course/learn/${this.$route.params.id}/keeper/homework/${this.$route.params.homeworkId}` })
        }
      })
    },
    back() {
      this.$router.push({ path: `/course/learn/${this.$route.params.id}/keeper/homework/${this.$route.params.homeworkId}` })
    },
    // 获取教师总体评价
    getComment(value) {
      console.log(value)
    },
    goToEdit() {
      this.$router.push(
        {
          path: `/course/learn/${this.$route.params.id}/keeper/homework/${this.id}/correct`,
          query: {
            studentId: this.$route.query.studentId,
            t: new Date().getTime()
          }
        }
      )
    },
    checkNumber(number, max) {
      //
      if (isNaN(parseFloat(number))) {
        return false
      }
      const score = parseFloat(number)
      if (score < 0 || score > max) {
        return false
      }
      return true
    }
  }
}
</script>

<style>

</style>
