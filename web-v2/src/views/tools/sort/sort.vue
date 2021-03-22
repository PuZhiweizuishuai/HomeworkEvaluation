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
        <h3 style="margin-top: 12px;">  输入主题并选择人数，分享页面给学生，系统随机分配各个同学的答题顺序。</h3>
      </v-col>
    </v-row>

    <v-card outlined>
      <v-row>
        <v-col />
      </v-row>
      <v-row justify="center">
        <v-col cols="10">
          <v-text-field
            v-model="sortDetails.title"
            :rules="rules"
            label="输入主题，如“表演顺序”"
          />
        </v-col>
      </v-row>

      <v-row justify="center">
        <v-col cols="10">
          <v-text-field
            v-model="sortDetails.number"
            :rules="numberRules"
            type="number"
            label="参与人数"
          />
        </v-col>
      </v-row>

      <v-row justify="center">
        <v-col cols="5">
          <img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" @click="getVerifyImage">
        </v-col>
        <v-col cols="5">
          <v-text-field
            v-model="sortDetails.verifyCode"
            placeholder="验证码"
            label="验证码"
            :rules="[() => sortDetails.verifyCode != null || '验证码不能为空']"
            clearable
          />
        </v-col>
      </v-row>

      <v-row justify="center">

        <v-btn
          color="primary"
          @click="save()"
        >
          马上发布
        </v-btn>

      </v-row>

      <v-row justify="center">
        <v-col cols="10">
          &nbsp;
        </v-col>
      </v-row>
    </v-card>
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
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      sortDetails: {
        title: null,
        number: 2,
        userId: '',
        verifyCode: null
      },
      snackbar: false,
      message: '',
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',

      rules: [
        value => !!value || '主题不能为空',
        value => (value && value.length <= 25) || '最大不能超过25个字符！'
      ],
      numberRules: [
        value => !!value || '参与人数不能为空',
        value => (value > 1 && value <= 1000) || '人数在1000到2之间'
      ]
    }
  },
  created() {
    if (localStorage.getItem('userID') == null) {
      this.httpGet('/user/id', (json) => {
        localStorage.setItem('userID', json.id)
        this.sortDetails.userId = localStorage.getItem('userID')
      })
    } else {
      this.sortDetails.userId = localStorage.getItem('userID')
    }
  },
  methods: {
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    },
    save() {
      this.sortDetails.number = parseInt(this.sortDetails.number)
      this.httpPost('/sort/save', this.sortDetails, (json) => {
        if (json.status !== 0) {
          this.message = json.message
          this.snackbar = true
        } else {
          this.message = '创建成功'
          this.snackbar = true
          this.$router.push(`/tools/sort/${json.data.id}`)
        }
      })
    }
  }
}
</script>

<style>

</style>
