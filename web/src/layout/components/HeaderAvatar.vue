<template>
  <div>
    <!-- 未登陆时显示 -->
    <router-link v-if="this.$store.state.userInfo === null" to="/login">
      <a-tooltip placement="bottom">
        <template slot="title">
          <span>点击登陆或注册</span>
        </template>
        <div style="float: right; cursor:pointer;">
          <a-avatar :size="40" icon="user" />
          <span v-if="isShowLogin">登陆 | 注册</span>
        </div>
      </a-tooltip>

    </router-link>
    <!-- 登陆成功显示 -->
    <div v-if="this.$store.state.userInfo">
      <div style="float: right; cursor:pointer;">
        <a-popover title="个人管理" placement="bottom">
          <template slot="content">
            <p class="login-meun-item">我的主页</p>
            <p class="login-meun-item">个人设置</p>
            <router-link v-if="isClassShow" to="/admin">
              <p class="login-meun-item" link="/admin" @click="clickClassAdmin">
                系统管理
              </p>
            </router-link>
            <a-divider />
            <p class="login-meun-item" @click="loginOut">退出</p>
          </template>
          <a-avatar :size="40" :src="this.$store.state.userInfo.userAvatarUrl" />
        </a-popover>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HeaderAvatar',
  data() {
    return {
      isShowLogin: false
    }
  },
  methods: {
    isClassShow() {
      const user = this.$store.state.userInfo
      const roles = user.role.role.splic(',')
      for (const role in roles) {
        if (role === 'ROLE_ADMIN' || role === 'ROLE_TEACHER') {
          return true
        }
      }
      return false
    },
    loginOut() {
      fetch(this.SERVER_API_URL + '/loginOut', {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          window.localStorage.clear()
          this.$store.commit('setUserInfo', null)
          window.sessionStorage.setItem('headerSelectKey', 'bbs')
          this.$router.push('/')
        })
        .catch(e => {
          this.$message.info('网络异常，请检查网络后重试！')
        })
    },
    clickClassAdmin() {
      window.sessionStorage.setItem('adminSelect', '首页')
    }
  }
}
</script>

<style scoped>
.login-meun-item {
  cursor: pointer;
  color: black;
}
.login-meun-item:hover {
  color: #1890ff;
}
</style>
