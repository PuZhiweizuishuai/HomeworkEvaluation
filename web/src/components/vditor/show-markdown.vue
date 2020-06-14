<template>
  <div>
    <div ref="markdownView" />
  </div>
</template>

<script>
import Vditor from 'vditor'
import 'vditor/src/assets/scss/index.scss'
export default {
  name: 'ShowMarkdown',
  props: {
    markdown: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      markdownText: this.markdown
    }
  },
  mounted() {
    this.previevMarkdown()
  },
  updated() {
    // this.previevMarkdown()
  },
  methods: {
    previevMarkdown() {
      Vditor.preview(this.$refs.markdownView,
        this.markdownText, {
          speech: {
            enable: true
          },
          emojiPath: '/emoji',
          anchor: 1
        })
    }
  }
}

</script>
