<template>
  <v-container>
    <v-row>
      <v-col>
        <v-btn depressed color="primary" @click="createTag">新建节点</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-divider />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-treeview
          hoverable
          activatable
          :items="tagList"
        >
          <template v-slot:prepend="{ item }">
            {{ item.courseMajor }}
            <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />
            <v-btn small text color="primary" @click="edit(item)">编辑</v-btn>
            <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />
            <v-btn small text color="error" @click="deleteItem(item)">删除</v-btn>
          </template>

        </v-treeview>
      </v-col>
    </v-row>

    <v-dialog v-model="dialog" max-width="1000">
      <v-card>
        <v-card-title>
          {{ title }}
        </v-card-title>
        <v-card-text>
          <v-row>
            <v-col>
              <v-text-field
                v-model="editTag.courseMajor"
                placeholder="分类名"
                label="分类名"
                :rules="[() => editTag.courseMajor != null || '分类名不能为空']"
                clearable
              />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-text-field
                v-model="editTag.sort"
                placeholder="排序"
                label="排序（越小越靠前，默认0）"
                type="number"
                clearable
              />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-text-field
                v-model="editTag.descript"
                placeholder="简介"
                label="简介（50字以内）"
                clearable
              />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              图标地址：<a href="https://materialdesignicons.com/" target="_blank">https://materialdesignicons.com/</a>
              格式为：mdi-  图标名， 如：mdi-tag 就是 <v-icon>mdi-tag</v-icon>
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-text-field
                v-model="editTag.icon"
                placeholder="图标"
                label="图标"
                clearable
              />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-select
                v-model="editTag.catelogId"
                :items="tagList"
                label="父分类，如果自身是一级分类，则不用填！"
                item-text="courseMajor"
                item-value="id"
                clearable
              />
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-btn depressed color="success" @click="submit">{{ btnName }}</v-btn>
          </v-row>
        </v-card-text>
      </v-card>
    </v-dialog>
    <v-dialog v-model="deleteDialog" max-width="500">
      <v-card>
        <v-card-title>
          警告：删除后不可恢复，并且如果你删除的是父分类，其下的子分类也会被删除！
          请谨慎操作，另外此操作可能会影响到该分类下课程的查找，你确定要操作吗！
        </v-card-title>
        <v-card-text>
          <v-col cols="5">
            <img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" @click="getVerifyImage">
          </v-col>
          <v-col cols="5">
            <v-text-field
              v-model="verifyCodeVo.verifyCode"
              placeholder="验证码"
              label="验证码"
              :rules="[() => verifyCodeVo.verifyCode != null || '验证码不能为空']"
              clearable
            />
          </v-col>
        </v-card-text>
        <v-card-actions>
          <v-btn depressed color="primary" @click="sendDelete">删除</v-btn>
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
export default {
  data() {
    return {

      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      tagList: [],
      editTag: {},
      dialog: false,
      title: '编辑分类',
      btnName: '编辑',
      message: '',
      showMessage: false,
      deleteDialog: false,
      deleteData: {},
      verifyCodeVo: {
        verifyCode: ''
      }
    }
  },
  created() {
    this.getTag()
  },
  methods: {
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    },
    deleteItem(item) {
      this.deleteData = item
      this.deleteDialog = true
    },
    sendDelete() {
      this.httpPost(`/course/tag/delete/${this.deleteData.id}`, this.verifyCodeVo, (json) => {
        if (json.status === 200) {
          this.message = '删除成功'
          this.showMessage = true
          this.getTag()
          this.deleteDialog = false
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    getTag() {
      this.httpGet('/course/tag/list', (json) => {
        if (json.status === 200) {
          this.tagList = json.data
        }
      })
    },
    edit(item) {
      this.title = '编辑分类'
      this.btnName = '编辑'
      this.editTag = item
      this.dialog = true
    },
    createTag() {
      this.title = '创建分类'
      this.btnName = '创建'
      this.editTag = {
        courseMajor: '',
        sort: 0,
        descript: '',
        icon: '',
        catelogId: 0
      }
      this.dialog = true
    },
    submit() {
      if (this.editTag.courseMajor === '' || this.editTag.courseMajor == null) {
        this.message = '分类名不能为空'
        this.showMessage = true
        return
      }
      if (this.editTag.catelogId == null) {
        this.editTag.catelogId = 0
      }
      if (this.btnName === '创建') {
        this.httpPost(`/course/tag/save`, this.editTag, (json) => {
          if (json.status === 200) {
            this.message = '创建成功'
            this.showMessage = true
            this.dialog = false
            this.getTag()
          } else {
            this.message = json.message
            this.showMessage = true
          }
        })
      } else {
        this.httpPost(`/course/tag/update`, this.editTag, (json) => {
          if (json.status === 200) {
            this.message = '修改成功'
            this.showMessage = true
            this.dialog = false
            this.getTag()
          } else {
            this.message = json.message
            this.showMessage = true
          }
        })
      }
    }
  }
}
</script>

<style>

</style>
