<template>
  <v-container>
    <v-row>
      <v-col>
        <h3>
          课程反馈，说说你对这门课程还有什么评价与意见吧
        </h3>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        你对这门课的评分：
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-rating v-model="comment.rating" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'CourseComment',
  data() {
    return {
      comment: {
        rating: 0,
        text: ''
      }
    }
  }
}
</script>

<style>

</style>
