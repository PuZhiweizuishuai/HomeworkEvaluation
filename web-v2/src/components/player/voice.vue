<template>
  <div id="aplayer" />
</template>

<script>
import APlayer from 'aplayer'
import 'aplayer/dist/APlayer.min.css'

export default {
  props: {
    voice: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      ap: null,
      vioceData: this.voice
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      this.ap = new APlayer({
        container: document.querySelector('#aplayer'),
        audio: [{
          name: this.vioceData.title,
          artist: this.vioceData.createTeacher,
          url: this.vioceData.fileUrl + '?key=' + encodeURIComponent(this.vioceData.key)

        }]
      })
    },
    destroy() {
      this.ap.destroy()
    }
  }
}
</script>

<style>

</style>
