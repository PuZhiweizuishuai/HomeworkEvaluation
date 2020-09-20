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
      <Cascader :label="`课程分类`" @tag="getCourseTag" />
      <!-- 课程号 -->
      <v-row justify="center">
        <v-col cols="10">
          <v-text-field
            v-model="course.classNumber"
            label="课程号"
            placeholder="课程号"
            :rules="[() => course.classNumber != null || '课程号不能为空！']"
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
            :return-value.sync="date"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="date"
                label="开课时间："
                prepend-icon="mdi-calendar-month"
                readonly
                v-bind="attrs"
                v-on="on"
              />
            </template>
            <v-date-picker v-model="date" no-title scrollable locale="zh-cn">
              <v-spacer />
              <v-btn text color="primary" @click="startTime = false">取消</v-btn>
              <v-btn text color="primary" @click="$refs.startTime.save(date)">确认</v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>
        <!-- 结课时间 -->
        <v-col cols="5">
          <v-menu
            ref="closeTime"
            v-model="closeTime"
            :close-on-content-click="false"
            :return-value.sync="date"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="date"
                label="结课时间："
                prepend-icon="mdi-calendar-month"
                readonly
                v-bind="attrs"
                v-on="on"
              />
            </template>
            <v-date-picker v-model="date" no-title scrollable locale="zh-cn">
              <v-spacer />
              <v-btn text color="primary" @click="closeTime = false">取消</v-btn>
              <v-btn text color="primary" @click="$refs.closeTime.save(date)">确认</v-btn>
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
            :placeholder="'详细的介绍，可以包含图片，视频，文件等'"
            :uploadurl="uploadurl"
            :markdown="course.curriculumInfo"
            @vditor-input="setVditorInput"
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
            <v-radio label="公开（注册用户均可加入）" value="2" />
            <v-radio label="密码（使用课程密码进入）" value="1" />
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
            <v-radio label="不限制（课程结束前均能加入课程进行学习）" value="2" />
            <v-radio label="限制（必须在此时间之前进入学习）" value="1" />
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
            :return-value.sync="date"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="date"
                label="年月日"
                prepend-icon="mdi-calendar-month"
                readonly
                v-bind="attrs"
                v-on="on"
              />
            </template>
            <v-date-picker v-model="date" no-title scrollable locale="zh-cn">
              <v-spacer />
              <v-btn text color="primary" @click="datePickerTime = false">取消</v-btn>
              <v-btn text color="primary" @click="$refs.datePickerTime.save(date)">确认</v-btn>
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
            :return-value.sync="time"
            transition="scale-transition"
            offset-y
            max-width="290px"
            min-width="290px"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="time"
                label="时分秒"
                prepend-icon="mdi-clock-outline"
                readonly
                v-bind="attrs"
                v-on="on"
              />
            </template>
            <v-time-picker
              v-if="dateTime"
              v-model="time"
              locale="zh-cn"
              full-width
              @click:minute="$refs.dateTime.save(time)"
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
            v-model="course.curriculumName"
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
      <v-row justify="center">
        <v-col cols="3">
          &nbsp;
        </v-col>
        <v-col cols="4">
          <v-btn block color="primary">修改</v-btn>
        </v-col>
        <v-col cols="3">
          &nbsp;
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script>
import Cascader from '@/components/form/cascader.vue'
import Vditor from '@/components/vditor/vditor.vue'
import Upload from '@/components/upload/upload-cropper.vue'
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
  data() {
    return {
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
        password: ''
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
      time: null
    }
  },
  created() {

  },
  methods: {
    getCourseTag(value) {
      console.log(value)
    },
    setVditorInput(data) {
      this.course.curriculumInfo = data
    },
    showMessageFromChild(data) {
      this.course.curriculumImageUrl = data
    }
  }
}
</script>

<style>

</style>
