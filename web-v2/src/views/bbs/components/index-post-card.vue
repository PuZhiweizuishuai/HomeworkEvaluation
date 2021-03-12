<template>
  <v-card outlined>
    <!-- 首页发帖与个人信息展示卡片 -->
    <v-card-subtitle>
      <v-col />
      <v-row justify="space-around">
        <div>
          <router-link to="/bbs/post?type=0">
            <v-btn
              class="mx-2"
              depressed
              fab

              color="primary"
            >
              <v-icon>
                mdi-pencil
              </v-icon>
            </v-btn>
            <p class="btn-text-info">发帖</p>
          </router-link>
        </div>
        <div>
          <router-link to="/bbs/post?type=2">
            <v-btn
              class="mx-2"
              depressed
              fab

              color="secondary"
            >
              <v-icon>
                mdi-comment-question
              </v-icon>
            </v-btn>
            <p class="btn-text-info">提问</p>
          </router-link>
        </div>
        <div>
          <v-btn
            class="mx-2"
            fab

            color="success"
            depressed
          >
            <v-icon>
              mdi-message-text
            </v-icon>
          </v-btn>
          <p class="btn-text-info">想法</p>
        </div>
      </v-row>
      <v-row>
        <v-divider />
      </v-row>
      <v-row>

        <v-btn block depressed @click="goToDraft">
          草稿箱
        </v-btn>

      </v-row>
    </v-card-subtitle>
  </v-card>
</template>

<script>
export default {
  methods: {
    goToDraft() {
      // <router-link to="/bbs/draft/box">
      this.$router.push('/bbs/draft/box')
    }
  }
}
</script>

<style>
  .btn-text-info {
    margin-left: 22px;
    color: black
  }
</style>
