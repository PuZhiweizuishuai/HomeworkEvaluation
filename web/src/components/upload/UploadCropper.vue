<template>
  <div style="display:flex;">
    <div class="info-item" style="">

      <a-button icon="scissor" class="btn btn-orange" style="display:inline-block;width: 70px;padding: 0;text-align: center;line-height: 28px;">
        <label for="uploads">选择图片</label>
      </a-button>

      <input id="uploads" type="file" :value="imgFile" style="position:absolute; clip:rect(0 0 0 0);" accept="image/png, image/jpeg, image/gif, image/jpg" @change="uploadImg($event, 1)">

      <a-button class="oper" icon="plus" style="margin-left: 10px" @click="changeScale(1)">放大</a-button>
      <a-button type="button" icon="minus" class="oper" style="margin-left: 10px" @click="changeScale(-1)">缩小</a-button>

      <!-- <input type="button" class="oper" style="height:20px;width:23px;font-size:20px;margin:3px 5px;" value="↺" title="左旋转" @click="rotateLeft">
      <input type="button" class="oper" style="height:20px;width:23px;font-size:20px;margin:3px 5px;" value="↻" title="右旋转" @click="rotateRight">
      <input type="button" class="oper" style="height:20px;width:23px;font-size:20px;margin:3px 5px;" value="↓" title="下载" @click="down('blob')"> -->

      <a-button type="primary" style="margin-left: 10px" icon="upload" @click="finish('blob')">上传</a-button>

      <div class="line" style="margin-left: -280px;margin-top: 85px;">
        <div class="cropper-content" style="margin-top:-60px;margin-left:260px;">
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
              @realTime="realTime"
              @imgLoad="imgLoad"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
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
      default: 'http://127.0.0.1:8080/api/upload/file'
    },
    fixednumber: {
      type: Array,
      default: () => { return [16, 9] }
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
        full: false, // 输出原图比例截图 props名full
        outputType: 'png',
        canMove: true,
        original: false,
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
      fetch(this.uploadurl, {
        headers: {
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: form
      }).then(response => response.json())
        .then(json => {
          if (json.status === 0) {
            if (json.status === 0) {
              this.backUploadurl = json.data.succMap[this.fileName]
              if (this.backUploadurl != null && this.backUploadurl !== '') {
                this.$emit('success-file', this.backUploadurl)
                this.$message.success('上传成功！')
              } else {
                this.$message.error('上传失败，请重试！')
              }
            } else {
              this.$message.error('上传失败，请重试！')
            }
          }
        })
        .catch(e => {
          this.$message.error('网络异常，请检查网络后重试！')
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
  .info {
    width: 720px;
    margin: 0 auto;
    .oper-dv {
      height:20px;
      text-align:right;
      margin-right:100px;
      a {
        font-weight: 500;
        &:last-child {
          margin-left: 30px;
        }
      }
    }
    .info-item {
      margin-top: 15px;
      label {
        display: inline-block;
        width: 100px;
        text-align: right;
      }
      .sel-img-dv {
        position: relative;
        .sel-file {
          position: absolute;
          width: 90px;
          height: 30px;
          opacity: 0;
          cursor: pointer;
          z-index: 2;
        }
        .sel-btn {
          position: absolute;
          cursor: pointer;
          z-index: 1;
        }
      }
    }
  }

  .cropper-content{
    display: flex;
    display: -webkit-flex;
    justify-content: flex-end;
    -webkit-justify-content: flex-end;
    .cropper{
      width: 640px;
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
        background: #cccccc;
        margin-left: 40px;
      }
    }
  }
  .cropper-content .show-preview .preview {margin-left: 0;}

</style>
