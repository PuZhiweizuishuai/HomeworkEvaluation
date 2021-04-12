<template>
  <v-main>
    <v-container fill-height>
      <!-- <video id="v1" autoplay loop muted>
        <source id="videoSource" src="/video/bg.mp4" type="video/mp4">
      </video> -->
      <v-row justify="center" align="center">
        <v-col cols="12">
          <v-card
            class="mx-auto"
            max-width="500"
          >
            <v-row style="height: 64px" justify="center">
              <v-col cols="10">

                <v-btn text @click="backHome">
                  <v-icon>
                    mdi-arrow-left
                  </v-icon>
                  返回
                </v-btn>

              </v-col>
            </v-row>
            <v-row justify="center">
              <img
                :src="this.$store.state.webInfo.logo"
                height="100px"
              >
            </v-row>
            <v-row justify="center">
              <h3>{{ type }}</h3>
            </v-row>
            <v-row style="height: 48px" />
            <LoginFrom v-show="showLogin" @login="userLogin" />
            <RegisterFrom v-show="showLogin == false" @register="register" />
            <v-row justify="center">
              <v-col cols="5">
                <v-btn text color="primary" @click="showForgetPas = true">忘记密码</v-btn>

              </v-col>
              <v-col cols="5" style="text-align:right">
                <v-btn text @click="moveRegister">{{ moveMessage }}</v-btn>
              </v-col>
            </v-row>

          </v-card>
        </v-col>

      </v-row>
      <v-dialog v-model="showForgetPas" max-width="600">
        <ForgetPassword @success="forgetSuccess()" />
      </v-dialog>

      <v-snackbar
        v-model="showMessage"
        :top="true"
        :timeout="3000"
      >
        {{ message }}

        <template v-slot:action="{ attrs }">
          <v-btn
            color="pink"
            text
            v-bind="attrs"
            @click="showMessage = false"
          >
            关闭
          </v-btn>
        </template>
      </v-snackbar>
    </v-container>
  </v-main>
</template>

<script>
import LoginFrom from '@/components/form/login-form.vue'
import RegisterFrom from '@/components/form/register-form.vue'
import ForgetPassword from '@/components/user/forget-password.vue'

export default {
  name: 'Login',
  components: {
    LoginFrom,
    RegisterFrom,
    ForgetPassword
  },
  data() {
    return {
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      user: {},
      type: '登录',
      moveMessage: '没有账号，创建账号',
      showLogin: true,
      message: '',
      showMessage: false,
      showForgetPas: false
    }
  },
  created() {

  },
  methods: {
    forgetSuccess() {
      this.showForgetPas = false
      this.message = '密码重置成功！请尝试登录！'
      this.showMessage = true
    },
    userLogin(value) {
      this.httpPost(`/login`, value, this.loginCallBack)
    },
    loginCallBack(json) {
      if (json.status === 200) {
        const user = json.data
        // 保存用户
        this.$store.commit('setUserInfo', user)
        // 跳转到首页
        this.$router.push('/')
      } else {
        this.message = json.message
        this.showMessage = true
      }
    },
    register(value) {
      console.log(value)
      this.httpPost(`/user/register`, value, (json) => {
        if (json.status === 200) {
          this.json = '注册成功,请登录'
          this.showMessage = true
          this.type = '登录'
          this.showLogin = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    moveRegister() {
      if (this.type === '登录') {
        this.type = '注册'
      } else {
        this.type = '登录'
      }
      if (this.moveMessage === '没有账号，创建账号') {
        this.moveMessage = '已有账号，我要登录'
      } else {
        this.moveMessage = '没有账号，创建账号'
      }
      this.showLogin = !this.showLogin
    },
    backHome() {
      this.$router.push('/')
    }
  }
}
</script>

<style>
        #v1{
            position: fixed;
            right: 0px;
            bottom: 0px;
            width: 100%;
            height: 100%;
            height: auto;
            width: auto;
            /*加滤镜*/
            /*filter: blur(15px); //背景模糊设置 */
            /*-webkit-filter: grayscale(100%);*/
            /*filter:grayscale(100%); //背景灰度设置*/
            z-index:0
        }
        #videoSource{
            width: 100%;
            height: 100%;
            /* height: auto;
            width: auto; */
        }
</style>
