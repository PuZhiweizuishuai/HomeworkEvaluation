<template>
  <!-- 社区页帖子列表卡片  -->
  <v-card outlined>
    <v-col />
    <router-link :to="`/bbs/${item.id}`">
      <v-row justify="center">

        <v-col cols="11">
          <router-link :to="`/user/${item.authorId}`">
            <v-avatar size="62" style="float: left;">
              <v-img :src="item.user.userAvatarUrl" />
            </v-avatar>
          </router-link>
          <v-row>
            <v-col cols="12" style="padding-top: 0px;">
              <h3 style="color:black">
                {{ item.title }}
                <v-chip
                  v-if="item.perfect == 1"
                  class="ma-2"
                  color="orange"
                  small
                  text-color="white"
                >
                  精品
                </v-chip>
              </h3>
            </v-col>
          </v-row>
          <v-row>
            {{ item.simpleContent }} ......
          </v-row>
          <v-row>
            <v-col style="padding-top: 0px;">
              <router-link :to="`/user/${item.authorId}`">{{ item.authorName }} </router-link>
              <span style="color:black">发布于：{{ TimeUtil.renderTime(item.createTime) }}
                |
                {{ item.viewCount }} 浏览， {{ item.commentCount }} 回复，  {{ item.likeCount }} 喜欢</span>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
    </router-link>
    <v-col />
  </v-card>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'

export default {
  props: {
    article: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      TimeUtil,
      item: this.article
    }
  }
}
</script>

<style>

</style>
