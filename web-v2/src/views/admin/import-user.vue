<template>
  <v-container>
    <v-row>
      <v-col>
        <v-btn class="mx-2" fab dark small depressed color="primary" @click="backToUser">
          <v-icon dark> mdi-arrow-left-thick </v-icon>
        </v-btn>
        返回
      </v-col>
    </v-row>
    <v-row justify="end">
      导入前请先查看示例文件！！！
      <router-link to="/example/import.xlsx" target="_blank">
        <v-btn depressed color="primary">下载示例文件</v-btn>
      </router-link>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <upload-excel-component :on-success="handleSuccess" :before-upload="beforeUpload" />
    <!-- <el-table :data="tableData" border highlight-current-row style="width: 100%;margin-top:20px;">
      <el-table-column v-for="item of tableHeader" :key="item" :prop="item" :label="item" />
    </el-table> -->
    <v-data-table :items="tableData" :headers="headers" hide-default-footer />

    <v-row>
      <v-col cols="12">
        <v-row justify="center">
          <v-btn color="primary" @click="sendImportUser">导入</v-btn>
        </v-row>
      </v-col>
    </v-row>

    <v-dialog
      v-model="showResultDialog"
      width="500"
    >
      <v-card height="500">
        <v-card-title>提示</v-card-title>
        <v-card-text>
          <li v-for="item in resultsData" :key="item.userId">
            账号：{{ item.userId }}, 密码： {{ item.password }}, 结果： {{ item.msg }}
          </li>
        </v-card-text>
        <v-card-actions>

          <v-btn color="primary" @click="showResultDialog = false">确 定</v-btn>
          <v-btn :loading="downloadLoading" style="margin:0 0 20px 20px;" color="primary" @click="handleDownload">
            导出为 Excel
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
import UploadExcelComponent from '@/components/UploadExcel/index.vue'
import { parseTime } from '@/utils'
export default {
  name: 'ImportUserPage',
  components: { UploadExcelComponent },
  data() {
    return {
      tableData: [],
      tableHeader: [],
      showResultDialog: false,
      resultsData: [],
      downloadLoading: false,
      bookType: 'xlsx',
      autoWidth: true,
      filename: 'userIdAndPassword',
      headers: [
        {
          text: '学号',
          sortable: false,
          value: 'userId'
        },
        { text: '姓名', sortable: false, value: 'username' },
        { text: '性别', sortable: false, value: 'sex' },
        { text: '年级', sortable: false, value: 'grade' },
        { text: '专业', sortable: false, value: 'major' },
        { text: '角色', sortable: false, value: 'role' }
      ],
      showMessage: false,
      message: ''
    }
  },
  methods: {
    backToUser() {
      this.$router.push(`/admin/user`)
    },
    beforeUpload(file) {
      const isLt1M = file.size / 1024 / 1024 < 1

      if (isLt1M) {
        return true
      }
      this.message = '请不要上传超过 1M 的文件'
      this.showMessage = true
      return false
    },
    handleSuccess({ results, header }) {
      this.tableData = results
      this.tableHeader = header
    },
    getTableDate() {
      console.log(this.tableData)
    },
    sendImportUser() {
      if (this.tableData.length === 0) {
        this.message = '请先选择需要导入的文件！'
        this.showMessage = true
        return
      }
      // 转换性别数据
      for (let i = 0; i < this.tableData.length; i++) {
        if (this.tableData[i].sex === '男') {
          this.tableData[i].sex = 0
        } else {
          this.tableData[i].sex = 1
        }
      }

      this.httpPost(`/admin/user/add`, this.tableData, (json) => {
        this.showResultDialog = true
        this.resultsData = json.data
        this.tableData = []
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = Object.keys(this.resultsData[0])
        const filterVal = Object.keys(this.resultsData[0])
        const data = this.formatJson(filterVal, this.resultsData)
        excel.export_json_to_excel({
          header: tHeader,
          data,
          filename: this.filename,
          autoWidth: this.autoWidth,
          bookType: this.bookType
        })
        this.downloadLoading = false
      })
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v => filterVal.map(j => {
        if (j === 'timestamp') {
          return parseTime(v[j])
        } else {
          return v[j]
        }
      }))
    }
  }
}
</script>
