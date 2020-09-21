<script>

const role = {
  ROLE_ADMIN: '管理员',
  ROLE_TEACHER: '教师',
  ROLE_ASSISTANT: '助教',
  ROLE_STUDENT: '学生',
  ROLE_USER: '用户'
}

const roleColor = {
  ROLE_ADMIN: 'red',
  ROLE_TEACHER: 'primary',
  ROLE_ASSISTANT: 'orange',
  ROLE_STUDENT: 'green',
  ROLE_USER: ''
}

const status = [
  '正常', '禁言中', '账号被锁定，无法登陆'
]

const statusColor = [
  'orange', '', 'red'
]

function getChinesRole(value) {
  return role[value]
}

function getRoleColor(value) {
  return roleColor[value]
}

function getStatus(code) {
  return status[code]
}

function getStatusColor(code) {
  return statusColor[code]
}

export default {
  getChinesRole,
  getRoleColor,
  getStatus,
  getStatusColor
}
</script>

