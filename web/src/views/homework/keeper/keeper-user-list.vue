<template>
  <el-table
    :data="tableData"
    style="width: 100%"
  >
    <el-table-column
      label="学号"
    >
      <template slot-scope="scope">
        <!-- TODO 超链接到该学生在本课程中的数据 -->
        <a href="#">
          <span v-text="scope.row.userId" />
        </a>
      </template>
    </el-table-column>
    <el-table-column
      label="姓名"
      prop="studentName"
    />
    <el-table-column
      label="提交时间"
    >
      <template slot-scope="scope">
        <!-- TODO 超链接到该学生在本课程中的数据 -->
        <span v-text="timeFormate(scope.row.submitTime)" />
      </template>

    </el-table-column>
    <el-table-column
      label="成绩"
      prop="score"
    />
    <el-table-column label="操作">
      <template slot-scope="scope">
        <el-button
          size="mini"
          @click="commentHomework(scope.$index, scope.row)"
        >
          {{ btnlable }}
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
export default {
  name: 'KeeperUserList',
  props: {
    userlist: {
      type: Array,
      default: () => []
    },
    btnlable: {
      type: String,
      default: '批改'
    }
  },
  data() {
    return {
      tableData: this.userlist
    }
  },
  methods: {
    timeFormate(date) {
      return TimeUtil.formateTimeToChinese(date)
    },
    commentHomework(index, row) {
      if (this.btnlable === '查看') {
        this.$router.push({ path: `/curriculum/keeper/homework/${row.homeworkId}/correcting`, query: { studentId: row.userId, type: 'see' }})
      } else {
        this.$router.push({ path: `/curriculum/keeper/homework/${row.homeworkId}/correcting`, query: { studentId: row.userId }})
      }
      // console.log(row)
    }
  }
}
</script>

<style>

</style>
