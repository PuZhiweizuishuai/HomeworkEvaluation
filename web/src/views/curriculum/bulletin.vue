<template>
  <div>
    <div style="float: right;">
      <a-button type="primary" size="large" @click="visible = true">新建公告</a-button>
    </div>
    <el-table
      :data="dataList"
      style="width: 100%;"
    >
      <el-table-column
        prop="id"
        header-align="center"
        align="center"
        label="ID"
      />
      <el-table-column
        prop="userId"
        header-align="center"
        align="center"
        label="发布者ID"
      />
      <el-table-column
        prop="title"
        header-align="center"
        align="center"
        label="标题"
      />

      <el-table-column
        header-align="center"
        align="center"
        label="发布时间"
      >
        <template slot-scope="scope">
          <span v-text="TimeUtil.renderTime(scope.row.updateTime)" />
        </template>
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        label="更新时间"
      >
        <template slot-scope="scope">
          <span v-text="TimeUtil.renderTime(scope.row.updateTime)" />
        </template>
      </el-table-column>
      <el-table-column
        label="状态"
        align="center"
        header-align="center"
      >
        <template slot-scope="scope">
          <span v-text="showStatus(scope.row.status)" />
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作"
      >
        <template>
          <el-button type="text" size="small">修改</el-button>
          <el-button type="text" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="currentPage"
      :page-sizes="[15, 20, 30, 40, 50]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="totalCount"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <a-drawer
      title="发布新公告"
      :placement="'bottom'"
      :closable="false"
      :visible="visible"
      height="600"
      @close="cancel"
    >
      <a-form-model :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-model-item label="标题">
          <a-input v-model="bulletinData.title" placeholder="标题" />
        </a-form-model-item>
      </a-form-model>

      <a-row>
        <a-col cols="24">
          <Vditor
            :idname="`bulletinPost`"
            :placeholder="'详细的内容，可以包含图片，视频，文件等'"
            :uploadurl="uploadurl"
            @vditor-input="setVditorInput"
          />
        </a-col>
      </a-row>
      <div style="margin: 24px;text-align: center;">
        <a-button type="primary" size="large" style="width:200px" @click="submit">
          发布
        </a-button>
      </div>
    </a-drawer>
  </div>
</template>

<script>
import Vditor from '@/components/vditor/vditor.vue'
import TimeUtil from '@/utils/time-util.vue'
export default {
  name: 'Bulletin',
  components: {
    Vditor
  },
  data() {
    return {
      TimeUtil,
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      uploadurl: this.SERVER_API_URL + '/upload/file',
      dataList: [],
      currentPage: 1,
      pageSize: 15,
      totalCount: 0,
      visible: false,
      bulletinData: {
        title: '',
        text: '',
        curriculumId: this.$route.params.id
      }
    }
  },
  created() {
    this.list()
  },
  methods: {
    list() {
      fetch(`${this.SERVER_API_URL}/bulletin/list/${this.$route.params.id}?page=${this.currentPage}&limit=${this.pageSize}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          this.dataList = json.data.list
          this.totalCount = json.totalCount
          this.currentPage = json.currPage
        })
        .catch(e => {
          return null
        })
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.list()
    },
    handleCurrentChange(value) {
      this.currentPage = value
      this.list()
    },
    submit() {
      if (this.bulletinData.text === '' || this.bulletinData.title === '') {
        this.$message.warning('标题或正文不能为空')
        return
      }
      fetch(`${this.SERVER_API_URL}/bulletin/post`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(this.bulletinData)
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.$message.success('发布成功！')
            this.visible = false
            this.list()
          } else {
            this.$message.error(json.message)
          }
        })
        .catch(e => {
          return null
        })
    },
    cancel() {
      this.visible = false
    },
    setVditorInput(value) {
      this.bulletinData.text = value
    },
    showStatus(value) {
      if (value === 1) {
        return '已删除'
      } else {
        return '已发布'
      }
    }
  }
}
</script>

<style>

</style>
