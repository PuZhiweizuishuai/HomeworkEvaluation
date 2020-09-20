<template>
  <router-link :to="`/course/info/${course.id}`">
    <v-card outlined>

      <v-img
        class="white--text align-end"
        :aspect-ratio="16/9"
        :src="course.curriculumImageUrl"
      >

        <v-card-title>{{ course.curriculumName }} </v-card-title>
      </v-img>

      <v-card-subtitle class="pb-0">
        教师： {{ course.teacherName }}
      </v-card-subtitle>
      <v-card-text class="text--primary">
        <div>时间： {{ TimeUtil.formateNoHoursTime(course.openingTime, course.closeTime) }}</div>
        <div>
          {{ course.simpleInfo }}
        </div>
        <div><v-icon>mdi-account-outline</v-icon> {{ course.studentNumber }}</div>
      </v-card-text>
    </v-card>
  </router-link>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
/**
 * 课程卡片
 */
export default {
  name: 'CourseCard',
  props: {
    course: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      TimeUtil
    }
  }
}
</script>

<style>

</style>
