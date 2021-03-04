<template>
  <v-card>
    <v-card-title>
      去往原帖：
      <router-link v-if="comment.articleEntity.type == 1" :to="`/course/learn/${comment.articleEntity.courseId}/bbs/${comment.articleEntity.id}#${comment.commentModel.id}`">
        {{ comment.articleEntity.title }}
      </router-link>
      <router-link v-if="comment.articleEntity.type != 1" :to="`/bbs/article/${comment.articleEntity.id}#${comment.commentModel.id}`">
        {{ comment.articleEntity.title }}
      </router-link>
    </v-card-title>
    <v-card-text>
      <v-row justify="center">
        <v-col cols="11">
          <CommentCard
            :comment="comment.commentModel"
            :artice="comment.articleEntity"
            :showcomment="false"
          />
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="11">
          <h3> 回复评论: </h3>
        </v-col>
      </v-row>

      <v-row justify="center">
        <v-col cols="11">
          <!-- <v-textarea
          v-model="commentData.content"
          label="评论"
          placeholder="友善评论"
          solo
        /> -->
          <SecondCommentVditor
            ref="secondCommentView"
            :placeholder="commentPlaceholder"
            :uploadurl="uploadurl"
            @vditor-input="getSecondCommentText"
          />
        </v-col>
      </v-row>

      <v-row justify="center">
        <v-col cols="3">
          <img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" @click="getVerifyImage">
        </v-col>
        <v-col cols="3">
          <v-text-field
            v-model="commentData.verifyCode"
            placeholder="验证码"
            label="验证码"
            :rules="[() => commentData.verifyCode != null || '验证码不能为空']"
            clearable
          />
        </v-col>
        <v-col cols="4">
          <v-row justify="center">
            <v-btn depressed color="success" @click="submit">
              评论
            </v-btn>
          </v-row>
        </v-col>
      </v-row>
    </v-card-text>
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
  </v-card>
</template>

<script>
import CommentCard from '@/components/comment/card.vue'
import SecondCommentVditor from '@/components/vditor/comment.vue'

export default {
  name: 'NotificationCommentCard',
  components: {
    CommentCard,
    SecondCommentVditor
  },
  props: {
    comment: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      commentPlaceholder: '',
      uploadurl: this.SERVER_API_URL + '/upload/file',
      commentData: {
        articleId: this.comment.articleEntity.id,
        content: '',
        verifyCode: '',
        type: 1,
        commentId: this.comment.commentModel.id
      },
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      message: '',
      showMessage: false
    }
  },
  methods: {
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    },
    getSecondCommentText(data) {
      this.commentData.content = data
    },
    submit() {
      console.log(this.commentData)
      if (!this.$store.state.userInfo) {
        this.message = '请先登录后再评论！'
        this.showMessage = true
        return
      }
      if (this.commentData.content == null || this.commentData.content === '') {
        this.message = '评论内容不能为空！'
        this.showMessage = true
        return
      }
      if (this.commentData.verifyCode == null || this.commentData.verifyCode === '') {
        this.message = '验证码不能为空！'
        this.showMessage = true
        return
      }
      this.httpPost('/comment/save', this.commentData, (json) => {
        if (json.status === 200) {
          this.message = '评论成功！'
          this.showMessage = true
          this.commentData.content = ''
          this.commentData.verifyCode = ''
          this.$emit('read', true)
        } else {
          //
          this.message = json.message
          this.showMessage = true
        }
      })
    }
  }

}
</script>

<style>

</style>
