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
        { icon: 'mdi-account', text: '个人主页', link: `/user/`, id: 0 },
        { icon: 'mdi-wrench', text: '个人设置', link: '/user/setting', id: 1 },
        { icon: 'mdi-logout', text: '退出', link: '/logout', id: 2 }
      ]
    }
  },
  created() {
    this.userInfo = this.$store.state.userInfo
  },
  methods: {
    headClick(value) {
      if (value === 0) {
        // this.$router.push('/user/' + this.userInfo.id)
        location.replace('/user/' + this.userInfo.userId)
      } else if (value === 1) {
        if (this.$route.path === '/user/setting') {
          return
        }
        this.$router.push('/user/setting')
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
