<template>
  <v-row justify="center" align="center">
    <v-col>
      <v-card
        class="mx-auto"
        outlined
      >
        <v-row justify="center">
          <v-col cols="10">
            <h2>头像修改</h2>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="10">
            <v-avatar size="100">
              <v-img :src="userInfo.userAvatarUrl" />
            </v-avatar>
          </v-col>
        </v-row>
        <v-row justify="center">
          <v-col cols="10">
            <Upload :fixednumber="[1, 1]" :uploadurl="uploadurl" :full="false" @success-file="getUrl" />
          </v-col>
        </v-row>
      </v-card>
    </v-col>
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
  </v-row>
</template>

<script>
import Upload from '@/components/upload/upload-cropper.vue'
export default {
  name: 'UserSetting',
  components: {
    Upload
  },
  data() {
    return {
      userInfo: {
        username: ''
      },
      files: [],
      rules: [
        value => !value || value.size < 2000000 || '头像大小必须在2MB以内！'
      ],
      showMessage: false,
      message: '',
      uploadurl: this.SERVER_API_URL + '/upload/file' // ?type=avatars'
    }
  },
  created() {
    this.userInfo = this.$store.state.userInfo
  },
  methods: {
    getUrl(value) {
      this.save(value)
    },
    save(value) {
      const data = {
        userAvatarUrl: value
      }
      this.httpPost(`/user/update/avatar`, data, (json) => {
        if (json.status === 200) {
          this.userInfo.userAvatarUrl = value
          this.$store.commit('setUserInfo', this.userInfo)
          this.message = '保存成功！'
          this.showMessage = true
        } else {
          //
          this.message = '保存失败！' + json.message
          this.showMessage = true
        }
      })
    }
  }
}
</script>

<style>

</style>
