<template>
  <div>
    显示作业页面
  </div>
</template>

<script>
export default {
  name: 'ShowHomework',
  data() {
    return {
      id: 0,
      hid: 0
    }
  },
  created() {
    this.id = this.$route.params.id
    this.hid = this.$route.params.hid
    console.log(this.id, this.hid)
  }
}
</script>
