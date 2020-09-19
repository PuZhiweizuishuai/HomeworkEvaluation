<template>
  <v-container fill-height fluid>
    测验与作业
  </v-container>
</template>

<script>
export default {
  name: 'Exam',
  props: {
    course: {
      type: Object,
      default: null
    },
    role: {
      type: Object,
      default: null
    }
  },
  data() {
    return {

    }
  }
}
</script>

<style>

</style>
