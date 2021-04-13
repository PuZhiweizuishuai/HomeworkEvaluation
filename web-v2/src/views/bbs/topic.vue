<template>
  <v-container>
    <v-card outlined>
      <v-card-title>
        <v-btn
          icon
          @click="back"
        >
          <v-icon>mdi-arrow-left-thick</v-icon>
        </v-btn>
        当前话题 <span v-html="'&nbsp;&nbsp;'" />
        <strong>{{ this.$route.params.topic }}</strong>
        <span v-html="'&nbsp;&nbsp;'" />
        共有 {{ totalCount }} 条讨论！
      </v-card-title>
    </v-card>
    <v-row>
      <v-col>
        <v-tabs v-model="sort">
          <v-tab @click="setTab(0)">相关性</v-tab>
          <v-tab @click="setTab(1)">时间倒序</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-col />
    <v-row v-for="item in searchResult" :key="item.id">
      <v-col>
        <ArticleCard :article="item" :showcard="false" />
      </v-col>
    </v-row>
    <v-col />
    <v-row>
      <v-col>
        <v-pagination
          v-model="page"
          :length="length"
          @input="pageChange"
        />
      </v-col>
    </v-row>
    <v-col />
  </v-container>
</template>

<script>
import ArticleCard from '@/views/bbs/components/card.vue'
export default {
  components: {
    ArticleCard
  },
  data() {
    return {
      searchResult: [],
      page: 1,
      size: 20,
      length: 1,
      totalCount: 0,
      took: 0,
      sort: 0
    }
  },
  created() {
    const sort = parseInt(this.$route.query.sort)
    if (!isNaN(sort)) {
      this.sort = sort
    }
    const page = parseInt(this.$route.query.page)
    if (!isNaN(page)) {
      if (page > 0) {
        this.page = page
      }
    }
    this.getTagList()
    this.getTopicList()
  },
  methods: {
    back() {
      this.$router.go(-1)
    },
    getTopicList() {
      this.httpGet(`/search/topic?key=${this.$route.params.topic}&page=${this.page}&size=${this.size}&sort=${this.sort}`, (json) => {
        if (json.data != null) {
          this.searchResult = json.data.list
          this.took = json.data.took
          this.totalCount = json.data.totalCount
          this.length = json.data.totalPage
        }
      })
    },
    pageChange(value) {
      this.page = value
      this.$router.push({
        path: this.$router.path,
        query: { page: this.page, sort: this.sort }
      })
      this.getTopicList()
    },
    getTagList() {
      this.httpGet('/article/tags/list', (json) => {
        //
        this.$store.commit('setTagMap', json.data)
      })
    },
    setTab(value) {
      this.sort = value
      this.page = 1
      this.$router.push({
        path: this.$router.path,
        query: { page: this.page, sort: this.sort }
      })
      this.getTopicList()
    }
  }
}
</script>

<style>

</style>
