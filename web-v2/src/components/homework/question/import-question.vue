<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-btn depressed color="primary" @click="openTableDialog">导入题目</v-btn>
        <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
        <v-btn depressed color="success" @click="newQuestionDialog = true">新建题目</v-btn>
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
          :items="questionList"
          hide-default-footer
          :items-per-page="size"
          :page.sync="page"
        >
          <!-- 题目内容 -->
          <template v-slot:item.question="{ item }">
            {{ showQuestionText(item.question) }}
          </template>

          <template v-slot:item.type="{ item }">
            <v-chip
              class="ma-2"
              small
              :color="Constant.QUESTION_TYPE_COLOR[item.type]"
              text-color="white"
            >
              {{ Constant.QUESTION_TYPE[item.type] }}
            </v-chip>
          </template>

          <template v-slot:item.actions="{ item }">
            <v-tooltip left>
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  icon
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="deleteItem(item)"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>

              </template>
              <span>删除</span>
            </v-tooltip>
          </template>
          <template v-slot:no-data>
            <v-btn color="success" @click="newQuestionDialog = true">新建题目</v-btn>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <v-row justify="space-around">
      <v-btn depressed @click="back">上一步</v-btn>
      <v-btn
        color="primary"
        depressed
        @click="submitQuestionList"
      >
        下一步
      </v-btn>

    </v-row>
    <!-- 导入问题弹窗 -->
    <v-dialog
      v-model="tableDialog"
    >
      <v-card outlined>
        <QuestionTable ref="QuestionTables" @questions="importQuestion" />
      </v-card>
    </v-dialog>
    <!--  -->
    <v-dialog
      v-model="newQuestionDialog"
    >
      <v-card outlined>
        <CreateQuestion @question="getNewQuestion" />
      </v-card>
    </v-dialog>
    <v-snackbar
      v-model="showMessage"
      :top="true"
      :timeout="3000"
    >
      {{ message }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="pink"
          text
          v-bind="attrs"
          @click="showMessage = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import QuestionTable from '@/components/homework/question/question-table.vue'
import CreateQuestion from '@/components/homework/question/create-question.vue'
import Constant from '@/utils/constant.vue'
export default {
  name: 'ImportQuestion',
  components: {
    QuestionTable,
    CreateQuestion
  },
  data() {
    return {
      Constant,
      headers: [
        { text: '题目标题', sortable: false, value: 'question' },
        { text: '题目类型', sortable: false, value: 'type' },
        { text: '标签', sortable: false, value: 'tag' },
        { text: '分值', sortable: false, value: 'score' },
        { text: '难度', sortable: false, value: 'difficulty' },
        { text: '操作', value: 'actions', sortable: false }
      ],
      questionList: [],
      // question: {
      //   id: null,
      //   question: '',
      //   // 类型
      //   type: 0,
      //   // 选择题选项
      //   options: [],
      //   // 选择题答案
      //   answer: [],
      //   // 问答，填空题答案
      //   otherAnswer: '',
      //   // 提示
      //   tips: '',
      //   // 是否分享 【0 私有  1 其它老师可见 】
      //   shareStatus: 0,
      //   // 难度
      //   difficulty: 0,
      //   // 标签
      //   tag: [],
      //   // 分值
      //   score: 1
      // },
      page: 1,
      size: 1000,
      tableDialog: false,
      newQuestionDialog: false,
      message: '',
      showMessage: false
    }
  },
  methods: {
    openTableDialog() {
      this.tableDialog = true
    },
    getNewQuestion(value) {
      this.questionList.push(value)
      this.newQuestionDialog = false
    },
    showQuestionText(value) {
      if (value.length < 25) {
        return value
      }
      return value.substring(0, 25)
    },
    deleteItem(item) {
      const index = this.questionList.indexOf(item)
      this.questionList.splice(index, 1)
    },
    importQuestion(value) {
      for (let i = 0; i < value.length; i++) {
        this.questionList.push(value[i])
      }
      this.tableDialog = false
    },
    submitQuestionList() {
      if (this.questionList.length === 0) {
        this.message = '请先添加问题！'
        this.showMessage = true
        return
      }
      let count = 0
      for (let i = 0; i < this.questionList.length; i++) {
        count = count + parseInt(this.questionList[i].score)
      }
      this.$emit('questions', this.questionList, count)
    },
    back() {
      this.$emit('back', true)
    }
  }
}
</script>

<style>

</style>
