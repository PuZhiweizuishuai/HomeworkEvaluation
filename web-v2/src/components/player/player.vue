<template>
  <div id="dplayer" ref="dplayer" />
</template>

<script>
import DPlayer from 'dplayer'
export default {
  name: 'Play',
  props: {
    video: {
      type: Object,
      default: () => {}
    },
    picurl: {
      type: String,
      default: ''
    },
    article: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      videoData: this.video,
      pic: this.picurl,
      id: this.article
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      new DPlayer({
        container: document.querySelector('#dplayer'),
        lang: 'zh-cn',
        screenshot: true,
        video: {
          url: this.videoData.fileUrl + '?key=' + encodeURIComponent(this.videoData.key) // 'https://api.dogecloud.com/player/get.mp4?vcode=5ac682e6f8231991&userId=17&ext=.mp4',
        },
        logo: '/logo.png',
        contextmenu: [
          {
            text: '不挂高数',
            link: 'https://www.buguagaoshu.com'
          }
        ],
        danmaku: {
          id: this.videoData.id,
          api: '/api/danmaku/',
          token: 'token',
          maximum: 1000,
          user: () => {
            if (this.$store.state.userInfo.id == null) {
              return 'null'
            } else {
              return this.$store.state.userInfo.id
            }
          },
          bottom: '15%',
          unlimited: true
        }
      })
    }
  }
}
</script>

<style>
#dplayer {
  height: 500px;
}
</style>
