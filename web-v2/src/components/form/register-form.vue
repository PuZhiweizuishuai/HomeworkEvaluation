<template>
  <div>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="registerUser.userId"
          placeholder="请输入你的学号"
          label="学号"
          :rules="[() => registerUser.userId != null || '学号不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="registerUser.email"
          placeholder="请输入你的邮箱"
          label="邮箱"
          :rules="[() => registerUser.email != null || '邮箱不能为空']"
          type="email"
          clearable
        />
      </v-col>
    </v-row>

    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="registerUser.phoneNumber"
          placeholder="请输入你的手机号（系统原因，此项选填）"
          label="手机号"

          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="registerUser.username"
          placeholder="姓名"
          label="姓名"
          :rules="[() => registerUser.username != null || '姓名不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-radio-group v-model="registerUser.sex" row label="性别">
          <v-radio
            label="男"
            value="0"
          />
          <v-radio
            label="女"
            value="1"
          />
        </v-radio-group>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <TimeForm :cols="12" :lable="`生日`" @time="getTime" />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="registerUser.password"
          placeholder="密码"
          label="密码"
          :rules="[() => registerUser.password != null || '密码不能为空']"
          clearable
          type="password"
        />
      </v-col>
    </v-row>
    <v-row v-if="showInvitationCode" justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="registerUser.invitationCode"
          placeholder="邀请码"
          label="邀请码"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="5">
        <img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" @click="getVerifyImage">
      </v-col>
      <v-col cols="5">
        <v-text-field
          v-model="registerUser.verifyCode"
          placeholder="验证码"
          label="验证码"
          :rules="[() => registerUser.verifyCode != null || '验证码不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row v-if="showEmailCheck" justify="center">
      <v-col cols="7">
        <v-text-field
          v-model="registerUser.code"
          placeholder="邮箱验证码"
          label="邮箱验证码"
          :rules="[() => registerUser.code != null || '收到的验证码不能为空！']"
          clearable
        />
      </v-col>
      <v-col cols="3">
        <v-btn color="primary" :disabled="emailBtnDisabled" @click="sendEmail()">
          {{ emailBtn }}
        </v-btn>
      </v-col>
    </v-row>

    <v-row justify="center">
      <v-btn color="primary" @click="submitRegister">注册</v-btn>
    </v-row>
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
  </div>
</template>

<script>
import TimeForm from '@/components/form/time-no-hour.vue'
export default {
  name: 'Register',
  components: {
    TimeForm
  },
  data() {
    return {
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      registerUser: {
        email: '',
        phoneNumber: '',
        password: '',
        invitationCode: '',
        verifyCode: '',
        username: '',
        userId: '',
        sex: null,
        birthday: '',
        code: ''
      },
      webInfo: {
        registerInvitationCode: 1,
        registerEmailCheck: 1
      },
      showEmailCheck: true,
      showInvitationCode: true,
      showMessage: false,
      message: '',
      emailBtnDisabled: false,
      emailBtn: '发送验证码'

    }
  },
  created() {
    this.httpGet('/index/info', (json) => {
      this.webInfo = json.data
      if (this.webInfo.registerInvitationCode === 1) {
        this.showInvitationCode = true
      } else {
        this.showInvitationCode = false
      }
      if (this.webInfo.registerEmailCheck === 1) {
        this.showEmailCheck = true
      } else {
        this.showEmailCheck = false
      }
    })
  },
  methods: {
    sendEmail() {
      if (!this.registerUser.email.includes('@')) {
        this.message = '邮箱格式错误！'
        this.showMessage = true
        return
      }
      if (this.registerUser.verifyCode === '' || this.registerUser.verifyCode == null) {
        this.message = '验证码不能为空！'
        this.showMessage = true
        return
      }

      this.httpPost('/verify/register/email', this.registerUser, (json) => {
        if (json.status === 200) {
          this.message = '邮件发送成功，，请注意查收！'
          this.showMessage = true
          this.countDown = window.setInterval(() => {
            this.emailBtn = (this.maxTime - 1000) / 1000
            this.maxTime = this.maxTime - 1000
            this.emailBtnDisabled = true
            if (this.maxTime < 0) {
              this.maxTime = 60000
              this.emailBtn = '再发送一遍'
              this.emailBtnDisabled = false
              window.clearInterval(this.countDown)
            }
          }, 1000)
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    getTime(value) {
      this.registerUser.birthday = value
    },
    submitRegister() {
      var re = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/
      if (!re.test(this.registerUser.email)) {
        this.message = '邮箱格式错误'
        this.showMessage = true
        return
      }
      if (this.registerUser.password === '' || this.registerUser.password.length < 6 || this.registerUser.password == null) {
        this.message = '密码为空或密码过短，'
        this.showMessage = true
        return
      }
      if (this.registerUser.verifyCode === '' || this.registerUser.verifyCode == null) {
        this.message = '验证码不能为空'
        this.showMessage = true
        return
      }
      if (this.registerUser.username === '' || this.registerUser.username == null) {
        this.message = '用户名不能为空'
        this.showMessage = true
        return
      }

      if (this.registerUser.phoneNumber !== '') {
        var myreg = /^[1][3,4,5,7,8][0-9]{9}$/
        if (!myreg.test((this.registerUser.phoneNumber))) {
          this.message = '手机号格式错误'
          this.showMessage = true
          return
        }
      }
      if (this.registerUser.sex === null) {
        return
      }
      this.$emit('register', this.registerUser)
    },
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    }
  }
}
</script>

<style>

</style>
