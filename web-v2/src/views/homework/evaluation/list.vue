<template>
  <v-container>
    <v-row>
      <v-col>
        <h3>
          <v-btn
            icon
            @click="back"
          >
            <v-icon>mdi-arrow-left-thick</v-icon>
          </v-btn>
          当前提交的作业列表
        </h3>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="11">
        <v-row justify="end">
          <v-btn color="primary">查看我的作业</v-btn>
        </v-row>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-tabs>
          <v-tab>提交时间</v-tab>

          <v-tab>最多评价</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-row v-for="item in userSubmitList" :key="item.id" justify="center">
      <v-col cols="12">
        <Card :info="item" />
      </v-col>
    </v-row>
    <v-row v-if="count == 0" justify="center">
      <h3>暂无提交</h3>
    </v-row>
  </v-container>
</template>

<script>
import Card from '@/views/homework/evaluation/card.vue'

export default {
  components: {
    Card
  },
  data() {
    return {
      userSubmitList: [],
      page: 1,
      size: 20,
      length: 0,
      count: 0
    }
  },
  created() {
    console.log('进入提交的作业列表')
    this.getSubmit()
  },
  methods: {
    back() {
      this.$router.push(`/course/learn/${this.$route.params.id}/evaluation`)
    },
    getSubmit() {
      this.httpGet(`/evaluation/list/${this.$route.params.homeworkId}?page=${this.page}&limit=${this.size}`, (json) => {
        if (json.status === 200) {
          //
          this.userSubmitList = json.data.list
          this.length = json.data.totalPage
          this.count = json.data.totalCount
        } else {
          //
        }
      })
    }
  }
}
</script>

<style>

</style>
