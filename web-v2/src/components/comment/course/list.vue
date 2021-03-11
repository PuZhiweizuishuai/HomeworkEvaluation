<template>
  <v-container>
    <v-row>
      <v-col>
        <h3 style="color: #ff9800">共 {{ count }} 人评价： {{ score }} 分</h3>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="9" style="padding-bottom: 0px;">
        <v-tabs>
          <v-tab @click="setType(0)">热门</v-tab>
          <v-tab @click="setType(1)">时间倒序</v-tab>
          <v-tab @click="setType(2)">顺序</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-row v-for="item in items" :key="item.id">
      <v-col>
        <Card :comment="item" :deletebtn="deletebtn" />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-pagination
        v-if="totalPage != 0"
        v-model="page"
        :length="totalPage"
        @input="pageChange"
      />
    </v-row>
  </v-container>
</template>

<script>
import Card from '@/components/comment/course/course-comment-card.vue'

export default {
  components: {
    Card
  },
  props: {
    score: {
      type: Number,
      default: 0
    },
    deletebtn: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      items: [],
      page: 1,
      size: 20,
      sort: 0,
      count: 0,
      totalPage: 0,
      id: this.$route.params.id
    }
  },
  created() {
    this.getCommentItem()
  },
  methods: {
    getCommentItem() {
      this.httpGet(`/article/course/rating/${this.id}?page=${this.page}&limit=${this.size}&sort=${this.sort}`, (json) => {
        this.page = json.data.currPage
        this.count = json.data.totalCount
        this.totalPage = json.data.totalPage
        this.items = json.data.list
      })
    },
    setType(value) {
      this.sort = value
      this.getCommentItem()
    },
    pageChange(page) {
      this.page = page
      this.getCommentItem()
    }
  }
}
</script>

<style>

</style>
