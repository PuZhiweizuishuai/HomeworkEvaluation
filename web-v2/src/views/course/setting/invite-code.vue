<template>
  <v-container>
    <v-row>
      <v-col>
        <v-btn depressed color="primary" @click="dialog = true">创建邀请码</v-btn>
        说明：使用此邀请码注册的用户将直接进入这门课程
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <Table />
    <v-dialog
      v-model="dialog"
      max-width="600"
    >
      <v-card>
        <v-card-title class="headline">创建邀请码</v-card-title>
        <v-card-text>
          <v-row justify="center">
            <v-col cols="10">
              <v-text-field
                v-model="inviteCode.useCount"
                placeholder="请输入邀请码可用次数"
                label="可用次数"
                :rules="[() => inviteCode.useCount != null || '可用次数不能为空']"
                type="number"
                clearable
              />
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="10">
              <TimeForm :cols="6" @time="getTime" />
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer />

          <v-btn
            color="green darken-1"
            text
            @click="dialog = false"
          >
            取消
          </v-btn>

          <v-btn
            color="green darken-1"
            text
            @click="submit"
          >
            创建
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
import Table from '@/components/course/invite-code-table.vue'
export default {
  components: {
    TimeForm,
    Table
  },
  data() {
    return {
      dialog: false,
      inviteCode: {
        useCount: 1,
        expireTime: null,
        classNumber: 0,
        type: 0
      },
      showMessage: false,
      message: ''
    }
  },
  created() {

  },
  methods: {
    getTime(value) {
      this.inviteCode.expireTime = new Date(value).getTime()
    },
    submit() {
      this.inviteCode.classNumber = parseInt(this.$route.params.id)
      if (isNaN(parseInt(this.inviteCode.useCount)) || parseInt(this.inviteCode.useCount) < 1) {
        this.message = '可用次数必须大于等于0，并且是数字！'
        this.showMessage = true
        return
      }
      this.httpPost(`/invite/code/create`, this.inviteCode, (json) => {
        if (json.status === 200) {
          this.message = '创建成功！'
          this.showMessage = true
          this.getList()
          this.dialog = false
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
