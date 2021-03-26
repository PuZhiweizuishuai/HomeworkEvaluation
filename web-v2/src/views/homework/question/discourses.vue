<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <div style="float:left;"> {{ index }}.({{ Constant.QUESTION_TYPE[question.type] }}  {{ question.score }}分)</div>
        <ShowMarkdown :markdown="question.question" />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="11">
        <div style="float:left;"> 答：</div>
        <Vditor v-if="disabled == false" :height="300" :markdown="question.otherAnswer" :idname="`Vditor-${question.id}`" :uploadurl="uploadurl" @vditor-input="getAnswer" />
        <ShowMarkdown v-if="disabled" :markdown="question.otherAnswer" />
      </v-col>
    </v-row>
    <!-- 教师点评组件 -->
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
import Vditor from '@/components/vditor/vditor.vue'
import TeacherComment from '@/views/homework/question/teacher-comment.vue'
import ShowAnswerForStudent from '@/views/homework/question/show-answer-for-student.vue'
/**
 *  填空，问答，论述题
 */
export default {
  name: 'Discourses',
  components: {
    ShowMarkdown,
    Vditor,
    TeacherComment,
    ShowAnswerForStudent
  },
  props: {
    // 题号
    index: {
      type: Number,
      default: 0
    },
    // 题目内容
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
    answer: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      Constant,
      uploadurl: this.SERVER_API_URL + `/uploads/file?type=homework&homework=${this.$route.params.id}`
    }
  },
  methods: {
    getAnswer(value) {
      this.$emit('answer', value, this.question.id, -1, this.index)
    },
    getComment(value) {
      this.$emit('teacher', value)
    }
  }
}
</script>

<style>

</style>
