<template>
  <v-container>
    <v-row>
      <v-col>
        <h3>作业互评:</h3>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <v-row v-for="item in homeworkList" :key="item.id">
      <v-col cols="12">
        <Card :info="item" />
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
export default {
  name: 'Evaluation',
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
    this.id = this.$route.params.id
    this.getEvaluation()
  },
  methods: {
    getEvaluation() {
      this.httpGet(`/evaluation/homework/${this.id}?page=${this.page}&limit=${this.size}`, (json) => {
        if (json.status === 200) {
          this.homeworkList = json.data.list
          this.length = json.data.totalPage
          this.count = json.data.totalCount
        } else {
          this.$router.push(`/course/info/${this.id}`)
        }
      })
    }
  }
}
</script>

<style>

</style>
