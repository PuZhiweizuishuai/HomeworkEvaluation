<template>
  <v-container fluid>
    <v-card>
      <v-card-title>
        评价：<span v-if="questionInfo.score == questionInfo.realityScore">✔</span>
        <span v-if="questionInfo.score != questionInfo.realityScore">❌</span>

      </v-card-title>
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
        <v-col cols="11">
          得分：<span style="color: red;font-weight:bold;"> {{ questionInfo.realityScore }} </span>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="11">
          教师点评： {{ questionInfo.teacherComment }}
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script>
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
/**
 * 为用户显示教师评价，得分，及答案
 */
export default {
  name: 'ShowAnswerForStudent',
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
      questionInfo: this.question
    }
  },
  methods: {
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
