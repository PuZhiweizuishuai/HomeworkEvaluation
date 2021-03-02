<template>
  <v-card outlined>
    <v-card-title>
      {{ TimeUtil.renderTime(notificatio.createTime) }}
    </v-card-title>
    <!-- 非评论通知 -->
    <v-card-text v-if="notificatio.commentId == null">
      <router-link :to="notificatio.url">
        {{ notificatio.text }}
      </router-link>
    </v-card-text>
    <v-card-text v-if="notificatio.type == 8">

      你在 <router-link :to="notificatio.url">《 {{ notificatio.text }} 》</router-link>提交的答案，有了新的评价。 {{ notificatio.commentContent }}

    </v-card-text>
    <!-- 评论通知 -->
    <v-card-text v-if="NotificatioUtil.isComment(notificatio)">
      <router-link :to="`/user/${notificatio.notifier}`">{{ notificatio.notifierName }} </router-link>
      在帖子
      <span>《{{ notificatio.text }}》 </span>
      下回复了你:
      {{ notificatio.commentContent }}

    </v-card-text>
    <v-col>
      <v-row justify="end">
        <v-card-actions>
          <v-btn v-if="NotificatioUtil.isComment(notificatio)" depressed small color="primary" @click="getComment">
            查看
          </v-btn>
          <v-btn
            v-if="notificatio.status == 0"
            color="primary"
            text
            @click="readMessage"
          >
            标为已读
          </v-btn>
        </v-card-actions>
      </v-row>
    </v-col>
    <v-dialog
      v-model="dialog"
      max-width="1000"
    >
      <CommentCard :comment="comment" @read="readMessage" />
    </v-dialog>
  </v-card>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import NotificatioUtil from '@/utils/notificatio-util.vue'
import CommentCard from '@/views/notification/comment/card.vue'

export default {
  name: 'NotificatioCard',
  components: {
    CommentCard
  },
  props: {
    notificatio: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      TimeUtil,
      NotificatioUtil,
      dialog: false,
      comment: {}
    }
  },
  methods: {
    getComment() {
      this.httpGet(`/comment/reply/${this.notificatio.commentId}`, (json) => {
        if (json.status === 200) {
          this.comment = json.data
          this.dialog = true
        } else {
          //
        }
      })
    },
    readMessage(value) {
      this.httpGet(`/notification/read?id=${this.notificatio.id}`, (json) => {
        this.$emit('read', true)
        this.dialog = false
        if (json.status === 200) {
          this.message = '设置成功！'
          this.showMessage = true
          this.dialog = false
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

</style>
