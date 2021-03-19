<template>
  <v-container v-resize="onResize">
    <v-row>
      <v-col>
        <h2>
          <v-btn
            icon
            @click="back"
          >
            <v-icon>mdi-arrow-left-thick</v-icon>
          </v-btn>

          <v-icon>
            mdi-tag
          </v-icon>
          话题分类
        </h2>
      </v-col>
    </v-row>
    <v-row v-for="item in tags" :key="item.key">
      <v-col>
        <v-card>
          <v-card-subtitle>
            <v-card-title>
              <router-link :to="`/bbs/tags/${item.id}`">  {{ item.title }} </router-link>
            </v-card-title>
            <v-row>
              <v-col v-for="tag in item.children" :key="tag.id" :cols="cols">
                <Card :tag="tag" />
              </v-col>
            </v-row>
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

  </v-container>
</template>

<script>
import Card from '@/views/bbs/tag/tag-card.vue'

export default {
  components: {
    Card
  },
  data() {
    return {
      tags: [],
      cols: 4,
      windowSize: {
        x: 0,
        y: 0
      }
    }
  },
  created() {
    this.getTag()
    this.onResize()
  },
  methods: {
    getTag() {
      this.httpGet('/article/tags/tree', (json) => {
        this.tags = json.data
      })
    },
    back() {
      this.$router.push(`/bbs`)
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
