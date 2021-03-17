<template>
  <v-container>
    <v-row v-for="item in thinkList" :key="item.id">
      <v-col>

        <ThinkCard :think="item" />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-pagination
        v-model="pageMode"
        :length="length"
        @input="pageChange"
      />
    </v-row>
  </v-container>
</template>

<script>
import ThinkCard from '@/components/think/show-think-card.vue'

export default {
  components: {
    ThinkCard
  },
  data() {
    return {
      pageMode: 1,
      thinkList: [],
      page: 1,
      size: 20,
      length: 1,
      totalCount: 0
    }
  },
  created() {
    this.getThinkList()
  },
  methods: {
    getThinkList() {
      this.httpGet(`/article/thinks/list?page=${this.page}&limit=${this.size}`, (json) => {
        this.thinkList = json.data.list
        this.totalCount = json.data.totalCount
        this.length = json.data.totalPage
        this.page = json.data.page
        this.$route.query.page = this.page
      })
    },
    pageChange(value) {
      this.page = value
      this.pageMode = value
      this.getThinkList()
    }
  }
}
</script>

<style>

</style>
