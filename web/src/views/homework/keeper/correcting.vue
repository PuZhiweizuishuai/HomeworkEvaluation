<template>
  <a-layout>
    <template>
      <a-page-header
        style="border: 1px solid rgb(235, 237, 240)"
        title="批改"
        @back="back()"
      />
    </template>
    <a-layout-content :style="{ padding: '0 50px' }">
      <a-card title="学生学号：">
        123456
      </a-card>
    </a-layout-content>
  </a-layout>
</template>

<script>
export default {
  name: 'Correcting',
  data() {
    return {

    }
  },
  created() {

  },
  methods: {
    back() {
      this.$router.push({ path: `/curriculum/keeper/homework/${this.$route.params.id}` })
    }
  }
}
</script>

<style>

</style>
