<script>
function isComment(value) {
  if (value.type === 7 || value.type === 100 || value.type === 104) {
    return true
  }
  return false
}

function isCourseArticle(value) {
  if (value.type === 0 || value.type === 1 || value.type === 2) {
    return true
  }
  return false
}

export default {
  isComment,
  isCourseArticle
}
</script>
