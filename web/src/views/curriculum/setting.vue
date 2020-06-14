<template>
  <div>
    <a-tabs default-active-key="6">
      <a-tab-pane key="6" tab="课程信息">
        <Edit />
      </a-tab-pane>
      <a-tab-pane key="1" tab="公告列表">
        Content of Tab Pane 1
      </a-tab-pane>

      <a-tab-pane key="2" tab="学生列表">
        <StudentList />
      </a-tab-pane>

      <a-tab-pane key="3" tab="作业测验考试列表">
        <HomeworkList />
      </a-tab-pane>
      <a-tab-pane key="4" tab="题目列表">
        Content of Tab Pane 3
      </a-tab-pane>
      <a-tab-pane key="5" tab="邀请码">
        Content of Tab Pane 3
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
// F:\Spring\Homework\web\src\views\curriculum\edit-curriculum.vue
import Edit from '@/views/curriculum/edit-curriculum.vue'
import StudentList from '@/views/curriculum/curriculum-stundet-list.vue'
import HomeworkList from '@/views/homework/homework-list.vue'
export default {
  name: 'Setting',
  components: { Edit, StudentList, HomeworkList },
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
