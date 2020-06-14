<template>
  <div>
    <el-row>
      <a-page-header
        style="border: 1px solid rgb(235, 237, 240)"
        title="用户列表"
      />
    </el-row>
    <br>
    <el-row>
      <el-form :inline="true" :model="selectKey">
        &nbsp;&nbsp;
        <el-form-item label="姓名">
          <el-input v-model="selectKey.username" placeholder="姓名" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="selectKey.userId" placeholder="学号" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="selectKey.role" placeholder="角色">
            <el-option label="管理员" value="ROLE_ADMIN" />
            <el-option label="教师" value="ROLE_TEACHER" />
            <el-option label="助教" value="ROLE_ASSISTANT" />
            <el-option label="学生" value="ROLE_STUDENT" />
            <el-option label="用户" value="ROLE_USER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="selectUser">查询</el-button>
        </el-form-item>
      </el-form>
    </el-row>

    <el-row>
      &nbsp;&nbsp;<el-button type="success" @click="addUserDialogFormVisible = true">添加用户</el-button>
      &nbsp;&nbsp;<el-button type="primary" @click="goToImportUserPage">导入用户</el-button>
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
          label="角色"
        >
          <template slot-scope="scope">
            <el-tag
              :type="getRoleTypeTag(scope.row.role.role)"
              disable-transitions
            >
              <span v-if="scope.row.role.role === 'ROLE_ADMIN'">
                管理员
              </span>
              <span v-if="scope.row.role.role === 'ROLE_TEACHER'">
                教师
              </span>
              <span v-if="scope.row.role.role === 'ROLE_ASSISTANT'">
                助教
              </span>
              <span v-if="scope.row.role.role === 'ROLE_STUDENT'">
                学生
              </span>
              <span v-if="scope.row.role.role === 'ROLE_USER'">
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
      <br>
      &nbsp;&nbsp;<el-button type="warning" @click="restPassword">重置密码</el-button>
      <br><br><br><br>
      <el-row>
        <el-form :inline="true">
          <el-form-item label="修改角色">
            <el-select v-model="updateRole" placeholder="角色">
              <el-option label="管理员" value="ROLE_ADMIN" />
              <el-option label="教师" value="ROLE_TEACHER" />
              <!-- <el-option label="助教" value="ROLE_ASSISTANT" /> -->
              <el-option label="学生" value="ROLE_STUDENT" />
              <el-option label="用户" value="ROLE_USER" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="alterUserRole">修改</el-button>
          </el-form-item>
        </el-form>
      </el-row>
      <el-row>
        <el-form :inline="true">
          <el-form-item label="禁言账号(单位分钟)">
            <el-input v-model="lockTime" type="number" autocomplete="off" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="lockUser(1)">禁言</el-button>
          </el-form-item>
        </el-form>
        <el-form :inline="true">
          <el-form-item label="封禁账号(单位分钟)">
            <el-input v-model="lockTime" type="number" autocomplete="off" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="lockUser(2)">封禁</el-button>
          </el-form-item>
        </el-form>
        <el-row v-if="editUserData.status !== 0">
          <el-button type="success" @click="lockUser(0)">提前结束账号禁言或封禁状态</el-button>
          <span><br><br>禁言结束时间为：<span v-text="renderTime(editUserData.startLockTime + editUserData.lockTime)" /></span>
        </el-row>
      </el-row>
    </el-drawer>
    <el-dialog title="添加新用户" :visible.sync="addUserDialogFormVisible">
      <el-form ref="addUserFrom" :model="addUserFrom" :rules="addUserRules">
        <el-form-item label="学号/工号" prop="userId">
          <el-input v-model="addUserFrom.userId" autocomplete="off" />
        </el-form-item>
        <el-form-item label="姓名" prop="username">
          <el-input v-model="addUserFrom.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="addUserFrom.sex" placeholder="性别">
            <el-option label="男" value="0" />
            <el-option label="女" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="学校">
          <el-input v-model="addUserFrom.school" autocomplete="off" />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="addUserFrom.grade" type="number" autocomplete="off" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="addUserFrom.major" autocomplete="off" />
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="addUserFrom.title" autocomplete="off" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="addUserFrom.role" placeholder="角色">
            <el-option label="管理员" value="ROLE_ADMIN" />
            <el-option label="教师" value="ROLE_TEACHER" />
            <!-- <el-option label="助教" value="ROLE_ASSISTANT" /> -->
            <el-option label="学生" value="ROLE_STUDENT" />
            <el-option label="用户" value="ROLE_USER" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addUserDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="addUser">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="提示"
      :visible.sync="addUserdialogVisible"
      width="50%"
    >
      <span>插入结果：</span>
      <br>
      <li v-for="item in addUserResultList" :key="item.userId">
        账号：{{ item.userId }}, 密码： {{ item.password }}, 结果： {{ item.msg }}
      </li>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addUserdialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import { MessageBox } from 'element-ui'
import { Message } from 'element-ui'
import { Notification } from 'element-ui'
export default {
  name: 'UserListTable',
  data() {
    return {
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
      // 控制添加用户显示窗口
      addUserDialogFormVisible: false,
      // 添加用户校验规则
      addUserRules: {
        userId: [
          { required: true, message: '请输入用户学号或工号', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请输入角色', trigger: 'blur' }
        ]
      },
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
      addUserdialogVisible: false,
      addUserResultList: [],
      lockTime: 1
    }
  },
  created() {
    this.getUserList()
    this.addUserFrom.grade = new Date().getFullYear()
  },
  methods: {
    getUserList() {
      fetch(this.SERVER_API_URL + `/teacher/user/list?page=${this.currentPage}&limit=${this.pageSize}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          this.userListData = json.list
          this.totalCount = json.totalCount
          this.currentPage = json.currPage
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
        Message.info('请输入查询信息后再查找')
        this.getUserList()
        return
      }
      fetch(this.SERVER_API_URL + `/teacher/user/list?page=${this.currentPage}&limit=${this.pageSize}&username=${this.selectKey.username}&userId=${this.selectKey.userId}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          this.userListData = json.list
          this.totalCount = json.totalCount
          this.currentPage = json.currPage
        })
    },
    editUser(index, row) {
      this.drawer = true
      this.editUserData = row
      this.updateRole = row.role.role
    },
    restPassword() {
      MessageBox.confirm(`此操作将重置用户 ${this.editUserData.userId} 的密码 , 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.sendrestPassword()
      }).catch(() => {
        Message({
          type: 'info',
          message: '已取消重置'
        })
      })
    },
    sendrestPassword() {
      fetch(this.SERVER_API_URL + `/admin/user/rest/password`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify({
          'userId': this.editUserData.userId
        })
      }).then(response => response.json())
        .then(json => {
          if (json.data.status === 200) {
            this.$alert(`请将重置后的密码：${json.data.password} 发送给用户： ${json.data.userId}，点击确定后该消息自动删除`, '修改成功', {
              confirmButtonText: '确定',
              callback: action => {
                Message({
                  type: 'success',
                  message: `重置成功`
                })
              }
            })
          } else {
            Notification.error({
              title: '错误',
              message: '请检查用户ID'
            })
          }
        })
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
      fetch(this.SERVER_API_URL + `/admin/user/alter/role`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify({
          'userId': this.editUserData.userId,
          'role': this.updateRole
        })
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.getUserList()
            Message({
              type: 'success',
              message: `重置成功`
            })
          } else {
            Notification.error({
              title: '错误',
              message: json.message
            })
          }
        })
    },
    addUser() {
      const userList = []
      userList.push(this.addUserFrom)
      fetch(this.SERVER_API_URL + `/admin/user/add`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(userList)
      }).then(response => response.json())
        .then(json => {
          console.log(json)
          this.addUserResultList = json.data
          this.addUserdialogVisible = true
          this.addUserDialogFormVisible = false
          this.getUserList()
        })
    },
    goToImportUserPage() {
      this.$router.replace('/admin/user/import')
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
    // 锁定账号
    lockUser(type) {
      if (this.lockTime <= 0 && type !== 0) {
        MessageBox({
          message: '时间必须大于 0',
          type: 'warning'
        })
        return
      }
      if (type === 1 || type === 2 || type === 0) {
        fetch(this.SERVER_API_URL + `/admin/user/alter/status`, {
          headers: {
            'Content-Type': 'application/json; charset=UTF-8',
            'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
          },
          method: 'POST',
          credentials: 'include',
          body: JSON.stringify({
            userId: this.editUserData.userId,
            status: type,
            time: this.lockTime
          })
        }).then(response => response.json())
          .then(json => {
            if (json.status === 200) {
              Message({
                message: '处理成功',
                type: 'success'
              })
              this.getUserList()
            } else {
              Message({
                message: json.message,
                type: 'warning'
              })
            }
          })
      } else {
        Message({
          message: '请求格式错误',
          type: 'warning'
        })
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
