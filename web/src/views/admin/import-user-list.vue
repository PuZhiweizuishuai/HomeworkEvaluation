<template>
  <div class="app-container">
    <a-page-header
      style="border: 1px solid rgb(235, 237, 240)"
      title="返回"
      @back="back"
    />
    <upload-excel-component :on-success="handleSuccess" :before-upload="beforeUpload" />
    <el-table :data="tableData" border highlight-current-row style="width: 100%;margin-top:20px;">
      <el-table-column v-for="item of tableHeader" :key="item" :prop="item" :label="item" />
    </el-table>
    <div>
      <el-button type="primary" @click="sendImportUser">导入</el-button>
    </div>
    <el-dialog
      title="提示"
      :visible.sync="showResultDialog"
      width="50%"
    >
      <span>插入结果：</span>
      <br>
      <li v-for="item in resultsData" :key="item.userId">
        账号：{{ item.userId }}, 密码： {{ item.password }}, 结果： {{ item.msg }}
      </li>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="showResultDialog = false">确 定</el-button>
        <el-button :loading="downloadLoading" style="margin:0 0 20px 20px;" type="primary" icon="el-icon-document" @click="handleDownload">
          导出为 Excel
        </el-button>
      </span>
    </el-dialog>
  </div>
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
      filename: 'userIdAndPassword'
    }
  },
  methods: {
    beforeUpload(file) {
      const isLt1M = file.size / 1024 / 1024 < 1

      if (isLt1M) {
        return true
      }

      this.$message({
        message: 'Please do not upload files larger than 1m in size.',
        type: 'warning'
      })
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
        this.$message({
          message: '请添加数据后导入！',
          type: 'warning'
        })
        return
      }
      fetch(this.SERVER_API_URL + `/admin/user/add`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(this.tableData)
      }).then(response => response.json())
        .then(json => {
          this.showResultDialog = true
          this.resultsData = json.data
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
    },
    back() {
      this.$router.replace('/admin/user/list')
    }
  }
}
</script>
