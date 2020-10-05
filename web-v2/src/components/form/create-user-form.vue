<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="addUserFrom.userId"
          placeholder="学号/工号"
          label="学号/工号"
          :rules="[() => addUserFrom.userId != null || '学号/工号不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="addUserFrom.username"
          placeholder="姓名"
          label="姓名"
          :rules="[() => addUserFrom.username != null || '姓名不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-radio-group v-model="addUserFrom.sex" row label="性别">
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
          v-model="addUserFrom.school"
          placeholder="学校"
          label="学校"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="addUserFrom.major"
          placeholder="专业"
          label="专业"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="addUserFrom.grade"
          placeholder="年级"
          label="年级"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-select
          v-model="addUserFrom.role"
          :items="roleItem"
          label="角色"
          item-text="text"
          item-value="value"

          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="addUserFrom.title"
          placeholder="职位"
          label="职位"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-btn depressed color="success" @click="addUser">
        创建
      </v-btn>
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
    <v-dialog v-model="showResult" max-width="500">
      <v-card>
        <v-card-title>创建结果,请记录好密码，此窗口关闭后密码将消失！</v-card-title>
        <v-card-text>
          <li v-for="item in relultList" :key="item.userId">
            账号：{{ item.userId }}, 密码： {{ item.password }}, 结果： {{ item.msg }}
          </li>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      addUserFrom: {
        userId: '',
        username: '',
        sex: null,
        school: '',
        major: '',
        grade: new Date().getFullYear(),
        role: '',
        title: ''
      },
      roleItem: [
        { text: '管理员', value: 'ROLE_ADMIN' },
        { text: '教师', value: 'ROLE_TEACHER' },
        { text: '学生', value: 'ROLE_STUDENT' }
      ],
      showMessage: false,
      message: '',
      showResult: false,
      relultList: []
    }
  },
  methods: {
    addUser() {
      const userList = []
      if (this.addUserFrom.userId == null || this.addUserFrom.userId === '') {
        this.message = '学号/工号不能为空'
        this.showMessage = true
        return
      }
      if (this.addUserFrom.username == null || this.addUserFrom.username === '') {
        this.message = '姓名不能为空'
        this.showMessage = true
        return
      }
      if (this.addUserFrom.sex == null || this.addUserFrom.sex === '') {
        this.message = '性别不能为空'
        this.showMessage = true
        return
      }
      if (this.addUserFrom.role == null || this.addUserFrom.role === '') {
        this.message = '角色号不能为空'
        this.showMessage = true
        return
      }
      userList.push(this.addUserFrom)
      this.httpPost(`/admin/user/add`, userList, (json) => {
        if (json.status === 200) {
          this.relultList = json.data
          this.showResult = true
          this.addUserFrom = {
            userId: '',
            username: '',
            sex: null,
            school: '',
            major: '',
            grade: new Date().getFullYear(),
            role: '',
            title: ''
          }
          this.$emit('success', true)
        } else {
          //
        }
      })
    }
  }
}
</script>

<style>

</style>
