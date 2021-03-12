<template>
  <v-container fluid>
    <v-col />
    <v-row>
      <v-col cols="9">
        <List />
      </v-col>
      <v-col cols="3">
        <PostCard />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import List from '@/views/bbs/components/article-list.vue'
import PostCard from '@/views/bbs/components/index-post-card.vue'

export default {
  name: 'BBS',
  components: {
    List,
    PostCard
  },
  data() {
    return {
    }
  }
}
</script>

<style>

</style>
