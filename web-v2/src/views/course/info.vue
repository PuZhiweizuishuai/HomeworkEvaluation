<template>
  <v-container v-resize="onResize" fill-height>
    <!-- 顶部 -->
    <v-row>
      <!-- 左边 -->
      <v-col :cols="topLeft">
        <v-img :src="courseInfo.curriculumImageUrl" :aspect-ratio="16/9" />
      </v-col>

      <!-- 中间 -->
      <v-col :cols="topCenter">
        <v-row>

          <h1> {{ courseInfo.curriculumName }} </h1>

        </v-row>
        <v-row>
          <v-col>
            {{ courseInfo.classNumber }} 班
            <v-divider />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            开课时间：{{ TimeUtil.formateNoHoursTime(courseInfo.openingTime, courseInfo.closeTime) }}
            <v-divider />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            简介：{{ courseInfo.simpleInfo }}
            <v-divider />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-icon>mdi-account-outline</v-icon>已有 {{ courseInfo.studentNumber }} 人参加
            <v-divider />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <div class="my-2">
              <v-btn depressed block color="primary">立即参加</v-btn>
            </div>
          </v-col>
        </v-row>
      </v-col>
      <!-- 右边 -->
      <v-col :cols="topRight">
        <v-row>
          <v-col>
            评分：

            <v-rating
              v-model="courseInfo.score"
              color="orange"
              half-increments

              :readonly="true"
            />
            <span v-if="courseInfo.score == 0">暂无评分</span>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            分享：
          </v-col>
        </v-row>
      </v-col>
    </v-row>

    <!-- 详细介绍 -->
    <v-row>
      <v-col cols="9">
        <v-tabs>
          <v-tab @click="showId = 0">
            课程详情
          </v-tab>
          <v-tab @click="showId = 1">
            课程评价
          </v-tab>
        </v-tabs>
        <v-divider />
        <!-- 课程详情 -->
        <v-row>
          <v-col>
            <div v-if="showId == 0" id="class-markdown-view" ref="curriculumInfo" />
          </v-col>
        </v-row>
      </v-col>
      <v-col cols="3">
        目录及教师信息
        <v-row>
          <v-col>
            <div id="markdown-view-catalog" ref="catalogView" />
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import Vditor from 'vditor'
import 'vditor/src/assets/scss/index.scss'

function initOutline() {
  const headingElements = []
  Array.from(document.getElementById('class-markdown-view').children).forEach((item) => {
    if (item.tagName.length === 2 && item.tagName !== 'HR' && item.tagName.indexOf('H') === 0) {
      headingElements.push(item)
    }
  })

  let toc = []
  window.addEventListener('scroll', () => {
    const scrollTop = window.scrollY
    toc = []
    headingElements.forEach((item) => {
      toc.push({
        id: item.id,
        offsetTop: item.offsetTop
      })
    })

    const currentElement = document.querySelector('.vditor-outline__item--current')
    for (let i = 0, iMax = toc.length; i < iMax; i++) {
      if (scrollTop < toc[i].offsetTop - 30) {
        if (currentElement) {
          currentElement.classList.remove('vditor-outline__item--current')
        }
        const index = i > 0 ? i - 1 : 0
        document.querySelector('div[data-id="' + toc[index].id + '"]').classList.add('vditor-outline__item--current')
        break
      }
    }
  })
}
/**
 * 课程详细介绍页面
 */
export default {
  name: 'CourseInfo',
  data() {
    return {
      TimeUtil,
      id: this.$route.params.id,
      windowSize: {
        x: 0,
        y: 0
      },
      courseInfo: {

      },
      topLeft: 6,
      topCenter: 4,
      topRight: 2,
      showId: 0
    }
  },
  created() {
    this.getCourseInfo()
  },
  updated() {
    window.scrollTo(0, 0)
    this.initRender()
  },
  methods: {
    getCourseInfo() {
      this.httpGet(`/curriculum/info/${this.id}`, (json) => {
        if (json.status === 200) {
          this.courseInfo = json.data
          document.title = json.data.curriculumName
        } else {
          this.$router.push('/')
        }
      })
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.topLeft = 12
        this.topCenter = 12
        this.topRight = 12
      } else {
        this.topLeft = 6
        this.topCenter = 4
        this.topRight = 2
      }
    },
    initRender() {
      const output = this.$refs.curriculumInfo
      const outlineElement = this.$refs.catalogView
      Vditor.preview(this.$refs.curriculumInfo,
        this.courseInfo.curriculumInfo, {
          speech: {
            enable: true
          },
          emojiPath: '/emoji',
          anchor: 1,
          after() {
            if (window.innerWidth <= 768) {
              return
            }
            Vditor.outlineRender(output, outlineElement)
            if (outlineElement.innerText.trim() !== '') {
              outlineElement.style.display = 'block'
              initOutline()
            }
          }
        })
    }
  }
}
</script>

<style>

</style>
