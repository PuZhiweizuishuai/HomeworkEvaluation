<template>
  <a-layout>
    <template>
      <a-page-header
        style="border: 1px solid rgb(235, 237, 240)"
        title="批改"
        @back="back()"
      />
    </template>
    <a-layout-content :style="{ padding: '0 50px' }">
      <a-card :title="`学生学号： ${this.$route.query.studentId}`">
        <a-row>
          <a-col>
            <h5>全卷总分： {{ homeworkData.totalScore }} 分, 当前实际得分： {{ homeworkData.score }} 分</h5>
            说明：选择判断题系统会自动进行判分，而选择填空因为答案可能不唯一，所以暂不提供自动判题
          </a-col>
        </a-row>
        <a-divider />
        <div v-for="(item, i) in homeworkData.questionsModels" :key="i">
          <ChoiceQuestion v-if="item.type == 1 || item.type == 0 || item.type == 4" :question="item" :number="i+1" :disabled="true" :scoreedit="true" :comment="true" @commentMsg="commentChange" />
          <QuestionAnswer v-if="item.type == 2 || item.type == 3" :question="item" :number="i+1" :disabled="true" :scoreedit="true" :comment="true" @commentMsg="commentChange" />
          <a-divider />
        </div>
        <br>
        <a-divider />
        <a-row>
          <a-col>
            <h5> 总评：</h5>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <Vditor :height="400" :hide="false" :idname="`Vditor`" :uploadurl="uploadurl" @vditor-input="getComment" />
          </a-col>
        </a-row>
        <div style="margin: 24px;text-align: center;">

          <a-button type="primary" size="large" style="width:200px">
            提交
          </a-button>

        </div>
      </a-card>
    </a-layout-content>
  </a-layout>
</template>

<script>
import ChoiceQuestion from '@/views/questions/choice-question.vue'
import QuestionAnswer from '@/views/questions/question-answer.vue'
import Vditor from '@/components/vditor/vditor.vue'
export default {
  name: 'Correcting',
  components: {
    ChoiceQuestion,
    QuestionAnswer,
    Vditor
  },
  data() {
    return {
      homeworkData: {},
      uploadurl: this.SERVER_API_URL + `/upload/file?type=homework&homework=${this.$route.params.id}`,
      commentMap: {
        Set: function(key, value) { this[key] = value },
        Get: function(key) { return this[key] },
        Contains: function(key) { return this.Get(key) != null },
        Remove: function(key) { delete this[key] }
      },
      commentList: []
    }
  },
  created() {
    this.getUserAnswer()
  },
  methods: {
    getUserAnswer() {
      fetch(`${this.SERVER_API_URL}/homework/keeper/correct/${this.$route.params.id}?studentId=${this.$route.query.studentId}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            //
            this.homeworkData = json.data
          } else {
            this.$message.error(json.message)
            this.$router.push({ path: `/curriculum/keeper/homework/${this.$route.params.id}` })
          }
        })
        .catch(e => {
          return null
        })
    },
    back() {
      this.$router.push({ path: `/curriculum/keeper/homework/${this.$route.params.id}` })
    },
    getComment(value) {
      console.log(this.commentMap)
      // for (const key in this.commentMap) {
      //   // this.commentList.push(this.commentMap[key])
      //   console.log(this.commentMap[key])
      // }
      // console.log(this.commentList)
      console.log(Object.entries(this.commentMap))
      console.log(value)
    },
    commentChange(value) {
      this.commentMap.Set(value.id, value)
    }
  }
}
</script>

<style>

</style>
