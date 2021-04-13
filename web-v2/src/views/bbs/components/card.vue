<template>
  <!-- 社区页帖子列表卡片 margin-left: 24px; -->
  <v-card outlined>
    <v-card-subtitle>
      <v-row no-gutters>
        <v-col
          cols="12"
          sm="8"
          md="10"
        >
          <!-- 标题内容 -->
          <router-link :to="`/bbs/article/${item.id}`">
            <v-row>
              <v-col cols="12">

                <h3 style="color:black">
                  <v-icon v-if="item.type == 11">
                    mdi-comment-question
                  </v-icon>
                  <v-icon v-if="item.type == 12">
                    mdi-vote
                  </v-icon>
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
            <!-- 简介 -->
            <v-row>
              <v-col cols="12" style="padding-top: 0px; color: black;">
                {{ item.simpleContent }} ......
              </v-col>
            </v-row>
          </router-link>
          <v-row>
            <v-col style="padding-top: 0px;">
              <!-- <router-link :to="`/user/${item.authorId}`">{{ item.authorName }} </router-link> -->
              <!-- 悬停显示个人信息卡片 -->
              <v-menu
                v-if="showcard"
                open-on-hover
                top
                offset-y
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-btn
                    text
                    color="primary"
                    depressed
                    v-bind="attrs"
                    v-on="on"
                  >
                    {{ item.authorName }}
                  </v-btn>

                </template>
                <UserInfoCard :user="item.user" />
              </v-menu>
              <v-btn
                v-if="!showcard"
                text
                color="primary"
                depressed
                link
                :to="`/user/${item.authorId}`"
              >
                {{ item.authorName }}
              </v-btn>
              <span style="color:black">更新于：{{ TimeUtil.timeToNowStrning(item.updateTime) }}
                |
                {{ item.viewCount }} 浏览， {{ item.commentCount }} 回复，  {{ item.likeCount }} 喜欢</span>
            </v-col>
          </v-row>

        </v-col>
        <!-- 右边 -->
        <v-col
          cols="4"
          md="2"
        >
          <!-- 类型，话题 -->
          <v-row>
            <v-col>
              <v-btn
                rounded
                link
                :to="`/bbs/tags/${this.$store.state.tagMap.Get(item.tagId).id}`"
                depressed
                x-small
              >
                {{ this.$store.state.tagMap.Get(item.tagId).title }}
              </v-btn>
            </v-col>

          </v-row>
        </v-col>
      </v-row>
    </v-card-subtitle>
  </v-card>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import UserInfoCard from '@/components/user/info-card.vue'

export default {
  components: {
    UserInfoCard
  },
  props: {
    article: {
      type: Object,
      default: null
    },
    lastpage: {
      type: Number,
      default: 1
    },
    showcard: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      TimeUtil,
      item: this.article
    }
  },
  created() {

  },
  methods: {

  }
}
</script>

<style>

</style>
