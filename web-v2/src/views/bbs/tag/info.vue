<template>
  <v-container v-resize="onResize">
    <!-- 父分类 -->
    <div v-if="tagInfo.now.catelogId == 0">
      <v-row>
        <v-col>
          <h2><v-btn
            icon
            @click="back(0)"
          >
            <v-icon>mdi-arrow-left-thick</v-icon>
          </v-btn><v-icon>
            mdi-tag
          </v-icon>{{ tagInfo.now.title }}</h2>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-expansion-panels accordion>
            <v-expansion-panel>
              <v-expansion-panel-header>
                点击展开分类
              </v-expansion-panel-header>
              <v-expansion-panel-content>
                <v-row>
                  <v-col v-for="item in tagInfo.now.children" :key="item.id" :cols="cols">
                    <Card :tag="item" />
                  </v-col>
                </v-row>
              </v-expansion-panel-content>
            </v-expansion-panel>
          </v-expansion-panels>
        </v-col>
      </v-row>
    </div>
    <div v-else>
      <v-row>
        <v-col>
          <h2><v-btn
            icon
            @click="back(1)"
          >
            <v-icon>mdi-arrow-left-thick</v-icon>
          </v-btn> {{ tagInfo.now.title }}</h2>
        </v-col>
      </v-row>
    </div>
    <!-- 内容 -->
    <v-divider />
    <v-row>
      <v-col>
        <List v-if="showList" ref="showArticleList" :tagid="tagInfo.now.id" :showtab="true" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Card from '@/views/bbs/tag/tag-card.vue'
import List from '@/views/bbs/components/article-list.vue'

export default {
  components: {
    Card,
    List
  },
  data() {
    return {
      tagId: this.$route.params.id,
      tagInfo: {
        now: {
          catelogId: 0
        }
      },
      cols: 4,
      windowSize: {
        x: 0,
        y: 0
      },
      showList: false
    }
  },
  created() {
    this.getTag()
    this.onResize()
  },
  methods: {
    back(value) {
      if (value === 1) {
        this.$router.push(`/bbs/tags/${this.tagInfo.now.catelogId}`)
      } else {
        this.$router.push(`/bbs/tags`)
      }
    },
    getTag() {
      this.httpGet(`/article/tags/info/${this.tagId}`, (json) => {
        if (json.status === 200) {
          this.tagInfo = json.data
          this.showList = true
        } else {
          this.$router.push('/bbs/tags')
        }
      })
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.cols = 6
      } else {
        this.cols = 4
      }
    }
  }
}
</script>

<style>

</style>
