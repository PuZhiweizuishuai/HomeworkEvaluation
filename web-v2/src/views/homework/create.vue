<template>
  <v-container>
    <v-stepper v-model="e1">
      <v-stepper-header>
        <v-stepper-step :complete="e1 > 1" step="1">创建作业</v-stepper-step>

        <v-divider />

        <v-stepper-step :complete="e1 > 2" step="2">导入题目</v-stepper-step>

        <v-divider />

        <v-stepper-step step="3">提交数据</v-stepper-step>
      </v-stepper-header>

      <v-stepper-items>
        <!-- 基本信息 -->
        <v-stepper-content step="1">
          <InfoForm @homework="getHomeworkInfo" />

        </v-stepper-content>

        <!-- 导入或新建问题 -->
        <v-stepper-content step="2">
          <ImportQuestion />

          <v-row justify="center">
            <v-btn
              color="primary"
              @click="e1 = 3"
            >
              下一步
            </v-btn>

            <v-btn text @click="e1 = 1">上一步</v-btn>
          </v-row>
        </v-stepper-content>

        <!-- 提交数据 -->
        <v-stepper-content step="3">
          <v-card
            class="mb-12"
            color="grey lighten-1"
            height="200px"
          />

          <v-row justify="center">
            <v-btn
              color="primary"
              @click="e1 = 1"
            >
              提交
            </v-btn>
            <v-btn text @click="e1 = 2">上一步</v-btn>
          </v-row>
        </v-stepper-content>
      </v-stepper-items>
    </v-stepper>
  </v-container>
</template>

<script>
import InfoForm from '@/components/homework/info-form.vue'
import ImportQuestion from '@/components/homework/question/import-question.vue'

export default {
  name: 'CreateHomwork',
  components: {
    InfoForm,
    ImportQuestion
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
      e1: 1,
      homework: {}
    }
  },
  created() {
    this.id = this.$route.params.id
    // 页面拦截
    // if (this.role.role !== 'ROLE_TEACHER') {
    //   this.$router.go(-1)
    // }
    this.$vuetify.goTo(0)
    document.title = '创建新的作业 - ' + this.course.curriculumName
  },
  methods: {
    getHomeworkInfo(info) {
      this.homework = info
      this.e1 = 2
    }
  }
}
</script>

<style>

</style>
