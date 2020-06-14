<template>
  <div id="login-bg">
    <img id="login-logo" src="/logo.png" width="300px" height="auto">
    <div id="login-with-logon">
      <a-menu
        mode="horizontal"
        :default-selected-keys="selectType"
        :style="{ lineHeight: '64px' }"
        @click="showSelectPage"
      >
        <a-menu-item key="login">
          登陆
        </a-menu-item>
        <a-menu-item key="logon">
          注册
        </a-menu-item>
      </a-menu>
      <div v-if="showLogin">

        <br>
        <input v-model="userFrom.userId" class="login-user-input" type="text" placeholder="请输入你的学号（手机与邮箱登陆开发中）">
        <br><br><br>
        <input v-model="userFrom.password" class="login-user-input" type="password" placeholder="请输入你的密码">

        <br><br>

        <a-row>
          <a-col :span="12">
            <img
              :src="verifyCodeImage.url"
              :title="verifyCodeImage.title"
              @click="refreshVerifyCodeImage"
            >
          </a-col>
          <a-col :span="2">
            <input v-model="userFrom.verifyCode" class="login-user-input-verify" type="text" placeholder="请输入验证码">
          </a-col>
        </a-row>
        <br>
        <a-row>
          <a-radio :checked="userFrom.rememberMe" style="float:left;margin-left: 10px;" @click="rememberMeChange">记住我</a-radio>
          <span style="float:right; margin-right: 20px;cursor:pointer" @click="forgetPassword">忘记密码</span>
        </a-row>
        <br>
        <a-button type="primary" style="width:300px" @click="login">
          登陆
        </a-button>

        <br>
        <br>
        <a-row>
          社交账号登陆待开发，招聘前端
        </a-row>
      </div>
      <div v-if="showLogin === false">

        <br>
        <input class="login-user-input" type="text" placeholder="请输入你的学号">
        <br><br>
        <input class="login-user-input" type="password" placeholder="请输入你的密码">
        <br><br>
        <input class="login-user-input" type="password" placeholder="请再输入一遍密码">

        <br><br>
        <input class="login-user-input" type="password" placeholder="请输入你的邀请码">
        <br><br>
        <a-row>
          <a-col :span="12">
            <img
              :src="verifyCodeImage.url"
              :title="verifyCodeImage.title"
              @click="refreshVerifyCodeImage"
            >
          </a-col>
          <a-col :span="2">
            <input class="login-user-input-verify" type="password" placeholder="请输入验证码">
          </a-col>
        </a-row>
        <br>
        <a-button type="primary" style="width:300px">
          注册
        </a-button>
      </div>
    </div>
    <Footer style="margin-top:650px;background-color:transparent" />
  </div>
</template>

<script>
import Footer from '@/layout/components/Footer.vue'
export default {
  name: 'LoginWithLoginOn',
  components: { Footer },
  data() {
    return {
      selectType: ['login'],
      userFrom: {
        userId: '',
        password: '',
        verifyCode: '',
        rememberMe: false
      },
      verifyCodeImage: {
        url: this.SERVER_API_URL + '/verifyImage?' + Math.random(),
        title: '点击刷新！'
      },
      showLogin: true
    }
  },
  methods: {
    showSelectPage(data) {
      this.selectType = data.keyPath
      if (data.keyPath[0] === 'login') {
        this.showLogin = true
      } else {
        this.showLogin = false
      }
    },
    // 刷新验证码
    refreshVerifyCodeImage() {
      this.verifyCodeImage.url = this.SERVER_API_URL + '/verifyImage?' + Math.random()
    },
    forgetPassword() {
      this.$message.info('开发中，如有需要请联系管理员')
    },
    login() {
      if (this.userFrom.userId === '' || this.userFrom.password === '' || this.userFrom.verifyCode === '') {
        this.$message.info('你还有没有填写的字段！')
        return
      }
      fetch(this.SERVER_API_URL + '/login', {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(this.userFrom)
      }).then(response => response.json())
        .then(json => {
          this.loginJudge(json)
        })
        .catch(e => {
          this.$message.info('网络异常，请检查网络后重试！')
        })
    },
    loginJudge(user) {
      if (user.status === 200) {
        this.$notification['success']({
          message: '登陆成功',
          description:
         `欢迎您 ${user.data.username}`
        })

        // 保存用户信息
        localStorage.setItem('user', user.data)
        this.userInfo = user.data
        // 保存用户
        this.$store.commit('setUserInfo', user.data)
        // 跳转到首页
        this.$router.push('/')
      } else {
        this.$message.info(user.error)
      }
    },
    rememberMeChange() {
      this.userFrom.rememberMe = !this.userFrom.rememberMe
    }
  }
}
</script>

<style scoped>
#login-with-logon {
  background-color: white;
  margin-top: 150px;
  width: 400px;
  text-align: center;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  height: 500px;
  box-shadow: 2px 2px 2px #888888;
}
#login-bg {
  width: 100%;
  height: 100%;
  background: url("/sign_bg.png") no-repeat;
  background-color: #b8e5f8;
  position: fixed;
}
#login-logo {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}
.login-user-input {
    width: 90%;
    height: 50px;
    background-color: transparent;
    border-top-style: none;
    border-right-style: none;
    border-left-style: none;
    border-bottom-style: solid;
    border-bottom-width: 1px;
    font-size: 15px;
    outline:0;  /*去掉按钮选中后的蓝色边线*/
    margin-right: 10px;
}
.login-user-input-verify {
    height: 50px;
    background-color: transparent;
    border-top-style: none;
    border-right-style: none;
    border-left-style: none;
    border-bottom-style: solid;
    border-bottom-width: 1px;
    font-size: 15px;
    outline:0;  /*去掉按钮选中后的蓝色边线*/
}
</style>
