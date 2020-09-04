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
      label="提交时间"
    >
      <template slot-scope="scope">
        <!-- TODO 超链接到该学生在本课程中的数据 -->
        <span v-text="timeFormate(scope.row.updateTime)" />
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
          批改
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

    }
  }
}
</script>

<style>

</style>
