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

        <span class="hidden-sm-and-down"><router-link to="/" style="color:white">{{ this.$store.state.webInfo.name }}管理</router-link></span>

      </v-toolbar-title>
      <v-text-field
        v-model="searchText"
        flat
        solo-inverted
        hide-details
        prepend-inner-icon="mdi-magnify"
        label="搜索"
        @keydown="search"
      />
      <v-spacer />

      <Notice v-if="this.$store.state.userInfo" />
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
      <router-view ref="mainRoutrView" />

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

import Notice from '@/layout/components/notice.vue'
import BackToTop from '@/components/back-to-top.vue'

export default {
  components: {
    Head,
    Footer,
    BackToTop,
    Notice
  },
  data: () => ({
    mini: false,
    drawer: true,
    searchText: '',
    items: [
      { icon: 'mdi-application-settings', text: '首页', link: '/admin' },
      { icon: 'mdi-account-cog', text: '用户列表', link: '/admin/user' },
      { icon: 'mdi-book-open-outline', text: '课程列表', link: '/admin/course' },
      { icon: 'mdi-tag', text: '课程分类管理', link: '/admin/coursetag' },
      { icon: 'mdi-facebook-messenger', text: '社区管理', link: '/admin/bbs' },
      { icon: 'mdi-tune', text: '首页轮播图', link: '/admin/topimg' }
    ],
    courseTagList: []
  }),
  created() {
  // this.getcourseTagList()
  },
  methods: {
    goToLoginPage() {
      this.$router.push('/login')
    },
    search(e) {
      if (e.key === 'Enter') {
        if (this.searchText === '') {
          return
        }
        if (this.$route.path === '/search') {
          this.$refs.mainRoutrView.setKey(this.searchText)
          this.$refs.mainRoutrView.getSearchData()
        } else {
          this.$router.push({ path: '/search', query: { key: this.searchText }})
        }
        this.searchText = ''
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
