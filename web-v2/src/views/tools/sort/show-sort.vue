<template>
  <v-container>
    <v-row>
      <v-col>
        <router-link to="/tools" style="float: left">
          <v-btn
            icon
            x-large
            color="success"
          >
            <v-icon>mdi-arrow-left-thick</v-icon>

          </v-btn>
        </router-link>
        <h3 style="margin-top: 12px;">参与排序</h3>
      </v-col>
    </v-row>

    <v-row justify="center">
      <v-col cols="10">
        <v-card
          color="#ffee93"
        >
          <v-card-title class="headline">
            {{ sortDetails.title }}
          </v-card-title>

          <v-card-subtitle>创建时间：{{ formateTimeToChinese(sortDetails.time) }}</v-card-subtitle>
          <v-card-subtitle>当前参与人数：{{ joinNumber }}</v-card-subtitle>
          <v-card-subtitle>剩余卡片：{{ sortDetails.number - joinNumber }}</v-card-subtitle>

          <v-row justify="center">
            <v-btn
              v-if="showSubmitBtn"
              color="success"
              @click="dialog = true"
            >
              参与
            </v-btn>
          </v-row>

          <!-- 显示结果 -->
          <v-row v-if="showResult" justify="center">
            <h3>恭喜你抽到第{{ sortDetails.userMap[user.userId].code + 1 }}位！</h3>

          </v-row>
          <v-row v-if="showResult" justify="center">
            <p v-if="showUUID">你的唯一识别码是：{{ user.userId }}</p>
          </v-row>

          <v-row v-if="isAuthor" justify="center">
            <v-col cols="10">
              <h4>创建成功，你可以直接点击右上角分享页面到微信群</h4>
              <h4>也可以直接复制此链接分享：{{ thisUrl }} <v-btn
                depressed
                color="primary"
                @click="copy()"
              >
                点击复制
              </v-btn></h4>
              <h4>当然你也可以生成二维码截图给你的朋友进行分享</h4>
              <br>
              <span>ps:如果第一次没有生成成功，请关闭后重新生成</span>
            </v-col>
          </v-row>
          <v-row v-if="isAuthor" justify="center">
            <v-btn
              depressed
              color="#ff5e78"
              @click="createQrCode()"
            >
              生成二维码
            </v-btn>

          </v-row>
          <v-row>
            <v-col />
          </v-row>
        </v-card>
      </v-col>
    </v-row>

    <!-- 功能选择 -->
    <v-row justify="center">
      <v-col cols="10">
        <v-card>
          <v-card-subtitle>
            <v-row>
              <v-col>
                <v-switch
                  v-if="isAuthor"
                  v-model="showUUID"
                  class="ma-2"
                  :label="`显示唯一识别码`"
                />

                <v-switch
                  v-model="showAutoflush"
                  style="float: right"
                  class="ma-2"
                  :label="`自动更新`"
                  @click="autoUpdate()"
                />
              </v-col>
            </v-row>
          </v-card-subtitle>
        </v-card>
      </v-col>
    </v-row>

    <!-- 显示人员列表 -->
    <v-row justify="center">
      <v-col cols="10">
        <v-card
          class="mx-auto"
          tile
        >
          <v-list flat>
            <v-subheader>参与人员列表:</v-subheader>
            <v-list-item-group
              color="primary"
            >
              <v-list-item
                v-for="(item, i) in Object.keys(sortDetails.userMap)"
                :key="i"
              >
                <v-list-item-content>
                  <v-list-item-title>
                    {{ sortDetails.userMap[item].name }}  <span style="float:right;color: #ff5e78">第 {{ sortDetails.userMap[item].code + 1 }} 位</span>
                  </v-list-item-title>
                  <v-list-item-subtitle>
                    时间：{{ formateTimeToChinese(sortDetails.userMap[item].time) }}
                  </v-list-item-subtitle>
                  <v-list-item-subtitle v-if="showUUID">
                    唯一识别码：{{ item }}
                  </v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>
            </v-list-item-group>
          </v-list>
        </v-card>
      </v-col>
    </v-row>

    <!-- 对话框 -->
    <v-dialog
      v-model="dialog"
      persistent
      max-width="600px"
    >
      <v-card>
        <v-card-title class="headline">
          请输入你参加本次活动想要使用的昵称
        </v-card-title>
        <v-card-subtitle>
          <v-row justify="center">
            <v-col cols="10">
              <v-text-field
                v-model="user.name"
                :rules="rules"
                label="你的昵称"
              />
            </v-col>
          </v-row>
        </v-card-subtitle>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="error"
            text
            @click="dialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="green darken-1"
            text
            @click="join()"
          >
            抽签
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar
      v-model="snackbar"
      :timeout="3000"
      :top="true"
    >
      {{ message }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="blue"
          text
          v-bind="attrs"
          @click="snackbar = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>

    <!-- 二维码 -->
    <v-dialog
      v-model="showQrCode"
      width="500"
    >

      <v-card
        max-width="500"
        max-height="500"
      >
        <v-card-title>
          二维码分享
        </v-card-title>

        <v-card-subtitle>
          <v-row>
            <v-col>
              <canvas ref="qrcodecanvas" />
            </v-col>
          </v-row>
        </v-card-subtitle>
        <v-divider />

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="primary"
            text
            @click="showQrCode = false"
          >
            关闭
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import QRCode from 'qrcode'
export default {
  data() {
    return {
      showUUID: false,
      isAuthor: false,
      id: this.$route.params.id,
      sortDetails: {
        title: ''
      },
      joinNumber: 0,
      showSubmitBtn: true,
      user: {
        userId: '',
        name: ''
      },
      dialog: false,
      rules: [
        value => !!value || '昵称不能为空',
        value => (value && value.length <= 25) || '最大不能超过25个字符！'
      ],
      snackbar: false,
      message: '',
      showResult: false,
      thisUrl: '',
      showQrCode: false,
      showAutoflush: false,
      interval: null
    }
  },
  created() {
    if (localStorage.getItem('userID') == null) {
      this.httpGet('/user/id', (json) => {
        localStorage.setItem('userID', json.id)
        this.user.userId = localStorage.getItem('userID')
      })
    } else {
      this.user.userId = localStorage.getItem('userID')
    }
    if (localStorage.getItem('name') != null) {
      this.user.name = localStorage.getItem('name')
    }
    this.getSortInfo()
    this.thisUrl = window.location.href
  },
  methods: {
    autoUpdate() {
      if (this.showAutoflush) {
        this.interval = window.setInterval(() => {
          this.getSortInfo()
        }, 10000)
      } else {
        window.clearInterval(this.interval)
      }
    },
    getSortInfo() {
      this.httpGet('/sort/get/' + this.id, (json) => {
        if (json.status !== 0) {
          this.$router.push(`/tools/sort`)
        } else {
          this.sortDetails = json.data
          document.title = this.sortDetails.title + '- 排序'
          this.joinNumber = Object.keys(this.sortDetails.userMap).length

          if (localStorage.getItem('userID') !== this.sortDetails.userId) {
            this.showSubmitBtn = true
          } else {
            this.showSubmitBtn = false
            this.isAuthor = true
          }
          if (this.sortDetails.number - this.joinNumber <= 0) {
            this.showSubmitBtn = false
          }

          if (this.sortDetails.userMap[localStorage.getItem('userID')] != null) {
            this.showResult = true
            this.showSubmitBtn = false
          }
        }
      })
    },
    createQrCode() {
      this.showQrCode = true
      QRCode.toCanvas(this.$refs.qrcodecanvas, this.thisUrl, { errorCorrectionLevel: 'H' }, function(err) {
        if (err) throw err
      })
    },
    formateTimeToChinese(date) {
      if (date === '' || date == null) {
        return ''
      }
      const da = new Date(date)
      return da.getFullYear() + '年' + (da.getMonth() + 1) + '月' + da.getDate() + '日 ' + da.getHours() + '时' + da.getMinutes() + '分' + da.getSeconds() + '秒'
    },
    join() {
      if (this.user.name != null && this.user.name.length > 0 && this.user.name.length <= 25) {
        localStorage.setItem('name', this.user.name)

        this.httpPost(`/sort/join/${this.id}`, this.user, (json) => {
          if (json.status === 0) {
            //
            this.dialog = false
            this.message = `恭喜你抽到第 ${json.data.user.code + 1} 位！`
            this.snackbar = true
            this.sortDetails = json.data.sortDetails
            this.showResult = true
            this.showSubmitBtn = false
          } else {
            this.dialog = false
            this.message = json.message
            this.snackbar = true
          }
        })
      }
    },
    copy(e) {
      const transfer = document.createElement('input')
      document.body.appendChild(transfer)
      transfer.value = this.thisUrl // 这里表示想要复制的内容
      transfer.focus()
      transfer.select()
      if (document.execCommand('copy')) {
        document.execCommand('copy')
      }
      transfer.blur()
      this.message = '复制成功！ '
      this.snackbar = true
      document.body.removeChild(transfer)
    }
  }
}
</script>

<style>

</style>
