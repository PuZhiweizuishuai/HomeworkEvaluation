<template>
  <v-card>
    <v-card-title>
      写想法
    </v-card-title>
    <v-card-subtitle>
      分享你此刻的想法
    </v-card-subtitle>
    <v-card-subtitle>
      <v-row>
        <v-col>
          <v-divider />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-textarea

            v-model="article.content"
            label="想法"
            hint="想法"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <!-- :rules="rules" , video/*-->
          <v-file-input
            multiple
            accept="image/*"
            placeholder="选择图片"
            prepend-icon="mdi-camera"
            label="选择图片(最大不超过20MB)"
            @change="setFile"
          >
            <template v-slot:selection="{index, text }">
              <span v-if="index < 0">{{ text }}</span>
            </template>
          </v-file-input>
        </v-col>
      </v-row>
      <!-- 显示图片 -->
      <v-row>
        <v-col v-for="(item, i) in filesShowUrl" :key="i" cols="3">
          <v-card outlined>
            <v-img :src="item" aspect-ratio="1" />
            <v-card-actions>
              <v-btn text color="error" small @click="deleteItem(item)">
                删除
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-btn color="primary" @click="submit">
          发布
        </v-btn>
      </v-row>
    </v-card-subtitle>
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
  </v-card>
</template>

<script>
export default {
  data() {
    return {
      article: {
        content: '',
        type: 100,
        files: null,
        forward: null
      },
      rules: [
        value => !value || value.size < 20971520 || '图片或视频大小不能超过20MB'
      ],
      files: [],
      filesShowUrl: [],
      showMessage: false,
      message: ''
    }
  },
  methods: {
    submit() {
      if (this.article.content == null || this.article.content === '') {
        this.message = '你还没有输入任何内容！'
        this.showMessage = true
        return
      }
      if (this.files.length === 0) {
        this.postThink()
      } else {
        this.uploadFile()
      }
    },
    postThink() {
      this.httpPost('/article/thinks/save', this.article, (json) => {
        if (json.status === 200) {
          this.message = '发送成功！'
          this.showMessage = true
          this.article = {
            content: '',
            type: 100,
            files: null,
            forward: null
          }
          this.files = []
          this.filesShowUrl = []
          // 页面跳转
          this.$emit('success', true)
          this.$router.push('/bbs?type=100')
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    setFile(value) {
      if (value.length === 0) {
        return
      }
      if (value.length > 9) {
        this.message = '一次最多只能选择9张图片'
        this.showMessage = true
        return
      }
      if (this.files.length >= 9) {
        this.message = '一次最多只能选择9张图片'
        this.showMessage = true
        return
      }
      for (let i = 0; i < value.length; i++) {
        if (value[i].size > 20971520) {
          this.message = '图片或视频大小不能超过20MB'
          this.showMessage = true
          return
        }
      }
      for (let i = 0; i < value.length; i++) {
        this.filesShowUrl.push(window.URL.createObjectURL(value[i]))
        this.files.push(value[i])
      }
    },
    deleteItem(item) {
      const index = this.filesShowUrl.indexOf(item)
      this.filesShowUrl.splice(index, 1)
      this.files.splice(index, 1)
    },
    uploadFile() {
      const formData = new FormData()
      for (let i = 0; i < this.files.length; i++) {
        formData.append('file[]', this.files[i])
      }
      this.uploadFiles('/uploads/file', formData, (json) => {
        if (parseInt(json.code) === 0) {
          const keys = Object.keys(json.data.succMap)
          this.article.files = []
          for (let i = 0; i < keys.length; i++) {
            this.article.files.push(json.data.succMap[keys[i]])
          }
          // console.log(this.article)
          this.postThink()
        } else {
          this.message = '文件上传失败，请稍后重试！'
          this.showMessage = true
        }
      })
    }
  }
}
</script>

<style>

</style>
