<template>
  <v-container fill-height>
    <v-row>
      <v-col cols="12">
        <v-tabs>
          <v-tab @click="type = 0">课程信息</v-tab>
          <v-tab @click="type = 1">公告列表</v-tab>
          <v-tab @click="type = 2">学生列表</v-tab>
          <v-tab @click="type = 3">作业列表</v-tab>
          <v-tab @click="type = 4">课件管理</v-tab>
          <v-tab @click="type = 5">数据分析</v-tab>
          <v-tab @click="type = 6">邀请码</v-tab>
          <v-tab @click="type = 7">学生评价</v-tab>
        </v-tabs>
        <v-divider />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="12">
        <CourseInfoForm v-if="type == 0" :update="true" />
        <BulletinTable v-if="type == 1" />
        <UserTable v-if="type == 2" />
        <HomeworkTable v-if="type == 3" />
        <Courseware v-if="type == 4" />
        <InviteCode v-if="type == 6" />
        <StudenComment v-if="type == 7" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import CourseInfoForm from '@/components/course/form/course-info-form.vue'
import BulletinTable from '@/views/course/setting/bulletin-table.vue'
import UserTable from '@/views/course/setting/user-table.vue'
import HomeworkTable from '@/views/course/setting/homework-table.vue'
import Courseware from '@/views/course/setting/courseware.vue'
import StudenComment from '@/views/course/setting/student-comment.vue'
import InviteCode from '@/views/course/setting/invite-code.vue'

export default {
  name: 'Setting',
  components: {
    CourseInfoForm,
    BulletinTable,
    UserTable,
    HomeworkTable,
    Courseware,
    InviteCode,
    StudenComment
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
      id: 0,
      type: 0
    }
  },
  created() {
    this.id = this.$route.params.id

    this.$vuetify.goTo(0)
    document.title = '设置 - ' + this.course.curriculumName
    // 页面拦截
    if (this.role.role !== 'ROLE_TEACHER') {
      this.$router.go(-1)
    }
  }
}
</script>

<style>

</style>
