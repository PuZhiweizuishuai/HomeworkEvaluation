<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h3>查找：</h3>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="3">
        <v-text-field
          v-model="selectKey.title"
          label="题目内容"
          clearable
        />

      </v-col>
      <v-col cols="3">
        <v-select
          v-model="selectKey.type"
          :items="questionTypeItem"
          label="类型"
          item-text="text"
          item-value="value"
          clearable
        />
      </v-col>
      <v-col cols="2">
        <v-btn depressed color="primary" @click="getMyQuestuinList">
          查找
        </v-btn>
      </v-col>
      <v-col cols="2">
        <v-btn depressed color="orange" @click="getMyQuestuinList(0)">
          重新加载
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn depressed color="success" @click="inportQuestion">
          导入已选择题目
        </v-btn>
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
          v-model="selected"
          :headers="headers"
          :items="questionList"
          hide-default-footer
          :items-per-page="size"
          :page.sync="page"
          :single-select="false"
          show-select
        >
          <!-- 题目内容 -->
          <template v-slot:item.question="{ item }">
            {{ showQuestionText(item.question) }}
          </template>
          <!-- 设置分数 -->
          <template v-slot:item.score="{ item }">
            <v-text-field
              v-model="item.score"
              label="设置分值"
              clearable
              type="number"
            />
          </template>
          <!-- 题目类型 -->
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
                  @click="editQuestion(item)"
                >
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>

              </template>
              <span>编辑</span>
            </v-tooltip>
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
    <v-dialog
      v-model="newQuestionDialog"
    >
      <v-card outlined>
        <CreateQuestion :type="1" :old="editItem" @update="updateQuestion" />
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
import Constant from '@/utils/constant.vue'
import CreateQuestion from '@/components/homework/question/create-question.vue'
/**
 * 显示需要导入的问题列表
 */
export default {
  name: 'QuestionTable',
  components: {
    CreateQuestion
  },
  data() {
    return {
      Constant,
      headers: [
        { text: '题目内容（只显示前25个字符）', sortable: false, value: 'question' },
        { text: '题目类型', sortable: false, value: 'type' },
        { text: '标签', sortable: false, value: 'tag' },
        { text: '难度', sortable: false, value: 'difficulty' },
        { text: '设置分数', sortable: false, value: 'score' },
        { text: '操作', value: 'actions', sortable: false }
      ],
      questionList: [],
      page: 1,
      size: 20,
      length: 0,
      selectKey: {
        title: '',
        type: ''
      },
      // 选中的问题
      selected: [],
      questionTypeItem: [
        { text: '单选', value: 0 },
        { text: '多选', value: 1 },
        { text: '填空', value: 2 },
        { text: '问答', value: 3 },
        { text: '判断', value: 4 }
      ],
      newQuestionDialog: false,
      editItem: {},
      message: '',
      showMessage: false
    }
  },
  created() {
    this.getMyQuestuinList()
  },
  methods: {
    getMyQuestuinList(value) {
      if (value === 0) {
        this.selectKey.title = ''
        this.selectKey.type = ''
        this.page = 1
      }
      this.httpGet(`/questions/list?page=${this.page}&limit=${this.size}&key=${this.selectKey.title}&type=${this.selectKey.type}`, (json) => {
        if (json.status === 200) {
          this.questionList = json.data.list
          this.length = json.totalPage
        } else {
          //
        }
      })
    },
    pageChange(value) {
      this.page = value
      this.getMyQuestuinList()
    },
    showQuestionText(value) {
      if (value.length < 25) {
        return value
      }
      return value.substring(0, 25)
    },
    seeQuestion(value) {
      console.log(value)
    },
    editQuestion(value) {
      this.editItem = value
      this.newQuestionDialog = true
    },
    inportQuestion() {
      for (let i = 0; i < this.selected.length; i++) {
        if (this.selected[i].score > 0) {
          //
        } else {
          this.message = '还没有设置分数'
          this.showMessage = true
          return
        }
      }
      this.$emit('questions', this.selected)
    },
    updateQuestion(value) {
      this.newQuestionDialog = false
      this.getMyQuestuinList()
    }
  }

}
</script>

<style>

</style>
