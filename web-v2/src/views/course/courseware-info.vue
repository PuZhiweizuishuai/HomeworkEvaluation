<template>
  <v-container fluid>
    <v-row justify="center">
      <v-col cols="1">
        <v-btn class="mx-2" fab dark small depressed color="primary" @click="back">
          <v-icon dark> mdi-arrow-left-thick </v-icon>
        </v-btn>
      </v-col>
      <v-col cols="5">
        <v-select
          v-model="selectFather"
          :items="coursewareList"
          label="请选择章节"
          item-text="title"
          item-value="id"
          @change="selectFatcherChange"
        />
      </v-col>
      <v-col cols="5">
        <v-select
          v-model="children"
          :items="childrenList"
          label="请选择章节"
          item-text="title"
          item-value="id"
          clearable
          @change="selectChildrenChange"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-divider />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="11">
        <h2>
          {{ courseware.title }}
        </h2>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-divider />
      </v-col>
    </v-row>
    <!-- 视频播放 -->
    <v-row v-if="courseware.fileType == 1" :key="videoKey">
      <v-col cols="12">
        <DPlayer :video="courseware" />
      </v-col>
    </v-row>

    <!-- 音乐播放 -->
    <v-row v-if="courseware.fileType == 4" :key="voiceKey" justify="center">
      <v-col cols="10">
        <Voice ref="voicePlayer" :voice="courseware" />
      </v-col>
    </v-row>

    <!-- PDF 预览 -->
    <v-row v-if="courseware.fileType == 2" justify="center">
      <v-col cols="11">
        <embed :src="courseware.fileUrl +'?key=' + encodeURIComponent(courseware.key)" type="application/pdf" style="width: 100%; height: 600px">
      </v-col>
    </v-row>
    <v-row>
      <v-col v-if="courseware.fileType == 2" cols="12">
        <v-divider />
      </v-col>
    </v-row>

    <!-- OFFICE 文档预览 -->
    <v-row v-if="courseware.fileType == 3" justify="center">
      <v-col v-if="courseware.status == 0" cols="11">
        <embed :src="courseware.fileUrl +'.pdf?key=' + encodeURIComponent(courseware.key)" type="application/pdf" style="width: 100%; height: 600px">
      </v-col>
    </v-row>

    <!-- 下载框 -->
    <v-row v-if="courseware.fileType != null" justify="center">
      <v-col cols="11">
        此文件如果不支持预览，那么请下载查看：
        <router-link :to="courseware.fileUrl+'?key=' + encodeURIComponent(courseware.key)" target="_blank">
          <v-btn depressed color="primary">下载文件</v-btn>
        </router-link>
      </v-col>
    </v-row>

    <!-- 分割线 -->
    <v-row>
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <!-- 文字内容 -->
    <v-row v-if="courseware.text != null && courseware.text != ''" :key="textKey" justify="center">
      <v-col cols="10">
        <ShowMarkdown :markdown="courseware.text" />
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-row justify="space-between">
          <v-btn text>上一章</v-btn>
          <v-btn text color="primary">下一章</v-btn>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
import DPlayer from '@/components/player/player.vue'
import Voice from '@/components/player/voice.vue'

export default {
  name: 'CoursewareInfo',
  components: {
    ShowMarkdown,
    DPlayer,
    Voice
  },
  props: {
    course: {
      type: Object,
      default: null
    },
    role: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      id: 0,
      coursewareId: 0,
      father: {},
      courseware: {},
      coursewareList: [],
      coursewareMap: {
        Set: function(key, value) { this[key] = value },
        Get: function(key) { return this[key] },
        Contains: function(key) { return this.Get(key) != null },
        Remove: function(key) { delete this[key] }
      },
      selectFather: 0,
      childrenList: [],
      children: 0,
      // 对视频与文件描述组件进行重新加载
      videoKey: Math.random(),
      textKey: Math.random(),
      voiceKey: Math.random(),
      next: {},
      last: {}
    }
  },
  created() {
    this.id = this.$route.params.id
    this.coursewareId = this.$route.params.coursewareId
    this.getCoursewareList()
  },
  methods: {
    getCoursewareList() {
      this.httpGet(`/course/courseware/${this.$route.params.id}`, (json) => {
        if (json.status === 200) {
          this.coursewareList = json.data || []
          this.initData()
        } else {
          //
        }
      })
    },
    initData() {
      for (let i = 0; i < this.coursewareList.length; i++) {
        this.coursewareMap.Set(this.coursewareList[i].id, this.coursewareList[i])
        if (this.coursewareList[i].id === parseInt(this.coursewareId)) {
          this.father = this.coursewareList[i]
          this.courseware = this.coursewareList[i]
          this.selectFather = this.coursewareList[i].id
          this.childrenList = this.coursewareList[i].children
        } else {
          for (let j = 0; j < this.coursewareList[i].children.length; j++) {
            if (this.coursewareList[i].children[j].id === parseInt(this.coursewareId)) {
              this.father = this.coursewareList[i]
              this.courseware = this.coursewareList[i].children[j]
              this.childrenList = this.coursewareList[i].children
              this.selectFather = this.father.id
              this.children = this.courseware.id
            }
          }
        }
      }
    },
    selectFatcherChange() {
      if (this.$refs.voicePlayer != null) {
        this.$refs.voicePlayer.destroy()
      }
      this.courseware = this.coursewareMap.Get(this.selectFather)
      this.father = this.courseware
      this.childrenList = this.courseware.children
      this.children = 0
      if (this.courseware.fileType === 1) {
        this.videoKey++
      } else if (this.courseware.fileType === 4) {
        this.voiceKey++
      }
      this.textKey++
    },
    selectChildrenChange() {
      if (this.$refs.voicePlayer != null) {
        this.$refs.voicePlayer.destroy()
      }
      if (this.children == null) {
        this.videoKey++
        this.textKey++
        this.voiceKey++
        this.courseware = this.father
        return
      }
      for (let i = 0; i < this.father.children.length; i++) {
        if (this.father.children[i].id === this.children) {
          this.courseware = this.father.children[i]
          if (this.courseware.fileType === 1) {
            this.videoKey++
          } else if (this.courseware.fileType === 4) {
            this.voiceKey++
          }
          this.textKey++
          return
        }
      }
    },
    back() {
      this.$router.push({ path: `/course/learn/${this.$route.params.id}/courseware` })
    }
  }
}
</script>

<style>

</style>
