<template>
  <div>
    <div>
      <a-row>
        <a-col :span="1">
          {{ number }} :
        </a-col>
        <a-col :span="23">
          <ShowMarkdown :anchor="0" :markdown="questionData.question" :speech="false" />
        </a-col>
      </a-row>
      <br>
      <a-row>
        <a-col :span="1" />

        <a-col :span="23">
          <a-radio-group v-if="questionData.type == 0" style="margin-left: 15px" @change="changeAnswerRadio">
            <a-radio v-for="(item, index) in questionData.options" :key="index" :value="item">
              {{ item }}
            </a-radio>
          </a-radio-group>
          <a-radio-group v-if="questionData.type == 4" style="margin-left: 15px" @change="JudgeAnswer">
            <a-radio :value="1">
              对
            </a-radio>
            <a-radio :value="0">
              错
            </a-radio>
          </a-radio-group>
          <a-checkbox-group
            v-if="questionData.type == 1"
            style="margin-left: 15px"
            :options="questionData.options"
            @change="changeAnswer"
          />
        </a-col>
      </a-row>
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
    }
  },
  data() {
    return {
      questionData: this.question
    }
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
    }
  }
}
</script>

<style>
.vditor-reset img {
  width: 720px;
}
</style>
