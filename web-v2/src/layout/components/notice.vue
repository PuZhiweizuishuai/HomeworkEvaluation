<template>
  <v-menu top offset-y :close-on-content-click="false" :max-height="600" style="background-color: white">
    <template v-slot:activator="{ on, attrs }">
      <v-btn
        dark
        v-bind="attrs"
        icon
        large
        v-on="on"
        @click="initNotification"
      >
        <v-badge
          v-if="total != 0 || total2 != 0 || total3 != 0 || total4 != 0"
          dot
          color="red"
        >
          <v-icon>mdi-bell</v-icon>
        </v-badge>
        <v-icon v-else>mdi-bell</v-icon>
      </v-btn>
    </template>
    <v-card :max-width="400">
      <!-- 头 -->
      <v-col>
        <v-row>
          <v-col>
            <v-tabs>
              <v-tab @click="setType(0)">
                <v-badge
                  v-if="total != 0"
                  color="green"
                  :content="total"
                >
                  课程通知
                </v-badge>
                <span v-if="total == 0">课程通知</span>
              </v-tab>
              <v-tab @click="setType(1)">
                <v-badge
                  v-if="total2 != 0"
                  color="green"
                  :content="total2"
                >
                  论坛通知
                </v-badge>
                <span v-if="total2 == 0">论坛通知</span>
              </v-tab>
              <v-tab @click="setType(2)">
                <v-badge
                  v-if="total3 != 0"
                  color="green"
                  :content="total3"
                >
                  私信
                </v-badge>
                <span v-if="total3 == 0">私信</span>
              </v-tab>
              <v-tab @click="setType(3)">
                <v-badge
                  v-if="total4 != 0"
                  color="green"
                  :content="total4"
                >
                  系统通知
                </v-badge>
                <span v-if="total4 == 0">系统通知</span>
              </v-tab>
            </v-tabs>
          </v-col>
        </v-row>
      </v-col>

      <!-- 上下分割线 -->
      <v-col>
        <v-row>
          <v-divider />
        </v-row>
      </v-col>

      <!-- 一件已读 -->
      <v-col>
        <v-row justify="center">
          <v-col cols="11">
            <v-row justify="end">
              <v-btn text color="primary" @click="readAll">
                <v-icon>mdi-notification-clear-all</v-icon>
                一键已读
              </v-btn>
            </v-row>
          </v-col>
        </v-row>
      </v-col>

      <!-- 上下分割线 -->
      <v-col>
        <v-row>
          <v-divider />
        </v-row>
      </v-col>

      <!-- 显示通知 -->

      <!-- <v-card height="500" outlined> -->

      <v-col>
        <v-row v-for="item in notification" :key="item.id" justify="center">
          <v-col style="padding-top: 0px;padding-bottom: 0px;">
            <NotificationCard :notificatio="item" @read="readMessage" />
          </v-col>
        </v-row>
      </v-col>
      <!--  -->
      <v-col>
        <v-row v-if="notification.length === 0">
          <v-col cols="11">
            <v-row justify="center">
              暂时没有新通知
            </v-row>
          </v-col>
        </v-row>
      </v-col>
      <!-- 底部按钮 -->

      <v-col>
        <v-row justify="center">
          <v-col cols="11">
            <v-row justify="end">
              <v-btn text color="success" @click="goTotNotification">
                更多通知
              </v-btn>
            </v-row>
          </v-col>
        </v-row>
      </v-col>
      <!-- </v-card> -->

    </v-card>
  </v-menu>
</template>

<script>
import NotificationCard from '@/views/notification/card.vue'

export default {
  name: 'Notice',
  components: {
    NotificationCard
  },
  data() {
    return {
      type: 0,
      notification: [],
      notification1: [],
      notification2: [],
      notification3: [],
      notification4: [],
      total: 0,
      total2: 0,
      total3: 0,
      total4: 0,
      page: 1,
      size: 20,
      length: 0
    }
  },
  created() {
    this.getNotification(3)
    this.getNotification(2)
    this.getNotification(1)
    this.getNotification(0)
  },
  methods: {
    getNotification(type) {
      this.httpGet(`/notification/list?page=${this.page}&limit=${this.size}&type=${type}&status=0`, (json) => {
        if (json.status === 200) {
          if (type === 0) {
            this.notification1 = json.data.list
            this.total = json.data.totalCount
          } else if (type === 1) {
            this.notification2 = json.data.list
            this.total2 = json.data.totalCount
          } else if (type === 2) {
            this.notification3 = json.data.list
            this.total3 = json.data.totalCount
          } else if (type === 3) {
            this.notification4 = json.data.list
            this.total4 = json.data.totalCount
          }
          this.notification = json.data.list
        }
      })
    },
    setType(value) {
      this.type = value
      this.getNotification(value)
    },
    readMessage(value) {
      console.log(this.type)
      this.getNotification(this.type)
    },
    read(value) {
      this.httpGet(`/notification/read?id=${value.id}`, (json) => {
        this.$router.push(value.url)
      })
    },
    initNotification() {
      this.notification = this.notification1
    },
    goTotNotification() {
      if (this.$route.path === '/notification') {
        return
      }
      this.$router.push('/notification')
    },
    readAll() {
      this.httpPost('/notification/readAll', null, (json) => {
        this.notification = []
        this.total = 0
      })
    }
  }
}
</script>

<style>

</style>
