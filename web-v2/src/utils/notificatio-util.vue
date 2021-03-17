<script>
function isComment(value) {
  if (value.type === 7 || value.type === 100 || value.type === 104) {
    return true
  }
  return false
}

export default {
  isComment
}
</script>
