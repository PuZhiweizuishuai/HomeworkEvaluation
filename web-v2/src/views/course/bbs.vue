<template>
  <v-container fill-height fluid>
    <v-row justify="center">
      <v-col cols="11">
        <h3 style="float:left;">讨论区：</h3> 欢迎大家来到讨论区！本讨论区供各位同学就课程问题进行交流学习。
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <v-row justify="center">
      <v-col cols="11">
        <v-row justify="end">
          <v-btn depressed color="primary" @click="goToArticle">发起主题</v-btn>
        </v-row>
      </v-col>
    </v-row>
    <v-row v-for="item in articleList" :key="item.id" justify="center">
      <v-col cols="11">
        <ArticleCard />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import ArticleCard from '@/components/article/article-card.vue'

export default {
  name: 'BBS',
  components: {
    ArticleCard
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
      articleList: [],
      page: 1,
      length: 0,
      size: 20,
      sort: 0,
      key: ''
    }
  },
  created() {
    this.$vuetify.goTo(0)
    document.title = '讨论区 - ' + this.course.curriculumName
    this.getArticle()
  },
  methods: {
    getArticle() {
      this.httpGet(`/article/list/${this.$route.params.id}?page=${this.page}&limit=${this.size}&sort=${this.sort}&key=${this.key}`, (json) => {
        if (json.status === 200) {
          this.articleList = json.data.list
          this.length = json.data.totalPage
        } else {
          //
        }
      })
    },
    goToArticle() {
      this.$router.push(`/course/learn/${this.$route.params.id}/bbs/article`)
    }
  }
}
</script>

<style>

</style>
