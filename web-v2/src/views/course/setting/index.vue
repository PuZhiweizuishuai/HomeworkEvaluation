<template>
  <v-container fill-height>
    <v-row>
      <v-col cols="12">
        <v-tabs>
          <v-tab @click="type = 0">课程信息</v-tab>
          <v-tab @click="type = 1">公告列表</v-tab>
          <v-tab @click="type = 2">学生列表</v-tab>
          <v-tab @click="type = 3">作业列表</v-tab>
          <v-tab @click="type = 4">题目列表</v-tab>
          <v-tab @click="type = 5">数据分析</v-tab>
          <v-tab @click="type = 6">邀请码</v-tab>
        </v-tabs>
        <v-divider />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="12">
        <CourseInfoForm v-if="type == 0" />
        <BulletinTable v-if="type == 1" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import CourseInfoForm from '@/components/course/form/course-info-form.vue'
import BulletinTable from '@/components/course/setting/bulletin-table.vue'
export default {
  name: 'Setting',
  components: {
    CourseInfoForm,
    BulletinTable
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
      type: 0
    }
  }
}
</script>

<style>

</style>
