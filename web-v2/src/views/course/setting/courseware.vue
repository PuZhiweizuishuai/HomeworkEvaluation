<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12">
        <v-btn depressed color="success" @click="dialog = true">
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
          selectable
          hoverable
          activatable
          :items="coursewareList"
        >
          <template v-slot:prepend="{ item }">
            <router-link :to="`/course/learn/${item.courseId}/courseware/${item.id}`">
              {{ item.title }}
            </router-link>
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

        <CoursewareForm @courseware="getCourseware" />

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
      message: ''
    }
  },
  created() {
    this.getCoursewareList()
  },
  methods: {
    getCourseware(value) {
      this.courseware = value
      this.httpPost('/course/courseware/save', this.courseware, (json) => {
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
    }
  }
}
</script>

<style>

</style>
