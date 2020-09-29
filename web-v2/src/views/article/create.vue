<template>
  <v-container>
    <!-- 头 -->
    <v-row>
      <v-col>
        <h2>
          <v-icon>mdi-comment-edit</v-icon>
          发起：
        </h2>
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <v-row v-resize="onResize">
      <!-- 左边 -->
      <v-col :cols="colsLeft">
        <v-row justify="center">
          <v-col cols="12">
            <v-text-field
              v-model="article.title"
              placeholder="标题，50字以内"
              label="标题，50字以内"
              :rules="[() => article.title != null || '请输入标题']"
              clearable
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="12">
            <Vditor
              ref="courseInfoText"
              :placeholder="'请自觉遵守互联网相关的政策法规，严禁发布色情、暴力、反动的言论。'"
              :uploadurl="uploadurl"
              :markdown="article.content"
              :height="700"
              @vditor-input="setVditorInput"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-combobox
              v-model="article.tag"
              label="标签(每个选项输入完成后，请按回车输入下一个选项)"
              multiple
              chips
            />
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="5">
            <img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" @click="getVerifyImage">
          </v-col>
          <v-col cols="5">
            <v-text-field
              v-model="article.verifyCode"
              placeholder="验证码"
              label="验证码"
              :rules="[() => article.verifyCode != null || '验证码不能为空']"
              clearable
            />
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-btn depressed color="primary" @click="submit">提交</v-btn>
        </v-row>
        <!-- 这个 col是上分界线的底部 -->
      </v-col>
      <!-- 中间分割线 -->
      <v-divider
        v-if="showCenterDriver"
        class="mx-4"
        inset
        vertical
      />
      <!-- 右边 -->
      <v-col :cols="colsRight">
        <v-row>
          <v-col>
            <h3>
              主题发起指南:
            </h3>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            •<span class="article-role-title">主题标题：</span>请用<span
              style="color: red; font-weight: bold;"
            >精简</span>的语言描述您发布的内容，不超过50字。
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            •<span class="article-role-title">详细内容：</span>详细补充您的问题内容，并确保内容描述清晰直观, 并提供一些相关的资料。
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            为保证你每个问题都能得到有效的解答：推荐阅读<a href=" https://github.com/ryanhanwu/How-To-Ask-Questions-The-Smart-Way/blob/master/README-zh_CN.md" target="_blank">提问的智慧</a>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <h3>
              编辑器使用指南:
            </h3>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            •<span class="article-role-title">使用说明：</span>
            这是一个Markdown编辑器，你应该使用Markdown语法来进行编写，如果你不熟悉Markdown语法，你可以在编辑器菜单栏选择切换编辑模式，将
            其更换为所见即所得。或者你可以花两分钟时间来学习一下这个非常简单且高效有用的语法。
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            •<span class="article-role-title">文件上传指南：</span>
            点击编辑器菜单栏上的上传图标即可选择上传文件或图片，最大支持10MB的文件或图片上传。
            支持 JPG, JPEG, PNG, GIF, ZIP, DOC, DOCX, PDF, RAR, 7z,PPT, PPTX, XLS, XLSX, APK, mp4, mp3, wav
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            最后：友善发帖，并遵守国家相关法律法规，互联网并非法外之地。
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-img src="/images/v2-c277d2ad52b61fcd3fa82577575efc4c_r.jpg" />
          </v-col>
        </v-row>
      </v-col>
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
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      article: {
        title: '',
        content: '',
        tag: [],
        type: 1,
        courseId: this.$route.params.id,
        verifyCode: ''
      },
      uploadurl: this.SERVER_API_URL + '/upload/file',
      colsRight: 3,
      colsLeft: 8,
      showCenterDriver: true,
      windowSize: {
        x: 0,
        y: 0
      },
      message: '',
      showMessage: false
    }
  },
  methods: {
    setVditorInput(value) {
      this.article.content = value
    },
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.colsRight = 12
        this.colsLeft = 12
        this.showCenterDriver = false
      } else {
        this.colsRight = 3
        this.colsLeft = 8
        this.showCenterDriver = true
      }
    },
    submit() {
      if (this.article.title == null || this.article.title === '') {
        this.message = '标题不能为空！'
        this.showMessage = true
        return
      }
      if (this.article.title.length > 50) {
        this.message = '标题不能超过50字！'
        this.showMessage = true
        return
      }
      if (this.article.content == null || this.article.content === '') {
        this.message = '正文不能为空！'
        this.showMessage = true
        return
      }
      if (this.article.verifyCode == null || this.article.verifyCode === '') {
        this.message = '验证码不能为空！'
        this.showMessage = true
        return
      }
      if (this.article.tag.length > 6) {
        this.message = '标签不能超过6个'
        this.showMessage = true
        return
      }
      console.log(this.article)
      this.httpPost('/article/save', this.article, (json) => {
        if (json.status === 200) {
          this.message = '发布成功！'
          this.showMessage = true
          this.$router.push(`/course/learn/${this.$route.params.id}/bbs`)
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
.article-role-title{
  font-weight: bold;
}
</style>
