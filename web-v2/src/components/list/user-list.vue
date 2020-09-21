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
    </v-row>
    <v-row>
      <v-col>
        <v-btn depressed color="success" @click="importStudent">
          导入已选择学生
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-data-table
          v-model="selected"
          :headers="headers"
          :items="userList"
          hide-default-footer
          :items-per-page="size"
          :page.sync="page"
          :single-select="false"
          show-select
          item-key="userId"
        >

          <template v-slot:item.userId="{ item }">
            <a :href="`/user/${item.userId}`" target="_blank"> {{ item.userId }} </a>
          </template>
          <template v-slot:item.sex="{ item }">
            <span v-text="getSex(item.sex)" />
          </template>
          <template v-slot:item.role.role="{ item }">
            <v-chip
              class="ma-2"
              small
              :color="Role.getRoleColor(item.role.role)"
              text-color="white"
            >
              {{ Role.getChinesRole(item.role.role) }}
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
  </v-container>
</template>

<script>
import Role from '@/utils/auth.vue'
export default {
  name: 'UserList',
  data() {
    return {
      id: 0,
      selected: [],
      searchKey: {
        name: '',
        userId: '',
        role: ''
      },
      roleItem: [
        { text: '管理员', value: 'ROLE_ADMIN' },
        { text: '教师', value: 'ROLE_TEACHER' },
        { text: '助教', value: 'ROLE_ASSISTANT' },
        { text: '学生', value: 'ROLE_STUDENT' }
      ],
      Role,
      userList: [],
      page: 1,
      size: 20,
      length: 1,
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
        { text: '角色', sortable: false, value: 'role.role' },
        { text: '状态', sortable: false, value: 'status' }

      ]
    }
  },
  created() {
    this.id = this.$route.params.id
    this.getUserList()
  },
  methods: {
    importStudent() {
      if (this.selected.length === 0) {
        return
      }
      const data = []
      for (let i = 0; i < this.selected.length; i++) {
        const user = {
          courseNumber: this.id,
          userId: this.selected[i].userId
        }
        data.push(user)
      }
      this.httpPost('/teacher/user/join', data, (json) => {
        if (json.status === 200) {
          // TODO 处理显示结果
        } else {
          //
        }
      })
    },
    searchUser() {
      if ((this.searchKey.name === '' || this.searchKey.name === null) &&
           (this.searchKey.userId === '' || this.searchKey.userId === null) &&
           (this.searchKey.role === '' || this.searchKey.role === null)) {
        this.getUserList()
        return
      }
      this.httpGet(`/user/list?page=${this.page}&limit=${this.size}&username=${this.searchKey.name}&userId=${this.searchKey.userId}&role=${this.searchKey.role}`, (json) => {
        if (json.status === 200) {
          this.userList = json.data.list
          this.length = json.data.totalPage
        }
      })
    },
    getUserList() {
      this.httpGet(`/user/list?page=${this.page}&limit=${this.size}`, (json) => {
        if (json.status === 200) {
          this.userList = json.data.list
          this.length = json.data.totalPage
        }
      })
    },
    pageChange(value) {
      this.page = value
      this.getUserList()
    },
    getSex(value) {
      if (value === 0) {
        return '男'
      } else {
        return '女'
      }
    }
  }
}
</script>

<style>

</style>
