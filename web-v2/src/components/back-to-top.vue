<template>
  <v-btn
    v-show="fab"
    v-scroll="onScroll"
    bottom
    color="pink"
    dark
    fab
    fixed
    right
    @click="toTop"
  >
    <v-icon>mdi-chevron-up</v-icon>
  </v-btn>
</template>

<script>
export default {
  name: 'BackToTop',
  data() {
    return {
      fab: false
    }
  },
  methods: {
    onScroll(e) {
      if (typeof window === 'undefined') return
      const top = window.pageYOffset || e.target.scrollTop || 0
      this.fab = top > 200
    //   if (window.pageYOffset > 100) {
    //     this.fab = true
    //   }
    },
    toTop() {
      this.$vuetify.goTo(0)
    }
  }
}
</script>
