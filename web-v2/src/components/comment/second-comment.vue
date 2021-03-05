<template>
  <v-container>
    <div id="commentTop" ref="commentTop" />
    <v-row justify="center">
      <v-col cols="11">
        <h3>评论</h3>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="11">
        <!-- <v-textarea
          v-model="comment.content"
          :placeholder="commentPlaceholder"
          label="评论"
          auto-grow
        /> -->
        <SecondCommentVditor
          :key="secondCommentKey"
          ref="secondCommentView"
          :placeholder="commentPlaceholder"
          :uploadurl="uploadurl"
          @vditor-input="getSecondCommentText"
        />
      </v-col>
    </v-row>
    <v-row justify="end">

      <v-col cols="3">
        <v-row justify="center">
          <img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" @click="getVerifyImage">
        </v-row>
      </v-col>

      <v-col cols="3">
        <v-text-field
          v-model="comment.verifyCode"
          placeholder="验证码"
          label="验证码"
          :rules="[() => comment.verifyCode != null || '验证码不能为空']"
          clearable
        />
      </v-col>
      <v-col cols="3">
        <v-row justify="end">
          <v-btn depressed color="success" @click="submit">
            评论
          </v-btn>
        </v-row>
      </v-col>
      <v-col cols="1" />
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <v-row>
      <v-col cols="10" style="padding-bottom: 0px;">
        <v-tabs>
          <v-tab @click="setSort(0)">评论时间</v-tab>
          <v-tab @click="setSort(1)">时间倒序</v-tab>
          <v-tab>最多点赞</v-tab>
          <v-tab @click="setSort(3)">最多评论</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <v-row v-for="item in secondList" :key="item.id" justify="center">
      <v-col cols="11">
        <Card :comment="item" :type="type" @comment="getComment" />
      </v-col>
    </v-row>

    <v-row v-if="total == 0" justify="center">
      <h3>暂无评论，来抢个沙发吧！</h3>
    </v-row>
    <v-row justify="center">
      <v-pagination
        v-if="length != 1"
        v-model="page"
        :length="length"
        @input="pageChange"
      />
    </v-row>
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
import Card from '@/components/comment/second-card.vue'
import SecondCommentVditor from '@/components/vditor/comment.vue'

export default {
  name: 'SecondComment',
  components: {
    Card,
    SecondCommentVditor
  },
  props: {
    father: {
      type: Object,
      default: null
    },
    type: {
      type: Number,
      default: 1
    }
  },
  data() {
    return {
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      secondList: [],
      page: 1,
      size: 10,
      length: 1,
      sort: 0,
      total: 0,
      comment: {
        content: '',
        verifyCode: '',
        type: this.type,
        commentId: this.father.id,
        articleId: this.father.articleId,
        fatherId: this.father.fatherId
      },
      commentPlaceholder: '',
      message: '',
      showMessage: false,
      secondCommentKey: 0,
      uploadurl: this.SERVER_API_URL + '/upload/file'
    }
  },
  created() {
    // 使用 type 值来判断
    // 如果 type 值是 1，则是普通帖子的二级评论
    // 如果 type 值是 11，则是作业互评的二级评论
    // 如果 type 值是 101，则是课程评价的二级评论
    this.getSecondList()
  },
  methods: {
    getComment(value) {
      this.$refs.commentTop.scrollIntoView()
      this.commentPlaceholder = '回复 @' + value.username + ': ' + value.content
      this.secondCommentKey++
      this.comment.commentId = value.id
      this.comment.fatherId = value.fatherId
    },
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    },
    getSecondList() {
      if (this.type === 1) {
        this.httpGet(`/comment/second/list/${this.father.id}?page=${this.page}&limit=${this.size}&sort=${this.sort}`, (json) => {
          if (json.status === 200) {
          //
            this.secondList = json.data.list
            this.length = json.data.totalPage
            this.total = json.data.totalCount
          } else {
          //
          }
        })
      } else if (this.type === 11) {
        this.httpGet(`/evaluation/comment/second/${this.father.id}?page=${this.page}&limit=${this.size}&sort=${this.sort}`, (json) => {
          if (json.status === 200) {
          //
            this.secondList = json.data.list
            this.length = json.data.totalPage
            this.total = json.data.totalCount
          } else {
          //
          }
        })
      }
    },
    submit() {
      //
      if (!this.$store.state.userInfo) {
        this.message = '请先登录后再评论！'
        this.showMessage = true
        return
      }
      if (this.comment.content == null || this.comment.content === '') {
        this.message = '评论内容不能为空！'
        this.showMessage = true
        return
      }
      if (this.comment.verifyCode == null || this.comment.verifyCode === '') {
        this.message = '验证码不能为空！'
        this.showMessage = true
        return
      }
      if (this.type === 1) {
        this.httpPost('/comment/save', this.comment, (json) => {
          if (json.status === 200) {
            this.message = '评论成功！'
            this.showMessage = true
            this.comment.verifyCode = ''
            this.comment.content = ''
            this.$refs.secondCommentView.setTextValue('')
            this.getSecondList()
          } else {
          //
            this.message = json.message
            this.showMessage = true
          }
        })
      } else if (this.type === 11) {
        const data = {
          content: this.comment.content,
          verifyCode: this.comment.verifyCode,
          type: this.type,
          commentId: this.father.id,
          submitId: this.father.articleId,
          fatherId: this.father.fatherId
        }

        this.httpPost(`/evaluation/comment/submit`, data, (json) => {
          if (json.status === 200) {
            this.message = '评论成功！'
            this.$refs.secondCommentView.setTextValue('')
            this.showMessage = true
            this.comment.verifyCode = ''
            this.comment.content = ''

            this.getSecondList()
          } else {
          //
            this.message = json.message
            this.showMessage = true
          }
        })
      }
    },
    pageChange(value) {
      this.page = value
      this.getSecondList()
    },
    getSecondCommentText(value) {
      this.comment.content = value
    },
    setSort(sort) {
      this.sort = sort
      this.getSecondList()
    }
  }
}
</script>

<style >

</style>
