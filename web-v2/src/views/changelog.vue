<template>
  <v-container>
    <v-row>
      <v-col>
        <div ref="changeLogView" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Vditor from 'vditor'
import 'vditor/src/assets/scss/index.scss'
export default {
  created() {
    this.getChangeLog()
  },
  methods: {
    getChangeLog() {
      fetch(`/changelog.md`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.text())
        .then(text => {
          // console.log(text)
          this.previevMarkdown(text)
        })
        .catch(e => {
          return null
        })
    },
    previevMarkdown(text) {
      Vditor.preview(this.$refs.changeLogView,
        text, {
          speech: {
            enable: false
          },
          cdn: '/vditor',
          theme: {
            path: '/vditor/dist/css/content-theme'
          },
          emojiPath: '/emoji',
          anchor: true
        })
    }
  }
}
</script>

<style>

</style>
