<template>
  <v-row justify="center" align="center">
    <v-col>
      <v-card
        class="mx-auto"
        outlined
      >
        <v-col />
        <v-row justify="center">
          <v-col cols="10">
            <span><h2>登录历史</h2>如果发现不熟悉设备，请及时修改密码</span>
          </v-col>
        </v-row>
        <v-row justify="center">

          <v-col cols="12">
            <v-data-table
              :headers="headers"
              :items="logList"
              hide-default-footer
              :items-per-page="size"
              :page.sync="page"
            >
              <template v-slot:item.loginTime="{ item }">
                {{ TimeUtil.renderTime(item.loginTime) }}
              </template>

              <template v-slot:no-data>
                <v-btn color="primary" @click="getLog">重新加载</v-btn>
              </template>
            </v-data-table>
          </v-col>
        </v-row>
        <v-col />
        <v-row justify="center">
          <v-pagination
            v-model="page"
            :length="length"
            @input="pageChange"
          />
        </v-row>
        <v-col />
      </v-card>
    </v-col>
  </v-row>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
export default {
  name: 'LoginLog',
  data() {
    return {
      TimeUtil,
      userInfo: {},
      logList: [],
      size: 20,
      length: 1,
      page: 1,
      headers: [
        {
          text: '登录时间',
          sortable: false,
          value: 'loginTime'
        },
        { text: 'IP', sortable: false, value: 'loginIp' },
        { text: '地点', sortable: false, value: 'loginCity' },
        { text: '登录设备', sortable: false, value: 'loginUa' }
      ]
    }
  },
  created() {
    this.userInfo = this.$store.state.userInfo
    this.getLog()
  },
  methods: {
    getLog() {
      this.httpGet(`/login/log/list?page=${this.page}&limit=${this.size}`, (json) => {
        if (json.status === 200) {
          this.logList = json.data.list
          this.page = json.data.currPage
          this.length = json.data.totalPage
        }
      })
    },
    pageChange(value) {
      this.page = value
      this.getLog()
    }
  }
}
</script>

<style>

</style>
