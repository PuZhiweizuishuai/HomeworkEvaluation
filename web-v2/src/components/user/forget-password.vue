<template>
  <v-card>
    <v-card-title>
      忘记密码
    </v-card-title>
    <v-card-subtitle>
      <v-row justify="center">
        <v-col cols="10">
          <v-text-field
            v-model="user.email"
            placeholder="请输入你的邮箱"
            label="邮箱"
            type="email"
            :rules="[() => user.email != null || '邮箱不能为空！']"
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
            v-model="user.verifyCode"
            placeholder="请输入验证码"
            label="验证码"
            :rules="[() => user.verifyCode != null || '验证码不能为空！']"
            clearable
          />
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="7">
          <v-text-field
            v-model="user.code"
            placeholder="你收到的验证码"
            label="收到的验证码"
            :rules="[() => user.code != null || '收到的验证码不能为空！']"
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
        <v-col cols="10">
          <v-text-field
            v-model="user.newPassword"
            placeholder="新密码"
            label="新密码"
            :rules="[() => user.newPassword != null || '新密码不能为空！']"
            clearable
          />
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="10">
          <v-text-field
            v-model="user.password"
            placeholder="请再输入一遍密码"
            label="请再输入一遍密码"
            :rules="[() => user.password != null || '密码不能为空！']"
            clearable
          />
        </v-col>
      </v-row>
      <v-col />
      <v-row justify="center">
        <v-btn color="success" @click="submit()">
          确认
        </v-btn>
      </v-row>
    </v-card-subtitle>
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
  </v-card>
</template>

<script>
export default {
  name: 'ForgetPassword',
  data() {
    return {
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      user: {
        email: '',
        verifyCode: '',
        code: '',
        newPassword: '',
        password: ''
      },
      showMessage: false,
      message: '',
      emailBtn: '发送验证码',
      countDown: null,
      maxTime: 60000,
      emailBtnDisabled: false
    }
  },
  methods: {
    sendEmail() {
      console.log(this.user)
      if (!this.user.email.includes('@')) {
        this.message = '邮箱格式错误！'
        this.showMessage = true
        return
      }
      if (this.user.verifyCode === '' || this.user.verifyCode == null) {
        this.message = '验证码不能为空！'
        this.showMessage = true
        return
      }

      this.httpPost('/verify/email', this.user, (json) => {
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
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    },
    check() {
      if (!this.user.email.includes('@')) {
        this.message = '邮箱格式错误！'
        this.showMessage = true
        return
      }
      if (this.user.verifyCode === '' || this.user.verifyCode == null) {
        this.message = '验证码不能为空！'
        this.showMessage = true
        return
      }
      if (this.user.code === '' || this.user.code == null) {
        this.message = '邮箱验证码不能为空！'
        this.showMessage = true
        return
      }
      if (this.user.newPassword === '' || this.user.newPassword == null) {
        this.message = '新密码不能为空！'
        this.showMessage = true
        return
      }
      if (this.user.newPassword.length < 6) {
        this.message = '密码必须大于6个字符'
        this.showMessage = true
        return
      }
      if (this.user.newPassword !== this.user.password) {
        this.message = '两次输入密码不相同！'
        this.showMessage = true
        return
      }
    },
    submit() {
      this.check()
      this.httpPost('/user/update/forget', this.user, (json) => {
        //
        if (json.status === 200) {
          this.message = '密码重置成功！'
          this.showMessage = true
          this.$emit('success', true)
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    }
  }
}
</script>

<style>

</style>
