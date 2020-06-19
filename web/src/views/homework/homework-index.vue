<template>
  <div>
    <a-row>
      <a-col :span="20">
        <a-page-header
          title="测验与作业"
        />
      </a-col>
      <a-col :span="4" style="margin-top: 14px;">
        <a-button type="primary">
          <router-link :to="`/curriculum/learn/${id}/add-homework`">
            添加作业
          </router-link>
        </a-button>
      </a-col>
    </a-row>
    <a-divider />
    <div v-for="item in homeworkList" :key="item.id">
      <b-row style="margin-top: 24px;">
        <b-col cols="8">
          <HomeworkInfo :homework="item" />
        </b-col>
        <b-col>
          <a-tag v-if="item.type === 2" color="red">
            考试
          </a-tag>
          <a-tag v-if="item.type === 1" color="green">
            测验
          </a-tag>
          <a-tag v-if="item.type === 0" color="blue">
            作业
          </a-tag>
          <el-button v-if="item.status === 1" type="success" @click="goToTest(item.id)">前往测验</el-button>
          <el-button v-if="item.status === 0">暂未开始</el-button>
        </b-col>
      </b-row>
    </div>

  </div>
</template>

<script>
import HomeworkInfo from '@/views/homework/homework-info.vue'

export default {
  name: 'HomeworkIndex',
  components: { HomeworkInfo },
  data() {
    return {
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
      fetch(this.SERVER_API_URL + `/homework/info/${this.id}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.homeworkList = json.data || []
          } else {
            this.$message.error(json.message)
          }
        })
        .catch(e => {
          this.$message.error('网络异常，请检查网络后重试！')
          return null
        })
    },
    goToTest(cid) {
      this.$router.push(`/curriculum/learn/${this.id}/homework/${cid}`)
    }
  }
}
</script>

<style scoped>
.homework-info-list{
  width: 80%;
  margin-top: 24px;
}
</style>
