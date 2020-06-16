<template>
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
      <a-col :span="1">答：</a-col>
      <a-col style="margin-left: 15px" :span="15">
        <Vditor :height="300" :hide="false" :idname="`Vditor-${number}`" :uploadurl="uploadurl" @vditor-input="getAnswer" />
      </a-col>
    </a-row>
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
    }
  },
  data() {
    return {
      id: 0,
      questionData: this.question,
      uploadurl: this.SERVER_API_URL + `/upload/file?type=homework&homework=${this.$route.params.id}`,
      answer: ''
    }
  },
  created() {
    this.id = this.$route.params.id
  },
  methods: {
    getAnswer(data) {
      this.answer = data
      this.$emit('answer', data, this.questionData.id)
    }
  }
}
</script>

<style>
.vditor-reset img {
  width: 720px;
}
</style>
