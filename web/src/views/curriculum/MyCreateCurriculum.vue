<template>
  <div>
    <a-page-header
      style="border: 1px solid rgb(235, 237, 240)"
      title="我创建的课程"
      sub-title="我创建的课程列表"
    />
    <b-row>
      <b-col v-for="item in classList" :key="item.id" cols="3" style="margin-top: 10px;">
        <a-card hoverable style="width: 300px">

          <img
            slot="cover"
            :title="item.curriculumName"
            :alt="item.curriculumName"
            :src="item.curriculumImageUrl"
            @click="jumpToCurriculumPage(item.id)"
          >

          <template slot="actions" class="ant-card-actions">
            <a-icon key="edit" type="edit" @click="jumpSetting(item.id)" />
            <a-icon key="ellipsis" type="ellipsis" @click="showMessage" />
          </template>

          <a-card-meta :title="item.curriculumName" :description="showDescription(item.simpleInfo, item.openingTime, item.closeTime)" @click="jumpToCurriculumPage(item.id)" />

        </a-card>
      </b-col>
    </b-row>
    <br><br>
    <div>
      <a-pagination show-quick-jumper :default-current="page" :total="totalCount" @change="onChange" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'MyCreateCurriculum',
  data() {
    return {
      classList: [],
      page: 1,
      linit: 20,
      totalCount: 0
    }
  },
  created() {
    this.getClassList()
  },
  methods: {
    getClassList() {
      const user = this.$store.state.userInfo
      fetch(this.SERVER_API_URL + `/curriculum/list?teacher=${user.userId}&page=${this.page}&limit=${this.linit}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.classList = json.page.list
            this.page = json.page.currPage
            this.totalCount = json.page.totalCount
          } else {
            this.$message.warning(json.message)
          }
        })
        .catch(e => {
          this.$message.warning('网络异常，请检查网络后重试')
        })
    },
    onChange(pageNumber) {
      this.page = pageNumber
      this.getClassList()
    },
    showMessage() {
      this.$message.success('不知道做什么功能，就先这么留着吧')
    },
    jumpToCurriculumPage(data) {
      window.sessionStorage.setItem('headerSelectKey', 'null')
      this.$router.push('/curriculum/learn/' + data)
    },
    showDescription(info, start, end) {
      return info + ' 开课时间：' + this.formateTime(start) + ' 结课时间：' + this.formateTime(end)
    },
    formateTime(data) {
      const da = new Date(data)
      return da.getFullYear() + '-' + (da.getMonth() + 1) + '-' + da.getDate() + ' ' + da.getHours() + ':' + da.getMinutes() + ':' + da.getSeconds()
    },
    jumpSetting(id) {
      this.$router.push('/curriculum/learn/' + id + '/setting')
    }
  }
}
</script>

<style scoped>

</style>
