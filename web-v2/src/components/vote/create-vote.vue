<template>
  <v-card outlined>
    <!-- 投票卡片 -->
    <v-card-title>
      创建投票
    </v-card-title>
    <v-card-subtitle>
      <v-row>
        <v-col>
          <v-text-field
            v-model="vote.title"
            label="投票标题"
          />
        </v-col>
      </v-row>

      <v-row>
        <v-col>
          <v-combobox
            v-model="vote.choices"
            label="选项(每个选项输入完成后，请按回车输入下一个选项)"
            multiple
            chips
          />
        </v-col>
      </v-row>

      <v-row>
        <v-col>
          <v-radio-group
            v-model="vote.type"
            row
          >
            <v-radio
              label="单选"
              :value="0"
            />
            <v-radio
              label="多选"
              :value="1"
            />
          </v-radio-group>
        </v-col>
      </v-row>
      <v-row v-if="vote.type == 1">
        <v-col>
          <v-text-field
            v-model="vote.maxChoice"
            label="最多选几个："
            type="number"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          投票如需定时结束，请完整设置时间：
          <TimeCard :cols="6" @time="getEndTime" />
        </v-col>
      </v-row>
      <v-col />
      <v-row justify="center">
        <v-btn color="primary" @click="submit">
          创建
        </v-btn>
      </v-row>
      <v-col />
    </v-card-subtitle>
  </v-card>
</template>

<script>
import TimeCard from '@/components/form/time-form.vue'

export default {
  components: {
    TimeCard
  },
  data() {
    return {
      vote: {
        title: '',
        type: 0,
        endTime: '',
        maxChoice: 2,
        choices: []
      }
    }
  },
  methods: {
    getEndTime(value) {
      this.vote.endTime = value
    },
    submit() {
      this.$emit('vote', this.vote)
    }
  }
}
</script>

<style>

</style>
