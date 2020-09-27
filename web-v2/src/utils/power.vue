<script>

function getTeacherPower(value) {
  if (value.role.role === 'ROLE_ADMIN' || value.role.role === 'ROLE_TEACHER') {
    return true
  }
  return false
}

export default {
  getTeacherPower
}
</script>
