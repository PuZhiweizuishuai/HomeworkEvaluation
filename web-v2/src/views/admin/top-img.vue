<template>
  <v-container fluid>
    <v-row>
      <v-col>
        <h3>主页顶部轮播图设置</h3>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn depressed color="primary" @click="createNew">新建</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-data-table :items="topImgList" :headers="headers" hide-default-footer>
          <template v-slot:item.createTime="{ item }">
            {{ TimeUtil.renderTime(item.createTime) }}
          </template>
          <template v-slot:item.startTime="{ item }">
            {{ TimeUtil.renderTime(item.startTime) }}
          </template>
          <template v-slot:item.endTime="{ item }">
            {{ TimeUtil.renderTime(item.endTime) }}
          </template>
          <template v-slot:item.type="{ item }">
            <span v-if="item.type === 1" v-text="'主页'" />
            <span v-if="item.type === 0" v-text="'社区'" />
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
              <span>编辑</span>
            </v-tooltip>
            <v-tooltip left>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  icon
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="stop(item)"
                >
                  <v-icon>mdi-close</v-icon>
                </v-btn>

              </template>
              <span>结束投放</span>
            </v-tooltip>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-pagination
        v-model="page"
        :length="length"
        @input="pageChange"
      />
    </v-row>
    <v-dialog v-model="dialog" max-width="1000">
      <v-card>
        <v-card-title> {{ cardTitle }} </v-card-title>
        <v-card-text>
          <TopImgForm :key="flushTopImgKey" :type="editType" :info="editAd" @success="success" />
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-dialog v-model="closeDialog" max-width="1000">
      <v-card>
        <v-card-title>你确定要结束这条广告： {{ closeData.title }} </v-card-title>
        <v-card-actions>
          <v-btn depressed @click="closeDialog = false">取消</v-btn>
          <v-btn depressed color="success" @click="sendClose">确认</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
import TimeUtil from '@/utils/time-util.vue'
import TopImgForm from '@/components/form/top-img-form.vue'
/**
  2020/10/06 计划，测试xss，使用xss后，会不会对页面产生影响
 */
export default {
  components: {
    TopImgForm
  },
  data() {
    return {
      dialog: false,
      TimeUtil,
      cardTitle: '新建',
      topImgList: [],
      headers: [
        {
          text: 'ID',
          sortable: false,
          value: 'id'
        },
        { text: '标题', sortable: false, value: 'title' },
        { text: '创建人', sortable: false, value: 'createUser' },
        { text: '创建时间', sortable: false, value: 'createTime' },
        { text: '开始时间', sortable: false, value: 'startTime' },
        { text: '结束时间', sortable: false, value: 'endTime' },
        { text: '位置', sortable: false, value: 'type' },
        { text: '点击量', sortable: false, value: 'viewCount' },
        { text: '操作', value: 'actions', sortable: false }
      ],
      page: 1,
      size: 20,
      length: 1,
      count: 0,
      editAd: {},
      editType: 0,
      flushTopImgKey: 0,
      closeData: {},
      closeDialog: false,
      message: '',
      showMessage: false
    }
  },
  created() {
    this.getTopImg()
  },
  methods: {
    success() {
      this.getTopImg()
      this.dialog = false
    },
    getTopImg() {
      this.httpGet(`/admin/topImg/list?page=${this.page}&limit=${this.size}`, (json) => {
        this.count = json.data.totalCount
        this.length = json.data.totalPage
        this.topImgList = json.data.list
      })
    },
    pageChange(value) {
      this.page = value
      this.getTopImg()
    },
    edit(item) {
      this.editAd = item
      this.editType = 1
      this.cardTitle = '修改'
      this.flushTopImgKey++
      this.dialog = true
    },
    createNew() {
      this.editAd = {
        id: -1,
        title: '',
        url: '',
        image: '',
        startTime: '',
        endTime: '',
        type: 1
      }
      this.editType = 0
      this.flushTopImgKey++
      this.cardTitle = '新建'
      this.dialog = true
    },
    stop(item) {
      this.closeData = item
      this.closeDialog = true
    },
    sendClose() {
      this.httpPost(`/admin/topImg/stop`, this.closeData.id, (json) => {
        if (json.status === 200) {
          this.message = '结束成功'
          this.showMessage = true
          this.getTopImg()
          this.closeDialog = false
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
