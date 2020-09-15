<template>
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
      <a-col :span="1">答：</a-col>
      <a-col style="margin-left: 15px" :span="15">
        <Vditor v-if="isEdit == false" :markdown="questionData.otherAnswer" :height="300" :hide="false" :idname="`Vditor-${number}`" :uploadurl="uploadurl" @vditor-input="getAnswer" />
        <ShowMarkdown v-if="isEdit" :anchor="0" :markdown="questionData.otherAnswer" :speech="false" />
      </a-col>
    </a-row>
    <br>
    <div v-if="isComment">
      <a-row>
        <a-col :span="2"><strong>参考答案：</strong></a-col>
        <a-col>
          {{ questionData.rightAnswer }}
        </a-col>
      </a-row>
      <br>
      <a-row>
        <a-col :span="2">
          当前得分：
        </a-col>
        <a-col>
          <strong>{{ questionData.realityScore }}</strong>
        </a-col>
      </a-row>
      <br>
      <a-row v-if="isReScore">
        <a-col :span="8"><strong>非选择判断题，系统不会自动判分，请手动打分：</strong></a-col>
        <a-col>
          <span style="color: red;font-weight: bolder;font-size: 20px;">打分-></span>
          <a-icon type="edit" theme="twoTone" two-tone-color="#eb2f96" />
          <a-input-number v-model="commentMessage.score" :min="0" :max="questionData.score" :step="0.1" @change="onChange()" />
          <a-alert :id="`question-${number}`" message="缺少成绩数据！" type="error" banner style="display: none" />
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
</template>

<script>
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
import Vditor from '@/components/vditor/vditor.vue'
export default {
  name: 'QuestionAndAnswer',
  components: { ShowMarkdown, Vditor },
  props: {
    question: {
      type: Object,
      default: Object
    },
    number: {
      type: Number,
      default: 0
    },
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
      id: 0,
      questionData: this.question,
      uploadurl: this.SERVER_API_URL + `/upload/file?type=homework&homework=${this.$route.params.id}`,
      answer: '',
      isEdit: this.disabled,
      isReScore: this.scoreedit,
      isComment: this.comment,
      commentMessage: {
        number: this.number,
        text: '',
        id: this.question.id,
        score: null,
        type: this.question.type
      }
    }
  },
  created() {
    this.id = this.$route.params.id
    this.commentMessage.text = this.questionData.teacherComment
  },
  methods: {
    getAnswer(data) {
      this.answer = data
      this.$emit('answer', data, this.questionData.id)
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
