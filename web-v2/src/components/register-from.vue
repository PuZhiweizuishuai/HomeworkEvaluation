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
    <v-row v-if="this.$store.state.webInfo.openInvitationRegister == 1" justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="registerUser.invitationCode"
          placeholder="邀请码"
          label="邀请码"
          clearable
          :rules="[() => registerUser.invitationCode != null || '邀请码不能为空']"
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
    <v-row justify="center">
      <v-btn color="primary" @click="submitRegister">注册</v-btn>
    </v-row>
  </div>
</template>

<script>
export default {
  name: 'Register',
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
        sex: null
      }
    }
  },
  methods: {
    submitRegister() {
      var re = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/
      if (!re.test(this.registerUser.email)) {
        console.log('邮箱格式错误')
        return
      }
      if (this.registerUser.password === '' || this.registerUser.password.length < 6 || this.registerUser.verifyCode === '' || this.registerUser.username === '') {
        console.log('密码过短')
        return
      }
      if (this.$store.state.webInfo.openInvitationRegister === 1 && this.registerUser.invitationCode === '') {
        console.log('缺少邀请码')
        return
      }

      if (this.registerUser.phoneNumber !== '') {
        var myreg = /^[1][3,4,5,7,8][0-9]{9}$/
        if (!myreg.test((this.registerUser.phoneNumber))) {
          console.log('手机格式错误')
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
