<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12">
        <v-btn depressed color="success" @click="createCourseware">
          上传课件
        </v-btn>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-treeview
          hoverable
          activatable
          :items="coursewareList"
        >
          <template v-slot:prepend="{ item }">
            <router-link :to="`/course/learn/${item.courseId}/courseware/${item.id}`">
              {{ item.title }}
            </router-link>
            <v-chip
              v-if="item.status == 1"
              class="ma-2"
            >
              转码中
            </v-chip>
            <v-chip
              v-if="item.status == 2"
              class="ma-2"
              color="red"
              text-color="white"
            >
              转码失败，此课件将无法正常预览，需要下载查看
            </v-chip>
            <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />
            <v-btn small text color="primary" @click="edit(item)">编辑</v-btn>
            <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />
            <v-btn small text color="error" @click="deleteItem(item)">删除</v-btn>
          </template>
        </v-treeview>
      </v-col>
    </v-row>
    <v-dialog
      v-model="dialog"
    >

      <v-card outlined>
        <v-card-title>
          上传课件
        </v-card-title>

        <CoursewareForm :key="coursewareKey" :isedit="isEdit" :info="nowItem" @courseware="saveCourseware" />

      </v-card>
    </v-dialog>
    <!-- 删除文件确定框 -->
    <v-dialog
      v-model="deleteDialog"
      max-width="500"
    >
      <v-card>
        <v-card-title class="headline">
          敏感操作，请输入密码后继续
        </v-card-title>
        <v-card-text>注意：删除后将无法恢复，请谨慎操作！！！删除父级目录将连同删除子目录。

          <br>
          你确定要删除：{{ nowItem.title }}
        </v-card-text>
        <v-card-subtitle>
          <v-text-field v-model="password" label="密码" placeholder="请输入密码" type="password" />
        </v-card-subtitle>
        <v-card-actions>
          <v-btn
            color="green darken-1"
            text
            @click="deleteDialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="error darken-1"
            text
            @click="sendDelete"
          >
            确定
          </v-btn>
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
import CoursewareForm from '@/components/course/form/courseware-form.vue'
export default {
  name: 'Courseware',
  components: {
    CoursewareForm
  },
  data() {
    return {
      coursewareList: [],
      dialog: false,
      courseware: {},
      showMessage: false,
      message: '',
      isEdit: false,
      nowItem: {
        title: ''
      },
      coursewareKey: 0,
      deleteDialog: false,
      password: ''
    }
  },
  created() {
    this.getCoursewareList()
  },
  methods: {
    saveCourseware(value, isEdit) {
      let url = '/course/courseware/save'
      if (this.isEdit) {
        url = '/course/courseware/update'
      }
      this.courseware = value
      this.httpPost(url, this.courseware, (json) => {
        if (json.status === 200) {
          this.message = '创建成功'
          this.showMessage = true
          this.dialog = false
          this.getCoursewareList()
        } else {
          //
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    getCoursewareList() {
      this.httpGet(`/course/courseware/${this.$route.params.id}`, (json) => {
        if (json.status === 200) {
          this.coursewareList = json.data
        } else {
          //
        }
      })
    },
    edit(item) {
      //
      this.isEdit = true
      this.nowItem = item
      this.coursewareKey++
      this.dialog = true
    },
    createCourseware() {
      this.nowItem = {
        sort: 1,
        courseId: this.$route.params.id,
        title: '',
        text: '',
        level: 0,
        fileUrl: '',
        fileName: '',
        fatherId: 0
      }
      this.isEdit = false
      this.coursewareKey++
      this.dialog = true
    },
    deleteItem(item) {
      //

      this.nowItem = item
      this.deleteDialog = true
    },
    sendDelete() {
      this.nowItem.password = this.password
      this.httpPost('/course/courseware/delete', this.nowItem, (json) => {
        if (json.status === 200) {
          this.message = '删除成功'
          this.showMessage = true
          this.deleteDialog = false
          this.password = ''
          this.getCoursewareList()
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
