<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="homeworkInfo.title"
          placeholder="作业标题"
          label="标题"
          :rules="[() => homeworkInfo.title != null || '请输入作业标题邮箱']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        作业要求或描述:
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <Vditor
          :placeholder="'详细的作业要求，可以包含图片，视频，文件等'"
          :markdown="homeworkInfo.content"
          :uploadurl="uploadurl"
          :height="400"
          @vditor-input="setVditorInput"
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        开始时间：
      </v-col>
    </v-row>
    <TimeForm @time="getOpenTime" />
    <v-row justify="center">
      <v-col cols="10">
        结束时间：
      </v-col>
    </v-row>
    <TimeForm @time="getCloseTime" />
    <v-row justify="center">
      <v-col cols="10">
        多选选对一半给分策略：
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-radio-group v-model="homeworkInfo.sourceType" :row="true">
          <v-radio label="给一半分" :value="0" />
          <v-radio label="不给分" :value="1" />
        </v-radio-group>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        是否开启互评：
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-radio-group v-model="homeworkInfo.evaluation" :row="true">
          <v-radio label="关闭" :value="0" />
          <v-radio label="开启" :value="1" />
        </v-radio-group>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        作业类型：
        <v-subheader>一点说明：
          普通作业：在作业区间内任意时间内均可提交。
          限时测验：进入后必须在限定的时间内提交，并且提交时间不能晚于结束时间，否则失去提交机会。
          考试：只能在限定时间进入，并且在限定时间提交</v-subheader>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-radio-group v-model="homeworkInfo.type" :row="true">
          <v-radio label="普通作业" :value="0" />
          <v-radio label="限时测验" :value="1" />
          <v-radio label="考试" :value="2" />
        </v-radio-group>
      </v-col>
    </v-row>
    <v-row v-if="homeworkInfo.type == 1" justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="homeworkInfo.time"
          placeholder="规定测验时间(单位：分钟)"
          label="规定测验时间(单位：分钟)"
          type="number"
          :rules="[() => homeworkInfo.time != null || '测验时间不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row v-if="homeworkInfo.type == 2" justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="homeworkInfo.limitTime"
          type="number"
          placeholder="规定开考后超过几分钟不能进入(单位：分钟)"
          label="规定开考后超过几分钟不能进入(单位：分钟)"
          :rules="[() => homeworkInfo.limitTime != null || '规定进入时间不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-btn
        color="primary"
        @click="backHomeworkInfo"
      >
        下一步
      </v-btn>
    </v-row>
    <v-snackbar
      v-model="showMessage"
      :top="true"
      :timeout="3000"
    >
      {{ message }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="pink"
          text
          v-bind="attrs"
          @click="showMessage = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import Vditor from '@/components/vditor/vditor.vue'
import TimeForm from '@/components/form/time-form.vue'
/**
 * 创建新作业时，输入作业基本信息的 from
 */
export default {
  name: 'InfoFrom',
  components: {
    Vditor,
    TimeForm
  },
  data() {
    return {
      homeworkInfo: {
        title: '',
        content: '',
        openTime: '',
        closeTime: '',
        sourceType: 0,
        evaluation: 0,
        type: 0,
        limitTime: 0,
        time: 0
      },
      uploadurl: this.SERVER_API_URL + '/upload/file',
      message: '',
      showMessage: false
    }
  },
  methods: {
    setVditorInput(value) {
      this.homeworkInfo.content = value
    },
    getOpenTime(value) {
      this.homeworkInfo.openTime = value
    },
    getCloseTime(value) {
      this.homeworkInfo.closeTime = value
    },
    backHomeworkInfo() {
      if (this.judge()) {
        console.log(this.homeworkInfo)
        this.$emit('homework', this.homeworkInfo)
      }
    },
    judge() {
      if (this.homeworkInfo.title === null || this.homeworkInfo.title === '') {
        this.message = '作业标题不能为空！'
        this.showMessage = true
        return false
      }
      if (this.homeworkInfo.openTime === null || this.homeworkInfo.openTime === '') {
        this.message = '作业开始时间不能为空！'
        this.showMessage = true
        return false
      }
      if (this.homeworkInfo.closeTime === null || this.homeworkInfo.closeTime === '') {
        this.message = '作业结束时间不能为空！'
        this.showMessage = true
        return false
      }
      if (this.homeworkInfo.closeTime === null || this.homeworkInfo.closeTime === '') {
        this.message = '作业结束时间不能为空！'
        this.showMessage = true
        return false
      }
      if (this.homeworkInfo.type === 1) {
        if (this.homeworkInfo.time === null || this.homeworkInfo.time <= 0) {
          this.message = '测验规定时间不能为空！并且必须大于0'
          this.showMessage = true
          return false
        }
      } else if (this.homeworkInfo.type === 2) {
        if (this.homeworkInfo.limitTime === null || this.homeworkInfo.limitTime <= 0) {
          this.message = '考试限定进入时间不能为空！并且必须大于0'
          this.showMessage = true
          return false
        }
      }
      return true
    }
  }
}
</script>

<style>

</style>
