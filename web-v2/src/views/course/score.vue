<template>
  <v-container fill-height fluid>
    评分标准
  </v-container>
</template>

<script>
export default {
  name: 'Score',
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
