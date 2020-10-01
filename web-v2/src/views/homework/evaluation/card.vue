<template>
  <v-card outlined>
    <router-link :to="`/course/learn/${this.$route.params.id}/evaluation/homework/${info.homeworkId}/comment/${info.id}`">
      <v-row justify="center">
        <v-col cols="11">
          <router-link :to="`/user/${info.userId}`">
            <strong>{{ info.studentName }}</strong>
          </router-link>
          <span style="color:black;"> 同学，提交于： {{ TimeUtil.renderTime(info.submitTime) }}</span>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="11">
          <span style="color:black;">  当前共有 {{ info.commentCount }} 条评价， {{ info.likeCount }} 个喜欢</span>
        </v-col>
      </v-row>
    </router-link>
  </v-card>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
export default {
  props: {
    info: {
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
