<template>
  <v-container fill-height>
    <v-row>
      <v-col cols="12">
        <h3 style="float: left;"> 课件列表 </h3> ps:观看记录功能可能会在下次更新添加，请注意关注
      </v-col>
    </v-row>

    <v-row>
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
              <v-icon v-if="item.fileType == 2">
                mdi-file-pdf
              </v-icon>
              <v-icon v-if="item.fileType == 1">
                mdi-video
              </v-icon>
              <v-icon v-if="item.fileType == 3">
                mdi-file-powerpoint
              </v-icon>
              <v-icon v-if="item.fileType == 0 || item.fileType == null">
                mdi-file
              </v-icon>
              <span v-html="`&nbsp;&nbsp;`" />
              {{ item.title }}
            </router-link>
          </template>
        </v-treeview>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'Courseware',
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
      coursewareList: []
    }
  },
  created() {
    this.$vuetify.goTo(0)
    document.title = '课件 - ' + this.course.curriculumName
    this.getCoursewareList()
  },
  methods: {
    getCoursewareList() {
      this.httpGet(`/course/courseware/${this.$route.params.id}`, (json) => {
        if (json.status === 200) {
          this.coursewareList = json.data
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
