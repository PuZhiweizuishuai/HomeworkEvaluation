<template>
  <v-container>
    <v-row>
      <v-col>
        <h3>公告列表：</h3>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn class="mx-2" dark color="indigo" @click="post">
          <v-icon dark>mdi-plus</v-icon>
          发布新的公告
        </v-btn>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="12">
        <v-data-table
          :headers="headers"
          :items="bulletinList"
          hide-default-footer
          :items-per-page="size"
          :page.sync="page"
        >
          <template v-slot:item.userId="{ item }">
            <a :href="`/user/${item.userId}`" target="_blank"> {{ item.userId }} </a>
          </template>
          <template v-slot:item.createTime="{ item }">
            {{ TimeUtil.renderTime(item.createTime) }}
          </template>
          <template v-slot:item.updateTime="{ item }">
            {{ TimeUtil.renderTime(item.updateTime) }}
          </template>
          <template v-slot:item.status="{ item }">
            {{ getStatus(item.status) }}
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn small dark color="cyan" @click="edit(item)">
              <v-icon dark>mdi-pencil</v-icon>
            </v-btn>
          </template>

          <template v-slot:no-data>
            <v-btn color="primary" @click="getBulletinList">重新加载</v-btn>
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
    <v-row justify="center">
      <v-dialog v-model="dialog" fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card>
          <v-toolbar dark color="primary">
            <v-btn icon dark @click="dialog = false">
              <v-icon>mdi-close</v-icon>
            </v-btn>
            <v-toolbar-title>{{ editTitle }}</v-toolbar-title>
            <v-spacer />
            <v-toolbar-items>
              <v-btn dark text @click="save">保存</v-btn>
            </v-toolbar-items>
          </v-toolbar>
          <v-row justify="center">
            <v-col cols="10">
              <v-text-field
                v-model="bulletinData.title"
                label="标题"
                placeholder="标题"
                :rules="[() => bulletinData.title != null || '标题不能为空！']"
                clearable
              />
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="10">
              <Vditor
                ref="bulletinText"
                :placeholder="'详细的介绍，可以包含图片，视频，文件等'"
                :uploadurl="uploadurl"
                :markdown="bulletinData.text"
                @vditor-input="setVditorInput"
                @after="setText"
              />
            </v-col>
          </v-row>
        </v-card>
      </v-dialog>
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
import TimeUtil from '@/utils/time-util.vue'
import Vditor from '@/components/vditor/vditor.vue'
export default {
  name: 'BulletinTable',
  components: {
    Vditor
  },
  data() {
    return {
      TimeUtil,
      headers: [
        {
          text: '公告ID',
          sortable: false,
          value: 'id'
        },
        { text: '标题', sortable: false, value: 'title' },
        { text: '发布的用户ID', sortable: false, value: 'userId' },
        { text: '发布时间', sortable: false, value: 'createTime' },
        { text: '更新时间', sortable: false, value: 'updateTime' },
        { text: '状态', sortable: false, value: 'status' },
        { text: '操作', value: 'actions', sortable: false }
      ],
      bulletinList: [],
      page: 1,
      size: 20,
      length: 1,
      id: 0,
      dialog: false,
      uploadurl: this.SERVER_API_URL + '/upload/file',
      bulletinData: {
        title: '',
        text: '',
        curriculumId: this.$route.params.id
      },
      editTitle: '发布',
      vditorIsShow: false,
      isEdit: false,
      message: '',
      showMessage: false
    }
  },
  created() {
    this.id = this.$route.params.id
    this.getBulletinList()
  },
  methods: {
    getStatus(status) {
      if (status === 0) {
        return '正常'
      } else {
        return '删除（不显示）'
      }
    },
    getBulletinList() {
      this.httpGet(`/bulletin/list/${this.id}?page=${this.page}&limit=${this.size}`, (json) => {
        if (json.status === 200) {
          this.bulletinList = json.data.list
          this.length = json.data.totalPage
        } else {
          // console.log(json.message)
        }
      })
    },
    post() {
      this.editTitle = '发布新公告'
      this.bulletinData = {
        title: '',
        text: '',
        curriculumId: this.id
      }
      this.dialog = true
      if (this.vditorIsShow) {
        this.$refs.bulletinText.setTextValue(this.bulletinData.text)
      }
      this.isEdit = false
    },
    edit(value) {
      this.bulletinData = value
      this.dialog = true
      if (this.vditorIsShow) {
        this.$refs.bulletinText.setTextValue(this.bulletinData.text)
      }
      this.editTitle = '修改'
      this.isEdit = true
    },
    setText(value) {
      this.vditorIsShow = true
    },
    pageChange(value) {
      this.page = value
      this.getBulletinList()
    },
    setVditorInput(value) {
      this.bulletinData.text = value
    },
    save() {
      let url = '/bulletin/post'
      if (this.isEdit) {
        url = '/bulletin/update'
      }
      if (this.bulletinData.text === null || this.bulletinData.title === '') {
        this.message = '公告内容不能为空'
        this.showMessage = true
        return
      }
      if (this.bulletinData.title === null || this.bulletinData.title === '') {
        this.message = '公告标题不能为空'
        this.showMessage = true
        return
      }
      this.httpPost(url, this.bulletinData, (json) => {
        if (json.status === 200) {
          this.message = '发布成功'
          this.showMessage = true
          this.dialog = false
          this.getBulletinList()
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
