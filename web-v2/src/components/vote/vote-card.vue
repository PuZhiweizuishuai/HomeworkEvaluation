<template>
  <v-container>
    <!-- 具有投票功能的卡片 -->
    <v-row>
      <v-col>
        <h4>{{ vote.title }}</h4> 当前参与人数： {{ vote.voteCount }}, <span v-if="vote.type == 1">多选，最多可选：{{ vote.maxChoice }}个</span>
      </v-col>
    </v-row>
    <v-row v-if="vote.endTime != 0" justify="end">
      结束时间: {{ TimeUtil.renderTime(vote.endTime) }}
    </v-row>
    <v-radio-group v-if="vote.type == 0" v-model="userCheck" :disabled="isDisable()" @change="check">
      <v-row v-for="(item, i) in Object.keys(vote.choices)" :key="i">
        <v-col>

          <v-radio :value="item">
            <template v-slot:label>
              <v-progress-linear v-if="item == userChoice.choices[0]" :value="getProgrss(item)" height="25" color="pink" background-color="#bad6f2">
                {{ item }} {{ getTextValue(item) }}
              </v-progress-linear>
              <v-progress-linear v-else :value="getProgrss(item)" height="25" background-color="#bad6f2">
                {{ item }} {{ getTextValue(item) }}
              </v-progress-linear>
            </template>
          </v-radio>
        </v-col>
      </v-row>
    </v-radio-group>
    <!-- 多选 -->
    <div v-if="vote.type == 1">
      <v-row v-for="(item, i) in Object.keys(vote.choices)" :key="i">
        <v-col
          style="padding-top: 0px;padding-bottom: 0px;"
        >
          <v-checkbox v-model="checkbox" :value="item" :disabled="isDisable()" @change="getUserCheck">
            <template v-slot:label>
              <v-progress-linear :value="getProgrss(item)" height="25" :color="changeColor(item)">
                {{ item }} <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'" />{{ getTextValue(item) }}
              </v-progress-linear>
            </template>
          </v-checkbox>
        </v-col>
      </v-row>
    </div>
    <v-row>
      <v-divider />
    </v-row>
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
import TimeUtil from '@/utils/time-util.vue'
export default {
  props: {
    vote: {
      type: Object,
      default: null
    },
    votelog: {
      type: Object,
      default: null
    },
    logmap: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      TimeUtil,
      userCheck: '',
      checkbox: [],
      showMessage: false,
      message: '',
      userChoice: {
        choices: [
          ''
        ]
      },
      userChoiceMap: {}
    }
  },
  created() {
    this.getUserChoice()
  },
  mounted() {

  },
  methods: {
    getUserChoice() {
      if (this.logmap != null) {
        this.userChoice = this.logmap[this.vote.id]
        for (let i = 0; i < this.userChoice.choices.length; i++) {
          this.userChoiceMap[this.userChoice.choices[i]] = this.userChoice.choices
        }
      } else {
        this.userChoice = {
          choices: [
            ''
          ]
        }
      }
    },
    changeColor(item) {
      if (this.logmap != null) {
        if (this.userChoiceMap[item] != null) {
          return 'pink'
        }
      }
      return '#bad6f2'
    },
    isDisable() {
      console.log(this.vote.endTime)
      if (this.vote.endTime !== 0) {
        if (new Date().getTime() > this.vote.endTime) {
          return true
        }
      }
      if (this.$store.state.userInfo) {
        if (this.votelog == null) {
          return false
        }
        return true
      } else {
        return false
      }
    },
    getProgrss(item) {
      if (this.votelog == null) {
        return 0
      }
      if (this.vote.voteCount === 0) {
        return 0
      }
      return (this.vote.choices[item] / this.vote.voteCount) * 100
    },
    getTextValue(item) {
      if (this.votelog == null) {
        return ''
      }
      if (this.vote.voteCount === 0) {
        return 0
      }
      return this.vote.choices[item] + '人'
    },
    getUserCheck(value) {
      if (value.length > this.vote.maxChoice) {
        this.message = `最多只能选择${this.vote.maxChoice}个选项！`
        this.showMessage = true
        this.checkbox = []
        value = []
      }
      const choice = {
        id: this.vote.id,
        choices: value
      }
      this.$emit('choice', choice)
    },
    check(value) {
      const choice = {
        id: this.vote.id,
        choices: [value]
      }
      this.$emit('choice', choice)
    }
  }
}
</script>

<style>

</style>
