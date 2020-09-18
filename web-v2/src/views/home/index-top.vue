<template>
  <v-container v-resize="onResize">
    <v-row>
      <v-col v-if="showTagList" cols="3">
        <CourseTagList :items="tag" />
      </v-col>
      <v-col :cols="colsRigth">
        <v-carousel
          cycle
          height="367"
          hide-delimiter-background
          show-arrows-on-hover
        >
          <v-carousel-item
            v-for="(item, i) in topImageList"
            :key="i"
            :src="item.image"
            :href="item.url"
            target="_blank"
          />
        </v-carousel>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import CourseTagList from '@/components/course/course-tag-list.vue'

/**
 * 主页顶部显示
 */
export default {
  name: 'Home',
  components: {
    CourseTagList
  },
  props: {
    tag: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      topImageList: [],
      showTagList: true,
      colsRigth: 9,
      windowSize: {
        x: 0,
        y: 0
      }
    }
  },
  created() {
    this.getCarousel()
    this.onResize()
  },
  methods: {
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.showTagList = false
        this.colsRigth = 12
      } else {
        this.showTagList = true
        this.colsRigth = 9
      }
    },
    getCarousel() {
      this.httpGet('/index/curriculum/ad', (json) => {
        if (json.status === 200) {
          this.topImageList = json.data
        }
      })
    }
  }

}
</script>

<style>

</style>
