<template>
  <div>
    <a-button type="primary">
      <router-link :to="`/curriculum/learn/${id}/add-homework`">
        添加作业
      </router-link>
    </a-button>
  </div>
</template>

<script>
export default {
  name: 'HomeworkList',
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
