<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <router-link :to="`/course/tag/${tag.id}`">
          <h3>
            {{ tag.courseMajor }}
          </h3>
        </router-link>
      </v-col>
    </v-row>
    <v-row v-resize="onResize">
      <v-col cols="12">
        <v-tabs>
          <v-tab @click="setTagId(tag.id)">
            最近更新
          </v-tab>
          <v-tab v-for="item in children" :key="item.id" @click="setTagId(item.id)">
            <span> {{ item.courseMajor }} </span>
          </v-tab>
        </v-tabs>
        <v-divider />
      </v-col>
    </v-row>
    <v-row v-if="classList.length != 0">
      <v-col v-for="item in classList" :key="item.id" :cols="colsWidth">
        <CourseCard :course="item" />
      </v-col>
    </v-row>
    <v-row v-else justify="center">

      暂无课程

    </v-row>
  </v-container>
</template>

<script>
import CourseCard from '@/components/course/course-card.vue'
/**
 * 首页课程分类显示
 */
export default {
  components: {
    CourseCard
  },
  props: {
    tag: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      children: [],
      colsWidth: 3,
      id: this.tag.id,
      classList: [],
      windowSize: {
        x: 0,
        y: 0
      }
    }
  },
  created() {
    this.initChildren()
    this.httpGet(`/curriculum/list?tag=${this.id}&limit=8`, (json) => {
      this.classList = json.page.list || []
    })
    this.onResize()
  },
  methods: {
    initChildren() {
      if (this.tag.children) {
        if (this.tag.children.length > 3) {
          for (let i = 0; i < 3; i++) {
            this.children.push(this.tag.children[i])
          }
        } else {
          this.children = this.tag.children
        }
      }
    },
    setTagId(value) {
      this.id = value
      this.httpGet(`/curriculum/list?tag=${this.id}&limit=8`, (json) => {
        this.classList = json.page.list || []
      })
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
    }
  }
}
</script>

<style>

</style>
