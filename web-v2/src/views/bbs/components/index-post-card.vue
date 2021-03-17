<template>
  <v-card outlined>
    <!-- 首页发帖与个人信息展示卡片 -->
    <v-card-subtitle>
      <v-col />
      <v-row justify="space-around">
        <div>
          <router-link to="/bbs/post?type=10">
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
          <router-link to="/bbs/post?type=12">
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
            <p class="btn-text-info">投票</p>
          </router-link>
        </div>
        <div>
          <v-btn
            class="mx-2"
            fab

            color="success"
            depressed
            @click="thinkDialog = true"
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
      <v-row>

        <v-btn block depressed @click="goToDraft">
          我的收藏
        </v-btn>

      </v-row>
    </v-card-subtitle>
    <v-dialog
      v-model="thinkDialog"

      max-width="500"
    >
      <ThinkCard @success="submitSucc" />
    </v-dialog>
  </v-card>
</template>

<script>
import ThinkCard from '@/components/think/think.vue'
export default {
  components: {
    ThinkCard
  },
  data() {
    return {
      thinkDialog: false
    }
  },
  methods: {
    goToDraft() {
      // <router-link to="/bbs/draft/box">
      this.$router.push('/bbs/draft/box')
    },
    submitSucc(value) {
      this.thinkDialog = false
      this.$emit('success', true)
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
