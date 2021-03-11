<template>
  <v-container>
    <v-row>
      <CommentList v-if="show" :score="courseScore" :deletebtn="true" />
    </v-row>
  </v-container>
</template>

<script>
import CommentList from '@/components/comment/course/list.vue'
export default {
  components: {
    CommentList
  },
  data() {
    return {
      courseScore: 0,
      show: false,
      id: this.$route.params.id
    }
  },
  created() {
    this.httpGet(`/curriculum/info/${this.id}`, (json) => {
      this.courseScore = json.data.score / json.data.ratingUserNumber
      this.show = true
    })
  }
}
</script>

<style>

</style>
