<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h3 style="float: left;"> 课程评价: </h3>写下你对本课程的意见或评价
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>

    <!-- 评价区 -->
    <v-row justify="center">
      <v-col cols="1">
        评分：
      </v-col>
      <v-col cols="9">
        <v-rating
          v-model="comment.courseRating"
          background-color="orange lighten-3"
          color="orange"
          half-increments
          hover
          large
        />
      </v-col>
    </v-row>

    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="comment.title"
          label="标题"
          clearable
        />
      </v-col>
    </v-row>

    <v-row justify="center">
      <v-col cols="10">
        <Vditor
          :placeholder="'写下你对本课程理性的评价，帮助老师更好的改进课程，让其它学生更了解这么课程'"
          :uploadurl="uploadurl"
          :height="400"
          @vditor-input="setVditorInput"
        />
      </v-col>
    </v-row>

    <v-row justify="center">
      <v-col cols="5">
        <img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" @click="getVerifyImage">
      </v-col>
      <v-col cols="5">
        <v-text-field
          v-model="comment.verifyCode"
          placeholder="验证码"
          label="验证码"
          :rules="[() => comment.verifyCode != null || '验证码不能为空']"
          clearable
        />
      </v-col>
    </v-row>

    <v-row justify="center">
      <v-btn
        depressed
        color="primary"
        @click="submit"
      >
        提交
      </v-btn>
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
import Vditor from '@/components/vditor/vditor.vue'
export default {
  components: {
    Vditor
  },
  data() {
    return {
      uploadurl: this.SERVER_API_URL + '/upload/file',
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      comment: {
        content: '',
        title: '',
        courseRating: 0,
        courseId: this.$route.params.id,
        type: 4,
        verifyCode: ''
      },
      showMessage: false,
      message: ''
    }
  },
  methods: {
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    },
    setVditorInput(value) {
      this.comment.content = value
    },
    submit() {
      if (this.comment.title === '' || this.comment.title === null) {
        this.message = '标题不能为空！'
        this.showMessage = true
        return
      }
      if (this.comment.courseRating < 0.0 || this.comment.courseRating > 5.0) {
        this.message = '评分取值错误!'
        this.showMessage = true
        return
      }
      if (this.comment.content === '' || this.comment.content === null) {
        this.message = '评价内容不能为空!'
        this.showMessage = true
        return
      }

      if (this.comment.verifyCode === '' || this.comment.verifyCode === null) {
        this.message = '验证码不能为空!'
        this.showMessage = true
        return
      }
      this.httpPost('/article/save', this.comment, (json) => {
        if (json.status === 200) {
          this.message = '评价成功!'
          this.showMessage = true
          console.log(json)
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
