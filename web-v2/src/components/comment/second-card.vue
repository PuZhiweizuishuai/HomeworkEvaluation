<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" style="padding-top: 0px;">
        <router-link :to="`/user/${comment.authorId}`">
          <v-avatar size="32" style="float: left;">
            <v-img :src="comment.avatarUrl" />
          </v-avatar>
        </router-link>
        <p style="margin-left: 60px;">
          <router-link :to="`/user/${comment.authorId}`">
            {{ comment.username }}
          </router-link>
          <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />
          {{ TimeUtil.renderTime(comment.createTime) }}
        </p>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="12">
        <span v-if="comment.fatherId != comment.commentId">
          回复<router-link :to="`/user/${comment.replyUserId}`" target="_blank">@ {{ comment.replyUserName }} :</router-link>
        </span>
        {{ comment.content }}
      </v-col>
    </v-row>
    <v-row justify="end">
      <v-btn
        color="indigo"
        icon
        small
        @click="setComment"
      >
        <v-icon>mdi-comment</v-icon>
      </v-btn>
      {{ comment.commentCount }}

      <span v-html="`&nbsp;&nbsp;`" />
      <span v-html="`&nbsp;&nbsp;`" />

      <v-btn
        color="primary"
        icon

        small
      >
        <v-icon>mdi-thumb-up</v-icon>
      </v-btn>
      {{ comment.likeCount }}

      <span v-html="`&nbsp;&nbsp;`" />
      <span v-html="`&nbsp;&nbsp;`" />
      <v-btn
        icon

        small
      >
        <v-icon>mdi-thumb-down</v-icon>
      </v-btn>
      {{ comment.badCount }}

      <span v-html="`&nbsp;&nbsp;`" />
      <span v-html="`&nbsp;&nbsp;`" />
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
  </v-container>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'

export default {
  props: {
    comment: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      TimeUtil
    }
  },
  created() {

  },
  methods: {
    setComment() {
      this.$emit('comment', this.comment)
    }
  }
}
</script>

<style>

</style>
