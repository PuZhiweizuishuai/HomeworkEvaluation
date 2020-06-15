<template>
  <div>
    <div>
      <ShowMarkdown :markdown="" />
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
      default: null
    }
  },
  data() {
    return {
      questionData: this.question
    }
  }
}
</script>
