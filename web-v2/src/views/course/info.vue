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
              <v-btn depressed block color="primary" @click="intoCourse">{{ clickMessage }}</v-btn>
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
      <!-- 课程详情与评价 -->
      <v-col :cols="closInfoLeft">
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
        <v-row v-show="showId == 0">
          <v-col>
            <div id="class-markdown-view" ref="curriculumInfo" />
          </v-col>
        </v-row>
      </v-col>
      <!-- 教师信息与大纲 -->
      <v-col :cols="colsInfoRight">
        <v-row>
          <v-col>
            <h2>教师：</h2>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-card outlined>
              <v-list three-line link>
                <router-link :to="`/user/${courseInfo.createTeacher}`">
                  <v-list-item>
                    <v-list-item-avatar>
                      <v-img :src="courseInfo.teacherAvatar" />
                    </v-list-item-avatar>
                    <v-list-item-content>
                      <v-list-item-title v-html="courseInfo.teacherName" />
                      <v-list-item-subtitle v-html="courseInfo.title" />
                    </v-list-item-content>
                  </v-list-item>
                </router-link>

                <router-link v-for="item in courseInfo.otherTeacher" :key="item.id" :to="`/user/${item.id}`">
                  <v-divider />
                  <v-list-item>
                    <v-list-item-avatar>
                      <v-img :src="item.userAvatar" />
                    </v-list-item-avatar>
                    <v-list-item-content>
                      <v-list-item-title v-html="item.name" />
                      <v-list-item-subtitle v-html="item.title" />
                    </v-list-item-content>
                  </v-list-item>
                </router-link>
              </v-list>
            </v-card>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <h2>大纲：</h2>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <div id="markdown-view-catalog" ref="catalogView" />
          </v-col>
        </v-row>
      </v-col>
    </v-row>
    <v-dialog
      v-model="dialog"
      width="500"
    >
      <!-- 未登录显示 -->
      <v-card v-if="this.$store.state.userInfo == null && timeJudge()">
        <v-img
          class="white--text align-end"
          height="200px"
          src="/images/pic1.png"
        >
          <v-card-title>登陆后即可加入</v-card-title>
        </v-img>
        <v-card-actions>
          <v-btn
            text
            @click="dialog = false"
          >
            取消
          </v-btn>

          <v-btn
            color="primary"
            text
            @click="goToLogin"
          >
            登录
          </v-btn>
        </v-card-actions>

      </v-card>
      <!-- 登录后显示 -->
      <v-card v-if="this.$store.state.userInfo && timeJudge()">
        <v-card-title class="headline grey lighten-2">
          加入课程
        </v-card-title>

        <v-card-text>
          <div v-if="courseInfo.accessMethod == 0">
            该课程进入需要邀请码,请输入课程邀请码： <v-text-field
              v-model="code.code"
              label="邀请码"
              single-line
            />
          </div>
          <div v-if="courseInfo.accessMethod == 1">
            该课程进入需要密码,请输入课程密码： <v-text-field
              v-model="code.code"
              label="密码"
              single-line
            />
          </div>
          <div v-if="courseInfo.accessMethod == 2">
            点击加入加入课程。
          </div>
          <v-row>
            <img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" @click="getVerifyImage">
            <v-text-field
              v-model="code.verifyCode"
              placeholder="验证码"
              label="验证码"
              :rules="[() => code.verifyCode != null || '验证码不能为空']"
              clearable
            />
          </v-row>
        </v-card-text>

        <v-divider />

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="primary"
            text
            @click="joinCourse()"
          >
            加入
          </v-btn>
        </v-card-actions>
      </v-card>
      <!-- 课程未开始或课程已结束 -->
      <v-card v-if="timeJudge() == false">
        <v-img
          class="white--text align-end"
          height="200px"
          src="/images/pic1.png"
        >
          <v-card-title>课程未开始或已结束</v-card-title>
        </v-img>
        <v-card-text class="text--primary">
          <div>选择其它的课程加入吧！</div>
        </v-card-text>
        <v-card-actions>
          <v-btn
            text
            @click="dialog = false"
          >
            确定
          </v-btn>
        </v-card-actions>

      </v-card>
    </v-dialog>
    <v-snackbar
      v-model="showMessage"
      :top="true"
      :timeout="3000"
    >
      {{ message }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="pink"
          text
          v-bind="attrs"
          @click="showMessage = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import Vditor from 'vditor'
import 'vditor/src/assets/scss/index.scss'

function initOutline() {
  const headingElements = []
  Array.from(document.querySelector('#class-markdown-view').children).forEach((item) => {
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
      showId: 0,
      judgeClick: false,
      clickMessage: '立即加入',
      dialog: false,
      code: {
        code: '',
        verifyCode: ''
      },
      message: '',
      showMessage: false,
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      closInfoLeft: 9,
      colsInfoRight: 3
    }
  },
  created() {
    window.scrollTo(0, 0)
    this.getCourseInfo()
    this.judge()
    this.onResize()
  },
  updated() {

  },
  methods: {
    getCourseInfo() {
      this.httpGet(`/curriculum/info/${this.id}`, (json) => {
        if (json.status === 200) {
          this.courseInfo = json.data
          document.title = json.data.curriculumName
          this.initRender()
        } else {
          this.$router.push('/')
        }
      })
    },
    judge() {
      this.httpGet(`/curriculum/role/${this.id}`, (json) => {
        if (json.status === 200) {
          this.judgeClick = true
          this.clickMessage = '已加入，进入学习'
        } else {
          this.judgeClick = false
          this.clickMessage = '立即加入'
        }
      })
    },
    intoCourse() {
      if (this.judgeClick) {
        this.$router.push(`/course/learn/${this.id}`)
      } else {
        this.dialog = true
      }
    },
    goToLogin() {
      this.$router.push('/login')
    },
    joinCourse() {
      if (this.courseInfo.accessMethod === 1 || this.courseInfo.accessMethod === 0) {
        if (this.code === '' || this.code === null) {
          this.message = '请输入邀请码或密码后再加入！'
          this.showMessage = true
          return
        }
      }
      // TODO 发送课程加入请求
      this.httpPost(`/curriculum/join/${this.id}`, this.code, (json) => {
        if (json.status === 200) {
          this.$router.push(`/course/learn/${this.id}`)
          this.dialog = false
        } else {
          this.message = json.message + '  ' + json.error
          this.showMessage = true
        }
      })
    },
    timeJudge() {
      const time = new Date().getTime()
      if (this.courseInfo.closeTime > time && this.courseInfo.openingTime < time) {
        return true
      }
      return false
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.topLeft = 12
        this.topCenter = 12
        this.topRight = 12
        this.colsInfoRight = 12
        this.closInfoLeft = 12
      } else {
        this.topLeft = 6
        this.topCenter = 4
        this.topRight = 2
        this.colsInfoRight = 3
        this.closInfoLeft = 9
      }
    },
    initRender() {
      const output = this.$refs.curriculumInfo
      const outlineElement = this.$refs.catalogView
      Vditor.preview(this.$refs.curriculumInfo,
        this.courseInfo.curriculumInfo, {
          speech: {
            enable: false
          },
          emojiPath: '/emoji',
          anchor: 1,
          cdn: '/vditor',
          theme: {
            path: '/vditor/dist/css/content-theme'
          },
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
    },
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    }
  }
}
</script>

<style>
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
            font-size: 15px;
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
</style>
