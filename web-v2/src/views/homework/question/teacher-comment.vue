<template>
  <v-container fluid>
    <v-card outlined>
      <v-card-title>评价与判分</v-card-title>
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
          />
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="11">
          <v-text-field
            v-model="questionInfo.teacherComment"
            label="简短评价"
            placeholder="简短评价，可以不填！"
          />
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script>
/**
 * 教师对题目进行评价的组件
 */
export default {
  name: 'TeacherComment',
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
  created() {

  }
}
</script>

<style>

</style>
