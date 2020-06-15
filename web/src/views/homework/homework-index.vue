<template>
  <div>
    <a-row>
      <a-col :span="20">
        <a-page-header
          title="测验与作业"
        />
      </a-col>
      <a-col :span="4" style="margin-top: 14px;">
        <a-button type="primary">
          <router-link :to="`/curriculum/learn/${id}/add-homework`">
            添加作业
          </router-link>
        </a-button>
      </a-col>
    </a-row>
    <a-divider />

    <HomeworkInfo />
  </div>
</template>

<script>
import HomeworkInfo from '@/views/homework/homework-info.vue'

export default {
  name: 'HomeworkIndex',
  components: { HomeworkInfo },
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
