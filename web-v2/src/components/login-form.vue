<template>
  <div>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="user.userId"
          placeholder="请输入你的学号或邮箱"
          label="学号或邮箱"
          :rules="[() => user.userId != null || '请输入你的学号或邮箱']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="user.password"
          placeholder="密码"
          label="密码"
          clearable
          :rules="[() => user.password != null || '密码不能为空']"
          type="password"
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
          placeholder="验证码"
          label="验证码"
          :rules="[() => user.verifyCode != null || '验证码不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="3">
        <v-switch v-model="user.rememberMe" label="记住我" />
      </v-col>
      <v-col cols="7" />
    </v-row>
    <v-row justify="center">
      <v-btn color="primary" @click="submitLog">登录</v-btn>
    </v-row>
  </div>
</template>

<script>
export default {
  name: 'LoginFrom',
  data() {
    return {
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      user: {
        userId: '',
        password: '',
        rememberMe: false,
        verifyCode: ''
      }
    }
  },
  methods: {
    submitLog() {
      if (this.user.userId === '' || this.user.password === '' || this.user.verifyCode === '' ||
      this.user.userId === null || this.user.password === null || this.user.verifyCode === null) {
        return
      }
      this.$emit('login', this.user)
    },
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    }
  }
}
</script>

<style>

</style>
