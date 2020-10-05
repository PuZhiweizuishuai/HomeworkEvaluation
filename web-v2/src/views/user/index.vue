<template>
  <div>
    <v-container fill-height fluid style="padding: 0px;">
      <v-row>
        <v-col style="padding: 0px;">
          <v-img :src="userInfo.topImgUrl" :aspect-ratio="5.98" />
        </v-col>
      </v-row>

    </v-container>
    <v-container fill-height>
      <v-row align="center">
        <v-col
          cols="12"
          md="8"
        >
          <v-avatar size="80" style="float: left;">
            <v-img :src="userInfo.userAvatarUrl" />
          </v-avatar>
          <h2 style="margin-top: 20px;margin-left: 100px;">
            {{ userInfo.username }}

            <v-chip v-if="userInfo.role.role == 'ROLE_TEACHER'" small color="orange">
              教师
              <v-icon right>mdi-star</v-icon>
            </v-chip>
            <v-chip v-if="userInfo.role.role == 'ROLE_ADMIN'" small color="primary">
              管理员
            </v-chip>
            <v-chip v-if="userInfo.role.role == 'ROLE_STUDENT'" small color="green">
              <v-avatar left>
                <v-icon>mdi-account-circle</v-icon>
              </v-avatar>
              学生
            </v-chip>
          </h2>
          <span v-if="userInfo.status == 1 || userInfo.status == 2" style="color: red;">
            <span v-if="(userInfo.startLockTime + userInfo.lockTime) > new Date().getTime()">
              该账号因为违反网站规定，已被管理页
              <span v-if="userInfo.status == 2" v-text="`锁定`" />
              <span v-if="userInfo.status == 1" v-text="`禁言`" />
              结束时间为：<span v-text="TimeUtil.renderTime(userInfo.startLockTime + userInfo.lockTime)" />
            </span>
          </span>
        </v-col>
        <v-col
          v-if="this.$store.state.userInfo && this.$store.state.userInfo.userId == id"
          cols="6"
          md="4"
          class="hidden-sm-and-down ml-0 pl-4"
        >
          <v-btn color="primary" @click="goToSetting">个人设置</v-btn>
        </v-col>
        <v-col
          v-if="this.$store.state.userInfo == null || this.$store.state.userInfo.userId != id"
          cols="6"
          md="4"
        >
          粉丝数：  {{ userInfo.fansCount }} <v-btn color="primary">关注他</v-btn>
        </v-col>
      </v-row>

      <v-tabs>
        <v-tab v-if="this.$store.state.userInfo == null || this.$store.state.userInfo.userId != id" @click="setType(0)">首页</v-tab>
        <v-tab v-else @click="setType(0)">我的课程</v-tab>
        <v-tab v-if="userInfo.role.role == 'ROLE_TEACHER' || userInfo.role.role == 'ROLE_ADMIN'" @click="setType(1)">
          <span v-if="this.$store.state.userInfo == null || this.$store.state.userInfo.userId != id">他</span>
          <span v-else>我</span>
          教的课程

        </v-tab>
        <v-tab @click="setType(2)">讨论</v-tab>
        <v-tab @click="setType(4)">简介</v-tab>
      </v-tabs>
    </v-container>
    <v-divider />
    <div id="top" />
    <v-container fill-height>
      <!-- 我的首页 -->
      <v-container v-if="type == 0" fluid>
        <v-row v-resize="onResize">
          <v-col v-for="item in courseList" :key="item.id" :cols="colsWidth">
            <CourseCard :course="item" />
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
      <!-- 如果是教师，显示教师教的课程 -->
      <v-container v-if="type == 1" fluid>
        <v-row v-resize="onResize">
          <v-col v-for="item in courseList" :key="item.id" :cols="colsWidth">
            <CourseCard :course="item" />
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
      <div v-if="type == 4">
        <v-row>
          <v-col>
            用户名: {{ userInfo.username }}
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            简介： {{ userInfo.userIntro }}
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            关注数： {{ userInfo.followerCount }}
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            学校： {{ userInfo.school }}
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            专业： {{ userInfo.major }}
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            年级： {{ userInfo.grade }}
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            加入时间： {{ TimeUtil.renderTime(userInfo.createTime) }}
          </v-col>
        </v-row>
      </div>
    </v-container>
  </div>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import CourseCard from '@/components/course/course-card.vue'
export default {
  name: 'UserHome',
  components: {
    CourseCard
  },
  data() {
    return {
      TimeUtil,
      id: 0,
      userInfo: {
        role: {
          role: ''
        }
      },
      courseList: [],
      page: 1,
      size: 20,
      length: 1,
      totalCount: 0,
      type: 0,
      colsWidth: 3,
      windowSize: {
        x: 0,
        y: 0
      }
    }
  },
  created() {
    this.id = this.$route.params.id
    this.$vuetify.goTo(0)
    this.getUserInfo()
    this.getCourseList()
  },
  methods: {
    getUserInfo() {
      this.httpGet(`/user/info/${this.id}`, (json) => {
        if (json.status === 200) {
          this.userInfo = json.data
          document.title = json.data.username
        } else {
          this.$router.push({ path: `/` })
        }
      })
    },
    getCourseList() {
      this.courseList = []
      this.httpGet(`/curriculum/list?page=${this.page}&limit=${this.size}&user=${this.id}`, (json) => {
        if (json.status === 200) {
          this.courseList = json.data.list
          this.length = json.data.totalPage
        } else {
          //
        }
      })
    },
    getTeacherCourseList() {
      this.courseList = []
      this.httpGet(`/curriculum/list?page=${this.page}&limit=${this.size}&teacher=${this.id}`, (json) => {
        if (json.status === 200) {
          this.courseList = json.data.list
          this.length = json.data.totalPage
        } else {
          //
        }
      })
    },
    pageChange(value) {
      this.page = value
      if (this.type === 0) {
        this.getCourseList()
      } else if (this.type === 1) {
        this.getTeacherCourseList()
      }
    },
    setType(type) {
      this.type = type
      this.page = 1
      if (type === 0) {
        this.getCourseList()
      } else if (type === 1) {
        this.getTeacherCourseList()
      } else if (type === 4) {
        //
      }
      // this.$vuetify.goTo(0)
      document.querySelector('#top').scrollIntoView()
    },
    goToSetting() {
      this.$router.push('/user/setting')
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
    }
  }
}
</script>

<style>

</style>
