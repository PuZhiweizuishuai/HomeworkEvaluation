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
          <v-text-field
            v-model="key"
            flat
            prepend-inner-icon="mdi-magnify"
            label="查找"
            clearable
            @keydown="search"
          />
          <v-btn depressed color="primary" @click="goToArticle">发起主题</v-btn>
        </v-row>
      </v-col>
    </v-row>
    <v-row v-if="showBack" justify="center">
      <v-col cols="11">
        <v-btn
          icon
          @click="back"
        >
          <v-icon>mdi-arrow-left-thick</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="11">
        <v-tabs v-model="checkTab">
          <v-tab @click="setSort(0)">最新发帖</v-tab>
          <v-tab @click="setSort(1)">最新回复</v-tab>
          <v-tab @click="setSort(2)">精品</v-tab>
          <v-tab @click="setSort(3)">消灭零回复</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-row v-for="item in articleList" :key="item.id" justify="center">
      <v-col cols="11">
        <ArticleCard :article="item" :course="course" />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-pagination
        v-model="page"
        :length="length"
        @input="pageChange"
      />
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
      size: 5,
      sort: 0,
      checkTab: 0,
      key: '',
      showBack: false
    }
  },
  created() {
    const p = parseInt(this.$route.query.page)
    const s = parseInt(this.$route.query.sort)
    if (!isNaN(p)) {
      //
      this.page = p
    }
    if (!isNaN(s)) {
      this.checkTab = s
      this.sort = s
    }
    this.$vuetify.goTo(0)
    document.title = '讨论区 - ' + this.course.curriculumName
    this.getArticle()
  },
  methods: {
    getArticle() {
      this.httpGet(`/article/list/course/${this.$route.params.id}?page=${this.page}&limit=${this.size}&sort=${this.sort}&key=${this.key}`, (json) => {
        if (json.status === 200) {
          this.articleList = json.data.list
          this.length = json.data.totalPage
        } else {
          //
        }
      })
    },
    search(e) {
      if (e.key === 'Enter') {
        if (this.key === '' || this.key == null) {
          return
        }
        this.showBack = true
        this.getArticle()
      }
    },
    back() {
      this.key = ''
      this.showBack = false
      this.getArticle()
    },
    goToArticle() {
      this.$router.push(`/course/learn/${this.$route.params.id}/bbs/article`)
    },
    pageChange(value) {
      this.page = value
      this.$router.push({
        path: this.$router.path,
        query: { page: this.page, sort: this.sort }
      })
      this.getArticle()
    },
    setSort(value) {
      this.sort = value
      this.$router.push({
        path: this.$router.path,
        query: { page: 1, sort: this.sort }
      })
      this.getArticle()
    }
  }
}
</script>

<style>

</style>
