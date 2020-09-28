<template>
  <v-card>
    <v-card-title>
      {{ TimeUtil.renderTime(notificatio.createTime) }}
    </v-card-title>
    <v-card-text>
      <router-link :to="notificatio.url">
        {{ notificatio.text }}
      </router-link>
    </v-card-text>
    <v-row justify="end">
      <v-card-actions>
        <v-btn
          v-if="notificatio.status == 0"
          color="primary"
          text
        >
          标为已读
        </v-btn>
      </v-card-actions>
    </v-row>
  </v-card>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'

export default {
  name: 'NotificatioCard',
  props: {
    notificatio: {
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
