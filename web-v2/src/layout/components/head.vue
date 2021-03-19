<template>
  <v-menu v-if="this.$store.state.userInfo" top offset-y>
    <template v-slot:activator="{ on, attrs }">
      <v-btn
        dark
        v-bind="attrs"
        icon
        large
        v-on="on"
      >
        <v-avatar
          size="32px"
          item
        >
          <v-img
            :src="userInfo.userAvatarUrl"
            :alt="userInfo.username"
            :title="userInfo.username"
          /></v-avatar>
      </v-btn>
    </template>
    <v-list>
      <v-list-item
        v-for="(item, index) in headItem"
        v-show="item.show"
        :key="index"
        link
        @click="headClick(item.id)"
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
    </v-list>
  </v-menu>
</template>

<script>
export default {
  name: 'Head',
  data() {
    return {
      userInfo: {},
      headItem: [
        { icon: 'mdi-account', text: '个人主页', link: `/user/`, id: 0, show: true, admin: false },
        { icon: 'mdi-book-variant', text: '我的课程', link: `/myclass`, id: 1, show: true, admin: false },
        { icon: 'mdi-wrench', text: '个人设置', link: '/user/setting', id: 2, show: true, admin: false },
        { icon: 'mdi-application-settings', text: '系统管理', link: '/admin', id: 3, show: false, admin: true },
        { icon: 'mdi-logout', text: '退出', link: '/logout', id: 4, show: true, admin: false }
      ]
    }
  },
  created() {
    this.userInfo = this.$store.state.userInfo
    for (let i = 0; i < this.headItem.length; i++) {
      if (this.headItem[i].admin) {
        if (this.userInfo.role.role === 'ROLE_ADMIN') {
          this.headItem[i].show = true
        } else {
          this.headItem[i].show = false
        }
      }
    }
  },
  methods: {
    headClick(value) {
      if (value === 0) {
        this.$router.push('/user/' + this.userInfo.userId)
        // location.replace('/user/' + this.userInfo.userId)
      } else if (value === 1) {
        if (this.$route.path === '/myclass') {
          return
        }
        this.$router.push('/myclass')
      } else if (value === 2) {
        if (this.$route.path === '/user/setting') {
          return
        }
        this.$router.push('/user/setting')
      } else if (value === 3) {
        if (this.$route.path === '/admin') {
          return
        }
        this.$router.push('/admin')
      } else {
        this.logout()
      }
    },
    logout() {
      console.log('退出')
      this.$store.commit('setUserInfo', null)
      this.httpGet(`/logInout`, (json) => {
        if (json.status === 200) {
          this.$store.commit('setUserInfo', null)
          if (this.$route.path === '/') {
            location.reload()
          } else {
            this.$router.push('/')
          }
        } else {
          //
        }
      })
    }
  }
}
</script>

<style>

</style>
