<template>
  <v-container>
    <UserList ref="UserList" :type="1" @create="createNewUser" @import="importUser" @edit="editUser" />

    <v-dialog v-model="createUserDialog" width="1000">
      <v-card width="1000">
        <v-card-title>创建新用户</v-card-title>
        <v-card-text>
          <CreateUser @success="getUserList" />
        </v-card-text>
      </v-card>
    </v-dialog>
    <v-bottom-sheet v-model="sheet">
      <v-sheet class="text-center" height="500px">
        <v-toolbar>
          <v-btn icon @click="sheet = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
          <v-toolbar-title>{{ editData.username }} 修改</v-toolbar-title>
          <v-spacer />
        </v-toolbar>
        <br>
        <v-row justify="center">
          <v-col cols="5">
            <v-select
              v-model="updateRole"
              :items="roleItem"
              label="身份修改"
              item-text="text"
              item-value="value"
              clearable
            />
          </v-col>
          <v-col cols="5">
            <v-row>
              <v-btn depressed color="success" @click="sendAlterUserRole">确认</v-btn>
            </v-row>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="10">
            <v-row>
              <v-btn depressed color="primary" @click="updatePassword">重置密码</v-btn>
            </v-row>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="10">
            禁言或封禁参考时间：一小时： 60， 一天： 1440， 七天： 10080， 三十天：43200，一年：525600
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="5">
            <v-text-field
              v-model="lockTime"
              placeholder="禁言或封禁时间"
              label="禁言或封禁时间（单位：分钟）"
              :rules="[() => lockTime > 0 || '禁言或封禁时间必须大于0']"
              clearable
            />
          </v-col>
          <v-col cols="5">
            <v-row>
              <v-btn depressed color="success" @click="lockUser(2)">禁言</v-btn>
              <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />
              <v-btn depressed color="primary" @click="lockUser(2)">锁定</v-btn>

              <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />
              <v-btn v-if="editData.status == 2 || editData.status == 1" depressed @click="lockUser(0)">取消禁言或锁定状态</v-btn>
            </v-row>
          </v-col>
        </v-row>
        <v-row v-if="editData.status == 2 || editData.status == 1" justify="center">
          <v-col cols="10">
            <span v-if="editData.status == 2" v-text="`锁定`" />
            <span v-if="editData.status == 1" v-text="`禁言`" />
            结束时间为：<span v-text="TimeUtil.renderTime(editData.startLockTime + editData.lockTime)" />
          </v-col>
        </v-row>
      </v-sheet>
    </v-bottom-sheet>
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
    <v-dialog v-model="showNewPassword" width="500">
      <v-card>
        <v-card-text>
          请将重置后的密码: <strong>{{ newPassword }}</strong>, 发送给用户 {{ editData.userId }}, 点击确认后此消息删除！
        </v-card-text>
        <v-card-actions>
          <v-btn text @click="showNewPassword = false">确认 </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import UserList from '@/components/list/user-list.vue'
import CreateUser from '@/components/form/create-user-form.vue'
export default {
  components: {
    UserList,
    CreateUser
  },
  data() {
    return {
      TimeUtil,
      createUserDialog: false,
      sheet: false,
      editData: {},
      roleItem: [
        { text: '管理员', value: 'ROLE_ADMIN' },
        { text: '教师', value: 'ROLE_TEACHER' },
        { text: '学生', value: 'ROLE_STUDENT' }
      ],
      message: '',
      showMessage: false,
      updateRole: '',
      showNewPassword: false,
      newPassword: '',
      lockTime: 0
    }
  },
  methods: {
    getUserList() {
      this.$refs.UserList.getUserList()
    },
    createNewUser(value) {
      this.createUserDialog = true
    },
    importUser(value) {
      this.$router.push('/admin/user/import')
    },
    editUser(value) {
      this.sheet = true
      this.editData = value
      this.updateRole = value.role.role
      console.log(value)
    },
    sendAlterUserRole() {
      const data = {
        'userId': this.editData.userId,
        'role': this.updateRole
      }
      this.httpPost(`/admin/user/update/role`, data, (json) => {
        if (json.status === 200) {
          this.message = '修改成功！'
          this.showMessage = true
          this.$refs.UserList.getUserList()
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    updatePassword() {
      const data = {
        'userId': this.editData.userId
      }
      this.httpPost(`/admin/user/update/password`, data, (json) => {
        if (json.status === 200) {
          this.newPassword = json.data.password
          this.showNewPassword = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    lockUser(value) {
      if (this.lockTime <= 0 && value !== 0) {
        this.message = '时间必须大于 0'
        this.showMessage = true
        return
      }
      const data = {
        userId: this.editData.userId,
        status: value,
        time: this.lockTime
      }
      this.httpPost(`/admin/user/update/status`, data, (json) => {
        if (json.status === 200) {
          this.message = '禁言成功'
          this.editData.status = value
          this.editData.startLockTime = new Date().getTime()
          this.editData.lockTime = this.lockTime * 60000
          this.showMessage = true
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
