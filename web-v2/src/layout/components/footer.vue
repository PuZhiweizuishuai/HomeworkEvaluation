<template>
  <v-footer absolute style="background-color: transparent;">
    <router-link to="/about">关于作业互评</router-link>

    <span v-html="`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`" />

    ©2020 - {{ new Date().getFullYear() }}
    Created by <a href="https://vuetifyjs.com/" target="_blank">Vuetify</a>
    <span v-html="`&nbsp;&nbsp;`" />
    and
    <span v-html="`&nbsp;&nbsp`" />
    <a href="https://www.buguagaoshu.com" target="_blank">不挂高数</a>
  </v-footer>
</template>

<script>
export default {

}
</script>

<style>

</style>
