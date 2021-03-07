<template>
  <v-card outlined>
    <v-card-title>
      <router-link :to="`/user/${comment.authorId}`" target="_blank">
        {{ comment.authorName }}
      </router-link>
      <v-rating
        v-model="comment.courseRating"
        size="16"
        color="orange"
        half-increments
        background-color="orange lighten-3"
        :readonly="true"
      />
    </v-card-title>
    <v-card-text>
      <v-row>
        <v-col>
          <ShowMarkdown :markdown="comment.content" :anchor="0" />
        </v-col>
      </v-row>
    </v-card-text>
    <v-card-subtitle>
      发布于：{{ TimeUtil.formateTimeToChinese(comment.createTime) }}
      <span style="float: right">
        <span v-html="`&nbsp;&nbsp;`" />
        <span v-html="`&nbsp;&nbsp;`" />
        <v-tooltip bottom>
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              color="primary"
              icon
              v-bind="attrs"
              small
              v-on="on"
            >
              <v-icon>mdi-thumb-up</v-icon>
            </v-btn>
          </template>
          <span>赞</span>
        </v-tooltip>
        {{ comment.likeCount }}
        <span v-html="`&nbsp;&nbsp;`" />

        <span v-html="`&nbsp;&nbsp;`" />
        <v-tooltip bottom>
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              icon
              v-bind="attrs"
              small
              v-on="on"
            >
              <v-icon>mdi-thumb-down</v-icon>
            </v-btn>
          </template>
          <span>踩</span>
        </v-tooltip>
        {{ comment.badCount }}
      </span>
    </v-card-subtitle>
  </v-card>
</template>

<script>
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
import TimeUtil from '@/utils/time-util.vue'

export default {
  components: {
    ShowMarkdown
  },
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
  }
}
</script>

<style>

</style>
