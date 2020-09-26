<template>
  <v-container>
    <v-row>
      <v-col>
        <v-text-field
          v-model="courseware.sort"
          label="章节号"
          placeholder="章节号，负责课件显示顺序，越小显示越靠前"
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
          :placeholder="'详细的文字介绍，可以留空，非必填'"
          :uploadurl="uploadurl"
          @vditor-input="getText"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-text-field
          v-model="courseware.level"
          label="分级， 0 是父分级， 1 就是子分级"
          placeholder="比如：第一章，第二章这种是父级，1-1这种就是父分级下属字分级"
          type="number"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        上传文件，视频，课件，目前支持MP4格式的视频和PDF文件在线预览。其它如doc，docx等文件预览有待后期支持
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
          label="父分级"
          item-text="title"
          item-value="id"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-btn depressed color="success" @click="submit">
        提交
      </v-btn>
    </v-row>
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
  data() {
    return {
      uploadurl: this.SERVER_API_URL + '/upload/file',
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
      fatherItems: []
    }
  },
  created() {
    this.getFaterList()
  },
  methods: {
    getText(value) {
      this.courseware.text = value
    },
    videoUploadSuccess(url) {
      this.courseware.fileName = Object.keys(url.data.succMap)[0]
      this.courseware.fileUrl = Object.values(url.data.succMap)[0]
    },
    submit() {
      this.$emit('courseware', this.courseware)
    },
    getFaterList() {
      this.httpGet(`/course/courseware/${this.$route.params.id}`, (json) => {
        if (json.status === 200) {
          this.fatherItems = json.data
        } else {
          //
        }
      })
    }
  }
}
</script>

<style>

</style>
