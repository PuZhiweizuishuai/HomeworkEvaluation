<template>
  <v-row v-resize="onResize">
    <v-col :cols="cloLeft">
      <v-expansion-panels v-model="panels">
        <v-expansion-panel>
          <v-expansion-panel-header>
            <v-row>
              <v-col cols="5">
                <h3>
                  {{ homeWorkData.title }}

                  <v-chip
                    class="ma-2"
                    small
                    :color="Constant.HOMEWORK__TYPE_COLOR[homeWorkData.type]"
                  >
                    {{ Constant.HOMEWORK_TYPE[homeWorkData.type] }}
                  </v-chip>
                </h3>
              </v-col>
              <v-col cols="5">
                截止时间：{{ TimeUtil.formateTimeToChinese(homeWorkData.closeTime) }}
              </v-col>
            </v-row>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <v-row>
              <v-col cols="12">
                简介：
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                <ShowMarkdown :markdown="homeWorkData.content" />
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                <v-divider />
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                开始时间：<strong>{{ TimeUtil.formateTimeToChinese(homeWorkData.openTime) }} </strong>
                结束时间：<strong>{{ TimeUtil.formateTimeToChinese(homeWorkData.closeTime) }} </strong>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                满分：{{ homeWorkData.score }}
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                注意：请务必在截止时间之前提交，截止时间后的将无法提交，你的成绩会被记为 0 分
              </v-col>
            </v-row>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-col>
    <v-col :cols="colRight">
      <v-btn v-if="homeWorkData.status === 1" depressed color="primary" @click="goToTest">进入测验</v-btn>
      <v-btn v-if="homeWorkData.status === 0" depressed disabled>暂未开始</v-btn>
      <v-btn v-if="homeWorkData.status === 4" depressed color="success" @click="goToTest">查看</v-btn>
    </v-col>
  </v-row>
</template>

<script>
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
import TimeUtil from '@/utils/time-util.vue'
import Constant from '@/utils/constant.vue'
/**
 * 作业简单介绍卡片
 */
export default {
  name: 'HomeworkInfoCard',
  components: {
    ShowMarkdown
  },
  props: {
    info: {
      type: Object,
      default: null
    },
    show: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      Constant,
      TimeUtil,
      homeWorkData: this.info,
      panels: this.show,
      windowSize: {
        x: 0,
        y: 0
      },
      cloLeft: 10,
      colRight: 2
    }
  },
  methods: {
    goToTest() {
      this.$router.push(`/course/learn/${this.homeWorkData.classNumber}/exam/homework/${this.homeWorkData.id}`)
    },
    onResize() {
      this.windowSize = { x: window.innerWidth, y: window.innerHeight }
      if (this.windowSize.x < 900) {
        this.cloLeft = 12
        this.colRight = 12
      } else {
        this.cloLeft = 10
        this.colRight = 2
      }
    }
  }
}
</script>

<style>

</style>
