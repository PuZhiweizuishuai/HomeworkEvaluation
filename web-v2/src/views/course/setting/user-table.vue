<template>
  <v-container>
    <!-- 顶部操作栏 -->
    <v-row>
      <v-col cols="12">
        <h3>查找：</h3>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="3">
        <v-text-field
          v-model="searchKey.userId"
          label="学号"
          clearable
        />

      </v-col>
      <v-col cols="3">
        <v-text-field
          v-model="searchKey.name"
          label="姓名"
          clearable
        />
      </v-col>
      <v-col cols="2">
        <v-select
          v-model="searchKey.role"
          :items="roleItem"
          label="角色"
          item-text="text"
          item-value="value"
          clearable
        />
      </v-col>
      <v-col cols="2">
        <v-btn depressed color="primary" @click="searchUser">
          查找
        </v-btn>
      </v-col>
      <v-col cols="2">
        <v-btn depressed color="success" @click="dialog = true">
          导入新学生
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <!-- 学生总人数显示 -->
    <v-row>
      <v-col>
        当前学生总数： <strong>{{ totalCount }}</strong>
      </v-col>
    </v-row>
    <!-- 表格显示 -->
    <v-row>
      <v-col cols="12">
        <v-data-table
          :headers="headers"
          :items="userList"
          hide-default-footer
          :items-per-page="size"
          :page.sync="page"
        >

          <template v-slot:item.userId="{ item }">
            <a :href="`/user/${item.userId}`" target="_blank"> {{ item.userId }} </a>
          </template>
          <template v-slot:item.sex="{ item }">
            <span v-text="getSex(item.sex)" />
          </template>
          <template v-slot:item.classRole="{ item }">
            <v-chip
              class="ma-2"
              small
              :color="Role.getRoleColor(item.classRole)"
              text-color="white"
            >
              {{ Role.getChinesRole(item.classRole) }}
            </v-chip>
          </template>
          <template v-slot:item.status="{ item }">
            <v-chip
              class="ma-2"
              small
              :color="Role.getStatusColor(item.status)"
              text-color="white"
            >
              {{ Role.getStatus(item.status) }}
            </v-chip>
          </template>
          <template v-slot:item.actions="{ item }">
            <v-tooltip left>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  icon
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="showData(item)"
                >
                  <v-icon>mdi-alert-circle</v-icon>
                </v-btn>

              </template>
              <span>查看数据</span>
            </v-tooltip>

            <v-tooltip left>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  icon
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="edit(item)"
                >
                  <v-icon>mdi-cog</v-icon>
                </v-btn>

              </template>
              <span>编辑</span>
            </v-tooltip>
          </template>

          <template v-slot:no-data>
            <v-btn color="primary" @click="getUserList">重新加载</v-btn>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <!-- 页码 -->
    <v-row justify="center">
      <v-pagination
        v-model="page"
        :length="length"
        @input="pageChange"
      />
    </v-row>
    <!-- 修改学生角色 -->
    <v-bottom-sheet v-model="editSheet">
      <v-toolbar dark color="primary">
        <v-btn icon dark @click="editSheet = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
        <v-toolbar-title>角色修改</v-toolbar-title>
        <v-spacer />

      </v-toolbar>
      <v-sheet height="500px">
        <v-row justify="center">
          <v-col cols="10">
            <h3> 修改 {{ editItem.username }} 的班级内角色：当前班级内角色： {{ Role.getChinesRole(editItem.classRole) }}</h3>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="10">
            <v-select
              v-model="editRole"
              :items="roleItem"
              label="角色选择"
              item-text="text"
              item-value="value"
            />
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-btn depressed color="primary" @click="postEditRole">
            确认
          </v-btn>
        </v-row>
      </v-sheet>
    </v-bottom-sheet>
    <!-- 导入新学生 -->
    <v-dialog
      v-model="dialog"
      width="1000"
    >
      <v-card outlined>
        <UserList @success="showResult" />
      </v-card>
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
</template>

<script>
import Role from '@/utils/auth.vue'
import UserList from '@/components/list/user-list.vue'
export default {
  name: 'UserTable',
  components: {
    UserList
  },
  data() {
    return {
      Role,
      id: 0,
      userList: [],
      page: 1,
      size: 20,
      length: 1,
      totalCount: 0,
      searchKey: {
        name: '',
        userId: '',
        role: ''
      },
      headers: [
        {
          text: '学号',
          sortable: false,
          value: 'userId'
        },
        { text: '姓名', sortable: false, value: 'username' },
        { text: '性别', sortable: false, value: 'sex' },
        { text: '年级', sortable: false, value: 'grade' },
        { text: '专业', sortable: false, value: 'major' },
        { text: '班级角色', sortable: false, value: 'classRole' },
        { text: '状态', sortable: false, value: 'status' },
        { text: '操作', value: 'actions', sortable: false }
      ],
      editSheet: false,
      editItem: {},
      editRole: '',
      roleItem: [
        { text: '教师', value: 'ROLE_TEACHER' },
        { text: '助教', value: 'ROLE_ASSISTANT' },
        { text: '学生', value: 'ROLE_STUDENT' }
      ],
      message: '',
      showMessage: false,
      dialog: false
    }
  },
  created() {
    this.id = this.$route.params.id
    this.getUserList()
  },
  methods: {
    showResult(value) {
      // TODO 细化提示信息
      if (value.status === 200) {
        this.message = '导入成功！'
        this.showMessage = true
        this.getUserList()
        this.dialog = false
      } else {
        this.message = value.message
        this.showMessage = true
      }
    },
    searchUser() {
      if ((this.searchKey.name === '' || this.searchKey.name === null) &&
           (this.searchKey.userId === '' || this.searchKey.userId === null) &&
           (this.searchKey.role === '' || this.searchKey.role === null)) {
        this.getUserList()
        return
      }
      this.httpGet(`/teacher/user/classList?page=${this.page}&limit=${this.size}&classId=${this.id}&name=${this.searchKey.name}&nameId=${this.searchKey.userId}&role=${this.searchKey.role}`, (json) => {
        if (json.status === 200) {
          this.userList = json.data.list
          this.length = json.data.totalPage
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    getUserList() {
      this.httpGet(`/teacher/user/classList?classId=${this.id}&page=${this.page}&limit=${this.size}`, (json) => {
        if (json.status === 200) {
          this.userList = json.data.list
          this.length = json.data.totalPage
          this.totalCount = json.data.totalCount - 1
        }
      })
    },
    getSex(value) {
      if (value === 0) {
        return '男'
      } else {
        return '女'
      }
    },
    pageChange(value) {
      this.page = value
      this.getUserList()
    },
    edit(item) {
      this.editItem = item
      this.editSheet = true
    },
    postEditRole() {
      const user = {
        userId: this.editItem.userId,
        role: this.editRole,
        courseNumber: this.id
      }
      this.httpPost('/teacher/user/update/role', user, (json) => {
        if (json.status === 200) {
          this.message = '修改成功'
          this.showMessage = true
          this.editSheet = false
          this.getUserList()
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    showData(value) {
      this.message = '开发中，预计下个版本上线！'
      this.showMessage = true
    }
  }
}
</script>

<style>

</style>
