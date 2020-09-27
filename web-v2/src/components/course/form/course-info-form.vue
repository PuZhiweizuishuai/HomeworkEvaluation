<template>
  <v-container>
    <v-card outlined>
      <v-row justify="center">
        <v-col cols="10">
          <h3>课程信息：</h3>
        </v-col>
      </v-row>
      <!-- 课程名 -->
      <v-row justify="center">
        <v-col cols="10">
          <v-text-field
            v-model="course.curriculumName"
            label="课程名"
            placeholder="课程名"
            :rules="[() => course.curriculumName != null || '课程名不能为空！']"
            clearable
          />
        </v-col>
      </v-row>
      <!-- 课程分类 -->
      <Cascader :label="`课程分类`" :select="true" :values="[course.fatherCourseTag, course.courseTag]" @tag="getCourseTag" />
      <!-- 课程号 -->
      <v-row justify="center">
        <v-col cols="10">
          <v-text-field
            v-model="course.classNumber"
            label="课程号(当前版本已不再需要，直接默认值就好)"
            placeholder="课程号"

            clearable
            type="number"
          />
        </v-col>
      </v-row>
      <!-- 开课和结课时间 -->
      <v-row justify="center">
        <!-- 开课时间 -->
        <v-col cols="5">
          <v-menu
            ref="startTime"
            v-model="startTime"
            :close-on-content-click="false"
            :return-value.sync="course.openingTime"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="course.openingTime"
                label="开课时间："
                prepend-icon="mdi-calendar-month"
                readonly
                v-bind="attrs"
                v-on="on"
              />
            </template>
            <v-date-picker v-model="course.openingTime" no-title scrollable locale="zh-cn">
              <v-spacer />
              <v-btn text color="primary" @click="startTime = false">取消</v-btn>
              <v-btn text color="primary" @click="$refs.startTime.save(course.openingTime)">确认</v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>
        <!-- 结课时间 -->
        <v-col cols="5">
          <v-menu
            ref="closeTime"
            v-model="closeTime"
            :close-on-content-click="false"
            :return-value.sync="course.closeTime"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="course.closeTime"
                label="结课时间："
                prepend-icon="mdi-calendar-month"
                readonly
                v-bind="attrs"
                v-on="on"
              />
            </template>
            <v-date-picker v-model="course.closeTime" no-title scrollable locale="zh-cn">
              <v-spacer />
              <v-btn text color="primary" @click="closeTime = false">取消</v-btn>
              <v-btn text color="primary" @click="$refs.closeTime.save(course.closeTime)">确认</v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>
      </v-row>
      <!-- 课程简介 -->
      <v-row justify="center">
        <v-col cols="10">
          <v-text-field
            v-model="course.simpleInfo"
            label="课程简介"
            placeholder="课程简介"
            :rules="[() => course.simpleInfo != null || '课程简介不能为空！']"
            clearable
          />
        </v-col>
      </v-row>
      <!-- 详细介绍 -->
      <v-row justify="center">
        <v-col cols="10">
          详细介绍:
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="10">
          <Vditor
            ref="courseInfoText"
            :placeholder="'详细的介绍，可以包含图片，视频，文件等'"
            :uploadurl="uploadurl"
            :markdown="course.curriculumInfo"
            @vditor-input="setVditorInput"
            @after="setText"
          />
        </v-col>
      </v-row>
      <!-- 课程进入方式 -->
      <v-row justify="center">
        <v-col cols="10">
          课程进入方式：
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="10">
          <v-radio-group v-model="course.accessMethod" row>
            <v-radio label="公开（注册用户均可加入）" :value="2" />
            <v-radio label="密码（使用课程密码进入）" :value="1" />
          </v-radio-group>
        </v-col>
      </v-row>
      <v-row v-if="course.accessMethod == 1" justify="center">
        <v-col cols="10">
          <v-text-field
            v-model="course.password"
            type="password"
            label="课程密码"
            placeholder="课程密码"
            :rules="[() => course.password != null || '课程密码不能为空！']"
            clearable
          />
        </v-col>
      </v-row>
      <!-- 加入时间 -->
      <v-row justify="center">
        <v-col cols="10">
          是否限制课程加入时间
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="10">
          <v-radio-group v-model="course.joinTimeLimit" row>
            <v-radio label="不限制（课程结束前均能加入课程进行学习）" :value="0" />
            <v-radio label="限制（必须在此时间之前进入学习）" :value="1" />
          </v-radio-group>
        </v-col>
      </v-row>
      <v-row v-if="course.joinTimeLimit == 1" justify="center">
        <!-- 年月日 -->
        <v-col cols="5">
          <v-menu
            ref="datePickerTime"
            v-model="datePickerTime"
            :close-on-content-click="false"
            :return-value.sync="limitYearTime"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="limitYearTime"
                label="年月日"
                prepend-icon="mdi-calendar-month"
                readonly
                v-bind="attrs"
                v-on="on"
              />
            </template>
            <v-date-picker v-model="limitYearTime" no-title scrollable locale="zh-cn">
              <v-spacer />
              <v-btn text color="primary" @click="datePickerTime = false">取消</v-btn>
              <v-btn text color="primary" @click="$refs.datePickerTime.save(limitYearTime)">确认</v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>
        <!-- 具体时间 -->
        <v-col cols="5">
          <v-menu
            ref="dateTime"
            v-model="dateTime"
            :close-on-content-click="false"
            :nudge-right="40"
            :return-value.sync="limitHourTime"
            transition="scale-transition"
            offset-y
            max-width="290px"
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="limitHourTime"
                label="时分秒"
                prepend-icon="mdi-clock-outline"
                readonly
                v-bind="attrs"
                v-on="on"
              />
            </template>
            <v-time-picker
              v-if="dateTime"
              v-model="limitHourTime"
              locale="zh-cn"
              full-width
              @click:minute="$refs.dateTime.save(limitHourTime)"
            />
          </v-menu>
        </v-col>
      </v-row>
      <!-- 标题图上传 -->
      <v-row justify="center">
        <v-col cols="10">
          标题图上传：（你可以输入图片地址或上传图片）
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="10">
          <Upload :uploadurl="uploadurl" @success-file="showMessageFromChild" />
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col cols="10">
          <v-text-field
            v-model="course.curriculumImageUrl"
            label="上传成功的图片地址或你输入的图片地址（上传成功后图片地址会自动填充）"
            placeholder="图片URL"
            :rules="[() => course.curriculumImageUrl != null || '图片URL不能为空！']"
            clearable
          />
        </v-col>
      </v-row>
      <!-- 分割线 -->
      <v-row justify="center">
        <v-col cols="10">
          <v-divider />
        </v-col>
      </v-row>
      <v-row v-if="update" justify="center">
        <v-col cols="3">
          &nbsp;
        </v-col>
        <v-col cols="4">
          <v-btn block color="primary" @click="dialog = true">修改</v-btn>
        </v-col>
        <v-col cols="3">
          &nbsp;
        </v-col>
      </v-row>
      <v-row v-if="update == false" justify="center">
        <v-col cols="3">
          &nbsp;
        </v-col>
        <v-col cols="4">
          <v-btn block color="primary" @click="submitSave">创建</v-btn>
        </v-col>
        <v-col cols="3">
          &nbsp;
        </v-col>
      </v-row>
    </v-card>
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
    <v-dialog
      v-model="dialog"
      width="500"
    >

      <v-card>
        <v-card-title class="headline grey lighten-2">
          提示：
        </v-card-title>

        <v-card-text>
          为了防止误操作，请输入验证码后点击确认修改：
        </v-card-text>
        <v-row justify="center">
          <v-col cols="5">
            <img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" @click="getVerifyImage">
          </v-col>
          <v-col cols="5">
            <v-text-field
              v-model="course.verifyCode"
              label="验证码"
              placeholder="验证码"
              :rules="[() => course.verifyCode != null || '验证码不能为空！']"
              clearable
            />
          </v-col>
        </v-row>
        <v-divider />

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="primary"
            text
            @click="updateData"
          >
            确认
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import Cascader from '@/components/form/cascader.vue'
import Vditor from '@/components/vditor/vditor.vue'
import Upload from '@/components/upload/upload-cropper.vue'
import TimeUtil from '@/utils/time-util.vue'
/**
 * 课程信息编辑与创建新课程组件
 */
export default {
  name: 'CourseInfoForm',
  components: {
    Cascader,
    Vditor,
    Upload
  },
  props: {
    update: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      TimeUtil,
      id: 0,
      course: {
        curriculumName: '',
        classNumber: 0,
        openingTime: '',
        closeTime: '',
        courseTag: 0,
        fatherCourseTag: 0,
        simpleInfo: '',
        curriculumInfo: '',
        accessMethod: '0',
        joinTimeLimit: '0',
        joinTime: '',
        curriculumImageUrl: '',
        password: '',
        verifyCode: ''
      },
      category: [],
      date: new Date().toISOString().substr(0, 10),
      startTime: false,
      closeTime: false,
      uploadurl: this.SERVER_API_URL + '/upload/file',
      // 保存限制进入的日期，年月日
      datePickerTime: false,
      // 保存限制进入课程的具体时分秒
      dateTime: false,
      time: null,
      limitYearTime: '',
      limitHourTime: '',
      message: '',
      showMessage: false,
      dialog: false,
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage'
    }
  },
  created() {
    this.id = this.$route.params.id
    if (this.update) {
      this.getInfo()
    }
  },
  methods: {
    getInfo() {
      this.httpGet(`/curriculum/info/${this.id}`, (json) => {
        if (json.status === 200) {
          this.course = json.data
          this.course.closeTime = TimeUtil.formateNoHours(this.course.closeTime)
          this.course.openingTime = TimeUtil.formateNoHours(this.course.openingTime)
          if (this.course.joinTimeLimit === 1) {
            this.course.joinTime = TimeUtil.formateHours(this.course.joinTime)
          }
          this.$refs.courseInfoText.setTextValue(json.data.curriculumInfo)
          // this.$refs.courseInfoText.setTextValue(123456)
          this.setTag()
        } else {
          //
        }
      })
    },
    setText() {
      this.$refs.courseInfoText.setTextValue(this.course.curriculumInfo)
    },
    updateData() {
      if (parseInt(this.course.joinTimeLimit) === 1) {
        this.course.joinTime = this.limitYearTime + ' ' + this.limitHourTime + ':00'
      }
      if (this.course.verifyCode === null || this.course.verifyCode === '') {
        this.message = '验证码不能为空！'
        this.showMessage = true
        return
      }

      this.httpPost('/teacher/curriculum/update', this.course, (json) => {
        if (json.status === 200) {
          this.message = '修改成功！'
          this.showMessage = true
          this.dialog = false
          this.getInfo()
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    getCourseTag(value) {
      this.course.fatherCourseTag = value[0]
      this.course.courseTag = value[1]
    },
    setVditorInput(data) {
      this.course.curriculumInfo = data
    },
    showMessageFromChild(data) {
      this.course.curriculumImageUrl = data
    },
    setTag() {
      this.$store.commit('setEditCourseInfoTag', [this.course.fatherCourseTag, this.course.courseTag])
    },
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    },
    submitSave() {
      if (this.course.curriculumName == null || this.course.curriculumName === '') {
        this.message = '课程名不能为空！'
        this.showMessage = true
        return
      }
      if (this.course.curriculumName.length > 50) {
        this.message = '课程名长度不能超过50个字符'
        this.showMessage = true
        return
      }
      if (this.course.courseTag == null || this.course.courseTag === 0) {
        this.message = '课程分类不能为空'
        this.showMessage = true
        return
      }
      if (this.course.simpleInfo == null || this.course.simpleInfo === '') {
        this.message = '课程简介不能为空'
        this.showMessage = true
        return
      }
      if (this.course.simpleInfo.length > 50) {
        this.message = '课程简介长度不能超过50个字符'
        this.showMessage = true
        return
      }
      if (this.course.openingTime == null || this.course.openingTime === '' || this.course.closeTime == null || this.course.closeTime === '') {
        this.message = '课程开课与结课时间不能为空'
        this.showMessage = true
        return
      }
      if (this.course.curriculumImageUrl == null || this.course.curriculumImageUrl === '') {
        this.message = '你还没有上传课程图'
        this.showMessage = true
        return
      }

      this.$emit('save', this.course)
    }
  }
}
</script>

<style>

</style>
