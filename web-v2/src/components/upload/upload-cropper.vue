<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-btn :depressed="true" color="primary">
          <label for="uploads" style="cursor:pointer;">选择图片</label>
        </v-btn>
        <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
        <input id="uploads" type="file" :value="imgFile" style="position:absolute; clip:rect(0 0 0 0);" accept="image/png, image/jpeg, image/gif, image/jpg" @change="uploadImg($event, 1)">

        <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
        <v-btn :depressed="true" @click="changeScale(1)">放大</v-btn>
        <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
        <v-btn :depressed="true" @click="changeScale(-1)">缩小</v-btn>
        <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
        <v-btn :depressed="true" color="success" @click="finish('blob')">
          上传 <v-icon right dark>mdi-cloud-upload</v-icon>
        </v-btn>
      </v-col>
    </v-row>

    <div class="cropper-content">
      <div class="cropper">
        <vueCropper
          ref="cropper"
          :img="option.img"
          :output-size="option.size"
          :output-type="option.outputType"
          :info="true"
          :info-true="true"
          :full="option.full"
          :can-move="option.canMove"
          :can-move-box="option.canMoveBox"
          :original="option.original"
          :auto-crop="option.autoCrop"
          :auto-crop-width="option.autoCropWidth"
          :auto-crop-height="option.autoCropHeight"
          :fixed-box="option.fixedBox"
          :center-box="true"
          :fixed-number="option.fixedNumber"
          :fixed="option.fixed"
          :high="true"
          @realTime="realTime"
          @imgLoad="imgLoad"
        />
      </div>
    </div>

  </v-container>
</template>

<script>
import { VueCropper } from 'vue-cropper'

export default {
  naem: 'UploadCropper',
  components: {
    VueCropper
  },
  props: {
    uploadurl: {
      type: String,
      default: '/api/upload/file'
    },
    fixednumber: {
      type: Array,
      default: () => { return [16, 9] }
    },
    serve: {
      type: Boolean,
      default: false
    },
    url: {
      type: String,
      default: ''
    },
    full: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      backUploadurl: 'url',
      headImg: '',
      // 剪切图片上传
      crap: false,
      previews: {},
      option: {
        img: '',
        outputSize: 1, // 剪切后的图片质量（0.1-1）
        full: this.full, // 输出原图比例截图 props名full
        outputType: 'png',
        canMove: true,
        original: true,
        canMoveBox: true,
        autoCrop: true,
        fixedBox: false,
        fixedNumber: this.fixednumber,
        fixed: true,
        centerBox: true
      },
      fileName: '', // 本机文件地址
      downImg: '#',
      imgFile: '',
      uploadImgRelaPath: '' // 上传后的图片的地址（不带服务器域名）
    }
  },
  created() {
    if (this.serve) {
      this.option.img = this.url
    }
  },
  methods: {
    // 放大/缩小
    changeScale(num) {
      console.log('changeScale')
      num = num || 1
      this.$refs.cropper.changeScale(num)
    },
    // 上传图片（点击上传按钮）
    finish(type) {
      console.log('finish')
      if (type === 'blob') {
        const formData = new FormData()
        this.$refs.cropper.getCropBlob((file) => {
          // do something
          // console.log(data)
          formData.append('file[]', file, this.fileName)
          this.uploadToSystemFile(formData)
          // this.retrieveNewURL(file, (file, url) => {
          //   // 上传文件到服务器
          //   this.uploadFile(file, url)
          // })
        })
      } else {
        this.$refs.cropper.getCropData((data) => {
          this.model = true
          this.modelSrc = data
        })
      }
    },
    // 获取文件上传的url
    retrieveNewURL(file, cb) {
      fetch(this.SERVER_API_URL + `/upload/file/url?filename=${this.fileName}`, {
        method: 'GET', // or 'PUT'
        credentials: 'include'
      }).then(res => res.json())
        .catch(error => console.error('Error:', error))
        .then(function(response) {
          if (response.status === 200) {
            file.name = response.data.filename
            cb(file, response.data.url)
          } else {
            this.$message.info('获取上传链接异常')
          }
        })
    },
    // 上传文件
    uploadFile(file, url) {
      fetch(url, {
        method: 'PUT',
        body: file
      }).then(() => {
        // If multiple files are uploaded, append upload status on the next line.
        // console.log(file.name)
        this.backUploadurl = url.split('?')[0]
        // console.log(this.backUploadurl)
        this.$emit('success-file', this.backUploadurl)
        this.$message.info('上传成功！')
      }).catch((e) => {
        console.error(e)
      })
    },
    uploadToSystemFile(form) {
      console.log('uploadToSystemFile')
      fetch(this.uploadurl, {
        headers: {
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: form
      }).then(response => response.json())
        .then(json => {
          if (json.code === 0) {
            this.backUploadurl = json.data.succMap[this.fileName]
            if (this.backUploadurl != null && this.backUploadurl !== '') {
              this.$emit('success-file', this.backUploadurl)
            } else {
              //
            }
          }
        })
        .catch(e => {
          return null
        })
    },
    // 实时预览函数
    realTime(data) {
      this.previews = data
    },
    // 下载图片
    down(type) {
      var aLink = document.createElement('a')
      aLink.download = 'author-img'
      if (type === 'blob') {
        this.$refs.cropper.getCropBlob((data) => {
          this.downImg = window.URL.createObjectURL(data)
          aLink.href = window.URL.createObjectURL(data)
          aLink.click()
        })
      } else {
        this.$refs.cropper.getCropData((data) => {
          this.downImg = data
          aLink.href = data
          aLink.click()
        })
      }
    },
    // 选择本地图片
    uploadImg(e, num) {
      console.log('uploadImg')
      var _this = this
      // 上传图片
      var file = e.target.files[0]
      _this.fileName = file.name
      if (!/\.(gif|jpg|jpeg|png|bmp|GIF|JPG|PNG)$/.test(e.target.value)) {
        alert('图片类型必须是.gif,jpeg,jpg,png,bmp中的一种')
        return false
      }
      var reader = new FileReader()
      reader.onload = (e) => {
        let data
        if (typeof e.target.result === 'object') {
          // 把Array Buffer转化为blob 如果是base64不需要
          data = window.URL.createObjectURL(new Blob([e.target.result]))
        } else {
          data = e.target.result
        }
        if (num === 1) {
          _this.option.img = data
        } else if (num === 2) {
          _this.example2.img = data
        }
      }
      // 转化为base64
      // reader.readAsDataURL(file)
      // 转化为blob
      reader.readAsArrayBuffer(file)
    },
    imgLoad(msg) {
      console.log('imgLoad')
      console.log(msg)
    }
  }

}
</script>

<style lang="less">
  .cropper-content{
    .cropper{

      height: 360px;
    }
    .show-preview{
      flex: 1;
      -webkit-flex: 1;
      display: flex;
      display: -webkit-flex;
      justify-content: center;
      -webkit-justify-content: center;
      .preview{
        overflow: hidden;
        border-radius: 50%;
        border:1px solid #cccccc;
        background-color: #cccccc;
        margin-left: 40px;
      }
    }
  }
  .cropper-content .show-preview .preview {margin-left: 0;}

</style>
