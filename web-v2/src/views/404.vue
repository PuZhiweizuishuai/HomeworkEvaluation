<template>
  <v-main>
    <v-container>
      <v-row justify="center">
        <h1>非常抱歉，你要找的页面走丢了！！！</h1>
      </v-row>
      <v-row justify="center">
        <v-col cols="5">
          <v-img src="/images/error.gif" />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-row justify="center">
            <v-btn large color="primary" @click="back">返回上一级</v-btn>
          </v-row>
        </v-col>
      </v-row>
      <v-row justify="center">
        <h1>404 了</h1>
      </v-row>
    </v-container>
  </v-main>
</template>

<script>
export default {
  methods: {
    back() {
      this.$router.go(-1)
    }
  }
}
</script>

<style>

</style>
