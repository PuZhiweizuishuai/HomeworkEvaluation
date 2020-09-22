<template>
  <v-container>
    <!-- 标题 -->
    <v-row>
      <v-col>
        <span style="font-size: 20px;font-weight: bolder;"> {{ course.curriculumName }} </span>
        <span v-text="course.simpleInfo" /> --
        <router-link :to="`/user/${course.createTeacher}`">
          <span v-text="course.teacherName" />
        </router-link>
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <!-- 公告内容 -->
    <v-row>
      <v-col cols="12">
        <v-card outlined>
          <v-card-title>公告：</v-card-title>
        </v-card>
      </v-col>
    </v-row>
    <v-row v-resize="onResize">
      <v-col :cols="left">
        <v-row v-for="item in bulletinList" :key="item.id">
          <v-col cols="12">
            <BulletinCard :bulletin="item" />
          </v-col>
        </v-row>
      </v-col>
      <v-col v-if="showCatalog" cols="2">
        <v-row>
          <v-col>
            <h3>大纲：</h3>
          </v-col>
        </v-row>
        <v-row v-for="item in bulletinList" :key="item.id">
          <v-col cols="12">
            <a :href="'#' + item.title">{{ item.title }}</a>
          </v-col>
        </v-row>
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
import BulletinCard from '@/components/course/bulletin-card.vue'

export default {
  name: 'Learn',
  components: {
    BulletinCard
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
      bulletinList: [],
      id: 0,
      page: 1,
      size: 10,
      length: 0,
      windowSize: {
        x: 0,
        y: 0
      },
      left: 10,
      showCatalog: true
    }
  },
  created() {
    // document.title = document.title
    this.$vuetify.goTo(0)
    this.id = this.$route.params.id
    document.title = '公告 - ' + this.course.curriculumName
    this.getBulletinList()
    this.onResize()
  },
  methods: {
    getBulletinList() {
      this.httpGet(`/bulletin/list/${this.id}?page=${this.page}&limit=${this.size}`, (json) => {
        if (json.status === 200) {
          this.bulletinList = json.data.list
          this.length = json.data.totalPage
        } else {
          console.log(json.message)
        }
      })
    },
    pageChange(value) {
      //
      this.page = value
      this.getBulletinList()
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.left = 12
        this.showCatalog = false
      } else {
        this.left = 10
        this.showCatalog = true
      }
    }
  }
}
</script>

<style>

</style>
