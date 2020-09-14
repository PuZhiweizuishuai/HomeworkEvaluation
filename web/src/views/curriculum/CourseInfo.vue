<template>
  <div>
    <a-row :gutter="[16,16]">
      <!-- 显示课程图片 -->
      <a-col :span="12">
        <img style="width: 95%" :src="courseInfo.curriculumImageUrl">
      </a-col>
      <!-- 显示课程简单介绍 -->
      <a-col :span="8">
        <h1 v-text="courseInfo.curriculumName" />
        <p>
          <span v-text="courseInfo.classNumber" /> 班
        </p>
        <a-divider />
        <p>开课时间：
          <span v-text="classTime()" />
        </p>
        <a-divider />
        <p>
          简介：<span v-text="courseInfo.simpleInfo" />
        </p>
        <a-divider />
        <p>
          <a-icon type="user" style="margin-bottom: 8px;" />&nbsp;已有 <span v-text="courseInfo.studentNumber" />  人参加
        </p>
        <a-button type="primary" style="width:80%; ">
          立即参加
        </a-button>
        <!-- <p>
          加入方式： <span v-if="courseInfo.accessMethod === 0">老师邀请</span>
        </p> -->
      </a-col>
      <!-- 显示分享 -->
      <a-col :span="4">

        <a-tooltip placement="top">
          <template slot="title">
            <span v-if="courseInfo.score === 0">暂无评分</span>
          </template>
          评分：
          <a-rate :default-value="courseInfo.score" disabled />
        </a-tooltip>
        <br><br>
        <p>
          分享课程：
        </p>
      </a-col>
    </a-row>

    <div style="height:64px;" />
    <!-- 详细介绍,左边 -->
    <a-row :gutter="[16,16]">
      <a-col :span="18">
        <div>
          <a-tabs default-active-key="1" size="large">
            <a-tab-pane key="1" tab="课程详情">
              <div id="class-markdown-view" ref="curriculumInfo" />
            </a-tab-pane>

            <a-tab-pane key="2" tab="课程评价" force-render>
              课程评价
            </a-tab-pane>
          </a-tabs>
        </div>
      </a-col>

      <!-- 右边 -->
      <a-col :span="6">
        <!-- 老师信息 -->
        <a-card :title="getTeacherNumber()" style="width: 100%" shadow="hover">
          <a-row>
            <router-link :to="`/user/${courseInfo.createTeacher}`">
              <a-col span="6">
                <a-avatar :size="64" icon="user" :src="courseInfo.teacherAvatar" />
              </a-col>
              <a-col span="6">
                <h5 v-text="courseInfo.teacherName" />
                <p v-text="courseInfo.title" />
              </a-col>
            </router-link>
          </a-row>
          <div v-if="teacherList()">
            <a-row v-for="item in courseInfo.otherTeacher" :key="item.id" style="margin-top: 20px;">
              <router-link :to="`/user/${item.id}`">
                <a-col span="6">
                  <a-avatar :size="64" icon="user" :src="item.userAvatar" />
                </a-col>
                <a-col span="6">
                  <h5 v-text="item.name" />
                  <p v-text="item.title" />
                </a-col>
              </router-link>
            </a-row>
          </div>
        </a-card>
        <!-- 目录信息 -->
        <br><br>
        <div>
          <h5>大纲：</h5>
          <a-divider />
        </div>

        <div id="markdown-view-catalog" ref="catalogView" />

      </a-col>

    </a-row>

    <!-- 返回顶部 -->
    <a-back-top />

  </div>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import Vditor from 'vditor'
import 'vditor/src/assets/scss/index.scss'

// function initRender(markdown) {
//   Vditor.preview(document.getElementById('class-markdown-view'),
//     markdown, {
//       speech: {
//         enable: true
//       },
//       anchor: 1,
//       after() {
//         if (window.innerWidth <= 768) {
//           return
//         }
//         const outlineElement = document.getElementById('class-markdown-view')
//         Vditor.outlineRender(document.getElementById('class-markdown-view'), outlineElement)
//         if (outlineElement.innerText.trim() !== '') {
//           outlineElement.style.display = 'block'
//           initOutline()
//         }
//       }
//     })
// }

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

export default {
  name: 'CourseInfo',
  data() {
    return {
      courseInfo: []
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
    getTeacherNumber() {
      let count = 0
      this.courseInfo.otherTeacher = this.courseInfo.otherTeacher || []
      if (this.courseInfo.otherTeacher !== 0) {
        count = this.courseInfo.otherTeacher.length
      }
      return (count + 1) + ' 位授课老师'
    },
    teacherList() {
      this.courseInfo.otherTeacher = this.courseInfo.otherTeacher || []
      if (this.courseInfo.otherTeacher.length !== 0) {
        return true
      }
      return false
    },
    getCourseInfo() {
      const id = this.$route.params.id

      fetch(this.SERVER_API_URL + `/curriculum/info/${id}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET'
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.courseInfo = json.data
            document.title = json.data.curriculumName
          } else {
            this.$message.error('课程不存在')
            this.$router.push('/curriculum')
          }
        })
        .catch(e => {
          this.$message.error('请检查网络后重试！')
          this.$router.push('/curriculum')
        })
    },
    classTime() {
      return TimeUtil.formateTimeToChinese(this.courseInfo.openingTime) + ' - ' + TimeUtil.formateTimeToChinese(this.courseInfo.closeTime)
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

<style >
        .header {
            background-color: #fff;
            box-shadow: rgba(0, 0, 0, 0.05) 0 1px 7px;
            border-bottom: 1px solid #e1e4e8;
        }

        #preview {
            margin: 0 auto;
            max-width: 768px;
        }

        .wrapper--preview {
            margin: 0 220px 0 0;
            max-width: none;
            width: auto;
        }

        #markdown-view-catalog {
            display: none;
            z-index: -99;
            /* position: fixed; */
            /* width: 186px; */
            top: 130px;
            right: 20px;
            bottom: 86px;
            overflow: auto;
            font-size: 12px;
            border-left: 1px solid #eee;
            background-color: transparent;
        }

        .vditor-outline__item {
            border-left: 1px solid transparent;
        }

        .vditor-outline__item--current {
            border-left: 1px solid #4285f4;
            color: #4285f4;
            background-color: #f6f8fa;
        }

        .vditor-outline__item:hover {
            color: #4285f4;
            background-color: #f6f8fa;
        }

        @media (max-width: 768px) {
            #markdown-view-catalog {
                display: none !important;
            }

            .wrapper--preview {
                margin: 0;
            }
        }
</style>
