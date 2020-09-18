<template>
  <div>
    <v-navigation-drawer
      v-model="drawer"
      clipped
      app
    >
      <v-list>
        <router-link v-for="item in items" :key="item.text" :to="item.link">
          <v-list-item

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
        <span class="hidden-sm-and-down">{{ this.$store.state.webInfo.name }}</span>
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
      <router-view />
    </v-main>
  </div>
</template>

<script>
/**
 * 主要布局
 */
import Head from '@/layout/components/head.vue'
import Footer from '@/layout/components/footer.vue'
export default {
  components: {
    Head,
    Footer
  },
  data: () => ({
    mini: false,
    drawer: false,
    items: [
      { icon: 'mdi-book-open-outline', text: '课程', link: '/' },
      { icon: 'mdi-facebook-messenger', text: '社区', link: '/bbs' },
      { icon: 'mdi-content-copy', text: '我的课程', link: '/myclass' }
    ],
    courseTagList: []
  }),
  created() {
  // this.getcourseTagList()
  },
  methods: {
    getcourseTagList() {
      this.httpGet('/course/tag/list', (json) => {
        if (json.status === 200) {
          this.courseTagList = json.data
        }
      })
    },
    goToLoginPage() {
      this.$router.push('/login')
    }
  }
}
</script>

<style>
a {
  text-decoration: none;
}
</style>
