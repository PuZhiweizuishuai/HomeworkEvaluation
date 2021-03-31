<template>
  <v-container>
    <v-row v-if="showtab">
      <v-col>
        <v-tabs v-model="checkTab">
          <v-tab @click="innerSetSort(0)">推荐</v-tab>
          <v-tab @click="innerSetSort(1)">最新回复</v-tab>
          <v-tab @click="innerSetSort(2)">精品</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-row v-for="item in articleList" :key="item.id">
      <v-col>
        <Card :article="item" :lastpage="pageMode" />
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
import Card from '@/views/bbs/components/card.vue'
export default {
  components: {
    Card
  },
  props: {
    showtab: {
      type: Boolean,
      default: false
    },
    type: {
      type: Number,
      default: 0
    },
    tagid: {
      type: String,
      default: '0'
    }
  },
  data() {
    return {
      pageMode: 1,
      page: 1,
      size: 20,
      sort: 0,
      tagId: this.tagid,
      typeCode: this.type,
      totalCount: 0,
      length: 1,
      articleList: [],
      preplist: [],
      lastType: 0,
      checkTab: 0
    }
  },
  created() {
    if (this.type === -1) {
      this.sort = 2
      this.typeCode = 0
    } else {
      this.sort = 0
      this.typeCode = this.type
    }
    if (this.$store.state.tagsLength === 0) {
      this.getTagList()
    }
    const p = parseInt(this.$route.query.page)
    if (!isNaN(p)) {
      this.pageMode = p
      this.page = p
    }
    const s = parseInt(this.$route.query.sort)
    if (!isNaN(s)) {
      if (s === 2) {
        this.sort = s
      }
      this.checkTab = s
    }
    this.getList()
  },
  methods: {
    getList() {
      this.httpGet(`/article/list?page=${this.page}&limit=${this.size}&tagId=${this.tagId}&type=${this.typeCode}&sort=${this.sort}`, (json) => {
        this.articleList = json.data.list
        this.totalCount = json.data.totalCount
        this.length = json.data.totalPage
        this.page = json.data.page
        this.$vuetify.goTo(0)
      })
    },
    innerSetSort(value) {
      this.sort = value
      this.typeCode = 0
      this.page = 1
      this.$router.push({
        path: this.$router.path,
        query: { page: this.page, sort: this.sort }
      })
      this.getList()
    },
    setSort(value) {
      this.sort = value
      this.typeCode = 0
      this.page = 1
      this.getList()
    },
    setType(type) {
      this.typeCode = type
      this.sort = 0
      this.page = 1
      this.getList()
    },
    getTagList() {
      this.httpGet('/article/tags/list', (json) => {
        //
        this.$store.commit('setTagMap', json.data)
      })
    },
    pageChange(value) {
      this.pageMode = value
      this.page = value
      let type = this.typeCode
      if (this.sort === 2) {
        type = -1
      }
      this.$router.push({
        path: this.$router.path,
        query: { page: this.page, type: type }
      })
      this.getList()
    }
  }

}
</script>

<style>

</style>
