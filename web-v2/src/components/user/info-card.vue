<template>
  <v-card>
    <!-- 个人简介卡片 -->
    <v-img
      :aspect-ratio="5.98"
      :width="500"
      :src="user.topImgUrl"
    />
    <v-card-title>
      <router-link :to="`/user/${user.userId}`">
        <v-avatar size="48" style="float: left;">
          <v-img :src="user.userAvatarUrl" />
        </v-avatar>
      </router-link>
      <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
      {{ user.username }}
      <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
      <span style="font-size: 14px">
        {{ user.userId }} - {{ user.userIntro }}
      </span>
    </v-card-title>
    <v-card-subtitle>
      <v-row>
        <v-col>
          关注了：<router-link :to="'#'">{{ user.followerCount }}</router-link>
          <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
          关注者： <router-link :to="'#'">{{ user.fansCount }}</router-link>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-btn link x-small color="primary" :to="`/user/${user.userId}`">
            查看主页
          </v-btn>
          <span v-html="'&nbsp;&nbsp;&nbsp;&nbsp;'" />
          <v-btn x-small color="success">
            关注
          </v-btn>
        </v-col>
      </v-row>
    </v-card-subtitle>
  </v-card>
</template>

<script>
export default {
  props: {
    user: {
      type: Object,
      default: null
    }
  }
}
</script>

<style>

</style>
