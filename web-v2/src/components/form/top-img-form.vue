<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="advertisementFrom.title"
          placeholder="标题"
          label="标题"
          :rules="[() => advertisementFrom.title != null || '标题不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        <v-text-field
          v-model="advertisementFrom.url"
          placeholder="点击后跳转的页面链接"
          label="页面链接"
          :rules="[() => advertisementFrom.url != null || '页面链接不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        图片上传：（你可以输入图片地址或上传图片）
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
          v-model="advertisementFrom.image"
          placeholder="显示的图片链接"
          label="图片链接"
          :rules="[() => advertisementFrom.image != null || '图片链接不能为空']"
          clearable
        />
      </v-col>
    </v-row>
    <v-row v-if="type === 1" justify="center">
      <v-col cols="10">
        原开始时间：  {{ TimeUtil.renderTime(advertisementFrom.startTime) }} 结束时间：  {{ TimeUtil.renderTime(advertisementFrom.endTime) }}
        <br>
        说明：如果时间显示是：1970-1-1 8:0:0，则说明此广告以结束投放或未开始投放
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="10">
        开始时间：
      </v-col>
    </v-row>
    <TimeForm :cols="5" @time="getStartTime" />
    <v-row justify="center">
      <v-col cols="10">
        结束时间：
      </v-col>
    </v-row>
    <TimeForm :cols="5" @time="getEndTime" />
    <v-row justify="center">
      <v-col cols="10">
        <v-select
          v-model="advertisementFrom.type"
          :items="typeList"
          label="显示位置"
          item-text="text"
          item-value="value"
          clearable
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-btn depressed color="success" @click="createNew">{{ btnName }}</v-btn>
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
import TimeForm from '@/components/form/time-form.vue'
import TimeUtil from '@/utils/time-util.vue'
import Upload from '@/components/upload/upload-cropper.vue'
export default {
  components: {
    TimeForm,
    Upload
  },
  props: {
    type: {
      type: Number,
      default: 0
    },
    info: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      TimeUtil,
      advertisementFrom: {
        id: -1,
        title: '',
        url: '',
        image: '',
        startTime: '',
        endTime: '',
        type: 1
      },
      btnName: '新建',
      typeList: [
        { text: '社区页', value: 0 },
        { text: '首页', value: 1 }
      ],
      uploadurl: this.SERVER_API_URL + '/uploads/file',
      showMessage: false,
      message: true
    }
  },
  created() {
    if (this.type === 1) {
      this.advertisementFrom = this.info
      this.btnName = '更新'
    }
  },
  methods: {
    getEndTime(value) {
      this.advertisementFrom.endTime = value
    },
    getStartTime(value) {
      this.advertisementFrom.startTime = value
    },
    showMessageFromChild(data) {
      this.advertisementFrom.image = data
    },
    createNew() {
      //
      let url = `/admin/topImg/add`
      if (this.type === 1) {
        url = `/admin/topImg/update`
      }
      this.httpPost(url, this.advertisementFrom, (json) => {
        if (json.status === 200) {
          this.message = this.btnName + '成功'
          this.showMessage = true
          this.$emit('success', true)
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    }
  }
}
</script>

<style>

</style>
