<template>
  <div>
    <a-collapse class="homework-info">
      <a-collapse-panel key="1" header="This is panel header 1">
        <p>123456</p>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>

<script>
export default {
  name: 'HomeworkInfo',
  data() {
    return {
      id: 0
    }
  },
  created() {
    this.id = this.$route.params.id
  }
}
</script>

<style scoped>
.homework-info {
  widows: 70%;
}
</style>
