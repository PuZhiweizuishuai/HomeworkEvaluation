<template>
  <v-card outlined>
    <v-card-title style="padding-bottom: 10px;">
      <v-btn
        icon
        link
        :to="`/user/${think.user.userId}`"
        large
      >
        <v-avatar
          size="48px"
          item
        >
          <v-img
            :src="think.user.userAvatarUrl"
            :alt="think.user.username"
            :title="think.user.username"
          />
        </v-avatar>
      </v-btn>
      <span v-html="'&nbsp;&nbsp;&nbsp;'" />

      <router-link class="think-card-href" :to="`/user/${think.user.userId}`">
        {{ think.user.username }}
      </router-link>

    </v-card-title>
    <v-card-subtitle>
      <span style="margin-left: 60px;">发布于：{{ TimeUtil.renderTime(think.createTime) }} </span>
    </v-card-subtitle>

    <v-card-subtitle>
      <v-row>
        <v-col style="padding-top: 0px;">
          <span style="margin-left: 60px;color:black;"> {{ think.content }} </span>
        </v-col>
      </v-row>
      <v-row justify="center">
        <!-- <v-col v-for="(item, i) in fileList" :key="i" cols="4">

          <v-img height="200" width="200" :src="item" />

        </v-col> -->
        <v-col cols="10">
          <viewer :images="fileList">
            <!-- <v-img height="200" width="200" :src="item" /> -->
            <img v-for="(item, i) in fileList" :key="i" class="show-tink-img" height="100" width="auto" :src="item">
          </viewer>
        </v-col>
      </v-row>
      <v-divider />
      <v-row>
        <v-col cols="4">
          <v-btn text block>
            <v-icon>
              mdi-arrange-bring-forward
            </v-icon>
            {{ think.forwardCount }}
          </v-btn>
        </v-col>
        <v-col cols="4">
          <v-btn text block @click="commentDialog = true">
            <v-icon>
              mdi-comment-text-outline
            </v-icon>
            {{ think.commentCount }}
          </v-btn>
        </v-col>
        <v-col cols="4">
          <v-btn text block>
            <v-icon>
              mdi-thumb-up-outline
            </v-icon>
            {{ think.likeCount }}
          </v-btn>
        </v-col>
      </v-row>
    </v-card-subtitle>
    <v-dialog
      v-model="commentDialog"
      max-width="800"
    >
      <v-card outlined>
        <Comment :artice="think" />
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import Comment from '@/components/comment/index.vue'

export default {
  components: {
    Comment
  },
  props: {
    think: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      TimeUtil,
      fileList: [],
      commentDialog: false
    }
  },
  created() {
    if (this.think.files != null) {
      this.fileList = JSON.parse(this.think.files)
    }
  }
}
</script>

<style>
.think-card-href {
  color: black;
  font-size: 18px;
}
.think-card-href:hover {
  color: #eb7350;
}
.show-tink-img {
  cursor: pointer;
  margin: 10px;
}
</style>
