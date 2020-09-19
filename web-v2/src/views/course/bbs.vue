<template>
  <v-container fill-height fluid>
    讨论区
  </v-container>
</template>

<script>
export default {
  name: 'BBS',
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
