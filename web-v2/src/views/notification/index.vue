<template>
  <v-container>
    <v-row>
      <v-col>
        <v-tabs>
          <v-tab @click="setType(0)">课程通知</v-tab>
          <v-tab @click="setType(1)">论坛通知</v-tab>
          <v-tab @click="setType(2)">私信</v-tab>
          <v-tab @click="setType(3)">系统通知</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-col>
      <v-row>
        <v-divider />
      </v-row>
    </v-col>
    <v-row>
      <v-col>
        <v-btn color="primary" depressed @click="readAll">
          <v-icon>mdi-notification-clear-all</v-icon>
          全部标记为已读
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <v-row v-for="item in notification" :key="item.id" justify="center">
      <v-col cols="12">
        <NotificatioCard :notificatio="item" @read="getNotification" />
      </v-col>
    </v-row>
    <v-row v-if="notification.length == 0" justify="center">
      <v-col cols="12">
        <v-row justify="center">
          <h3>暂无消息</h3>
        </v-row>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-pagination
        v-model="page"
        :length="length"
        @input="pageChange"
      />
    </v-row>
    <v-snackbar
      v-model="showMessage"
      :top="true"
      :timeout="3000"
    >
      {{ message }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="pink"
          text
          v-bind="attrs"
          @click="showMessage = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import NotificatioCard from '@/views/notification/card.vue'

/**
 * 显示全部消息通知
 */
export default {
  components: {
    NotificatioCard
  },
  data() {
    return {
      notification: [],
      total: 0,
      size: 20,
      length: 0,
      page: 1,
      type: 0,
      message: '',
      showMessage: false
    }
  },
  created() {
    this.getNotification()
  },
  methods: {
    getNotification() {
      this.httpGet(`/notification/list?page=${this.page}&limit=${this.size}&type=${this.type}`, (json) => {
        if (json.status === 200) {
          this.notification = json.data.list
          this.total = json.data.totalCount
          this.length = json.data.totalPage
        }
      })
    },
    setType(value) {
      this.type = value
      this.page = 1
      this.getNotification()
    },
    pageChange(value) {
      this.page = value
      this.getNotification()
    },
    readAll() {
      this.httpPost('/notification/readAll', null, (json) => {
        if (json.status === 200) {
          this.message = '设置成功！'
          this.showMessage = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    }
  }

}
</script>

<style>

</style>
