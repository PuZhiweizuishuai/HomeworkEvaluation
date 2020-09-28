<template>
  <v-container fill-height>
    <NoLoginShow v-if="this.$store.state.userInfo == null" />
    <v-container v-else v-resize="onResize">
      <v-row>
        <v-tabs>
          <v-tab @click="setType(0)">我的课程</v-tab>
          <v-tab v-if="Power.getTeacherPower(this.$store.state.userInfo)" @click="setType(1)">我创建的课程</v-tab>
        </v-tabs>
      </v-row>
      <v-row>
        <v-divider />
      </v-row>
      <v-row v-if="type == 1">
        <v-col>
          <v-btn depressed color="primary" @click="goToCreate">新建课程</v-btn>
        </v-col>
      </v-row>
      <v-row v-if="type == 1">
        <v-col cols="12">
          <v-divider />
        </v-col>
      </v-row>
      <v-row>
        <v-col v-for="item in classList" :key="item.id" :cols="colsWidth">
          <CourseCard :course="item" :src="`/course/learn/`" />
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-pagination
          v-model="page"
          :length="length"
          @input="pageChange"
        />
      </v-row>
    </v-container>
  </v-container>
</template>

<script>
import NoLoginShow from '@/components/no-login-show.vue'
import Power from '@/utils/power.vue'
import CourseCard from '@/components/course/course-card.vue'

export default {
  name: 'MyClass',
  components: {
    NoLoginShow,
    CourseCard
  },
  data() {
    return {
      Power,
      classList: [],
      windowSize: {
        x: 0,
        y: 0
      },
      colsWidth: 3,
      page: 1,
      size: 20,
      length: 1,
      totalCount: 0,
      type: 0
    }
  },
  created() {
    this.getCourseList()
  },
  methods: {
    getCourseList() {
      this.courseList = []
      this.httpGet(`/curriculum/list?page=${this.page}&limit=${this.size}&user=${this.$store.state.userInfo.userId}`, (json) => {
        if (json.status === 200) {
          this.classList = json.data.list || []
          this.length = json.data.totalPage
        } else {
          //
        }
      })
    },
    getTeacherCourseList() {
      this.courseList = []
      this.httpGet(`/curriculum/list?page=${this.page}&limit=${this.size}&teacher=${this.$store.state.userInfo.userId}`, (json) => {
        if (json.status === 200) {
          this.classList = json.data.list || []
          this.length = json.data.totalPage
        } else {
          //
        }
      })
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x > 950) {
        this.colsWidth = 3
      } else if (this.windowSize.x > 600) {
        this.colsWidth = 6
      } else {
        this.colsWidth = 12
      }
    },
    setType(type) {
      this.type = type
      this.page = 1
      if (type === 0) {
        this.getCourseList()
      } else if (type === 1) {
        this.getTeacherCourseList()
      }
      this.$vuetify.goTo(0)
    },
    pageChange(value) {
      this.page = value
      if (this.type === 0) {
        this.getCourseList()
      } else if (this.type === 1) {
        this.getTeacherCourseList()
      }
    },
    goToCreate() {
      this.$router.push({ path: `/course/create` })
    }
  }
}
</script>

<style>

</style>
