<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-btn depressed color="success" @click="goToCreate">新建作业</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-divider />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-data-table
          :headers="headers"
          :items="homeworkList"
          hide-default-footer
          :items-per-page="size"
          :page.sync="page"
        >
          <template v-slot:item.createTeacher="{ item }">
            <a :href="`/user/${item.createTeacher}`" target="_blank">{{ item.createTeacher }}</a>
          </template>
          <template v-slot:item.openTime="{ item }">
            <span v-text="TimeUtil.renderTime(item.openTime)" />
          </template>
          <template v-slot:item.closeTime="{ item }">
            <span v-text="TimeUtil.renderTime(item.closeTime)" />
          </template>
          <!-- 作业类型 -->
          <template v-slot:item.type="{ item }">
            <v-chip
              class="ma-2"
              small
              :color="Constant.HOMEWORK__TYPE_COLOR[item.type]"
              text-color="white"
            >
              {{ Constant.HOMEWORK_TYPE[item.type] }}
            </v-chip>
          </template>
          <!-- 作业状态 -->
          <template v-slot:item.status="{ item }">
            <v-chip
              class="ma-2"
              small
              :color="Constant.HOMEWORK_STATUS_COLOR[item.status+1]"
              text-color="white"
            >
              {{ Constant.HOMEWORK_STATUS[item.status+1] }}
            </v-chip>
          </template>
          <!-- 操作 -->
          <template v-slot:item.actions="{ item }">
            <v-tooltip left>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  icon
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="showHomeworkStatus(item)"
                >
                  <v-icon>mdi-alert-circle</v-icon>
                </v-btn>

              </template>
              <span>
                <span v-if="item.status == 4">查看</span>
                <span v-else>批改</span>
              </span>
            </v-tooltip>
            <!-- 删除，如有必要再添加 -->
            <!-- <v-tooltip left>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  icon
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="showHomeworkStatus(item)"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>

              </template>
              <span>删除</span>
            </v-tooltip> -->
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-pagination
        v-model="page"
        :length="length"
        @input="pageChange"
      />
    </v-row>
  </v-container>
</template>

<script>
import Constant from '@/utils/constant.vue'
import TimeUtil from '@/utils/time-util.vue'
export default {
  data() {
    return {
      Constant,
      TimeUtil,
      homeworkList: [],
      page: 1,
      size: 20,
      length: 1,
      id: 0,
      headers: [
        {
          text: 'ID',
          sortable: false,
          value: 'id'
        },
        { text: '标题', sortable: false, value: 'title' },
        { text: '创建老师', sortable: false, value: 'createTeacher' },
        { text: '开始时间', sortable: false, value: 'openTime' },
        { text: '结束时间', sortable: false, value: 'closeTime' },
        { text: '类型', sortable: false, value: 'type' },
        { text: '状态', sortable: false, value: 'status' },
        { text: '操作', value: 'actions', sortable: false }
      ]
    }
  },
  created() {
    this.id = this.$route.params.id
    this.getHomeworkList()
  },
  methods: {
    showHomeworkStatus(value) {
      console.log(value)
      this.$router.push(`/course/learn/${this.id}/keeper/homework/${value.id}`)
    },
    getHomeworkList() {
      this.httpGet(`/homework/list/${this.id}?page=${this.page}&limit=${this.size}`, (json) => {
        if (json.status === 200) {
          this.homeworkList = json.data.list
          this.length = json.data.totalPage
        } else {
          this.$router.push(`/course/info/${this.id}`)
        }
      })
    },
    pageChange(value) {
      this.page = value
      this.getHomeworkList()
    },
    goToCreate() {
      this.$router.push(`/course/learn/${this.id}/create/homework`)
    }
  }
}
</script>

<style>

</style>
