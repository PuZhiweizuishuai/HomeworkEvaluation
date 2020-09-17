<template>
  <div>
    <a-form-model
      ref="ruleForm"
      :model="course"
      :rules="rules"
      :label-col="labelCol"
      :wrapper-col="wrapperCol"
    >
      <a-form-model-item ref="curriculumName" label="课程名" prop="curriculumName">
        <a-input
          v-model="course.curriculumName"
          :disabled="isShowcurriculumName"
          @blur="
            () => {
              $refs.curriculumName.onFieldBlur();
            }
          "
        />
        <a-button @click="() => {isShowcurriculumName = !isShowcurriculumName}">修改</a-button>
      </a-form-model-item>

      <a-form-model-item ref="courseTag" label="课程分类" prop="courseTag">
        <span> 当前分类：<span style="font-weight: bolder;margin-right: 20px;" v-text="getNowTag()" /> </span>
        <a-cascader
          v-if="showClassTag"
          :options="treeData"
          expand-trigger="hover"
          placeholder="请选择"
          @change="courseTagOnChange"
          @blur="
            () => {
              $refs.courseTag.onFieldBlur();
            }
          "
        />
        <a-button @click="() => {showClassTag = !showClassTag}">修改</a-button>
      </a-form-model-item>

      <a-form-model-item label="课程号" prop="classNumber">
        <a-input-number
          id="inputNumber"
          v-model="course.classNumber"
          :min="1"
          :max="10000"
          @blur="
            () => {
              $refs.classNumber.onFieldBlur();
            }
          "
        />
      </a-form-model-item>

      <a-form-model-item label="开课~结课时间" required>
        <a-range-picker
          v-if="showEditTime"
          :locale="locale"
          :input-read-only="true"

          :show-time="{
            hideDisabledOptions: true,
            defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('12:00:00', 'HH:mm:ss')],
          }"
          format="YYYY-MM-DD HH:mm:ss"
          @change="panelChangeTime"
        />
        <!-- :disabled-date="disabledDate" -->
        <span v-text="formateTime()" />
        <a-button @click="() => {showEditTime = !showEditTime}">修改</a-button>
      </a-form-model-item>

      <a-form-model-item ref="simpleInfo" label="课程简介" prop="simpleInfo">
        <a-textarea
          v-model="course.simpleInfo"
          :disabled="showSimpleInfo"
          placeholder="显示再标题下面的简短介绍(50字以内)"
          :auto-size="{ minRows: 2, maxRows: 6 }"
          @blur="
            () => {
              $refs.simpleInfo.onFieldBlur();
            }
          "
        />
        <a-button @click="() => {showSimpleInfo = !showSimpleInfo}">修改</a-button>
      </a-form-model-item>

      <a-form-model-item ref="curriculumInfo" label="详细介绍" prop="curriculumInfo">
        <Vditor
          :placeholder="'详细的介绍，可以包含图片，视频，文件等'"
          :uploadurl="uploadurl"
          :markdown="course.curriculumInfo"
          @vditor-input="setVditorInput"
        />
      </a-form-model-item>

      <a-form-model-item label="学生进入课程方式：">
        <span v-text="accessMethodStr()" />
        <a-radio-group v-if="showAccessMethod" v-model="course.accessMethod" button-style="solid" @change="isShowPassword">
          <a-radio-button value="0">
            邀请进入（通过导入用户或邀请码加入课程）
          </a-radio-button>
          <a-radio-button value="1">
            密码进入（通过密码验证，进入课程）
          </a-radio-button>
          <a-radio-button value="2">
            公开（注册用户均可加入）
          </a-radio-button>
        </a-radio-group>
        <a-button @click="showAccessMethod = !showAccessMethod">修改</a-button>
      </a-form-model-item>

      <a-form-model-item v-if="showPasswordInput" label="课程密码：">
        <a-input-password v-model="course.password" placeholder="请输入课程密码" />
      </a-form-model-item>

      <a-form-model-item name="joinTimeLimit" label="是否限制加入时间：">
        <span v-text="joinTimeLimitStr()" />
        <a-radio-group v-if="showJoinTimeLimit" v-model="course.joinTimeLimit" button-style="solid" @change="isShowJoinTime">
          <a-radio-button value="0">
            不限制（结课前均可加入课程）
          </a-radio-button>
          <a-radio-button value="1">
            限制加入时间
          </a-radio-button>
        </a-radio-group>
        <a-button @click="showJoinTimeLimit = !showJoinTimeLimit">修改</a-button>
      </a-form-model-item>

      <a-form-model-item v-if="showJoinTime" label="加入截止时间：">
        <a-date-picker
          :locale="locale"
          :show-time="{
            hideDisabledOptions: true,
            defaultValue: moment('00:00:00', 'HH:mm:ss'),
          }"
          @change="getJoinTime"
        />
      </a-form-model-item>

      <!-- 图片上传 -->
      <a-form-model-item label="标题图上传">
        <Upload :uploadurl="uploadurl" @success-file="showMessageFromChild" />
      </a-form-model-item>

      <a-form-model-item :wrapper-col="{ span: 14, offset: 4 }">
        <a-button type="primary" @click="onSubmit">
          修改
        </a-button>
      </a-form-model-item>
    </a-form-model>
    <a-back-top />
  </div>
</template>

<script>

import Vditor from '@/components/vditor/vditor.vue'
import Upload from '@/components/upload/UploadCropper.vue'
import moment from 'moment'
import 'moment/locale/zh-cn'
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN'
import TimeUtil from '@/utils/time-util.vue'
export default {
  name: 'EditCurriculum',
  components: { Vditor, Upload },

  data() {
    return {
      classId: this.id,
      uploadurl: this.SERVER_API_URL + '/upload/file',
      locale,
      moment,
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      other: '',
      course: {
        id: 0,
        curriculumName: '',
        courseTag: 0,
        fatherCourseTag: 0,
        classNumber: 0,
        openingTime: '',
        closeTime: '',
        simpleInfo: '',
        curriculumInfo: '',
        curriculumImageUrl: '',
        accessMethod: '0',
        joinTimeLimit: '0',
        joinTime: '',
        password: ''
      },
      showPasswordInput: false,
      showJoinTime: false,
      treeData: [],

      // 修改显示部分
      isShowcurriculumName: true,
      showClassTag: false,
      showEditTime: false,
      showSimpleInfo: true,
      showAccessMethod: false,
      showJoinTimeLimit: false,
      rules: {
        curriculumName: [
          { required: true, message: '请输入课程名', trigger: 'blur' },
          { min: 1, max: 50, message: '最长不超过50个字符', trigger: 'blur' }
        ],
        courseTag: [
          { required: true, message: '请选择课程分类', trigger: 'blur' }
        ],
        classNumber: [{ required: true, message: '输入课程号，便于区分多个班的同名课程', trigger: 'blur' }],
        simpleInfo: [
          { required: true, message: '请输入课程简介', trigger: 'blur' },
          { min: 1, max: 50, message: '最长不超过50个字符', trigger: 'blur' }
        ],
        curriculumInfo: [
          { required: true, message: '请输入课程详细简介', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.classId = this.$route.params.id
    this.getClassInfo()
    this.getTreeList()
  },
  methods: {
    joinTimeLimitStr() {
      if (this.course.joinTimeLimit === 0) {
        return '不限制（结课前均可加入课程）'
      } else {
        return '限制加入时间'
      }
    },
    accessMethodStr() {
      if (this.course.accessMethod === 0) {
        return '邀请进入（通过导入用户或邀请码加入课程）'
      } else if (this.course.accessMethod === 1) {
        return '密码进入（通过密码验证，进入课程）'
      } else {
        return '公开（注册用户均可加入）'
      }
    },
    formateTime() {
      return TimeUtil.formateTime(this.course.openingTime, this.course.closeTime)
    },
    getNowTag() {
      let flag = false
      if (this.course.fatherCourseTag === this.course.courseTag) {
        flag = true
      }
      const father = this.treeData[this.course.fatherCourseTag - 1]
      // console.log(this.treeData[this.course.fatherCourseTag - 1].label)
      let label = this.treeData[this.course.fatherCourseTag - 1].label
      if (flag) {
        return label
      }

      for (let i = 0; i < father.children.length; i++) {
        if (father.children[i].key === this.course.courseTag) {
          label = label + ': ' + father.children[i].label
          break
        }
      }
      return label
    },
    onSubmit() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          if (this.course.curriculumImageUrl == null || this.course.curriculumImageUrl === '') {
            this.$message.error('请上传课程标题图')
            return false
          }
          this.createClass()
        } else {
          return false
        }
      })
    },
    getClassInfo() {
      fetch(this.SERVER_API_URL + `/curriculum/info/${this.classId}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          this.course = json.data
        })
        .catch(e => {
          this.$message.error('网络异常，请检查网络后重试')
        })
    },
    createClass() {
      fetch(this.SERVER_API_URL + `/teacher/update/curriculum`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(this.course)
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.$notification['success']({
              message: '修改成功',
              description:
          '即将为你刷新课程数据'
            })
          } else {
            this.$message.error(json.message)
          }
        })
        .catch(e => {
          this.$message.error('网络异常，请检查网络后重试')
        })
    },
    resetForm() {
      this.$refs.ruleForm.resetFields()
    },
    getTreeList() {
      fetch(this.SERVER_API_URL + '/course/tag/list', {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          this.treeData = this.getTreeData(json.data)
        })
        .catch(e => {

        })
    },
    /**
     * 将接收到数据的 key 值替换为 a-tree-select 可解析的 key
     */
    getTreeData(data) {
      if (data === undefined) {
        return null
      }
      const result = data.map(o => {
        return {
          label: o.courseMajor,
          key: o.id,
          value: o.id,
          children: this.getTreeData(o.children)
        }
      })
      return result
    },
    courseTagOnChange(value, selectedOptions) {
      this.course.fatherCourseTag = value[0]
      if (value.length === 1) {
        this.course.courseTag = value[0]
      } else {
        this.course.courseTag = value[1]
      }
    },
    range(start, end) {
      const result = []
      for (let i = start; i < end; i++) {
        result.push(i)
      }
      return result
    },
    disabledDate(current) {
      // Can not select days before today and today
      return current && current < moment().startOf('day')
    },
    panelChangeTime(date, dateString) {
      this.course.openingTime = date[0]._d.toString()
      this.course.closeTime = date[1]._d.toString()
    },
    isShowPassword(data) {
      if (this.course.accessMethod === '1') {
        this.showPasswordInput = true
      } else {
        this.showPasswordInput = false
      }
    },
    isShowJoinTime() {
      if (this.course.joinTimeLimit === '1') {
        this.showJoinTime = true
      } else {
        this.showJoinTime = false
      }
    },
    getJoinTime(date, dateString) {
      this.course.joinTime = date._d.toString()
    },
    showMessageFromChild(data) {
      this.course.curriculumImageUrl = data
    },
    setVditorInput(data) {
      this.course.curriculumInfo = data
    }
  }
}
</script>
