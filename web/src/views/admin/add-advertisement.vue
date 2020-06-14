<template>
  <div>
    <a-drawer
      :title="getTitle"
      :width="720"
      :visible="visible"
      :body-style="{ paddingBottom: '80px' }"
      @close="onClose"
    >
      <!-- 表单部分 -->
      <a-form-model
        ref="ruleForm"
        :model="advertisementFrom"
        :rules="rules"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item ref="title" label="广告标题：" prop="title">
              <a-input
                v-model="advertisementFrom.title"
                placeholder="50字以内"
                @blur="
                  () => {
                    $refs.title.onFieldBlur();
                  }
                "
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item ref="url" label="点击后跳转的链接：" prop="url">
              <a-input
                v-model="advertisementFrom.url"
                placeholder="符合标准的URL链接"
                @blur="
                  () => {
                    $refs.url.onFieldBlur();
                  }
                "
              />
            </a-form-model-item>
          </a-col>
        </a-row>

        <!-- 输入文件部分 -->
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item ref="image" label="显示的图片地址（为保证最佳显示效果，建议选择19：9大小的图片）" prop="image">
              <a-input
                v-model="advertisementFrom.image"
                placeholder="你可以选择上传或者直接输入"
                :disabled="showUpload"
                @blur="
                  () => {
                    $refs.image.onFieldBlur();
                  }
                "
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-button style="margin-top: 50px;margin-left: 12px;" @click="showUploadFile" v-text="showUploadInfo" />
          </a-col>
        </a-row>

        <!-- 上传文件部分 -->
        <a-row v-if="showUpload" :gutter="16">
          <Upload :fixednumber="[19, 9]" @success-file="setImageUrl" />
        </a-row>

        <!-- 时间设置部分 -->
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-model-item ref="startTime" :label="getTimeLabe" prop="startTime">
              <a-date-picker
                format="YYYY-MM-DD HH:mm:ss"
                :disabled-date="disabledDate"
                :locale="locale"
                :show-time="{ defaultValue: moment('00:00:00', 'HH:mm:ss') }"
                placeholder="开始时间"
                :input-read-only="true"
                @change="setStartTime"
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item ref="endTime" label="持续时间(单位：天)：" prop="endTime">
              <a-input-number

                v-model="advertisementFrom.endTime"
                :min="1"
                :max="10000"
                @blur="
                  () => {
                    $refs.endTime.onFieldBlur();
                  }
                "
              />
            </a-form-model-item>
          </a-col>

          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-model-item ref="type" label="显示位置" prop="type">
                <a-select v-model="advertisementFrom.type" placeholder="请选择显示位置">
                  <a-select-option value="0">
                    首页顶部轮播大图
                  </a-select-option>
                  <a-select-option value="1">
                    课程页顶部轮播大图
                  </a-select-option>
                  <a-select-option value="2">
                    首页广告
                  </a-select-option>
                  <a-select-option value="3">
                    课程页广告
                  </a-select-option>
                </a-select>
              </a-form-model-item>
            </a-col>
          </a-row>

        </a-row>
      </a-form-model>
      <div
        :style="{
          position: 'absolute',
          right: 0,
          bottom: 0,
          width: '100%',
          borderTop: '1px solid #e9e9e9',
          padding: '10px 16px',
          background: '#fff',
          textAlign: 'right',
          zIndex: 1,
        }"
      >
        <a-button :style="{ marginRight: '8px' }" @click="onClose">
          取消
        </a-button>
        <a-button type="primary" @click="checkFrom">
          提交
        </a-button>
      </div>
    </a-drawer>
  </div>

</template>

<script>
import Upload from '@/components/upload/UploadCropper.vue'
import moment from 'moment'
import 'moment/locale/zh-cn'
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN'
import TimeUtil from '@/utils/time-util.vue'
export default {
  name: 'AddAdvertisement',
  components: {
    Upload
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    edittype: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      locale,
      showUpload: false,
      showUploadInfo: '我选择上传图片',
      addVisible: this.visible,
      advertisementFrom: {
        id: -1,
        title: '',
        url: '',
        image: '',
        startTime: '',
        endTime: 10,
        type: 0
      },
      rules: {
        title: [
          { required: true, message: '请输入广告标题', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在1~50', trigger: 'blur' }
        ],
        url: [{ required: true, message: '请输入点击后跳转的链接：', trigger: 'blur' }],
        image: [{ required: true, message: '请上传或输入图片链接', trigger: 'blur' }],
        endTime: [{ required: true, message: '请输入持续时间', trigger: 'blur' }],
        startTime: [{ required: true, message: '请输入开始时间', trigger: 'blur' }],
        type: [{ required: true, message: '请输入显示位置', trigger: 'blur' }]
      }
    }
  },
  created() {
    if (this.edittype) {
      this.advertisementFrom = this.advertisementupdate
    }
  },
  methods: {
    addEditData(data) {
      this.advertisementFrom = JSON.parse(JSON.stringify(data))
      this.advertisementFrom.endTime = (data.endTime - data.startTime) / 86400000
    },
    getTimeLabe() {
      if (this.edittype) {
        return '开始投放时间：' + TimeUtil.renderTime(this.advertisementFrom.startTime)
      } else {
        return '开始投放时间：'
      }
    },
    addData() {
      this.advertisementFrom = {
        title: '',
        url: '',
        image: '',
        startTime: '',
        endTime: 10,
        type: 0
      }
    },
    getTitle() {
      if (this.edittype) {
        return '修改广告'
      } else {
        return '创建新的广告'
      }
    },
    checkFrom() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          this.sendAdd()
        } else {
          return false
        }
      })
    },
    sendAdd() {
      let url = this.SERVER_API_URL
      if (this.edittype) {
        url = url + '/admin/ad/update'
      } else {
        url = url + '/admin/ad/add'
      }
      fetch(url, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(this.advertisementFrom)
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.addVisible = false
            this.$emit('close', this.visible)
            this.$emit('reload', true)
            this.$message.success('创建成功！')
            // this.visible = false
          } else {
            this.$message.error('创建失败！' + json.message)
          }
        })
        .catch(e => {
          this.$message.error('请检查网络后重试！')
        })
    },

    onClose() {
      // this.visible = false
      this.addVisible = false
      this.$emit('close', this.visible)
    },
    showUploadFile() {
      this.showUpload = !this.showUpload
      if (this.showUpload) {
        this.showUploadInfo = '我选择输入图片地址'
      } else {
        this.showUploadInfo = '我选择上传图片'
      }
    },
    setImageUrl(data) {
      this.advertisementFrom.image = data
    },
    setStartTime(value, mode) {
      if (value == null) {
        this.advertisementFrom.startTime = ''
        return
      }
      this.advertisementFrom.startTime = value._d.toString()
    },

    moment,
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

    disabledRangeTime(_, type) {
      if (type === 'start') {
        return {
          disabledHours: () => this.range(0, 60).splice(4, 20),
          disabledMinutes: () => this.range(30, 60),
          disabledSeconds: () => [55, 56]
        }
      }
      return {
        disabledHours: () => this.range(0, 60).splice(20, 4),
        disabledMinutes: () => this.range(0, 31),
        disabledSeconds: () => [55, 56]
      }
    }
  }
}
</script>
