<template>
  <v-container>
    <!-- 标题头 -->
    <v-row>
      <v-col cols="10">
        <h2>

          <span v-if="article.type == 100">想法：</span>
          {{ article.title }}
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
      <v-col v-if="this.$store.state.userInfo" cols="2">
        <v-btn v-if="this.$store.state.userInfo.userId == article.authorId || role.role === 'ROLE_ADMIN'" outlined small color="error" @click="deleteDialog = true">删除</v-btn>
        <span v-html="`&nbsp;&nbsp;`" />
        <v-btn v-if="role.role === 'ROLE_ADMIN'" outlined small @click="perfect">{{ addPerfect }}</v-btn>
        <span v-html="`&nbsp;&nbsp;`" />
        <v-btn outlined small>举报</v-btn>
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
        <v-btn v-if="article.hasCollectStatus" depressed small color="primary" @click="collectArticle">你已经收藏过了，不用再次收藏: {{ article.collectCount }}</v-btn>
        <v-btn v-else outlined small color="primary" @click="collectArticle">收藏: {{ article.collectCount }}</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <!-- 分割 -->
    <div style="height: 34px"><span v-html="'&nbsp;'" /></div>
    <!-- 内容部分 -->
    <v-row v-resize="onResize">
      <!-- 正文显示 -->
      <v-col :cols="colsLeft">
        <!-- 正文 -->
        <v-row>
          <div id="class-article-content-view" ref="articleContent" />
        </v-row>
        <!-- 显示投票 -->

        <v-row>
          <v-col>
            <v-divider />
          </v-col>
        </v-row>
        <!-- 显示图片 -->
        <v-row v-if="article.type == 100" justify="center">
          <v-col cols="10">
            <viewer :images="fileList">
              <!-- <v-img height="200" width="200" :src="item" /> -->
              <img v-for="(item, i) in fileList" :key="i" class="show-tink-img" height="100" width="auto" :src="item">
            </viewer>
          </v-col>
        </v-row>
        <VoteList v-if="article.type == 12" :votes="article.votes" :votelog="article.voteLog" @success="getArticle" />
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
            <v-btn
              v-for="item in article.tag"
              :key="item"
              text
              small
              rounded
              class="ma-2"
              color="green"
              text-color="white"
              link
              :to="`/bbs/topic/${item}`"
            >
              {{ item }}
            </v-btn>
          </v-col>
        </v-row>
        <!-- 评论区 -->
        <Comment :artice="article" :comments="comment" />
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
              <v-img :src="article.user.userAvatarUrl" />
            </v-avatar>
          </router-link>
        </v-row>
        <!-- 作者 -->
        <v-row>
          <v-col>
            <v-row justify="center">
              <router-link :to="`/user/${article.authorId}`">
                {{ article.user.username }}
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
        <v-row id="catalog-view" v-scroll="onScroll" style="height: 500px;overflow-y:scroll;">
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
        <div id="catalog-anchor" />
      </v-col>
    </v-row>
    <v-dialog
      v-model="deleteDialog"
      width="500"
    >
      <v-card>
        <v-card-title>删除</v-card-title>
        <v-card-text>你确定要删除这篇帖子吗？</v-card-text>
        <v-card-actions>
          <v-btn text color="error" @click="deleteArticle">
            删除
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
import Comment from '@/components/comment/index.vue'
import VoteList from '@/components/vote/vote-list.vue'

let anchorTop = 0

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
  anchorTop = document.querySelector('#catalog-anchor').offsetTop
  console.log(anchorTop)
}

export default {
  components: {
    Comment,
    VoteList
  },
  props: {
    role: {
      type: Object,
      default: () => {
        return {
          role: 'NONE'
        }
      }
    }
  },
  data() {
    return {
      TimeUtil,
      article: {
        user: {}
      },
      comment: [],

      colsRight: 2,
      colsLeft: 10,
      showCenterDriver: true,
      windowSize: {
        x: 0,
        y: 0
      },
      message: '',
      showMessage: false,
      deleteDialog: false,
      addPerfect: '加精',
      page: 1,
      fileList: null
    }
  },
  created() {
    if (this.$store.state.userInfo == null) {
      this.role.role = 'NONE'
    } else {
      this.role = this.$store.state.userInfo.role
      if (this.role == null) {
        this.role.role = 'NONE'
      }
    }

    this.getArticle()
  },
  mounted() {

  },
  methods: {
    getArticle() {
      this.httpGet(`/article/info/${this.$route.params.articleId}`, (json) => {
        if (json.status === 200) {
          this.article = json.data
          document.title = json.data.title
          if (this.article.perfect === 1) {
            this.addPerfect = '取消加精'
          } else {
            this.addPerfect = '加精'
          }
          if (this.article.type === 100 && this.article.files != null) {
            this.fileList = JSON.parse(this.article.files)
          }
          this.initRender()
          this.$vuetify.goTo(0)
        } else {
          this.$router.push(`/bbs`)
        }
      })
    },
    back() {
      if (this.$route.query.lastPage != null) {
        this.$router.push(`/bbs?page=${this.$route.query.lastPage}`)
      } else {
        this.$router.push(`/bbs`)
      }
    },
    initRender() {
      const output = this.$refs.articleContent
      const outlineElement = this.$refs.articleCatalogView
      Vditor.preview(this.$refs.articleContent,
        this.article.content, {
          speech: {
            enable: false
          },
          cdn: '/vditor',
          theme: {
            path: '/vditor/dist/css/content-theme'
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
    },
    perfect() {
      const data = {
        id: this.article.id
      }
      this.httpPost('/article/perfect', data, (json) => {
        if (json.status === 200) {
          if (this.article.perfect === 1) {
            this.message = '取消加精成功！'
            this.addPerfect = '加精'
            this.article.perfect = 0
          } else {
            this.message = '已经成功设置为精品贴'
            this.addPerfect = '取消加精'
            this.article.perfect = 1
          }
          this.showMessage = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    deleteArticle() {
      const data = {
        id: this.article.id
      }
      this.httpPost('/article/delete', data, (json) => {
        if (json.status === 200) {
          this.message = '帖子删除成功！'
          this.showMessage = true
          this.$router.push(`/course/learn/${this.$route.params.id}/bbs`)
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    onScroll(e) {
      if (typeof window === 'undefined') return
      const top = window.pageYOffset || e.target.scrollTop || 0
      // this.fab = top > 200
      // style="position: fixed; top: 100px"
      const d = document.querySelector('#catalog-view')
      if (top > anchorTop + 64 && this.windowSize.x > 900) {
        // console.log(e.style)

        d.style.position = 'fixed'
        d.style.top = '150px'
      } else {
        d.style.position = 'static'
        d.style.top = '150px'
      }
    },
    collectArticle() {
      const collect = {
        articleId: this.article.id
      }
      this.httpPost('/collects/save', collect, (json) => {
        if (json.status === 200) {
          this.message = '收藏成功!'
          this.showMessage = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
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
        .show-tink-img {
  cursor: pointer;
  margin: 10px;
}

</style>
