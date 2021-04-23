<template>
  <v-container fluid>
    <v-row>
      <v-col>
        <router-link to="/tools" style="float: left">
          <v-btn
            icon
            x-large
            color="success"
          >
            <v-icon>mdi-arrow-left-thick</v-icon>

          </v-btn>
        </router-link>
        <h3 style="    margin-top: 12px;">电视直播</h3>
      </v-col>
    </v-row>
    <v-col />
    <v-row>
      <v-col>
        <h3>当前播放： {{ nowTV.title }}</h3>
      </v-col>
    </v-row>
    <v-row v-resize="onResize">
      <v-col :cols="colsLeft">
        <div id="dplayer" ref="dplayer" style="height: 500px" />
      </v-col>
      <v-col :cols="colsRight">
        <v-card
          elevation="16"
          height="500"
          outlined
        >
          <v-virtual-scroll
            :items="tvList"
            height="500"
            item-height="64"
          >
            <template v-slot:default="{ item }">
              <v-list-item :key="item.id" link @click="changeTV(item)">
                <v-list-item-content>
                  <v-list-item-title v-text="item.title" />
                </v-list-item-content>
              </v-list-item>
              <v-divider />
            </template>
          </v-virtual-scroll>
        </v-card>
      </v-col>
    </v-row>
    <v-col />
  </v-container>
</template>

<script>
import DPlayer from 'dplayer'

export default {
  data() {
    return {
      tvList: [],
      dplayer: null,
      nowTV: {
        title: '暂未选择'
      },
      colsLeft: 10,
      colsRight: 2,
      windowSize: {
        x: 0,
        y: 0
      }
    }
  },
  created() {
    this.getTVList()
  },
  mounted() {
    window.Hls = require('hls.js')
    this.init()
  },
  methods: {
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x > 800) {
        this.colsLeft = 10
        this.colsRight = 2
      } else {
        this.colsLeft = 12
        this.colsRight = 12
      }
    },
    getTVList() {
      this.httpGet('/index/tvlist', (json) => {
        this.tvList = json.data || []
      })
    },
    init() {
      this.dplayer = new DPlayer({
        container: document.querySelector('#dplayer'),
        lang: 'zh-cn',
        screenshot: true,
        video: {
          //
        },
        logo: '/logo.png'
      })
    },
    changeTV(item) {
      this.nowTV = item
      this.dplayer.switchVideo({ url: item.link })
      this.dplayer.play()
    }
  }
}
</script>

<style>

</style>
