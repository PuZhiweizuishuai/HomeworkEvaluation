<template>
  <v-container>
    <v-row>
      <v-col>
        <v-data-table
          :headers="headers"
          :items="inviteCodeList"
          hide-default-footer
          :items-per-page="size"
          :page.sync="page"
        >
          <template v-slot:item.expireTime="{ item }">
            {{ TimeUtil.renderTime(item.expireTime) }}
          </template>

          <template v-slot:item.createTime="{ item }">
            {{ TimeUtil.renderTime(item.createTime) }}
          </template>

          <template v-slot:item.status="{ item }">
            <span v-if="item.status == 0">
              <v-chip
                small
                color="green"
                text-color="white"
              >
                可用
              </v-chip>
            </span>
            <span v-if="item.status == 1">
              <v-chip
                small
              >
                不可用
              </v-chip>
            </span>
          </template>
          <template v-slot:item.type="{ item }">
            <span v-if="item.type == 0">
              <v-chip
                small
                color="primary"
                text-color="white"
              >
                课程邀请码
              </v-chip>
            </span>
            <span v-if="item.type == 1">
              <v-chip
                small
                color="secondary"
                text-color="white"
              >
                用户邀请码
              </v-chip>
            </span>
          </template>
          <template v-slot:item.actions="{ item }">
            <v-tooltip left>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  icon
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="edit(item)"
                >
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>

              </template>
              <span>查看使用情况</span>
            </v-tooltip>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-pagination
        v-if="count > size"
        v-model="page"
        :length="length"
        @input="pageChange"
      />
    </v-row>
    <v-dialog v-model="dialog" max-width="1000">
      <v-card>
        <v-card-text>
          <v-row v-for="item in logList" :key="item.id" justify="center">
            <v-col cols="11">
              使用人：<router-link :to="`/user/${item.studentId}`">{{ item.studentId }}</router-link>
              使用时间：   {{ TimeUtil.renderTime(item.useTime) }}
            </v-col>
          </v-row>
          <v-pagination
            v-if="logCount > size"
            v-model="logPage"
            :length="logLength"
            @input="pageLogChange"
          />
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
export default {
  data() {
    return {
      TimeUtil,
      headers: [
        {
          text: 'ID',
          sortable: false,
          value: 'id'
        },
        { text: '邀请码', sortable: false, value: 'code' },
        { text: '生成人', sortable: false, value: 'generatorId' },
        { text: '创建时间', sortable: false, value: 'createTime' },
        { text: '过期时间', sortable: false, value: 'expireTime' },
        { text: '剩余可使用次数', sortable: false, value: 'useCount' },
        { text: '状态', sortable: false, value: 'status' },
        { text: '类型', sortable: false, value: 'type' },
        { text: '操作', value: 'actions', sortable: false }
      ],
      inviteCodeList: [],
      size: 20,
      page: 1,
      length: 1,
      count: 0,
      dialog: false,
      logList: [],
      logPage: 1,
      logLength: 1,
      logCount: 0,
      code: null
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.httpGet(`/invite/code/list?page=${this.page}&size=${this.size}&course=${this.$route.params.id}`, (json) => {
        this.inviteCodeList = json.data.list
        this.length = json.data.totalPage
        this.count = json.data.totalCount
      })
    },
    pageChange(value) {
      this.page = value
      this.getList()
    },
    edit(item) {
      this.dialog = true
      this.code = item
      this.getLog()
    },
    pageLogChange(value) {
      this.logPage = value
      this.getLog()
    },
    getLog() {
      this.httpGet(`/invite/code/log?page=${this.logPage}&size=${this.size}&code=${this.code.id}`, (json) => {
        this.logList = json.data.list
        this.logLength = json.data.totalPage
        this.logCount = json.data.totalCount
      })
    }
  }
}
</script>

<style>

</style>
