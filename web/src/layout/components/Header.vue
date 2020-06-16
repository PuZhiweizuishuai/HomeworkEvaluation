<template>
  <div id="web-header">
    <a-layout-header id="web-layout-header" :style="{ position: position, zIndex: 1, width: '100%' }">

      <a-row>
        <a-col :span="3">
          <div v-if="logoIsShow" class="logo">

            <img src="/logo.png" width="100px" @click="clickLogo">

          </div>
        </a-col>
        <a-col :span="7">
          <a-input-search :style="{margin: '10px 24px 10px 0'}" placeholder="搜索感兴趣的课程" style="width: 80%" />
        </a-col>
        <a-col :span="8">
          <a-menu
            mode="horizontal"
            :default-selected-keys="selectKey"
            :style="{ lineHeight: '62px', width: '90%' }"
            @click="nowSelectKey"
          >
            <a-menu-item key="curriculum">
              <router-link to="/">课程</router-link>
            </a-menu-item>
            <a-menu-item key="bbs">
              <router-link to="/bbs">社区</router-link>
            </a-menu-item>

            <a-menu-item key="about">
              <router-link to="/about">关于</router-link>
            </a-menu-item>
          </a-menu>
        </a-col>
        <a-col :span="6">
          <HeaderAvatar />
        </a-col>
      </a-row>
    </a-layout-header>
  </div>
</template>

<script>
import HeaderAvatar from '@/layout/components/HeaderAvatar.vue'
export default {
  name: 'Header',
  components: { HeaderAvatar },
  props: {
    position: {
      type: String,
      default: 'fixed'
    },
    logoIsShow: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      isShowLogin: false,
      selectKey: ['curriculum']
    }
  },
  created() {
    console.log(window.location.pathname)
    this.setSelectKey()
  },
  methods: {
    nowSelectKey(data) {
      this.selectKey = data.keyPath
      window.sessionStorage.setItem('headerSelectKey', data.keyPath[0])
    },
    setSelectKey() {
      const key = window.sessionStorage.getItem('headerSelectKey')
      if (key) {
        this.selectKey = [key]
      } else {
        this.selectKey = ['bbs']
      }
    },
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
          this.$router.push('/')
        })
        .catch(e => {
          this.$message.info('网络异常，请检查网络后重试！')
        })
    },
    clickLogo() {
      window.sessionStorage.setItem('headerSelectKey', 'bbs')
      this.selectKey = ['bbs']
    }
  }
}
</script>

<style scoped>
.logo {
  width: 80%;
  height: 31px;
  margin: 0px 24px 16px 0;
  float: left;
}
#web-layout-header {
  background-color: white;
  box-shadow: 0px 2px 1px #888888;
}
.login-meun-item {
  cursor: pointer;
  color: black;
}
.login-meun-item:hover {
  color: #1890ff;
}
</style>
