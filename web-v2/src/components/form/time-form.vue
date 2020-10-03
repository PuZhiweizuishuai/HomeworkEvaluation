<template>
  <v-row justify="center">
    <!-- 年月日 -->
    <v-col :cols="cols">
      <v-menu
        ref="datePickerTime"
        v-model="datePickerTime"
        :close-on-content-click="false"
        :return-value.sync="limitYearTime"
        transition="scale-transition"
        offset-y
        min-width="290px"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-text-field
            v-model="limitYearTime"
            label="年月日"
            prepend-icon="mdi-calendar-month"
            readonly
            v-bind="attrs"
            v-on="on"
          />
        </template>
        <v-date-picker v-model="limitYearTime" no-title scrollable locale="zh-cn">
          <v-spacer />
          <v-btn text color="primary" @click="datePickerTime = false">取消</v-btn>
          <v-btn text color="primary" @click="getTime">确认</v-btn>
        </v-date-picker>
      </v-menu>
    </v-col>
    <!-- 具体时间 -->
    <v-col :cols="cols">
      <v-menu
        ref="dateTime"
        v-model="dateTime"
        :close-on-content-click="false"
        :nudge-right="40"
        :return-value.sync="limitHourTime"
        transition="scale-transition"
        offset-y
        max-width="290px"
        min-width="290px"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-text-field
            v-model="limitHourTime"
            label="时分秒"
            prepend-icon="mdi-clock-outline"
            readonly
            v-bind="attrs"
            v-on="on"
          />
        </template>
        <v-time-picker
          v-if="dateTime"
          v-model="limitHourTime"
          locale="zh-cn"
          full-width
          @click:minute="getTime"
        />
      </v-menu>
    </v-col>
  </v-row>
</template>

<script>
export default {
  props: {
    cols: {
      type: Number,
      default: 5
    }
  },
  data() {
    return {
      datePickerTime: false,
      dateTime: false,
      limitYearTime: '',
      limitHourTime: ''
    }
  },
  methods: {
    getTime() {
      this.$refs.dateTime.save(this.limitHourTime)
      this.$refs.datePickerTime.save(this.limitYearTime)
      let time = this.limitYearTime + ' ' + this.limitHourTime + ':00'
      if (this.limitHourTime === '' || this.limitHourTime == null) {
        time = this.limitYearTime + ' ' + '00:00:00'
      }
      this.$emit('time', time)
    }
  }
}
</script>

<style>

</style>
