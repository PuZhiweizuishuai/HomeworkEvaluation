<template>
  <v-container>
    <v-row>
      <v-col cols="10">
        <h3>新建课程：</h3>
      </v-col>
    </v-row>
    <!-- <v-row>
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="course.curriculumName"
          placeholder="请输入课程名"
          label="课程名"
          :rules="[() => course.curriculumName != null || '请输入课程名']"
          clearable
        />
      </v-col>
    </v-row>
    <Cascader :label="`课程分类`" @tag="getTag" />
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="course.classNumber"
          placeholder="课程号（当前版本不在需要，直接默认值就好）"
          label="课程号（当前版本不在需要，直接默认值就好）"
          type="number"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        开始时间：
      </v-col>
    </v-row>
    <TimeForm @time="getStartTime" />
    <v-row justify="center">
      <v-col cols="10">
        结课时间：
      </v-col>
    </v-row>
    <TimeForm @time="getCloseTime" />
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="course.simpleInfo"
          placeholder="显示再标题下面的简短介绍(50字以内)"
          label="课程简介"
          :rules="[() => course.simpleInfo != null || '请输入课程简介']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        详细介绍：
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <Vditor
          :placeholder="'详细的介绍，可以包含图片，视频，文件等'"
          :uploadurl="uploadurl"
          @vditor-input="setVditorInput"
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">

      </v-col>
    </v-row> -->
    <CourseInfo @save="getSave" />
  </v-container>
</template>

<script>
import CourseInfo from '@/components/course/form/course-info-form.vue'

export default {
  name: 'CreateCourse',
  components: {
    CourseInfo
  },
  data() {
    return {
    }
  },
  methods: {
    getSave(value) {
      console.log(value)
      this.httpPost('/teacher/curriculum/create', value, (json) => {
        if (json.status === 200) {
          //
          this.$router.push({ path: `/course/learn/${json.data.id}` })
        } else {
          //
        }
      })
    }
  }
}
</script>

<style>

</style>
