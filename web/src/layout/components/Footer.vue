<template>
  <a-layout-footer :style="{ textAlign: 'center' }">
    Copyright © 2020 - {{ year }} · <a href="/"> 作业互评系统</a>
  </a-layout-footer>
</template>

<script>
export default {
  name: 'Footer',
  deta() {
    return {
      year: ''
    }
  },
  created() {
    this.getYear()
  },
  methods: {
    getYear() {
      this.year = new Date().getFullYear()
    }
  }
}
</script>
