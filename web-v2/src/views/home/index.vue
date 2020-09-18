<template>
  <v-container fill-height>
    <TopComponent :tag="tagList" />
    <v-row>
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <v-row v-for="item in tagList" :key="item.id">
      <v-col cols="12">
        <CourseList :tag="item" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import TopComponent from '@/views/home/index-top.vue'
import CourseList from '@/components/course/course-list.vue'
/**
 * 主页显示
 */
export default {
  name: 'Home',
  components: {
    TopComponent,
    CourseList
  },
  data() {
    return {
      tagList: []
    }
  },
  created() {
    this.getTagList()
  },
  methods: {
    getTagList() {
      this.httpGet('/course/tag/list', (json) => {
        if (json.status === 200) {
          this.tagList = json.data
        }
      })
    }
  }
}
</script>

<style>

</style>
