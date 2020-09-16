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
      <a-card :title="`学生学号： ${this.$route.query.studentId}  学生姓名：${homeworkData.studentName}`">
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
        <a-row v-if="this.$route.query.type == 'see'">
          <a-col :span="2">
            总评：
          </a-col>
          <a-col :span="22">
            <ShowMarkdown v-if="isShowTeacherComment" :anchor="0" :markdown="homeworkData.teacherComment" :speech="false" />
          </a-col>
        </a-row>

        <a-row v-if="this.$route.query.type != 'see'">
          <a-col>
            <h5> 总评：（如果打回，也可以在这里写上打回原因）
            </h5>
          </a-col>
        </a-row>
        <a-row v-if="this.$route.query.type != 'see'">
          <a-col>
            <Vditor :height="400" :hide="false" :idname="`Vditor`" :uploadurl="uploadurl" @vditor-input="getComment" />
          </a-col>
        </a-row>
        <div v-if="this.$route.query.type != 'see'" style="margin: 24px;text-align: center;">
          <a-button size="large" style="width:200px" @click="submit(-1)">
            打回
          </a-button>
          <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
          <a-button type="primary" size="large" style="width:200px" @click="submit(3)">
            完成
          </a-button>
        </div>
        <div v-if="this.$route.query.type != 'see'" style="margin: 24px;text-align: center;">
          tips: 点击打回或完成，会自动跳转到下一名学生，如果没有下一名学生，则会返回批改页面
        </div>
        <div v-if="this.$route.query.type == 'see'" style="margin: 24px;text-align: center;">
          <a-button type="primary" size="large" style="width:200px" @click="edit">
            修改
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
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
export default {
  name: 'Correcting',
  components: {
    ChoiceQuestion,
    QuestionAnswer,
    Vditor,
    ShowMarkdown
  },
  data() {
    return {
      homeworkData: {},
      uploadurl: this.SERVER_API_URL + `/upload/file?type=homework&homework=${this.$route.params.id}`,
      commentMap: {

      },
      commentData: {
        studentId: this.$route.query.studentId,
        questionList: [],
        comment: '',
        status: 3,
        id: this.$route.params.id
      },
      isShowTeacherComment: false
    }
  },
  created() {
    this.getUserAnswer()
  },
  methods: {
    edit() {
      this.$router.push({ path: `/curriculum/keeper/homework/${this.$route.params.id}/correcting`, query: { studentId: this.$route.query.studentId }})
    },
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
            this.isShowTeacherComment = true
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
    submit(value) {
      this.commentList = []
      if (value !== -1) {
        console.log('开始检查提交数据')
        this.checkCommentData()
      }

      // console.log(this.commentData)
      this.commentData.status = value
      fetch(`${this.SERVER_API_URL}/homework/keeper/correct`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(this.commentData)
      }).then(response => response.json())
        .then(json => {
          // 批改成功
          if (json.status === 200) {
            this.$message.success('批改完成')
            if (json.data.length === 0) {
              this.$router.push(`/curriculum/keeper/homework/${this.$route.params.id}`)
            }
            location.replace(`/curriculum/keeper/homework/${this.$route.params.id}/correcting?studentId=${json.data[0].userId}`)
          } else {
            this.$message.error(json.message)
          }
          console.log(json)
        })
        .catch(e => {
          return null
        })
    },
    checkCommentData() {
      const length = this.homeworkData.questionsModels.length
      const questionList = this.homeworkData.questionsModels
      this.commentList = []
      for (let i = 0; i < length; i++) {
        // 如果是判断题或者问答题，判断是否缺少成绩数据
        if (questionList[i].type === 2 || questionList[i].type === 3) {
          // 如果没有评价数据
          if (this.commentMap[questionList[i].id] === undefined) {
            document.querySelector(`#question-${i + 1}`).style.display = 'inline'
            this.$message.error(`第${(i + 1)}题缺少成绩数据！`)
            return
          } else {
            // 如果有评价数据，但成绩数据是null
            if (this.commentMap[questionList[i].id].score === null) {
              document.querySelector(`#question-${i + 1}`).style.display = 'inline'
              this.$message.error(`第${(i + 1)}题缺少成绩数据！`)
              return
              // 成绩数据不是 null
            } else if (this.checkNumber(this.commentMap[questionList[i].id].score, questionList[i].score) === false) {
              this.$message.error(`第${(i + 1)}题成绩不符合规则！`)
              return
            }
          }
        }
        // 填充评价与成绩数据
        if (this.commentMap[questionList[i].id] !== undefined) {
          this.commentData.questionList.push(this.commentMap[questionList[i].id])
        }
      }
    },
    getComment(value) {
      this.commentData.comment = value
    },
    commentChange(value) {
      this.commentMap[value.id] = value
    },
    checkNumber(number, max) {
      //
      if (isNaN(parseFloat(number))) {
        return false
      }
      const score = parseFloat(number)
      if (score < 0 || score > max) {
        return false
      }
      return true
    }
  }
}
</script>

<style>

</style>
