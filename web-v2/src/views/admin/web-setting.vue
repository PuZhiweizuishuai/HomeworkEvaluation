<template>
  <v-container>
    <v-card outlined>
      <v-card-title>
        <v-row justify="center">
          <v-col cols="10">
            网站基本信息设置
          </v-col>
        </v-row>
      </v-card-title>
      <!-- 设置 -->
      <v-card-text>
        <v-row justify="center">
          <v-col cols="10">
            <v-text-field
              v-model="webInfo.name"
              placeholder="网站名称"
              label="网站名称"
              :rules="[() => webInfo.name != null || '网站名称不能为空！']"
              clearable
            />
          </v-col>
        </v-row>

        <v-row justify="center">
          <v-col cols="10">
            <v-text-field
              v-model="webInfo.logo"
              placeholder="网站Logo"
              label="网站Logo"
              :rules="[() => webInfo.logo != null || '网站Logo不能为空！']"
              clearable
            />
          </v-col>
        </v-row>

        <v-row justify="center">
          <v-col cols="10">
            <v-text-field
              v-model="webInfo.faviconUrl"
              placeholder="网站favicon"
              label="网站favicon"
              :rules="[() => webInfo.faviconUrl != null || '网站favicon不能为空！']"
              clearable
            />
          </v-col>
        </v-row>

        <v-row justify="center">
          <v-col cols="10">
            <v-text-field
              v-model="webInfo.registerInvitationCode"
              placeholder="是否开启邀请码注册"
              label="是否开启邀请码注册（0 关闭， 1 开启）"
              clearable
            />
          </v-col>
        </v-row>

        <v-row justify="center">
          <v-col cols="10">
            <v-text-field
              v-model="webInfo.registerEmailCheck"
              placeholder="是否开启邮箱验证"
              label="是否开启邮箱验证（0 关闭， 1 开启，此项需要配置邮件模块）"
              clearable
            />
          </v-col>
        </v-row>
        <v-col />
        <v-row justify="center">
          <v-btn color="success" @click="submit()">
            保存
          </v-btn>
        </v-row>
        <v-col />
      </v-card-text>
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
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      showMessage: false,
      message: '',
      webInfo: {
        name: '吉林师范大学作业互评系统',
        logo: '/logo.png',
        faviconUrl: '/logo.png',
        registerInvitationCode: 1,
        registerEmailCheck: 1
      }
    }
  },
  created() {
    this.webInfo = this.$store.state.webInfo
  },
  methods: {
    submit() {
      delete this.webInfo['status']
      this.httpPost('/admin/web/update/info', this.webInfo, (json) => {
        this.webInfo = json.data
        this.webInfo.status = 1
        this.$store.state.webInfo = this.webInfo
        this.message = '保存成功！'
        this.showMessage = true
      })
    }
  }
}
</script>

<style>

</style>
