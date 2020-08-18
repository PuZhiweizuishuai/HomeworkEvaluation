<template>
  <div>
    <a-collapse>
      <a-collapse-panel key="1" :header="setHeader()">
        <div>
          <ShowMarkdown :anchor="0" :markdown="homeworkData.content" :speech="false" />
        </div>
        <a-divider />
        <div>
          <a-descriptions>
            <a-descriptions-item label="截止时间">
              <span v-text="timeFormate(homeworkData.closeTime)" />
            </a-descriptions-item>
            <a-descriptions-item label="开始时间">
              <span v-text="timeFormate(homeworkData.openTime)" />
            </a-descriptions-item>
            <a-descriptions-item label="满分">
              <span v-text="homeworkData.score" />
            </a-descriptions-item>
            <a-descriptions-item label="注意">
              请务必在截止时间之前提交，截止时间后的将无法提交，你的成绩会被记为 0 分
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import ShowMarkdown from '@/components/vditor/show-markdown.vue'

export default {
  name: 'HomeworkInfo',
  components: { ShowMarkdown },
  props: {
    homework: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      id: 0,
      homeworkData: this.homework
    }
  },
  created() {
    this.id = this.$route.params.id
  },
  methods: {
    setHeader() {
      return `${this.homeworkData.title}  (截止日期：${TimeUtil.formateTimeToChinese(this.homeworkData.closeTime)})`
    },
    timeFormate(date) {
      return TimeUtil.formateTimeToChinese(date)
    }
  }
}
</script>

<style scoped>

</style>
