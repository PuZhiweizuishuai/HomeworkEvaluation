<template>
  <v-container v-resize="onResize" fluid>
    <v-row>
      <v-col>
        <PostCard v-if="this.$store.state.userInfo && showPostCard == false" />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-divider />
      </v-col>
    </v-row>
    <v-col />
    <v-row>
      <v-col :cols="colsLift">
        <List />
      </v-col>
      <v-col :cols="colsRight">
        <PostCard v-if="this.$store.state.userInfo && showPostCard" />
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
import PostCard from '@/views/bbs/components/index-post-card.vue'

export default {
  name: 'BBS',
  components: {
    List,
    PostCard
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
      showPostCard: true
    }
  },
  created() {
    this.getAd()
  },
  methods: {
    getAd() {
      this.httpGet('/index/bbs/ad', (json) => {
        this.adList = json.data
      })
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
