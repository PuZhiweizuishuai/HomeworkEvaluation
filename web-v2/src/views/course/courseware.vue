<template>
  <v-container fill-height fluid>
    课件
  </v-container>
</template>

<script>
export default {
  name: 'Courseware',
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
