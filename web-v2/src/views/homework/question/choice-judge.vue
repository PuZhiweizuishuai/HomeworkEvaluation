<template>
  <v-container>
    <!-- 题目介绍 -->
    <v-row>
      <v-col cols="12">
        <div style="float:left;"> {{ index }}.({{ Constant.QUESTION_TYPE[question.type] }}  {{ question.score }}分)</div>
        <ShowMarkdown :markdown="question.question" />
      </v-col>
    </v-row>
    <!-- 答案及选项 -->
    <v-row justify="center">
      <v-col cols="11">
        <!-- 单选 -->
        <v-row v-if="question.type == 0">
          <v-radio-group v-model="question.answer[0]" :row="true" :disabled="disabled">
            <v-radio v-for="(item, i) in question.options" :key="i" :label="item" :value="item" @click="changeAnswerRadio" />
          </v-radio-group>
        </v-row>
        <!-- 多选 -->
        <v-row v-if="question.type == 1">
          <v-checkbox v-for="(item, i) in question.options" :key="i" v-model="question.answer" :disabled="disabled" :label="item" :value="item" @click="changeAnswer" />
        </v-row>
        <!-- 判断 -->
        <v-radio-group v-if="question.type == 4" v-model="judge" :row="true" :disabled="disabled">
          <v-radio label="对" :value="1" @click="JudgeAnswer" />
          <v-radio label="错" :value="0" @click="JudgeAnswer" />
        </v-radio-group>
      </v-col>
    </v-row>
    <v-row v-if="teacher">
      <TeacherComment :question="question" @teacher="getComment" />
    </v-row>
    <v-row v-if="answer">
      <ShowAnswerForStudent :question="question" />
    </v-row>
  </v-container>
</template>

<script>
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
import Constant from '@/utils/constant.vue'
import TeacherComment from '@/views/homework/question/teacher-comment.vue'
import ShowAnswerForStudent from '@/views/homework/question/show-answer-for-student.vue'
/**
 * 单选，多选，判断
 */
export default {
  name: 'ChoiceWithJudge',
  components: {
    ShowMarkdown,
    TeacherComment,
    ShowAnswerForStudent
  },
  props: {
    // 题号
    index: {
      type: Number,
      default: 0
    },
    question: {
      type: Object,
      default: null
    },
    // 是否可编辑
    disabled: {
      type: Boolean,
      default: false
    },
    // 是否显示教师点评组件
    teacher: {
      type: Boolean,
      default: false
    },
    // 是否显示答案
    answer: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      Constant,
      judge: []
    }
  },
  created() {
    if (this.question.type === 1 && this.question.answer.length === 1 && this.question.answer[0] === '') {
      this.question.answer = []
    } else if (this.question.type === 4) {
      this.judge = parseInt(this.question.otherAnswer)
    }
  },
  methods: {
    changeAnswer() {
      // console.log(this.question.answer)
      this.$emit('answer', this.question.answer, this.question.id, this.question.type)
    },
    changeAnswerRadio() {
      // console.log(this.question.answer)
      this.$emit('answer', this.question.answer, this.question.id, this.question.type)
    },
    JudgeAnswer() {
      console.log(this.judge)
      this.$emit('answer', this.judge, this.question.id, this.question.type)
    },
    getComment(value) {
      this.$emit('teacher', value)
    }
  }
}
</script>

<style>

</style>
