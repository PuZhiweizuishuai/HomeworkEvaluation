<template>
  <v-container>
    <v-row v-for="item in votes" :key="item.id.counter">
      <v-col>
        <!--  -->
        <Card v-if="showCard" :key="cardKey" :vote="item" :votelog="votelog" :logmap="logMap" @choice="getUserChoice" />
      </v-col>
    </v-row>
    <v-row v-if="showSubmitBtn" justify="center">
      <v-btn color="success" depressed @click="submit">投票</v-btn>
    </v-row>
    <v-col />
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
import Card from '@/components/vote/vote-card.vue'
export default {
  components: {
    Card
  },
  props: {
    votes: {
      type: Array,
      default: () => { return [] }
    },
    votelog: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      vote: {
        articleId: this.$route.params.articleId,
        choices: []
      },
      map: {},
      showMessage: false,
      message: '',
      logMap: null,
      showCard: false,
      showSubmitBtn: true,
      cardKey: 0
    }
  },
  created() {
    this.createLogMap()
    this.showSubmit()
  },
  methods: {
    createLogMap() {
      if (this.votelog != null) {
        this.logMap = {}
        for (let i = 0; i < this.votelog.choices.length; i++) {
          this.logMap[this.votelog.choices[i].id] = this.votelog.choices[i]
        }
      }
      this.showCard = true
    },
    showSubmit() {
      if (this.$store.state.userInfo) {
        if (this.votelog == null) {
          this.showSubmitBtn = true
          return true
        }
        this.showSubmitBtn = false
        return false
      } else {
        this.showSubmitBtn = false
        return false
      }
    },
    getUserChoice(value) {
      this.map[value.id] = value
    },
    submit() {
      Object.keys(this.map).forEach((key) => {
        this.vote.choices.push(this.map[key])
      })
      if (this.vote.choices.length === 0) {
        if (this.votes[0].endTime !== 0 && this.votes[0].endTime < new Date().getTime()) {
          this.message = '投票已结束'
          this.showMessage = true
          return
        }
        return
      }

      this.httpPost('/vote', this.vote, (json) => {
        if (json.status === 200) {
          this.message = '投票成功！'
          this.votelog = json.data
          this.$emit('success', true)
          this.createLogMap()
          this.cardKey++
          this.showMessage = true
          this.showSubmitBtn = false
        } else if (json.status === 500) {
          this.message = '请先登录后再投票！'
          this.showMessage = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    }
  }
}
</script>

<style>

</style>
