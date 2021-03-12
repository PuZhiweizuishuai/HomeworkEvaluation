<template>
  <v-container>
    <v-row v-for="item in articleList" :key="item.id">
      <v-col>
        <Card :article="item" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Card from '@/views/bbs/components/card.vue'
export default {
  components: {
    Card
  },
  data() {
    return {
      page: 1,
      size: 20,
      tagId: -1,
      totalCount: 0,
      length: 1,
      articleList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.httpGet(`/article/list?page=${this.page}&limit=${this.size}&tagId=${this.tagId}`, (json) => {
        this.articleList = json.data.list
        this.totalCount = json.data.totalCount
        this.length = json.data.totalPage
        this.page = json.data.page
        this.$route.query.page = this.page
      })
    }
  }

}
</script>

<style>

</style>
