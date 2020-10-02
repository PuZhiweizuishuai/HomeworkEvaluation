<template>
  <v-container>
    <v-row>
      <v-col>
        <h3>
          <span style="color:#1976d2;cursor:pointer" @click="setTag(tag)"> {{ tag.courseMajor }} </span> / {{ tagName }}
        </h3>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn v-for="item in tag.children" :key="item.id" text color="primary" @click="setTag(item)">
          {{ item.courseMajor }}
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-divider />
      </v-col>
    </v-row>
    <v-row v-if="totalCount != 0" v-resize="onResize">
      <v-col v-for="item in classList" :key="item.id" :cols="colsWidth">
        <CourseCard :course="item" />
      </v-col>
    </v-row>
    <v-row v-else justify="center">

      暂无课程

    </v-row>
    <v-row>
      <v-col v-if="totalCount > size">

        <v-row justify="center">
          <v-pagination
            v-model="page"
            :length="length"
            @input="pageChange"
          />
        </v-row>

      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import CourseCard from '@/components/course/course-card.vue'
export default {
  components: {
    CourseCard
  },
  data() {
    return {
      tagId: 0,
      tag: {},
      colsWidth: 3,
      classList: [],
      page: 1,
      size: 20,
      length: 1,
      totalCount: 0,
      tagName: '',
      windowSize: {
        x: 0,
        y: 0
      }
    }
  },
  created() {
    this.tagId = this.$route.params.tagId
    this.getTag()
    this.onResize()
  },
  methods: {
    getTag() {
      this.httpGet(`/course/tag/${this.tagId}`, (json) => {
        if (json.status === 200) {
          this.tag = json.data
          if (this.tag.id === parseInt(this.tagId)) {
            this.tagName = this.tag.courseMajor
          } else {
            for (let i = 0; i < this.tag.children.length; i++) {
              if (this.tag.children[i].id === parseInt(this.tagId)) {
                this.tagName = this.tag.children[i].courseMajor
                break
              }
            }
          }
          this.getCourseList()
        } else {
          this.$router.push({ path: `/` })
        }
      })
    },
    getCourseList() {
      this.httpGet(`/curriculum/list?tag=${this.tagId}&limit=${this.size}&page=${this.page}`, (json) => {
        this.classList = json.data.list
        this.length = json.data.totalPage
        this.totalCount = json.data.totalCount
      })
    },
    setTag(item) {
      this.tagId = item.id
      this.tagName = item.courseMajor
      this.getCourseList()
    },
    pageChange(value) {
      this.page = value
      this.getCourseList()
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
