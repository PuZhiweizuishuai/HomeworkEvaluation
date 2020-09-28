<template>
  <v-container fill-height>
    <v-row>
      <v-col cols="12">
        <h3> 测验与作业 </h3>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <v-row v-for="(item, index) in homeworkList" :key="item.id">
      <v-col cols="12">
        <Card v-if="index == 0" :info="item" :show="0" />
        <Card v-else :info="item" />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-pagination
        v-if="count > size"
        v-model="page"
        :length="length"
        @input="pageChange"
      />
    </v-row>
  </v-container>
</template>

<script>
import Card from '@/components/homework/info-card.vue'

/**
 * 显示测验与作业列表
 */
export default {
  name: 'Exam',
  components: {
    Card
  },
  props: {
    course: {
      type: Object,
      default: null
    },
    role: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      homeworkList: [],
      page: 1,
      size: 20,
      length: 1,
      id: 0,
      count: 0
    }
  },
  created() {
    this.$vuetify.goTo(0)
    this.id = this.$route.params.id
    this.getHomeworkList()
    document.title = '测验 - ' + this.course.curriculumName
  },
  methods: {
    getHomeworkList() {
      this.httpGet(`/homework/list/${this.id}?page=${this.page}&limit=${this.size}`, (json) => {
        if (json.status === 200) {
          this.homeworkList = json.data.list
          this.length = json.data.totalPage
          this.count = json.data.totalCount
        } else {
          this.$router.push(`/course/info/${this.id}`)
        }
      })
    },
    pageChange(value) {
      this.page = value
      this.getHomeworkList()
    }
  }
}
</script>

<style>

</style>
