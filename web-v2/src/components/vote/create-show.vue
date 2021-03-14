<template>
  <v-card>
    <v-card-title>
      <v-row justify="center">
        <v-col cols="10">
          {{ vote.title }}
        </v-col>
      </v-row>
    </v-card-title>
    <v-card-subtitle>
      <v-row justify="center">
        <v-col cols="10">
          选项：
        </v-col>
      </v-row>
      <v-row v-for="(item, i) in vote.choices" :key="i" justify="center">
        <v-col cols="10">
          <h4>{{ item }} </h4>
        </v-col>
      </v-row>

      <v-row justify="end">
        <v-col cols="3">
          <!-- <v-btn color="primary" depressed>修改</v-btn> -->
          <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
          <v-btn color="error" depressed @click="deleteItem">删除</v-btn>
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

    }
  },
  methods: {
    deleteItem() {
      this.$emit('delete', this.vote)
    }
  }
}
</script>

<style>

</style>
