<template>
  <v-container>
    <v-row>
      <v-col>
        <v-tabs v-model="tabIndex">
          <v-tab @click="setTab(0)">课程</v-tab>
          <v-tab @click="setTab(1)">讨论贴</v-tab>
          <v-tab @click="setTab(2)">想法</v-tab>
          <v-tab @click="setTab(3)">用户</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-divider />
    <v-col />

    <v-row>
      <v-col>
        <v-btn small color="primary" depressed @click="setSort(0)">
          默认排序
        </v-btn>
        <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />
        <v-btn small color="success" depressed @click="setSort(1)">
          时间倒序
        </v-btn>
        <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />

        <span v-if="sort == 0">默认排序</span><span v-if="sort == 1">时间倒序</span>
        <span v-html="`&nbsp;&nbsp;`" />
        共找到<strong> {{ totalCount }} </strong>与<strong> {{ key }} </strong>相关的结果，用时<strong> {{ took }}</strong> 毫秒!
        当前为第<strong> {{ page }}</strong> 页
      </v-col>
    </v-row>

    <v-col />

    <!-- 课程 -->
    <div v-if="tabIndex == 0">
      <v-row v-if="totalCount != 0" v-resize="onResize">
        <v-col v-for="item in searchResult" :key="item.id" :cols="colsWidth">
          <CourseCard :course="item" />
        </v-col>
      </v-row>
    </div>
    <!-- 帖子 -->
    <div v-if="tabIndex == 1">
      <v-row v-for="item in searchResult" :key="item.id">
        <v-col>
          <ArticleCard :article="item" :showcard="false" />
        </v-col>
      </v-row>
    </div>

    <!-- 想法 -->
    <div v-if="tabIndex == 2">
      <v-row v-for="item in searchResult" :key="item.id">
        <v-col>

          <ThinkCard :think="item" />
        </v-col>
      </v-row>
    </div>
    <!-- 用户列表 -->
    <div v-if="tabIndex == 3">
      <v-row v-for="item in searchResult" :key="item.id">
        <v-col>
          <UserInfoCard :user="item" />
        </v-col>
      </v-row>
    </div>

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
const tableName = [
  'curriculum',
  'article',
  'article',
  'user'
]

import CourseCard from '@/components/course/course-card.vue'
import ArticleCard from '@/views/bbs/components/card.vue'
import ThinkCard from '@/components/think/search-think-card.vue'
import UserInfoCard from '@/components/user/info-card.vue'
export default {
  components: {
    CourseCard,
    ArticleCard,
    ThinkCard,
    UserInfoCard
  },
  data() {
    return {
      searchResult: [],
      tabIndex: 0,
      index: 'curriculum',
      totalCount: 0,
      page: 1,
      size: 20,
      length: 1,
      sort: 0,
      key: '',
      schema: '',
      took: 0,
      windowSize: {
        x: 0,
        y: 0
      },
      colsWidth: 3
    }
  },
  created() {
    this.initData()
    this.getSearchData()
  },
  methods: {
    setKey(key) {
      this.key = key
    },
    initData() {
      const key = this.$route.query.key
      if (key == null || key === undefined || key === '') {
        this.key = ''
      } else {
        this.key = key
      }
      const sort = parseInt(this.$route.query.sort)
      if (!isNaN(sort)) {
        this.sort = sort
      }
      const index = parseInt(this.$route.query.index)
      if (!isNaN(index)) {
        if (index >= 0 && index <= 3) {
          this.tabIndex = index
          this.index = tableName[index]
        }
        if (index === 2) {
          this.schema = 100
        }
      } else {
        this.tabIndex = 0
        this.index = tableName[0]
      }
      const page = parseInt(this.$route.query.page)
      if (!isNaN(page)) {
        if (page > 0) {
          this.page = page
        }
      }
      if (this.$store.state.tagsLength === 0) {
        this.getTagList()
      }
    },
    getSearchData() {
      this.httpGet(`/search/public?key=${this.key}&index=${this.index}&page=${this.page}&size=${this.size}&sort=${this.sort}&schema=${this.schema}`, (json) => {
        if (json.data != null) {
          this.searchResult = json.data.list
          this.took = json.data.took
          this.totalCount = json.data.totalCount
          this.length = json.data.totalPage
        }
      })
    },
    setTab(value) {
      this.tabIndex = value
      this.index = tableName[value]
      this.page = 1
      this.sort = 0
      if (value === 2) {
        this.schema = 100
        this.$router.push({
          path: this.$router.path,
          query: { key: this.key, page: this.page, sort: this.sort, index: this.tabIndex, schema: 200 }
        })
      } else {
        this.schema = ''
        this.$router.push({
          path: this.$router.path,
          query: { key: this.key, page: this.page, sort: this.sort, index: this.tabIndex }
        })
      }

      this.getSearchData()
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x > 950) {
        this.colsWidth = 3
      } else if (this.windowSize.x > 600) {
        this.colsWidth = 6
      } else {
        this.colsWidth = 12
      }
    },
    setSort(value) {
      this.sort = value
      this.page = 1
      this.$router.push({
        path: this.$router.path,
        query: { key: this.key, page: this.page, sort: this.sort, index: this.tabIndex }
      })
      this.getSearchData()
    },
    pageChange(value) {
      this.page = value
      if (this.tabIndex === 2) {
        this.schema = 100
        this.$router.push({
          path: this.$router.path,
          query: { key: this.key, page: this.page, sort: this.sort, index: this.tabIndex, schema: 200 }
        })
      } else {
        this.$router.push({
          path: this.$router.path,
          query: { key: this.key, page: this.page, sort: this.sort, index: this.tabIndex }
        })
      }

      this.getSearchData()
    },
    getTagList() {
      this.httpGet('/article/tags/list', (json) => {
        //
        this.$store.commit('setTagMap', json.data)
      })
    }
  }
}
</script>

<style>

</style>
