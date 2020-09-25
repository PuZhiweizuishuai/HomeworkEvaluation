<template>
  <v-row>
    <v-col cols="12">
      <v-data-table
        :headers="headers"
        :items="user"
        hide-default-footer
      >
        <template v-slot:item.userId="{ item }">
          <a :href="`/user/${item.userId}`" target="_blank"> {{ item.userId }} </a>
        </template>

        <template v-slot:item.submitTime="{ item }">
          {{ TimeUtil.renderTime(item.submitTime) }}
        </template>
        <template v-slot:item.actions="{ item }">
          <v-tooltip left>
            <template v-slot:activator="{ on, attrs }">
              <v-btn
                icon
                class="mr-2"
                v-bind="attrs"
                v-on="on"
                @click="show(item)"
              >
                <v-icon>mdi-pencil</v-icon>
              </v-btn>

            </template>
            <span>{{ lable }}</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-col>
  </v-row>
</template>

<script>
import TimeUtil from '@/utils/time-util.vue'
/**
 * 作业批改页需要显示的列表
 */
export default {
  name: 'UserList',
  props: {
    user: {
      type: Array,
      default: () => []
    },
    lable: {
      type: String,
      default: '批改'
    }
  },
  data() {
    return {
      id: 0,
      TimeUtil,
      headers: [
        {
          text: '学号',
          sortable: false,
          value: 'userId'
        },
        { text: '姓名', sortable: false, value: 'studentName' },
        { text: '提交时间', sortable: false, value: 'submitTime' },
        { text: '当前成绩', sortable: false, value: 'score' },
        { text: '操作', value: 'actions', sortable: false }
      ]
    }
  },
  created() {
    this.id = this.$route.params.id
    this.homeworkId = this.$route.params.homeworkId
  },
  show(row) {
    //
    this.$router.push({ path: `/course/learn/${this.id}/keeper/homework/${this.homeworkId}`, query: { studentId: row.userId, type: 'see' }})
  }
}
</script>

<style>

</style>
