<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="10">
        <h3>创建新的问题</h3>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-divider />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        题目内容：
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <Vditor
          ref="questionText"
          :placeholder="'详细的题目内容，描述，可以包含图片，视频，文件等,支持 LaTeX 数学公式！ '"
          :uploadurl="uploadurl"
          :idname="`create-new-question`"
          :height="400"
          :markdown="question.question"
          @vditor-input="setVditorInput"
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-select
          v-model="question.type"
          :items="questionTypeItem"
          label="题目类型"
          item-text="text"
          item-value="value"
          clearable
        />
      </v-col>
    </v-row>
    <v-row v-if="question.type == 0 || question.type == 1" justify="center">
      <v-col cols="10">
        <v-combobox
          v-model="question.options"
          label="选项(每个选项输入完成后，请按回车输入下一个选项)"
          multiple
          chips
        />
      </v-col>
    </v-row>
    <v-row v-if="question.type == 0" justify="center">
      <v-col cols="10">
        <v-select
          v-model="choiceAnswer"
          :items="question.options"
          label="答案"
          chips
        />
      </v-col>
    </v-row>
    <v-row v-if="question.type == 1" justify="center">
      <v-col cols="10">
        <v-select
          v-model="multipleChoice"
          :items="question.options"
          multiple
          chips
          label="答案"
        />
      </v-col>
    </v-row>
    <v-row v-if="question.type == 4" justify="center">
      <v-col cols="10">
        判断答案：
      </v-col>
    </v-row>
    <v-row v-if="question.type == 4" justify="center">
      <v-col cols="10">
        <v-radio-group v-model="question.otherAnswer" :row="true">
          <v-radio label="对" :value="1" />
          <v-radio label="错" :value="0" />
        </v-radio-group>
      </v-col>
    </v-row>
    <v-row v-if="question.type == 2 || question.type == 3" justify="center">
      <v-col cols="10">
        填空或问答题参考答案：
      </v-col>
    </v-row>
    <v-row v-if="question.type == 2 || question.type == 3" justify="center">
      <v-col cols="10">
        <Vditor
          ref="questionAnswer"
          :placeholder="'填空问答题不支持自动判题，需要教师手都判题打分'"
          :uploadurl="uploadurl"
          :idname="`question-answer-vditor`"
          :height="400"
          :markdown="question.otherAnswer"
          @vditor-input="getAnswer"
        />
      </v-col>
    </v-row>
    <!--  -->
    <v-row justify="center">
      <v-col cols="10">
        题目难度：
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-rating
          v-model="question.difficulty"
          background-color="orange lighten-3"
          color="orange"
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        是否开启分享（开启分享后，其他老师也可以使用这道题目）
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-radio-group v-model="question.shareStatus" :row="true">
          <v-radio label="关闭" :value="0" />
          <v-radio label="开启" :value="1" />
        </v-radio-group>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        题目提示：（选填）
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="question.tips"
          label="题目提示"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        分值：
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="question.score"
          label="分值"
          placeholder="只是在这套题目中的分值，不影响其它题目"
          clearable
          type="number"
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        题目标签:
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-combobox
          v-model="question.tag"
          label="标签"
          multiple
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-btn v-if="type == 0" depressed color="success" @click="saveQuestion">
        导入
      </v-btn>
      <v-btn v-if="type == 1" depressed color="success" @click="updateQuestion">
        更新
      </v-btn>
    </v-row>
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
import Vditor from '@/components/vditor/vditor.vue'
/**
 * 创建新的问题
 */
export default {
  name: 'CreateQuestion',
  components: {
    Vditor
  },
  props: {
    // 类型，创建还是编辑，默认是创建
    type: {
      type: Number,
      default: 0
    },
    // 如果是编辑的花，编辑的题目数据
    old: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      question: {
        question: '',
        type: 0,
        options: [],
        answer: [],
        otherAnswer: '',
        difficulty: 0,
        shareStatus: 0,
        tips: '',
        tag: [],
        score: ''
      },
      choiceAnswer: '',
      multipleChoice: [],
      uploadurl: this.SERVER_API_URL + '/upload/file',
      questionTypeItem: [
        { text: '单选', value: 0 },
        { text: '多选', value: 1 },
        { text: '填空', value: 2 },
        { text: '问答', value: 3 },
        { text: '判断', value: 4 }
      ],
      message: '',
      showMessage: false
    }
  },
  created() {
    if (this.type === 1) {
      this.question = this.old
      if (this.question.type === 0) {
        this.choiceAnswer = this.question.answer[0]
      } else if (this.question.type === 1) {
        this.multipleChoice = this.question.answer
      }
    }
  },
  methods: {
    setVditorInput(value) {
      this.question.question = value
    },
    getAnswer(value) {
      this.question.otherAnswer = value
    },
    saveQuestion() {
      if (this.question.type === 0) {
        this.question.answer = [this.choiceAnswer]
      } else if (this.question.type === 1) {
        this.question.answer = this.multipleChoice
      }
      // console.log(this.question)
      if (this.questionValid()) {
        if (this.question.score <= 0) {
          this.message = '分数必须大于0'
          this.showMessage = true
          return
        }
        const questions = JSON.parse(JSON.stringify(this.question))
        this.question = {
          question: '',
          type: 0,
          options: [],
          answer: [],
          otherAnswer: '',
          difficulty: 0,
          shareStatus: 0,
          tips: '',
          tag: [],
          score: ''
        }

        this.$emit('question', questions)
        this.$refs.questionText.setTextValue('')
        this.$refs.questionAnswer.setTextValue('')
      }
    },
    updateQuestion() {
      console.log(this.question)
      if (this.questionValid()) {
        this.httpPost('/question/update', this.question, (json) => {
          if (json.status === 200) {
            this.message = '修改成功！'
            this.showMessage = true
            this.$emit('update', true)
          } else {
            this.message = json.message
            this.showMessage = true
          }
        })
      }
    },
    questionValid() {
      if (this.question.question == null || this.question.question === '') {
        this.message = '问题正文不能为空！'
        this.showMessage = true
        return false
      }
      if (this.question.type === 0 || this.question.type === 1) {
        if (this.question.options.length === 0) {
          this.message = '选项不能为空！'
          this.showMessage = true
          return false
        }
        if (this.question.answer.length === 0) {
          this.message = '选择题答案不能为空！'
          this.showMessage = true
          return false
        }
      } else if (this.question.type === 2 || this.question.type === 3) {
        if (this.question.otherAnswer == null || this.question.otherAnswer === '') {
          this.message = '答案不能为空！'
          this.showMessage = true
          return false
        }
      } else {
        if (parseInt(this.question.otherAnswer) !== 1 && parseInt(this.question.otherAnswer) !== 0) {
          this.message = '判断题答案设置错误'
          this.showMessage = true
          return false
        }
      }
      return true
    }
  }
}
</script>

<style>

</style>
