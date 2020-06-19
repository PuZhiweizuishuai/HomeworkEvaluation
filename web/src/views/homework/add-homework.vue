<template>
  <div>
    <a-page-header
      style="margin-bottom: 24px;"
      title="创建作业"
      sub-title="为课程创建新的作业/测验/考试"
    />

    <a-steps :current="current">
      <a-step v-for="item in steps" :key="item.title" :title="item.title" />
    </a-steps>
    <a-divider />
    <div class="steps-content">
      <div v-if="current === 0">
        <a-form-model ref="homeworkForm" :rules="homeworkRules" :model="homeworkFrom" :label-col="{span: 4}" :wrapper-col="{span: 14}">

          <a-form-model-item prop="title" label="作业标题">
            <a-input v-model="homeworkFrom.title" />
          </a-form-model-item>

          <a-form-model-item label="作业要求或描述">
            <Vditor :placeholder="'详细的作业要求，可以包含图片，视频，文件等'" :markdown="homeworkFrom.content" :uploadurl="uploadurl" @vditor-input="setVditorInput" />
          </a-form-model-item>

          <a-form-model-item label="作业时间">
            <a-range-picker
              :locale="locale"
              :input-read-only="true"
              :disabled-date="disabledDate"
              :show-time="{
                hideDisabledOptions: true,
                defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('00:00:00', 'HH:mm:ss')],
              }"
              format="YYYY-MM-DD HH:mm:ss"
              @change="startAndEndTime"
            />
          </a-form-model-item>
          <a-form-model-item label="多选半对给分">
            <a-radio-group v-model="homeworkFrom.sourceType">
              <a-radio value="0">
                给一半分
              </a-radio>
              <a-radio value="1">
                不给分
              </a-radio>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="作业类型">
            <a-radio-group v-model="homeworkFrom.type">
              <a-radio value="0">
                普通作业(在时间区间内完成即可，可重复进入)
              </a-radio>
              <a-radio value="1">
                测验（进入后需在规定时间内完成）
              </a-radio>
              <a-radio value="2">
                考试（在时间区间内完成，设置开考后几分钟不能进入）
              </a-radio>
            </a-radio-group>
          </a-form-model-item>

          <!--  @change="showlimitTime()" -->

          <a-form-model-item v-if="homeworkFrom.type == 1" label="测验时间(单位：分钟)">
            <a-input v-model="homeworkFrom.time" placeholder="(单位：分钟)" type="number" />
          </a-form-model-item>
          <a-form-model-item v-if="homeworkFrom.type == 2" label="开考后几分钟不能进入">
            <a-input v-model="homeworkFrom.limitTime" placeholder="(单位：分钟)" type="number" />
          </a-form-model-item>
        </a-form-model>
      </div>
      <div v-if="current === 1">
        <ImportQuestion :questionlist="homeworkFrom.questionsModels" @question-list="setQuestionList" />
      </div>
      <div v-if="current === 2" style="text-align: center;">
        <a-row>
          <a-divider>作业标题</a-divider>
          <h4 v-text="homeworkFrom.title" /><br>
          <a-divider>作业要求</a-divider>
          <ShowMarkdown :markdown="homeworkFrom.content" /><br>
          <a-divider>基本信息</a-divider>
          <p>
            时间： <span v-text="getTime()" />
          </p>
          <p>
            类型： <span v-text="getType()" />
          </p>
          <p>
            总分： <span v-text="getScoreNumber()" />
          </p>
        </a-row><br>
        <a-divider>题目列表</a-divider>
        <div v-for="(item, index) in homeworkFrom.questionsModels" :key="index">
          题目： {{ item.question }}  分值：{{ item.score }}<br><br>
        </div>
      </div>
    </div>

    <div class="steps-action" style="margin-top: 24px;text-align: center;">
      <a-button v-if="current < steps.length - 1" type="primary" @click="next">
        下一步
      </a-button>
      <a-button
        v-if="current == steps.length - 1"
        type="primary"
        @click="publishHomework()"
      >
        发布作业
      </a-button>
      <a-button v-if="current > 0" style="margin-left: 8px" @click="prev">
        上一步
      </a-button>
    </div>
  </div>
</template>

<script>
import Vditor from '@/components/vditor/vditor.vue'
import ShowMarkdown from '@/components/vditor/show-markdown.vue'
import ImportQuestion from '@/views/homework/import-question.vue'
import moment from 'moment'
import 'moment/locale/zh-cn'
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN'
import TimeUtil from '@/utils/time-util.vue'
export default {
  name: 'AddHomework',
  components: { Vditor, ImportQuestion, ShowMarkdown },
  data() {
    return {
      id: 0,
      uploadurl: this.SERVER_API_URL + '/upload/file',
      homeworkFrom: {
        id: null,
        title: '',
        content: '',
        openTime: 0,
        closeTime: 0,
        classNumber: this.id,
        type: '0',
        sourceType: '0',
        limitTime: 0,
        time: 0,
        questionsModels: []
      },
      locale,
      moment,
      current: 0,
      steps: [
        {
          title: '创建作业',
          content: 'First-content'
        },
        {
          title: '导入题目',
          content: 'Second-content'
        },
        {
          title: '提交数据',
          content: '提交数据'
        }
      ],
      // 数据校验规则
      homeworkRules: {
        title: [
          { required: true, message: '标题不能为空', trigger: 'blur' },
          { min: 3, max: 50, message: '长度再3到50个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.id = this.$route.params.id
  },
  methods: {
    publishHomework() {
      this.homeworkFrom.classNumber = this.id

      fetch(this.SERVER_API_URL + '/homework/add', {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(this.homeworkFrom)
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.$notification['success']({
              message: '创建作业成功',
              description:
          '即将为您跳转到作业页面'
            })
            this.$router.push(`/curriculum/learn/${this.id}/homework`)
          } else {
            this.$message.warning(`创建作业失败：${json.message}`)
            return null
          }
        })
        .catch(e => {
          return null
        })
    },
    setQuestionList(data) {
      this.homeworkFrom.questionsModels = data
    },
    setVditorInput(data) {
      this.homeworkFrom.content = data
    },
    next() {
      if (this.current === 0) {
        if (this.homeworkFrom.openTime === 0 || this.homeworkFrom.closeTime === 0) {
          this.$message.warning('作业时间不能为空')
          return
        }
        if (this.homeworkFrom.type === '2') {
          if (parseInt(this.homeworkFrom.limitTime) === 0 || parseInt(this.homeworkFrom.time) === 0) {
            this.$message.warning('时间限制不能为空')
            return
          }
        } else if (this.homeworkFrom.type === '1') {
          if (parseInt(this.homeworkFrom.time) === 0) {
            this.$message.warning('测验时间不能为空')
            return
          }
        }
        this.homeworkSubmit()
        return
      }
      if (this.current === 1) {
        if (this.homeworkFrom.questionsModels.length === 0) {
          this.$message.warning('请导入或添加题目')
          return
        }
        this.current++
      }
      // this.current++
    },
    homeworkSubmit() {
      this.$refs.homeworkForm.validate(valid => {
        if (valid) {
          this.current++
          return true
        } else {
          return false
        }
      })
    },
    prev() {
      this.current--
    },
    disabledDate(current) {
      return current && current < moment().startOf('day')
    },
    startAndEndTime(data, str) {
      const start = new Date(data[0]._d.toString()).getTime()
      const endTime = new Date(data[1]._d.toString()).getTime()
      this.homeworkFrom.openTime = start
      this.homeworkFrom.closeTime = endTime
    },
    getTime() {
      return TimeUtil.formateTime(this.homeworkFrom.openTime, this.homeworkFrom.endTime)
    },
    getType() {
      if (this.homeworkFrom.type === '0') {
        return '普通作业'
      } else if (this.homeworkFrom.type === '1') {
        return '测验'
      } else {
        return '考试'
      }
    },
    getScoreNumber() {
      let score = 0
      this.homeworkFrom.questionsModels.forEach((s) => {
        score = score + s.score
      })
      return score
    }
  }
}
</script>

<style scoped>
.steps-content {
  margin-top: 24px;

}
</style>
