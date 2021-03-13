<template>
  <v-container>
    <v-row>
      <v-col>
        <h3>草稿箱：</h3>
      </v-col>
    </v-row>
    <List :type="-1" />
  </v-container>
</template>

<script>
import List from '@/views/bbs/components/article-list.vue'
export default {
  components: {
    List
  },
  data() {
    return {

    }
  }
}
</script>

<style>

</style>
