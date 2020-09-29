<template>
  <v-container>
    <!-- 标题头 -->
    <v-row>
      <v-col cols="11">
        <h2>
          <v-btn
            icon
            @click="back"
          >
            <v-icon>mdi-arrow-left-thick</v-icon>
          </v-btn>
          {{ article.title }}
          <v-chip
            v-if="article.isTeacher"
            class="ma-2"
            color="primary"
            small
          >
            教师
          </v-chip>
          <v-chip
            v-if="article.perfect == 1"
            class="ma-2"
            color="orange"
            small
            text-color="white"
          >
            精品
          </v-chip>
        </h2>
      </v-col>
      <v-col cols="1">
        <v-btn v-if="this.$store.state.userInfo.userId == article.authorId || role.role === 'ROLE_TEACHER'" outlined small color="error">删除</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col style="padding-top: 0px;color: #c6c5c1;font-size:13px;">
        发布于：{{ TimeUtil.renderTime(article.createTime) }}
        <span v-html="`&nbsp;&nbsp;`" />
        |
        <span v-html="`&nbsp;&nbsp;`" />
        阅读数：  {{ article.viewCount }}

        <span v-html="`&nbsp;&nbsp;`" />
        |
        <span v-html="`&nbsp;&nbsp;`" />
        <v-tooltip bottom>
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              color="primary"
              icon
              v-bind="attrs"
              v-on="on"
            >
              <v-icon>mdi-thumb-up</v-icon>
            </v-btn>
          </template>
          <span>赞</span>
        </v-tooltip>
        {{ article.likeCount }}
        <span v-html="`&nbsp;&nbsp;`" />
        |
        <span v-html="`&nbsp;&nbsp;`" />
        <v-tooltip bottom>
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              icon
              v-bind="attrs"
              v-on="on"
            >
              <v-icon>mdi-thumb-down</v-icon>
            </v-btn>
          </template>
          <span>踩</span>
        </v-tooltip>
        {{ article.badCount }}
        <span v-html="`&nbsp;&nbsp;`" />
        |
        <span v-html="`&nbsp;&nbsp;`" />
        <v-btn outlined small color="primary">收藏: {{ article.collectCount }}</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <!-- 内容部分 -->
    <v-row v-resize="onResize">
      <!-- 正文显示 -->
      <v-col :cols="colsLeft">
        <!-- 正文 -->
        <v-row>
          <div id="class-article-content-view" ref="articleContent" />
        </v-row>
        <!-- 分割线 -->
        <v-row>
          <v-col>
            <v-divider />
          </v-col>
        </v-row>
        <!-- 显示标签 -->
        <v-row>
          <v-col>
            标签：
            <v-chip
              v-for="item in article.tag"
              :key="item"
              small
              class="ma-2"
              color="green"
              text-color="white"
            >
              {{ item }}
            </v-chip>
          </v-col>
        </v-row>
        <!-- 评论区 -->
        <Comment :artice="article" :comments="comment" :total="total" />
        <!-- 左侧底部 -->
      </v-col>
      <!-- 中间分割线 -->

      <!-- 大纲及信息显示 -->
      <v-col :cols="colsRight">
        <!-- 发布者 -->
        <v-row>
          <v-col>

            <h3>发起人：</h3>

          </v-col>
        </v-row>
        <v-row justify="center">
          <router-link :to="`/user/${article.authorId}`">
            <v-avatar size="62">
              <v-img :src="article.avatarUrl" />
            </v-avatar>
          </router-link>
        </v-row>
        <!-- 作者 -->
        <v-row>
          <v-col>
            <v-row justify="center">
              <router-link :to="`/user/${article.authorId}`">
                {{ article.authorName }}
              </router-link>
            </v-row>
          </v-col>
        </v-row>
        <!-- 分割线 -->
        <v-row>
          <v-col>
            <v-divider />
          </v-col>
        </v-row>
        <!-- 大纲 -->
        <v-row>
          <v-col>
            <h3>目录：</h3>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <div id="markdown-view-catalog" ref="articleCatalogView" />
          </v-col>
        </v-row>
        <!-- 相关讨论 -->
        <v-row>
          <v-col>
            <h3>相关讨论：</h3>
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
import Comment from '@/components/comment/index.vue'

function initOutline() {
  const headingElements = []
  Array.from(document.querySelector('#class-article-content-view').children).forEach((item) => {
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
  components: {
    Comment
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
      TimeUtil,
      article: {},
      comment: [],
      page: 1,
      size: 20,
      length: 0,
      total: 0,
      type: 0,
      colsRight: 2,
      colsLeft: 10,
      showCenterDriver: true,
      windowSize: {
        x: 0,
        y: 0
      }
    }
  },
  created() {
    this.getArticle()
    this.getComment()
  },
  methods: {
    getArticle() {
      this.httpGet(`/article/info/course/${this.$route.params.articleId}`, (json) => {
        if (json.status === 200) {
          this.article = json.data
          this.initRender()
        } else {
          this.$router.push(`/course/learn/${this.$route.params.id}/bbs`)
        }
      })
    },
    getComment() {
      this.httpGet(`/comment/list/${this.$route.params.articleId}`, (json) => {
        if (json.status === 200) {
          this.comment = json.data.list
          this.length = json.data.totalPage
          this.total = json.data.totalCount
        } else {
          //
        }
      })
    },
    back() {
      this.$router.push(`/course/learn/${this.$route.params.id}/bbs`)
    },
    initRender() {
      const output = this.$refs.articleContent
      const outlineElement = this.$refs.articleCatalogView
      Vditor.preview(this.$refs.articleContent,
        this.article.content, {
          speech: {
            enable: false
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
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.colsRight = 12
        this.colsLeft = 12
      } else {
        this.colsRight = 3
        this.colsLeft = 9
      }
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
