<template>
  <div style="margin: 24px;">
    <b-row>
      <router-link :to="`/curriculum/tag/${tagId}`">
        <h4 class="tag-name" v-text="tagName" />
      </router-link>
    </b-row>
    <b-row>
      <div>
        <a-tabs :size="'small'" :default-active-key="tagid" style="width:100%" @change="getTabClassList">
          <a-tab-pane :key="tagid" tab="最近更新">
            <b-row v-if="classList.length !== 0">
              <b-col v-for="course in classList" :key="course.id" style="margin-top: 24px" cols="3">
                <a-card hoverable style="width: 95%" @click="routInfo(course.id)">
                  <img
                    slot="cover"
                    :alt="course.curriculumName"
                    :src="course.curriculumImageUrl"
                  >
                  <a-card-meta :title="course.curriculumName">
                    <template slot="description">
                      <span style="font-size: 10px" v-text="classTime(course.openingTime, course.closeTime)" /><br>
                      <span v-text="course.simpleInfo" /><br>
                      <span v-text="course.teacherName" /><br><br>
                      <span><a-icon style="margin-bottom: 8px;" type="user" />&nbsp; <span v-text="course.studentNumber" /> </span>
                    </template>
                  </a-card-meta>
                </a-card>
              </b-col>
            </b-row>
            <b-row v-if="classList.length === 0">
              <a-empty description="暂无课程" />
            </b-row>
          </a-tab-pane>

          <a-tab-pane v-for="t in getforData()" :key="t.id" :tab="t.courseMajor" force-render>
            <b-row v-if="classList.length !== 0">
              <b-col v-for="course in classList" :key="course.id" style="margin-top: 24px" cols="3">
                <a-card hoverable style="width: 95%" @click="routInfo(course.id)">
                  <img
                    slot="cover"
                    :alt="course.curriculumName"
                    :src="course.curriculumImageUrl"
                  >
                  <a-card-meta :title="course.curriculumName">
                    <template slot="description">
                      <span style="font-size: 10px" v-text="classTime(course.openingTime, course.closeTime)" /><br>
                      <span v-text="course.simpleInfo" /><br>
                      <span v-text="course.teacherName" /><br><br>
                      <span><a-icon style="margin-bottom: 8px;" type="user" />&nbsp; <span v-text="course.studentNumber" /> </span>
                    </template>
                  </a-card-meta>
                </a-card>
              </b-col>
            </b-row>
            <b-row v-if="classList.length === 0">
              <a-empty description="暂无课程" />
            </b-row>
          </a-tab-pane>

        </a-tabs>
      </div>
    </b-row>
  </div>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
export default {
  name: 'IndexCourseList',
  props: {
    tagid: {
      type: Number,
      default: 0
    },
    tagname: {
      type: String,
      default: ''
    },
    tab: {
      type: Array,
      default: () => { return [] }
    }
  },
  data() {
    return {
      tagId: this.tagid,
      tagName: this.tagname,
      classList: [],
      tag: this.tab
    }
  },
  created() {
    this.tag = this.tab || []
    this.getClassList(this.tagId)
  },
  methods: {
    getClassList(id) {
      fetch(this.SERVER_API_URL + `/curriculum/list?tag=${id}&limit=10`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET'
      }).then(response => response.json())
        .then(json => {
          this.classList = json.page.list || []
        })
        .catch(e => {
          return null
        })
    },
    classTime(start, end) {
      return TimeUtil.formateTimeToChinese(start) + ' - ' + TimeUtil.formateTimeToChinese(end)
    },
    routInfo(id) {
      this.$router.push(`/curriculum/info/${id}`)
    },
    getTabClassList(id) {
      // TODO 优化网络加载
      this.getClassList(id)
    },
    getforData() {
      if (this.tag.length === 0) {
        return []
      }
      return this.tag.slice(0, 2)
    }
  }
}
</script>

<style scoped>
.tag-name {
  font-weight: bold;
}
.tag-name:hover {
  color: #1890ff;

}
</style>
