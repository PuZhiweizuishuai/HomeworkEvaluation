<template>
  <div>
    <a-page-header
      style="border: 1px solid rgb(235, 237, 240)"
      title="广告及大图管理"
      sub-title="对主页以及其它页面页头，广告进行管理"
    />
    <br>
    <a-row>
      <a-form-model layout="inline">
        <a-form-model-item>
          <a-input v-model="title" placeholder="标题" />
        </a-form-model-item>
        <a-form-model-item label="是否在投放">
          <a-select v-model="status" style="width:200px" placeholder="是否在投放">
            <a-select-option value="0">
              没有
            </a-select-option>
            <a-select-option value="1">
              有
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="显示位置">
          <a-select v-model="type" style="width:200px" placeholder="显示位置">
            <a-select-option value="0">
              社区顶部轮播大图
            </a-select-option>
            <a-select-option value="1">
              课程页顶部轮播大图
            </a-select-option>
            <a-select-option value="2">
              社区广告
            </a-select-option>
            <a-select-option value="3">
              课程页广告
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item>
          <a-button
            type="primary"
            html-type="submit"
            @click="selectAd"
          >
            查找
          </a-button>
        </a-form-model-item>
      </a-form-model>
    </a-row>
    <br>
    <a-button icon="plus-circle" @click="showAdd">
      添加新的广告
    </a-button>
    <br>
    <el-table
      :data="adList"
      style="width: 100%"
    >
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="创建时间">

              <span v-text="formatterTime(null, null, props.row.createTime, null)" />
            </el-form-item>
            <el-form-item label="更新时间">

              <span v-text="formatterTime(null, null, props.row.updateTime, null)" />
            </el-form-item>
            <el-form-item label="跳转地址">
              <a :href="props.row.url" target="_blank">
                <span>{{ props.row.url }}</span>
              </a>
            </el-form-item>
            <el-form-item label="图片地址">
              <a :href="props.row.image" target="_blank">
                <span>{{ props.row.image }}</span>
              </a>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column
        label="ID"
        prop="id"
      />
      <el-table-column
        label="标题"
        prop="title"
      />
      <el-table-column
        label="投放位置"
        prop="type"
        :formatter="formatterType"
      />
      <el-table-column
        label="开始时间"
        prop="startTime"
        :formatter="formatterTime"
      />
      <el-table-column
        label="结束时间"
        prop="endTime"
        :formatter="formatterTime"
      />
      <el-table-column
        label="创建人"
        prop="createUser"
      />
      <el-table-column
        label="点击量"
        prop="viewCount"
      />
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="editAd(scope.$index, scope.row)"
          >编辑</el-button>
          <el-button
            v-if="showStop(scope.$index, scope.row)"
            size="mini"
            type="danger"
            @click="stopAd(scope.$index, scope.row)"
          >结束</el-button>
        </template>
      </el-table-column>
    </el-table>
    <br>
    <a-pagination show-quick-jumper :default-current="page" :total="total" @change="onPageChange" />
    <AddAdvertisement ref="addAdView" :visible="addVisible" :edittype="editType" @close="closeAdd" @reload="reload" />
  </div>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import AddAdvertisement from '@/views/admin/add-advertisement.vue'
export default {
  name: 'AD',
  components: {
    AddAdvertisement
  },
  data() {
    return {
      adList: [],
      page: 1,
      total: 0,
      limit: 20,
      addVisible: false,
      title: '',
      status: -1,
      type: -1,
      slectflag: false,
      editType: false
    }
  },
  created() {
    this.getADList()
  },
  methods: {
    reload(data) {
      this.getADList()
    },
    stopAd(data, row) {
      fetch(this.SERVER_API_URL + '/admin/ad/stop', {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(row.id)
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.$message.success('停止成功')
            this.getADList()
          } else {
            this.$message.error(json.message)
          }
        })
        .catch(e => {
          this.$message.error('网络异常，请检查网络后重试')
        })
    },
    getADList() {
      fetch(this.SERVER_API_URL + `/admin/ad/list?page=${this.page}&limit=${this.limit}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          this.total = json.page.totalCount
          this.page = json.page.currPage
          this.adList = json.page.list
          this.slectflag = false
        })
        .catch(e => {
          return null
        })
    },
    selectAd() {
      console.log(this.title, this.status, this.type)
      if (this.title === '' && this.status === -1 && this.type === -1) {
        this.getADList()
        this.$message.info('请输入查询内容！')
        return
      }
      let url = this.SERVER_API_URL + `/admin/ad/list?title=${this.title}&status=${this.status}&type=${this.type}`
      if (this.slectflag) {
        url = this.SERVER_API_URL + `/admin/ad/list?title=${this.title}&status=${this.status}&type=${this.type}&page=${this.page}&limit=${this.limit}`
      }
      fetch(url, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          this.total = json.page.totalCount
          this.page = json.page.currPage
          this.adList = json.page.list
          this.slectflag = true
        })
        .catch(e => {
          return null
        })
    },
    formatterTime(row, column, cellValue, index) {
      return TimeUtil.renderTime(cellValue)
    },
    editAd(index, scope) {
      this.addVisible = true
      this.editType = true
      this.$refs.addAdView.addEditData(scope)
    },
    onPageChange(pageNumber) {
      this.page = pageNumber
      this.getADList()
    },
    showAdd() {
      this.addVisible = true
      this.editType = false
      this.$refs.addAdView.addData()
    },
    closeAdd(data) {
      this.addVisible = false
    },
    showStop(index, row) {
      const time = new Date().getTime()

      if (row.startTime <= time && row.endTime >= time) {
        return true
      } else {
        return false
      }
    },
    formatterType(row, column, cellValue, index) {
      if (cellValue === 0) {
        return '社区顶部轮播大图'
      } else if (cellValue === 1) {
        return '课程页顶部轮播大图'
      } else if (cellValue === 2) {
        return '社区广告'
      } else {
        return '课程页广告'
      }
    }
  }
}
</script>

<style scoped>

</style>
