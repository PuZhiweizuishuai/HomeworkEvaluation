<template>
  <div>
    <a-row :gutter="[16,8]">
      <!-- 课程分类 -->
      <a-col :span="5">
        <a-menu theme="dark" mode="vertical">
          <template v-for="item in menuitemList">
            <a-menu-item v-if="item.id < 8" :key="item.id">
              <a-icon type="tags" />
              {{ item.courseMajor }}
            </a-menu-item>
            <a-sub-menu v-if="item.id == 8" :key="item.id" :title="'更多'">
              <template v-for="m in menuitemList">
                <a-menu-item v-if="m.id >=8" :key="m.id">{{ m.courseMajor }}</a-menu-item>
              </template>
            </a-sub-menu>
          </template>
        </a-menu>
      </a-col>

      <!-- 轮播图 -->
      <a-col :span="15">
        <a-carousel autoplay>
          <div v-for="photo in carouselList" :key="photo.id">
            <a :href="photo.url" target="_blank">
              <img v-if="photo.type === 1" height="384px" :src="photo.image" :alt="photo.title" :title="photo.title">
            </a>
          </div>
        </a-carousel>
      </a-col>
      <!-- 个人课程信息显示 -->
      <a-col :span="4">
        <a-card hoverable>
          <a-avatar :size="64" icon="user" />

          <a-card-meta title="Europe Street beat">
            <template slot="description">
              www.instagram.com
            </template>
          </a-card-meta>
        </a-card>
      </a-col>
    </a-row>
    <a-divider />
    <div>
      <a-row v-for="tag in menuitemList" :key="tag.id" :gutter="[16,8]">
        <IndexCourseList :tagid="tag.id" :tagname="tag.courseMajor" :tab="tag.children" />
      </a-row>
    </div>
    <a-back-top />
  </div>
</template>

<script>
import IndexCourseList from '@/views/curriculum/index-course-list.vue'

export default {
  name: 'CurriculumList',
  components: { IndexCourseList },
  data() {
    return {
      menuitemList: [],
      carouselList: []
    }
  },
  created() {
    // 获取按钮列表
    this.getMenuItem()
    this.getCarousel()
  },
  methods: {
    getMenuItem() {
      fetch(this.SERVER_API_URL + '/course/tag/list', {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET'
      }).then(response => response.json())
        .then(json => {
          this.menuitemList = json.data
        })
        .catch(e => {

        })
    },
    getCarousel() {
      fetch(this.SERVER_API_URL + '/index/curriculum/ad', {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET'
      }).then(response => response.json())
        .then(json => {
          this.carouselList = json.data || []
        })
        .catch(e => {
          return null
        })
    }
  }
}
</script>
