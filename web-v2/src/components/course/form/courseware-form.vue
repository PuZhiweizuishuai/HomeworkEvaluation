<template>
  <v-container>
    <v-row>
      <v-col>
        <v-text-field
          v-model="courseware.sort"
          label="排序"
          placeholder="排序号，负责课件显示顺序，越小显示越靠前，默认为1"
          type="number"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-text-field
          v-model="courseware.title"
          label="标题"
          placeholder="章节标题"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <Vditor
          :placeholder="'详细的文字介绍，可以留空，非必填,上传课件文件请使用下面的上传接口，下面的接口支持文件预览与视频在线播放！，外部链接或一些其它资料可以写在这里！'"
          :uploadurl="uploadurl"
          :markdown="info.text"
          @vditor-input="getText"
        />
      </v-col>
    </v-row>
    <!-- <v-row>
      <v-col>
        <v-text-field
          v-model="courseware.level"
          label="分级， 0 是父分级， 1 就是子分级"
          placeholder="比如：第一章，第二章这种是父级，1-1这种就是父分级下属字分级"
          type="number"
        />
      </v-col>
    </v-row> -->
    <v-row>
      <v-col>
        上传文件，视频，课件，目前支持MP4格式的视频和PDF，doc，docx等文件在线预览。其它文件预览有待后期支持
        <br>
        由于Office系列文件上传后系统需要进行转码，所以需要稍等一会才能看见课件。
      </v-col>
    </v-row>
    <v-row v-if="isedit">
      <v-col>
        之前文件地址 ：
        <span v-if="info.fileUrl != ''">
          <router-link :to="info.fileUrl +'?key=' + encodeURIComponent(courseware.key)">{{ info.fileUrl }} </router-link>
          <br>修改文件上传后，之前的文件将被删除，所以建议修改前请先保存好之前的文件！
        </span>
        <span v-if="info.fileUrl == ''">此课件之前未上传文件，点击可以上传</span>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <FilePondUpdate @video="videoUploadSuccess" />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-select
          v-model="courseware.fatherId"
          :items="fatherItems"
          label="所属目录，不选为一级目录"
          item-text="title"
          item-value="id"
          clearable
          @change="fatherIdChange"
        />
      </v-col>
    </v-row>
    <v-col />
    <v-row justify="center">
      <v-btn large depressed color="success" @click="submit">
        {{ submitBtnText }}
      </v-btn>
    </v-row>
    <v-col />
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
import FilePondUpdate from '@/components/upload/filepond-upload.vue'

export default {
  components: {
    Vditor,
    FilePondUpdate
  },
  props: {
    isedit: {
      type: Boolean,
      default: true
    },
    info: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      uploadurl: this.SERVER_API_URL + '/uploads/file',
      courseware: {
        sort: 1,
        courseId: this.$route.params.id,
        title: '',
        text: '',
        level: 0,
        fileUrl: '',
        fileName: '',
        fatherId: 0
      },
      fatherItems: [],
      message: '',
      showMessage: false,
      submitBtnText: '提交'
    }
  },
  created() {
    this.getFaterList()
    if (this.isedit) {
      this.courseware = this.info
      this.submitBtnText = '修改'
    } else {
      this.submitBtnText = '提交'
    }
  },
  methods: {
    getText(value) {
      this.courseware.text = value
    },
    videoUploadSuccess(url) {
      if (url.status === 200) {
        this.courseware.fileName = url.data.filename // Object.keys(url.data.succMap)[0]
        this.courseware.fileUrl = url.data.path // Object.values(url.data.succMap)[0]
        this.message = '上传成功！'
        this.showMessage = true
      } else {
        this.message = url.message
        this.showMessage = true
      }
    },
    submit() {
      this.$emit('courseware', this.courseware, this.isedit)
    },
    getFaterList() {
      this.httpGet(`/course/courseware/${this.$route.params.id}`, (json) => {
        if (json.status === 200) {
          this.fatherItems = json.data
        } else {
          //
        }
      })
    },
    fatherIdChange() {
      if (this.courseware.fatherId == null) {
        this.courseware.level = 0
      } else {
        this.courseware.level = 1
      }
    }
  }
}
</script>

<style>

</style>
