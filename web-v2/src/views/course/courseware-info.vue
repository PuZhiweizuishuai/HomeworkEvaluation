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
    <!-- 视频播放 -->
    <v-row v-if="courseware.fileType == 1" :key="videoKey">
      <v-col cols="12">
        <DPlayer :video="courseware" />
      </v-col>
    </v-row>

    <!-- PDF 预览 -->
    <v-row v-if="courseware.fileType == 2" justify="center">
      <v-col cols="11">
        <embed :src="courseware.fileUrl" type="application/pdf" style="width: 100%; height: 600px">
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <!-- 下载框 -->
    <v-row v-if="courseware.fileType == 2 || courseware.fileType == 1" justify="center">
      <v-col cols="11">
        如你的浏览器不支持，那么请下载查看：
        <router-link :to="courseware.fileUrl" target="_blank">
          <v-btn depressed color="primary">下载文件</v-btn>
        </router-link>
      </v-col>
    </v-row>
    <v-row v-if="courseware.fileType == 3 || courseware.fileType == 0" justify="center">
      <v-col cols="11">
        非常抱歉，当前暂不支持这类文件的在线预览，请下载后查看：
        <router-link :to="courseware.fileUrl" target="_blank">
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
  </v-container>
</template>

<script>
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
import DPlayer from '@/components/player/player.vue'
export default {
  name: 'CoursewareInfo',
  components: {
    ShowMarkdown,
    DPlayer
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
      videoKey: 0,
      textKey: 0
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
      this.courseware = this.coursewareMap.Get(this.selectFather)
      this.father = this.courseware
      this.childrenList = this.courseware.children
      this.children = 0
      if (this.courseware.fileType === 1) {
        this.videoKey++
      }
      this.textKey++
      // console.log(this.courseware)
    },
    selectChildrenChange() {
      if (this.children == null) {
        this.courseware = this.father
        return
      }
      for (let i = 0; i < this.father.children.length; i++) {
        if (this.father.children[i].id === this.children) {
          console.log('this.courseware = this.father.children[i]', this.father.children[i])
          this.courseware = this.father.children[i]
          if (this.courseware.fileType === 1) {
            this.videoKey++
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
