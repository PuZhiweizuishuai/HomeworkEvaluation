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
            <h2>基本信息修改</h2>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="8">
            <v-text-field
              v-model="userInfo.username"
              placeholder="用户名"
              label="用户名(25字以内)"
              clearable
              :rules="[() => userInfo.username != null || '标题不能为空']"
              :disabled="usernameAlter"
            />
          </v-col>
          <v-col cols="2">
            <v-btn color="primary" @click="usernameAlter = !usernameAlter">修改</v-btn>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="8">
            <v-text-field
              v-model="userInfo.userIntro"
              label="简介"
              clearable
              placeholder="填写个人简介，让更多人认识你！"
              :disabled="introductionAlter"
            />
          </v-col>
          <v-col cols="2">
            <v-btn color="primary" @click="introductionAlter = !introductionAlter">修改</v-btn>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="8">
            <Time :cols="12" :time="userInfo.birthday" :lable="'生日'" @time="getBirthday" />
          </v-col>
          <v-col cols="2" />
        </v-row>
        <v-row justify="center">
          <v-col cols="8">
            <v-text-field
              v-model="userInfo.school"
              placeholder="学校"
              label="学校"
              clearable
              :disabled="schoolAlter"
            />
          </v-col>
          <v-col cols="2">
            <v-btn color="primary" @click="schoolAlter = !schoolAlter">修改</v-btn>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="8">
            <v-text-field
              v-model="userInfo.major"
              placeholder="专业"
              label="专业"
              clearable
              :disabled="majorAlter"
            />
          </v-col>
          <v-col cols="2">
            <v-btn color="primary" @click="majorAlter = !majorAlter">修改</v-btn>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="8">
            <v-text-field
              v-model="userInfo.grade"
              placeholder="年级"
              label="年级"
              clearable
              :disabled="gradeAlter"
            />
          </v-col>
          <v-col cols="2">
            <v-btn color="primary" @click="gradeAlter = !gradeAlter">修改</v-btn>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="8">
            <v-text-field
              v-model="userInfo.userQq"
              placeholder="QQ"
              label="QQ"
              clearable
              :disabled="userQQAlter"
            />
          </v-col>
          <v-col cols="2">
            <v-btn color="primary" @click="userQQAlter = !userQQAlter">修改</v-btn>
          </v-col>
        </v-row>
        <v-col />
        <v-row justify="center">
          <v-col cols="2">
            <v-btn color="primary" @click="save">保存</v-btn>
          </v-col>
          <v-col cols="8">
            说明： 点击保存后才会最终修改成功
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
import Time from '@/components/form/time-no-hour.vue'

export default {
  name: 'UserSetting',
  components: {
    Time
  },
  data() {
    return {
      userInfo: {
        username: '',
        birthday: ''
      },
      usernameAlter: true,
      introductionAlter: true,
      message: '',
      showMessage: false,
      birthdayAlter: true,
      menu: false,
      userQQAlter: true,
      schoolAlter: true,
      gradeAlter: true,
      majorAlter: true
    }
  },
  created() {
    this.userInfo = this.$store.state.userInfo
  },
  methods: {
    getBirthday(value) {
      this.userInfo.birthday = value
    },
    save() {
      const data = {
        userIntro: this.userInfo.userIntro,
        school: this.userInfo.school,
        major: this.userInfo.major,
        grade: this.userInfo.grade,
        userQq: this.userInfo.userQq
      }
      this.httpPost('/user/update/info', data, (json) => {
        if (json.status === 200) {
          this.$store.commit('setUserInfo', this.userInfo)
          this.message = '修改成功'
          this.showMessage = true
        } else {
          this.message = '修改失败！' + json.message
          this.showMessage = true
        }
      })
    }
  }
}
</script>

<style>

</style>
