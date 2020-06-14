<template>
  <div>
    <!-- 新建题目 -->
    <a-button type="primary" @click="()=>{editType = false, newQuestionVisible = true}">
      新建题目
    </a-button>
    <a-button style=" margin-left: 24px;" @click="questionListVisible = true">
      导入题目
    </a-button>

    <!-- 显示当前题目列表 -->
    <el-table
      ref="multipleTable"
      :data="questionTableData"
      tooltip-effect="dark"
      style="width: 100%"
    >
      <el-table-column
        label="题目"
        width="300"
      >
        <template slot-scope="scope">{{ scope.row.question }}</template>
      </el-table-column>
      <el-table-column
        label="类型"
      >
        <template slot-scope="scope">
          <el-tag
            :type="'primary'"
            disable-transitions
          >
            <span v-text="getQuestionType(scope.row.type)" />
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="答案"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-text="getQuestionAnswer(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column
        prop="score"
        label="分值"
      />
      <el-table-column label="编辑">
        <template slot-scope="scope">
          <!-- <el-button
            size="mini"
            @click="editQuestion(scope.$index, scope.row)"
          >编辑</el-button> -->
          <el-button
            size="mini"
            type="danger"
            @click="removeQuestion(scope.$index, scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 导入题目需要显示的题目列表 -->
    <el-dialog title="题目列表" :visible.sync="questionListVisible">
      <el-form :inline="true" :model="selectKey">
        <el-form-item label="题目内容：">
          <a-input v-model="selectKey.title" placeholder="题目关键字" />
        </el-form-item>

        <el-form-item label="类型：">
          <el-select v-model="selectKey.type" style="width:200px" placeholder="题目类型">
            <el-option label="单选" value="0" />
            <el-option label="多选" value="1" />
            <el-option label="填空" value="2" />
            <el-option label="问答，客观题" value="3" />
            <el-option label="判断" value="4" />
          </el-select>
        </el-form-item>
        <a-button
          type="primary"
          :disabled="selectKey.title === '' || selectKey.type == 0"
        >
          查找
        </a-button>
      </el-form>

      <el-table>
        <!-- 查找题目部分 -->
        <el-table-column property="date" label="题目" width="150" />
        <el-table-column property="name" label="类型" width="200" />
        <el-table-column property="address" label="答案" />
      </el-table>
    </el-dialog>

    <!-- 新建题目需要显示的题目列表 -->
    <el-dialog title="新建题目" :visible.sync="newQuestionVisible" width="80%">
      <el-form :model="question">
        <el-form-item label="题目类型">
          <el-select v-model="question.type" style="width:200px" placeholder="题目类型">
            <el-option label="单选" value="0" />
            <el-option label="多选" value="1" />
            <el-option label="填空" value="2" />
            <el-option label="问答，客观题" value="3" />
            <el-option label="判断" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="作业题目">
          <Vditor :placeholder="'详细的题目内容，可以包含图片，视频，文件等'" :uploadurl="uploadurl" @vditor-input="setVditorInput" />
        </el-form-item>

        <!-- 添加选择题选项 -->
        <el-form-item v-if="question.type == 0 || question.type == 1" label="选项">
          <el-tag
            v-for="tag in question.options"
            :key="tag"
            closable
            :disable-transitions="false"
            @close="closeOptions(tag)"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-if="inputVisible"
            ref="saveTagInput"
            v-model="optionValue"
            class="input-new-tag"
            size="small"
            @keyup.enter.native="handleInputConfirm"
            @blur="handleInputConfirm"
          />
          <el-button v-else class="button-new-tag" size="small" @click="addNewOptions">添加选项</el-button>
        </el-form-item>

        <!-- 添加选择题答案 -->
        <el-form-item v-if="question.type == 0 || question.type == 1" label="答案">
          <el-tag
            v-for="tag in question.answer"
            :key="tag"
            closable
            :disable-transitions="false"
            @close="closeAnswer(tag)"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-if="inputAnswerVisible"
            ref="saveAnswerInput"
            v-model="answerValue"
            class="input-new-tag"
            size="small"
            @keyup.enter.native="handleAnswerConfirm"
            @blur="handleAnswerConfirm"
          />
          <el-button v-else class="button-new-tag" size="small" @click="addNewAnswer">添加答案</el-button>
        </el-form-item>

        <!-- 添加填空与问答题答案 -->
        <el-form-item v-if="question.type == 2 || question.type == 3" label="答案">
          <el-input v-model="question.otherAnswer" type="textarea" style="width:80%" placeholder="填空问答题不支持自动判题，需要教师手都判题打分，此处只是参考答案，所以暂不支持图片文件上传，如有需要，请自行输入链接" />
        </el-form-item>

        <!-- 添加判断题答案 -->
        <el-form-item v-if="question.type == 4" label="答案">
          <el-select v-model="question.otherAnswer" style="width:200px" placeholder="答案">
            <el-option label="对" value="0" />
            <el-option label="错" value="1" />
          </el-select>
        </el-form-item>

        <el-form-item label="提示">
          <el-input v-model="question.tips" type="textarea" style="width:80%" placeholder="选填" />
        </el-form-item>

        <el-form-item label="分值">
          <el-input-number v-model="question.score" :min="1" :max="100" label="题目分值" />

        </el-form-item>

        <el-form-item label="是否分享">
          <el-select v-model="question.shareStatus" style="width:200px" placeholder="是否分享">
            <el-option label="私有" value="0" />
            <el-option label="其他老师可见" value="1" />
          </el-select>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <a-alert
          v-if="showError"

          message="输入数据有误，请检查你的输入数据！"
          banner
          closable
        />
        <el-button @click="()=>{newQuestionVisible = false, editType = false}">取 消</el-button>
        <el-button type="primary" @click="addQuestionToList()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Vditor from '@/components/vditor/vditor.vue'
export default {
  name: 'ImportQuestuion',
  components: { Vditor },
  props: {
    questionlist: {
      type: Array,
      default: () => { return [] }
    }
  },
  data() {
    return {
      answerValue: '',
      optionValue: '',
      inputVisible: false,
      inputAnswerVisible: false,
      questionTableData: this.questionlist,
      question: {
        question: '',
        type: '0',
        answer: [],
        options: [],
        otherAnswer: '',
        tips: '',
        score: 1,
        shareStatus: '0'
      },
      questionListVisible: false,
      newQuestionVisible: false,
      selectKey: {
        title: '',
        type: '0'
      },
      showError: false,
      uploadurl: this.SERVER_API_URL + '/upload/file',
      editType: false
    }
  },
  created() {

  },
  methods: {
    editQuestion(index, data) {
      this.question = data
      this.newQuestionVisible = true
      this.editType = true
    },
    removeQuestion(index, data) {
      const dataSource = [...this.questionTableData]
      this.questionTableData = dataSource.filter(item => {
        return item.question !== data.question
      })
    },
    getQuestionAnswer(data) {
      if (data.type === '0' || data.type === '1') {
        return data.answer.toString()
      } else if (data.type === '2' || data.type === '3') {
        return data.otherAnswer
      } else {
        if (data.otherAnswer === '0') {
          return '对'
        } else {
          return '错'
        }
      }
    },
    getQuestionType(type) {
      if (type === '0') {
        return '单选'
      } else if (type === '1') {
        return '多选'
      } else if (type === '2') {
        return '填空'
      } else if (type === '3') {
        return '问答，客观题'
      } else {
        return '判断'
      }
    },
    addQuestionToList() {
      if (this.checkQuestionVuale()) {
        if (this.editType) {
          this.editType = false
          this.showError = false
          this.newQuestionVisible = false
          return
        }
        this.questionTableData.push(JSON.parse(JSON.stringify(this.question)))
        this.$emit('question-list', this.questionTableData)
        this.editType = false
        this.showError = false
        this.newQuestionVisible = false
      } else {
        this.editType = false
        this.showError = false
        this.newQuestionVisible = false
      }
    },
    checkQuestionVuale() {
      if (this.question.question !== '') {
        if (this.question.type === '0' || this.question.type === '1') {
          if (this.question.answer.length !== 0 && this.question.options.length !== 0) {
            return true
          }
        } else if (this.question.type === '2' || this.question.type === '3' || this.question.type === '4') {
          return true
        }
      }
      return false
    },
    handleInputConfirm() {
      const inputValue = this.optionValue
      if (inputValue) {
        this.question.options.push(inputValue)
      }
      this.inputVisible = false
      this.optionValue = ''
    },
    handleAnswerConfirm() {
      const inputValue = this.answerValue
      if (inputValue) {
        this.question.answer.push(inputValue)
      }
      this.inputAnswerVisible = false
      this.answerValue = ''
    },
    addNewOptions() {
      this.inputVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    addNewAnswer() {
      this.inputAnswerVisible = true
      this.$nextTick(_ => {
        this.$refs.saveAnswerInput.$refs.input.focus()
      })
    },
    closeOptions(tag) {
      this.question.options.splice(this.question.options.indexOf(tag), 1)
    },
    closeAnswer(tag) {
      this.question.answer.splice(this.question.answer.indexOf(tag), 1)
    },
    setVditorInput(data) {
      this.question.question = data
    }
  }
}
</script>

<style scoped>
.el-tag + .el-tag {
    margin-left: 10px;
  }
  .button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
  }
  .input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }
</style>
