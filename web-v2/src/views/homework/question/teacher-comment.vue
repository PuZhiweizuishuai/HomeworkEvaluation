<template>
  <v-container fluid>
    <v-card>
      <v-card-title>评价与判分</v-card-title>
      <v-row justify="center">
        <v-col cols="10">
          <span v-if="questionInfo.type == 0 || questionInfo.type == 1 || questionInfo.type == 4">
            参考答案：<strong> {{ rigthAnswer(questionInfo) }} </strong>
          </span>
          <div v-else>
            <span style="float: left;"> 参考答案：</span>
            <ShowMarkdown :markdown="questionInfo.rightAnswer" />
          </div>
        </v-col>
        <v-col cols="1">
          难度：{{ questionInfo.difficulty }}
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="4">
          此题满分：
          <span style="color:red">{{ questionInfo.score }}</span>
          ,
          当前得分：
          <span style="color:red">{{ questionInfo.realityScore }}</span>
        </v-col>
        <v-col cols="4">
          <v-chip
            v-if="questionInfo.type == 3 || questionInfo.type === 2"
            style="float:left;"
            class="ma-2"
            color="red"
            text-color="white"
          >
            必填
          </v-chip>
          <v-text-field
            v-model="questionInfo.realityScore"
            label="修改得分"
            placeholder="输入学生此题得分"
            type="number"
            @change="getComment"
          />
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="11">
          <v-text-field
            v-model="questionInfo.teacherComment"
            label="简短评价"
            placeholder="简短评价，可以不填！"
            @change="getComment"
          />
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script>
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
/**
 * 教师对题目进行评价的组件
 */
export default {
  name: 'TeacherComment',
  components: {
    ShowMarkdown
  },
  props: {
    question: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      questionInfo: this.question,
      comment: {
        text: '',
        type: this.question.type,
        id: this.question.id,
        score: this.question.realityScore
      }
    }
  },
  created() {

  },
  methods: {
    getComment() {
      this.comment.text = this.questionInfo.teacherComment
      this.comment.score = this.questionInfo.realityScore
      this.$emit('teacher', this.comment)
    },
    rigthAnswer(question) {
      if (question.type === 4) {
        if (parseInt(question.rightAnswer) === 1) {
          return '对'
        } else {
          return '错'
        }
      }
      return question.rightAnswer
    }
  }
}
</script>

<style>

</style>
