<template>
  <v-container>
    <v-row v-for="item in articleList" :key="item.id">
      <v-col>
        <Card :article="item" />
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
    type: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      pageMode: 1,
      page: 1,
      size: 20,
      tagId: -1,
      typeCode: this.type,
      totalCount: 0,
      length: 1,
      articleList: []
    }
  },
  created() {
    if (this.$store.state.tagsLength === 0) {
      this.getTagList()
    }
    this.getList()
  },
  methods: {
    getList() {
      this.httpGet(`/article/list?page=${this.page}&limit=${this.size}&tagId=${this.tagId}&type=${this.typeCode}`, (json) => {
        this.articleList = json.data.list
        this.totalCount = json.data.totalCount
        this.length = json.data.totalPage
        this.page = json.data.page
        this.$route.query.page = this.page
        this.$vuetify.goTo(0)
      })
    },
    setType(type) {
      this.typeCode = type
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
      this.getList()
    }
  }

}
</script>

<style>

</style>
