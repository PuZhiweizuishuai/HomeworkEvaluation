<template>
  <div>
    <v-navigation-drawer
      v-model="drawer"
      clipped
      app
    >
      <router-link :to="`/course/learn/${id}`">
        <v-row justify="center" align="center">
          <v-col cols="11" style="text-align: center">

            <v-img :aspect-ratio="16/9" :src="courseInfo.curriculumImageUrl" />

          </v-col>
        </v-row>
      </router-link>
      <v-list>
        <router-link v-for="item in items" :key="item.text" :to="item.link">
          <v-list-item
            v-if="item.show"
            link
          >
            <v-list-item-action>
              <v-icon>{{ item.icon }}</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>
                {{ item.text }}
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </router-link>
      </v-list>
      <v-divider />
      <Footer />
    </v-navigation-drawer>

    <v-app-bar
      :clipped-left="$vuetify.breakpoint.lgAndUp"
      app
      color="blue darken-3"
      dark
    >
      <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
      <v-toolbar-title
        style="width: 200px"
        class="ml-0 pl-4"
      >
        <!-- <v-img :src="this.$store.state.webInfo.logo" width="50" /> -->

        <span class="hidden-sm-and-down"><router-link to="/" style="color:white">{{ this.$store.state.webInfo.name }}</router-link></span>

      </v-toolbar-title>
      <v-text-field
        flat
        solo-inverted
        hide-details
        prepend-inner-icon="mdi-magnify"
        label="搜索"
      />
      <v-spacer />
      <v-btn icon>
        <v-icon>mdi-apps</v-icon>
      </v-btn>
      <v-btn icon>
        <v-icon>mdi-bell</v-icon>
      </v-btn>
      <!-- 登陆后显示 -->
      <Head v-if="this.$store.state.userInfo" />
      <!-- 未登录显示 -->
      <v-btn
        v-if="this.$store.state.userInfo == null"
        outlined
        @click="goToLoginPage"
      >
        <v-icon left dark>mdi-account</v-icon> 登录
      </v-btn>
    </v-app-bar>
    <v-main>

      <router-view :course="courseInfo" :role="userRole" />

    </v-main>
    <BackToTop />

  </div>
</template>

<script>
/**
 * 主要布局
 */
import Head from '@/layout/components/head.vue'
import Footer from '@/layout/components/footer.vue'
import BackToTop from '@/components/back-to-top.vue'

export default {
  components: {
    Head,
    Footer,
    BackToTop
  },
  data: () => ({
    mini: false,
    drawer: true,
    id: 0,
    items: [
      { icon: 'mdi-alert-circle-outline', text: '公告', link: `/course/learn/`, type: '', teacher: false, show: true },
      { icon: 'mdi-calculator-variant', text: '评分标准', link: `/course/learn/`, type: '/score', teacher: false, show: true },
      { icon: 'mdi-file-table-box-multiple-outline', text: '课件', link: `/course/learn/`, type: '/courseware', teacher: false, show: true },
      { icon: 'mdi-file-document-edit', text: '测验与作业', link: `/course/learn/`, type: '/exam', teacher: false, show: true },
      { icon: 'mdi-facebook-messenger', text: '讨论区', link: `/course/learn/`, type: '/bbs', teacher: false, show: true },
      { icon: 'mdi-cog', text: '课程管理', link: `/course/learn/`, type: '/setting', teacher: true, show: false }
    ],
    courseInfo: {},
    userRole: {}
  }),
  created() {
    this.id = this.$route.params.id
    this.getCourseInfo()
  },
  methods: {
    goToLoginPage() {
      this.$router.push('/login')
    },
    getCourseInfo() {
      this.httpGet(`/curriculum/learn/${this.id}`, (json) => {
        if (json.status === 200) {
          document.title = json.data.course.curriculumName
          this.courseInfo = json.data.course
          this.userRole = json.data.user
          this.setList()
        } else {
          this.$router.push(`/course/info/${this.id}`)
        }
      })
    },
    setList() {
      for (let i = 0; i < this.items.length; i++) {
      // 获取课程内权限
        if (this.items[i].teacher) {
          if (this.userRole.role === 'ROLE_TEACHER') {
            this.items[i].show = true
          }
        }
        this.items[i].link = this.items[i].link + this.id + this.items[i].type
      }
    }
  }
}
</script>

<style>
a {
  text-decoration: none;
}
</style>
