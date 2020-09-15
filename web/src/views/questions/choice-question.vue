<template>
  <div>
    <div>
      <a-row>
        <a-col :span="1">
          {{ number }} :
        </a-col>
        <a-col :span="23">
          <ShowMarkdown :anchor="0" :markdown="questionData.question" :speech="false" />
          ({{ questionData.score }} 分)
        </a-col>
      </a-row>
      <br>
      <a-row>
        <a-col :span="1" />

        <a-col :span="23">
          <a-radio-group v-if="questionData.type == 0" :default-value="questionData.answer[0]" style="margin-left: 15px" @change="changeAnswerRadio">
            <a-radio v-for="(item, index) in questionData.options" :key="index" :value="item" :disabled="isEdit">
              {{ item }}
            </a-radio>
          </a-radio-group>
          <a-radio-group v-if="questionData.type == 4" v-model="questionData.otherAnswer" :disabled="isEdit" style="margin-left: 15px" @change="JudgeAnswer">
            <a-radio value="1">
              对
            </a-radio>
            <a-radio value="0">
              错
            </a-radio>
          </a-radio-group>
          <a-checkbox-group
            v-if="questionData.type == 1"
            v-model="questionData.answer"
            :disabled="isEdit"
            style="margin-left: 15px"
            :options="questionData.options"
            @change="changeAnswer"
          />
        </a-col>
      </a-row>
      <br>
      <div v-if="isComment">
        <a-row>
          <a-col :span="2"><strong>参考答案：</strong></a-col>
          <a-col>
            <span v-text="rigthAnswer(questionData)" />
          </a-col>
        </a-row>
        <br>
        <a-row>
          <a-col :span="2"><strong>系统判分：</strong></a-col>
          <a-col :span="4">
            {{ questionData.realityScore }}
          </a-col>
          <a-col v-if="isReScore">
            <strong>重新打分：</strong><a-input-number v-model="commentMessage.score" :min="0" :max="questionData.score" :step="0.1" @change="onChange()" />
          </a-col>
        </a-row>
        <br>
        <a-row>
          <a-col :span="2"><strong>短评</strong></a-col>
          <a-col :span="22">
            <a-textarea
              v-model="commentMessage.text"
              placeholder="输入你对学生这道作答情况的简单评价，可以不填"
              :auto-size="{ minRows: 2, maxRows: 6 }"
              @change="onChange()"
            />
          </a-col>
        </a-row>
      </div>
      <br>
    </div>
  </div>
</template>

<script>
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
export default {
  name: 'ChoiceQuestion',
  components: { ShowMarkdown },
  props: {
    question: {
      type: Object,
      default: Object
    },
    number: {
      type: Number,
      default: 0
    },
    // 选择框是否可以编辑，默认可以
    disabled: {
      type: Boolean,
      default: false
    },
    // 是否显示重新打分框
    // 默认不显示
    scoreedit: {
      type: Boolean,
      default: false
    },
    // 是否显示评论框
    comment: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      questionData: this.question,
      isEdit: this.disabled,
      isReScore: this.scoreedit,
      isComment: this.comment,
      commentMessage: {
        number: this.number,
        text: '',
        id: this.question.id,
        score: null
      }
    }
  },
  created() {
    this.commentMessage.text = this.questionData.teacherComment
  },
  methods: {
    changeAnswer(answer) {
      this.$emit('answer', answer, this.questionData.id, this.questionData.type)
    },
    changeAnswerRadio(answer) {
      this.$emit('answer', [answer.target.value], this.questionData.id, this.questionData.type)
    },
    JudgeAnswer(answer) {
      this.$emit('answer', answer.target.value, this.questionData.id, this.questionData.type)
    },
    rigthAnswer(question) {
      if (question.type === 4) {
        if (parseInt(question.rightAnswer) === 1) {
          return '对'
        } else {
          return '错'
        }
      } else {
        return question.rigthAnswer
      }
    },
    onChange() {
      this.$emit('commentMsg', this.commentMessage)
    }
  }
}
</script>

<style>
.vditor-reset img {
  width: 720px;
}
</style>
