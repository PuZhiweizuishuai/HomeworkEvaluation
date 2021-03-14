<template>
  <v-card outlined>
    <!-- 具有投票功能的卡片 -->
    <v-card-subtitle>
      <v-row>
        <v-col>
          <h4>{{ vote.title }}</h4> 当前参与人数： {{ vote.voteCount }}
        </v-col>
      </v-row>
      <v-radio-group v-if="vote.type == 0" v-model="userCheck">
        <v-row v-for="(item, i) in Object.keys(vote.choices)" :key="i">
          <v-col>

            <v-radio :value="item">
              <template v-slot:label>
                <v-progress-linear :value="getProgrss(item)" height="25">
                  {{ item }} {{ getProgrss(item) }}%
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
            <v-checkbox v-model="checkbox" :value="item">
              <template v-slot:label>
                <v-progress-linear :value="getProgrss(item)" height="25">
                  {{ item }} {{ getProgrss(item) }}%
                </v-progress-linear>
              </template>
            </v-checkbox>
          </v-col>
        </v-row>
      </div>
      <v-row>
        <v-col>
          <v-btn @click="check">test</v-btn>
        </v-col>
      </v-row>
    </v-card-subtitle>
  </v-card>
</template>

<script>
export default {
  props: {
    vote: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      userCheck: '',
      checkbox: []
    }
  },
  mounted() {

  },
  methods: {
    getProgrss(item) {
      if (this.vote.voteCount === 0) {
        return 0
      }
      return (this.vote.choices[item] / this.vote.voteCount) * 100
    },
    check() {
      console.log(this.userCheck)
      console.log(this.checkbox)
    }
  }
}
</script>

<style>

</style>
