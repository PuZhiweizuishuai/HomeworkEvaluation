<template>
  <v-row justify="center" align="center">
    <v-col>
      <v-card
        class="mx-auto"
        outlined
      >
        <v-col />
        <v-row justify="center">
          <v-col cols="10">
            <h2>密码修改</h2>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="10">
            <v-text-field
              v-model="passoword.oldPassword"
              placeholder="原始密码"
              label="原始密码"
              clearable
              type="password"
              :rules="[() => passoword.oldPassword != null || '原始密码不能为空']"
            />
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="10">
            <v-text-field
              v-model="passoword.newPassword"
              placeholder="新密码"
              label="新密码"
              clearable
              type="password"
              :rules="[() => passoword.newPassword != null || '新密码不能为空']"
            />
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="10">
            <v-text-field
              v-model="temp"
              placeholder="请再输入一遍"
              label="请再输入一遍"
              clearable
              type="password"
              :rules="[() => temp == passoword.newPassword || '两次密码不相同']"
            />
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="5">
            <v-img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" max-width="200" @click="getVerifyImage" />
          </v-col>
          <v-col cols="5">
            <v-text-field
              v-model="passoword.verifyCode"
              placeholder="验证码"
              label="验证码"
              :rules="[() => passoword.verifyCode != null || '验证码不能为空']"
              clearable
            />
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="2">
            <v-btn color="primary" @click="save">保存</v-btn>
          </v-col>
          <v-col cols="8">
            说明： 修改密码后需要重新登录
          </v-col>
        </v-row>
        <v-col />
        <v-row justify="center">
          <v-col cols="10">
            {{ getNowEmail() }}
          </v-col>
        </v-row>

        <v-row justify="center">
          <v-col cols="10">
            <BindingEmail v-if="showBindingEmail" />
            <CancelEmail v-if="!showBindingEmail" />
          </v-col>
        </v-row>

        <v-col />
      </v-card>

    </v-col>

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

  </v-row>
</template>

<script>
import BindingEmail from '@/components/user/binding-email.vue'
import CancelEmail from '@/components/user/cancel-email.vue'
export default {
  name: 'UserSetting',
  components: {
    BindingEmail,
    CancelEmail
  },
  data() {
    return {
      passoword: {
        oldPassword: '',
        newPassword: '',
        verifyCode: ''
      },
      temp: '',
      verifyImageUrl: '/api/verifyImage',
      showMessage: false,
      message: '',
      showEmailDialog: false,
      emailBtn: '取消绑定邮箱',
      showBindingEmail: false
    }
  },
  created() {
    this.getNowEmail()
    this.showEmailDialog = true
  },
  methods: {
    clickShowEmail() {
      this.showEmailDialog = true
    },
    getNowEmail() {
      if (this.$store.state.userInfo.email != null) {
        this.showEmailDialog = false
        this.emailBtn = '取消绑定邮箱'
        this.showBindingEmail = false
        return '当前绑定邮箱：' + this.$store.state.userInfo.email
      }
      this.showBindingEmail = true
      this.showEmailDialog = true
      this.emailBtn = '绑定邮箱'
      return '暂未绑定邮箱，请立即绑定！'
    },
    getVerifyImage() {
      this.verifyImageUrl = '/api/verifyImage?t=' + new Date().getTime()
    },
    save() {
      if (this.passoword.oldPassword === '') {
        return
      }
      if (this.passoword.newPassword === '') {
        return
      }
      if (this.passoword.newPassword !== this.temp) {
        return
      }
      if (this.passoword.verifyCode === '') {
        return
      }
      this.httpPost('/user/update/password', this.passoword, (json) => {
        if (json.status === 200) {
          this.message = '修改成功，即将跳转，请重新登录！'
          this.showMessage = true
          this.$store.commit('setUserInfo', null)
          this.$router.push('/login')
        } else {
          this.message = '修改失败!' + json.message
          this.showMessage = true
        }
      })
    }
  }
}
</script>

<style>

</style>
