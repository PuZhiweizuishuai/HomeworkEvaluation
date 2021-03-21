<template>
  <v-container v-resize="onResize" fluid>
    <v-row>
      <v-col>
        <PostCard v-if="this.$store.state.userInfo && showPostCard == false" @success="thinkSuc" />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn text color="primary" link :to="'/bbs'">
          <v-icon color="error">
            mdi-fire
          </v-icon>
          广场
        </v-btn>
        <v-btn text color="primary" link :to="'/bbs/tags'">
          <v-icon>
            mdi-tag
          </v-icon>
          话题分类
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-divider />
      </v-col>
    </v-row>
    <!-- 导航 -->
    <v-row>
      <v-col>
        <v-tabs v-model="checkTab">
          <v-tab @click="getList(0)">推荐</v-tab>
          <v-tab @click="getList(-1)">精品</v-tab>
          <v-tab @click="getList(100)">想法</v-tab>
          <v-tab @click="getList(12)">投票</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-col />
    <v-row>
      <v-col :cols="colsLift">
        <List v-show="pageType == 10 || pageType == 12 || pageType == 0 || pageType == -1" ref="showArticleList" :type="pageType" />
        <ThinkList v-show="pageType == 100" ref="showThinkList" />
      </v-col>
      <v-col :cols="colsRight">
        <PostCard v-if="this.$store.state.userInfo && showPostCard" @success="thinkSuc" />
        <v-col />
        <!-- 广告 -->
        <v-card outlined>
          <v-card-title>新闻：</v-card-title>
          <v-row>
            <v-col>
              <v-carousel
                cycle
                hide-delimiter-background
                show-arrows-on-hover
                height="200"
              >
                <v-carousel-item
                  v-for="(item, i) in adList"
                  :key="i"

                  :src="item.image"
                  :href="item.url"
                  target="_blank"
                />
              </v-carousel>

            </v-col>
          </v-row>
        </v-card>
        <v-col />
        <!-- 热门 -->
        <v-card outlined>
          <v-card-title>热门：</v-card-title>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import List from '@/views/bbs/components/article-list.vue'
import ThinkList from '@/components/think/think-list.vue'
import PostCard from '@/views/bbs/components/index-post-card.vue'

export default {
  name: 'BBS',
  components: {
    List,
    PostCard,
    ThinkList
  },
  data() {
    return {
      adPage: 1,
      adList: [],
      colsRight: 3,
      colsLift: 9,
      windowSize: {
        x: 0,
        y: 0
      },
      showPostCard: true,
      pageType: 10,
      checkTab: 0
    }
  },
  created() {
    const type = parseInt(this.$route.query.type)
    if (!isNaN(type)) {
      //
      if (type === 100 || type === 10 || type === 11 || type === 12 || type === -1) {
        this.pageType = type
        if (type === 100) {
          this.checkTab = 2
        } else if (type === 12) {
          this.checkTab = 3
        } else if (type === -1) {
          this.checkTab = 1
        }
      } else {
        this.pageType = 0
        this.checkTab = 0
      }
    }
    this.getAd()
  },
  methods: {
    thinkSuc() {
      this.$refs.showThinkList.getThinkList()
    },
    getAd() {
      this.httpGet('/index/bbs/ad', (json) => {
        this.adList = json.data
      })
    },
    getList(value) {
      this.$router.push({
        path: this.$router.path,
        query: { type: value }
      })
      this.pageType = value
      if (value === 10 || value === 12 || value === 0) {
        this.$refs.showArticleList.setType(value)
      } else if (value === 100) {
        //
        this.$refs.showThinkList.getThinkList()
      } else if (value === -1) {
        this.$refs.showArticleList.setSort(2)
      }
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.colsRight = 12
        this.colsLift = 12
        this.showPostCard = false
      } else {
        this.showPostCard = true
        this.colsRight = 3
        this.colsLift = 9
      }
    }
  }
}
</script>

<style>

</style>
