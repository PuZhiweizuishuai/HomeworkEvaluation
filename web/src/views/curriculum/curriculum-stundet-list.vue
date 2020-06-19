<template>
  <div>
    <el-row>
      <el-form :inline="true" :model="selectKey">
        &nbsp;&nbsp;
        <el-form-item label="姓名">
          <el-input v-model="selectKey.username" placeholder="姓名" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="selectKey.userId" placeholder="学号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="selectUser">查询</el-button>
        </el-form-item>
      </el-form>
    </el-row>

    <el-row>
      &nbsp;&nbsp;<el-button type="primary" @click="()=>{importUserVisible = true, getNoJoinUserList()}">导入学生</el-button>
    </el-row>
    <br>
    <div>
      <el-table
        :data="userListData"
        style="width: 100%"
      >
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="系统角色">
                <span>{{ props.row.role.role }}</span>
              </el-form-item>
              <el-form-item label="自我介绍">
                <span>{{ props.row.userIntro }}</span>
              </el-form-item>
              <el-form-item label="加入时间">
                <span>{{ props.row.createTime }}</span>
              </el-form-item>
              <el-form-item label="上次登陆时间">
                <span>{{ props.row.latestLoginTime }}</span>
              </el-form-item>
              <el-form-item label="上次登陆IP">
                <span>{{ props.row.latestLoginIp }}</span>
              </el-form-item>
              <el-form-item label="头像">
                <span>{{ props.row.userAvatarUrl }}</span>
              </el-form-item>
              <el-form-item label="角色授予人">
                <span>{{ props.row.role.operator }}</span>
              </el-form-item>
              <el-form-item label="年级">
                <span>{{ props.row.grade }}</span>
              </el-form-item>
              <el-form-item label="学校">
                <span>{{ props.row.school }}</span>
              </el-form-item>
              <el-form-item label="专业">
                <span>{{ props.row.major }}</span>
              </el-form-item>
              <el-form-item label="发帖">
                <span>{{ props.row.articleCount }}</span>
              </el-form-item>
              <el-form-item label="回帖">
                <span>{{ props.row.commentCount }}</span>
              </el-form-item>
              <el-form-item label="获赞">
                <span>{{ props.row.likeCount }}</span>
              </el-form-item>
              <el-form-item label="关注">
                <span>{{ props.row.followerCount }}</span>
              </el-form-item>
              <el-form-item label="粉丝">
                <span>{{ props.row.fansCount }}</span>
              </el-form-item>
              <el-form-item label="选课">
                <span>{{ props.row.curriculumCount }}</span>
              </el-form-item>
              <el-form-item v-if="props.row.status !== 0" label="封禁状态">
                <span v-if="props.row.status === 1">
                  禁言开始时间： {{ props.row.startLockTime }} , 禁言时长：{{ props.row.lockTime }}
                </span>
                <span v-if="props.row.status === 2">
                  封号开始时间： {{ props.row.startLockTime }} , 封号时长：{{ props.row.lockTime }}
                </span>

              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column
          label="学号/工号"
          prop="userId"
        />
        <el-table-column
          label="姓名"
          prop="username"
        />
        <el-table-column
          label="性别"
        >
          <template slot-scope="scope">
            <span v-if="scope.row.sex === 0">男</span>
            <span v-if="scope.row.sex === 1">女</span>
          </template>
        </el-table-column>
        <el-table-column
          label="年龄"
          prop="birthday"
        />
        <el-table-column
          label="状态"
        >
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.status === 0 ? 'success' : 'warning'"
              disable-transitions
            >
              <span v-if="scope.row.status === 0">
                正常
              </span>
              <span v-if="scope.row.status === 1">
                被禁言
              </span>
              <span v-if="scope.row.status === 2">
                被封号
              </span>
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="班级角色"
        >
          <template slot-scope="scope">
            <el-tag
              :type="getRoleTypeTag(scope.row.classRole)"
              disable-transitions
            >
              <span v-if="scope.row.classRole === 'ROLE_ADMIN'">
                管理员
              </span>
              <span v-if="scope.row.classRole === 'ROLE_TEACHER'">
                教师
              </span>
              <span v-if="scope.row.classRole === 'ROLE_ASSISTANT'">
                助教
              </span>
              <span v-if="scope.row.classRole === 'ROLE_STUDENT'">
                学生
              </span>
              <span v-if="scope.row.classRole === 'ROLE_USER'">
                用户
              </span>
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="editUser(scope.$index, scope.row)"
            >编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div>
      <el-pagination
        :current-page="currentPage"
        :page-sizes="[15, 20, 30, 40, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-drawer
      title="用户信息控制页面"
      :visible.sync="drawer"
      :with-header="false"
    >
      <h2>&nbsp;&nbsp;用户信息控制页面</h2>

      <br><br><br><br>
      <el-row>
        <el-form :inline="true">
          <el-form-item label="修改班级内角色">
            <el-select v-model="updateRole" placeholder="角色">
              <el-option label="教师" value="ROLE_TEACHER" />
              <el-option label="助教" value="ROLE_ASSISTANT" />
              <el-option label="学生" value="ROLE_STUDENT" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="alterUserRole">修改</el-button>
          </el-form-item>
        </el-form>
      </el-row>

    </el-drawer>

    <el-dialog title="学生列表" :visible.sync="importUserVisible" width="80%">
      <el-form :inline="true" :model="selectKey">
        &nbsp;&nbsp;
        <el-form-item label="姓名">
          <el-input v-model="searchName" placeholder="姓名" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="searchId" placeholder="学号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getNoJoinUserList">查询</el-button>
        </el-form-item>
      </el-form>
      <el-button type="success" @click="uploadUserJoinList">导入选中学生</el-button>
      <el-table
        :data="noJoinUserList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="55"
        />
        <el-table-column property="userId" label="学号" width="150" />
        <el-table-column property="username" label="姓名" width="200" />
        <el-table-column property="sex" label="性别">
          <template slot-scope="scope">
            <span v-if="scope.row.sex === 0">男</span>
            <span v-if="scope.row.sex === 1">女</span>
          </template>
        </el-table-column>
        <el-table-column property="school" label="学校" />
        <el-table-column property="major" label="专业" />
        <el-table-column property="grade" label="年级" />
      </el-table>
      <el-pagination
        :current-page="noJoinUserListPage"
        :page-size="20"
        layout="total, sizes, prev, pager, next, jumper"
        :total="noJoinUserListCount"
        @current-change="importUserPageChange"
      />
    </el-dialog>
    <el-dialog
      title="结果"
      :visible.sync="showResult"
      width="30%"
    >
      <div v-for="(item, index) in resultData" :key="index">
        {{ index }} ： {{ item }}
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="()=>{showResult = false, getUserList() }">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { MessageBox } from 'element-ui'
import { Message } from 'element-ui'
import { Notification } from 'element-ui'
export default {
  name: 'StudentList',
  data() {
    return {
      id: 0,
      userListData: [],
      // 总数
      totalCount: 0,
      pageSize: 20,
      // 初始页码
      currentPage: 1,
      // 查找参数
      selectKey: {
        username: '',
        userId: '',
        role: ''
      },
      // 修改页码显示控制
      drawer: false,
      editUserData: [],
      // 修改角色
      updateRole: '',
      addUserFrom: {
        userId: '',
        username: '',
        sex: 0,
        school: '',
        major: '',
        grade: 2017,
        role: '',
        title: ''
      },
      lockTime: 1,
      importUserVisible: false,
      noJoinUserList: [],
      noJoinUserListPage: 1,
      noJoinUserListCount: 0,
      searchName: '',
      searchId: '',
      importUserList: [],
      showResult: false,
      resultData: []
    }
  },
  created() {
    this.id = this.$route.params.id
    this.getUserList()
    this.addUserFrom.grade = new Date().getFullYear()
  },
  methods: {
    uploadUserJoinList() {
      if (this.importUserList.length === 0) {
        this.$message.info('没有选中任何学生!')
        return
      }
      const data = []
      for (let i = 0; i < this.importUserList.length; i++) {
        const user = {
          courseNumber: this.id,
          userId: this.importUserList[i].userId
        }
        data.push(user)
      }
      fetch(this.SERVER_API_URL + '/teacher/user/join', {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(data)
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.showResult = true
            this.resultData = json.data
          } else {
            this.$message.error(json.message)
          }
        })
        .catch(e => {
          return null
        })
    },
    handleSelectionChange(val) {
      this.importUserList = val
    },
    importUserPageChange(page) {
      this.noJoinUserListPage = page

      this.getNoJoinUserList()
    },
    getNoJoinUserList() {
      fetch(this.SERVER_API_URL + `/teacher/user/list?page=${this.noJoinUserListPage}&limit=20&username=${this.searchName}&userId=${this.searchId}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          this.noJoinUserList = json.list
          this.noJoinUserListCount = json.totalCount
          this.noJoinUserListPage = json.currPage
        })
        .catch(e => {
          return null
        })
    },
    getUserList() {
      fetch(this.SERVER_API_URL + `/teacher/user/classList?page=${this.currentPage}&limit=${this.pageSize}&classId=${this.id}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          this.userListData = json.page.list
          this.totalCount = json.page.totalCount
          this.currentPage = json.page.currPage
        })
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.getUserList()
    },
    handleCurrentChange(size) {
      this.currentPage = size
      this.getUserList()
    },
    selectUser() {
      if (this.selectKey.username === '' && this.selectKey.userId === '') {
        this.$message.info('请输入查询信息后再查找')
        return
      }
      fetch(this.SERVER_API_URL + `/teacher/user/classList?page=${this.currentPage}&limit=${this.pageSize}&classId=${this.id}&name=${this.selectKey.username}&nameId=${this.selectKey.userId}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.userListData = json.page.list
            this.totalCount = json.page.totalCount
            this.currentPage = json.page.currPage
          } else {
            this.$message.error(json.message)
          }
        })
    },
    editUser(index, row) {
      this.drawer = true
      this.editUserData = row
      this.updateRole = row.role.role
    },
    // 修改角色
    alterUserRole() {
      MessageBox.confirm(`此操作将修改用户 ${this.editUserData.userId} 的角色 , 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        this.sendAlterUserRole()
      }).catch(() => {
        Message({
          type: 'info',
          message: '已取消重置'
        })
      })
    },
    sendAlterUserRole() {
      fetch(this.SERVER_API_URL + `/teacher/user/alter/role`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify({
          'userId': this.editUserData.userId,
          'role': this.updateRole,
          'courseNumber': this.id
        })
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            Message({
              type: 'success',
              message: `重置成功`
            })
            this.getUserList()
          } else {
            Notification.error({
              title: '错误',
              message: json.message
            })
          }
        })
    },
    getRoleTypeTag(role) {
      if (role === 'ROLE_ADMIN') {
        return 'danger'
      } else if (role === 'ROLE_TEACHER') {
        return 'warning'
      } else if (role === 'ROLE_ASSISTANT') {
        return 'success'
      } else {
        return ''
      }
    },

    renderTime(date) {
      if (date === '' || date == null) {
        return ''
      }
      const da = new Date(date)
      return da.getFullYear() + '-' + (da.getMonth() + 1) + '-' + da.getDate() + ' ' + da.getHours() + ':' + da.getMinutes() + ':' + da.getSeconds()
    }
  }
}
</script>
