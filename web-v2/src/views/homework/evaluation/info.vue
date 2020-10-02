<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h3>
          <v-btn
            icon
            @click="back"
          >
            <v-icon>mdi-arrow-left-thick</v-icon>
          </v-btn>
          <router-link :to="`/user/${userHomework.studentId}`">
            <v-avatar size="62">
              <v-img :src="userHomework.userAvatar" />
            </v-avatar>
            {{ userHomework.studentName }}
          </router-link>
          提交的作业： {{ userHomework.title }} <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;`" />展示
        </h3>
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <v-row>
      <v-col>
        <!-- 显示作业的延展纸 -->
        <v-expansion-panels>
          <v-expansion-panel>
            <v-expansion-panel-header>点击展开作业细节</v-expansion-panel-header>
            <v-expansion-panel-content>
              <!-- 学生提交的答案及题目展示 -->
              <v-row v-for="(item, index) in userHomework.questionsModels" :key="item.id">
                <v-col cols="12">
                  <Choice v-if="item.type == 1 || item.type == 0 || item.type == 4" :index="index + 1" :question="item" :disabled="true" :answer="true" />
                  <Discourses v-if="item.type == 2 || item.type == 3" :index="index + 1" :question="item" :disabled="true" :answer="true" />
                  <v-divider />
                </v-col>
              </v-row>
            </v-expansion-panel-content>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-col>
    </v-row>
    <!-- 评论区 -->
    <v-row>
      <v-col>
        <Comment :homework="userHomework" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Choice from '@/views/homework/question/choice-judge.vue'
import Discourses from '@/views/homework/question/discourses.vue'
import Comment from '@/components/comment/evaluation/index.vue'

export default {
  components: {
    Choice,
    Discourses,
    Comment
  },
  data() {
    return {
      submitId: 0,
      userHomework: {}
    }
  },
  created() {
    this.submitId = this.$route.params.submitId
    this.getUserSubmit()
  },
  methods: {
    getUserSubmit() {
      this.httpGet(`/evaluation/submit/${this.submitId}`, (json) => {
        if (json.status === 200) {
          this.userHomework = json.data
        } else {
          //
        }
      })
    },
    back() {
      this.$router.push(`/course/learn/${this.$route.params.id}/evaluation/homework/${this.$route.params.homeworkId}`)
    }
  }
}
</script>

<style>

</style>
