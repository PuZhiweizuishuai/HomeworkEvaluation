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
            :label="lable"
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
  </v-row>
</template>

<script>
export default {
  props: {
    cols: {
      type: Number,
      default: 8
    },
    time: {
      type: String,
      default: ''
    },
    lable: {
      type: String,
      default: '年月日'
    }
  },
  data() {
    return {
      datePickerTime: false,
      dateTime: false,
      limitYearTime: this.time,
      limitHourTime: ''
    }
  },
  methods: {
    getTime() {
      this.$refs.datePickerTime.save(this.limitYearTime)

      this.$emit('time', this.limitYearTime)
    }
  }
}
</script>

<style>

</style>
