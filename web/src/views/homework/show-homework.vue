<template>
  <div>
    <a-divider>标题</a-divider>

    <h3 style="text-align: center;">
      <span v-text="homeworkData.title" />
      <a-tag v-if="homeworkData.type === 2" color="red">
        考试
      </a-tag>
      <a-tag v-if="homeworkData.type === 1" color="green">
        测验
      </a-tag>
      <a-tag v-if="homeworkData.type === 0" color="blue">
        作业
      </a-tag>
    </h3>
    <a-divider>要求</a-divider>
    <div>
      <p style="text-align: center;">时间 ： <span v-text="setTime()" />
      </p>
      <div ref="homeworkContent" />
      <!-- <ShowMarkdown :markdown="homeworkData.content" :speech="false" /> -->

    </div>
    <a-divider>题目</a-divider>
    <div>
      <div v-for="(item, i) in homeworkData.questionsModels" :key="i">
        <ChoiceQuestion v-if="item.type == 1 || item.type == 0 || item.type == 4" :question="item" :number="i+1" @answer="getAnswer" />
        <QuestionAnswer v-if="item.type == 2 || item.type == 3" :question="item" :number="i+1" @answer="getAnswer" />
      </div>

    </div>
    <a-divider>已经到底了</a-divider>
    <div style="text-align: center;">
      <a-popconfirm
        title="你只有一次提交机会，请检查无误后再提交！"
        ok-text="是的，我已经检查过，没有问题了"
        cancel-text="我还要再检查一遍"
      >
        <a-button size="large" type="primary">
          提交
        </a-button>
      </a-popconfirm>
      <a-button style="margin-left: 80px;" size="large" @click="saveAnswer">暂时保存</a-button>
    </div>
  </div>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import ChoiceQuestion from '@/views/questions/choice-question.vue'
import QuestionAnswer from '@/views/questions/question-answer.vue'
import Vditor from 'vditor'
import 'vditor/src/assets/scss/index.scss'

export default {
  name: 'ShowHomework',
  components: { ChoiceQuestion, QuestionAnswer },
  data() {
    return {
      id: 0,
      hid: 0,
      homeworkData: {},
      submitData: {
        homeworkId: 0,
        type: 0,
        answers: []
      }
    }
  },
  created() {
    this.id = this.$route.params.id
    this.hid = this.$route.params.hid
    this.getHomeworkData()
  },
  updated() {
    Vditor.preview(this.$refs.homeworkContent,
      this.homeworkData.content, {
        speech: {
          enable: false
        },
        emojiPath: '/emoji',
        anchor: 0
      })
  },
  methods: {
    saveAnswer() {
      this.submitData.type = 0
      console.log(this.submitData)
      fetch(this.SERVER_API_URL + '/homework/submit', {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(this.submitData)
      }).then(response => response.json())
        .then(json => {
          console.log(json)
        })
        .catch(e => {
          return null
        })
    },
    getAnswer(ans, id, type) {
      const answer = {
        questionId: 0,
        otherAnswer: '',
        answer: []
      }

      answer.questionId = id
      if (type === 1 || type === 0) {
        answer.answer = ans
      } else {
        answer.otherAnswer = ans
      }
      this.unique(answer)
    },
    unique(answer) {
      for (let i = 0; i < this.submitData.answers.length; i++) {
        if (this.submitData.answers[i].questionId === answer.questionId) {
          this.submitData.answers[i] = answer
          return
        }
      }
      this.submitData.answers.push(answer)
    },
    getHomeworkData() {
      fetch(this.SERVER_API_URL + `/homework/question/${this.hid}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.homeworkData = json.data
            this.submitData.homeworkId = json.data.id
          } else {
            this.$notification['error']({
              message: '发生异常，请退出重试！',
              description: json.message
            })
            this.$router.push(`/curriculum/learn/${this.id}/homework`)
          }
        })
        .catch(e => {
          return null
        })
    },
    setTime() {
      return TimeUtil.formateTime(this.homeworkData.openTime, this.homeworkData.closeTime)
    }
  }
}
</script>
