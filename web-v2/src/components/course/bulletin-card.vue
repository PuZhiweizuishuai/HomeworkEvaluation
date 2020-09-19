<template>
  <v-card outlined>
    <v-card-title>{{ bulletin.title }}</v-card-title>
    <v-card-subtitle>发布时间：{{ TimeUtil.renderTime(bulletin.createTime) }} </v-card-subtitle>
    <v-row justify="center">
      <v-col cols="11">
        <ShowMarkdown :markdown="bulletin.text" :anchor="0" />
      </v-col>
    </v-row>
  </v-card>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
export default {
  name: 'BulletinCard',
  components: {
    ShowMarkdown
  },
  props: {
    bulletin: {
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
