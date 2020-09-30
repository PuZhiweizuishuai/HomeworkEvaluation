<script>
function isComment(value) {
  if (value.type === 7 || value.type === 100) {
    return true
  }
  return false
}

export default {
  isComment
}
</script>
