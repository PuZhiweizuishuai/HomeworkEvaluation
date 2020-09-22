<template>
  <div>
    <a-button type="primary">
      <router-link :to="`/curriculum/learn/${id}/add-homework`">
        添加作业
      </router-link>
    </a-button>
    <el-table
      :data="homeworkList"
      style="width: 100%"
    >
      <el-table-column
        label="标题"
        prop="title"
      />
      <el-table-column
        label="类型"
      >

        <template slot-scope="scope">
          <span v-text="getHomeworkType(scope.row.type)" />
        </template>
      </el-table-column>
      <el-table-column
        label="开始时间"
      >
        <template slot-scope="scope">
          <span v-text="timeFormate(scope.row.openTime)" />
        </template>
      </el-table-column>
      <el-table-column
        label="结束时间"
      >
        <template slot-scope="scope">
          <span v-text="timeFormate(scope.row.closeTime)" />
        </template>
      </el-table-column>
      <el-table-column
        label="提交人数"
      >
        <template slot-scope="scope">
          <span v-text="scope.row.submitCount" />
        </template>
      </el-table-column>
      <el-table-column
        label="状态"
      >
        <template slot-scope="scope">
          <span v-text="getHomeworkStatus(scope.row.status)" />
        </template>
      </el-table-column>
      <el-table-column
        label="创建老师"
      >
        <template slot-scope="scope">
          <span v-text="scope.row.createTeacher" />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="seeStudentSubmit(scope.$index, scope.row)"
          >

            <span v-if="scope.row.status === 4">查看</span>
            <span v-else>批改</span>
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
import { getHomeworkStatus, getHomeworkType } from '@/utils/homework-utils.js'

export default {
  name: 'HomeworkList',
  data() {
    return {
      getHomeworkStatus,
      getHomeworkType,
      id: 0,
      homeworkList: []
    }
  },
  created() {
    this.id = this.$route.params.id
    this.getHomeworkList()
  },
  methods: {
    getHomeworkList() {
      fetch(this.SERVER_API_URL + `/homework/list/${this.id}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.homeworkList = json.data.list || []
          } else {
            this.$message.error(json.message)
          }
        })
        .catch(e => {
          this.$message.error('网络异常，请检查网络后重试！')
          return null
        })
    },
    timeFormate(date) {
      return TimeUtil.formateTimeToChinese(date)
    },
    seeStudentSubmit(index, row) {
      // console.log(index, row)
      window.open('/curriculum/keeper/homework/' + row.id, '_blank')
    }
  }
}
</script>
