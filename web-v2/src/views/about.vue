<template>
  <v-container fill-height>
    <v-row>
      <v-col cols="12">
        <h1>关于</h1>
        <v-divider />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        本站是一个仿中国大学MOOC的在线学习网站。
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <h1>浏览器建议：</h1>
        <v-divider />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        推荐使用最新版的 Chrome， 新 Edge， Firefox 访问此网站，使用其它浏览器可能造成页面无法正常显示，功能异常等 Bug
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        另外请在官方网站进行浏览器下载，不推荐任何第三方网站 <br>
        Chrome： <a href="https://www.google.cn/chrome/" target="_blank">https://www.google.cn/chrome/</a> <br>
        新 Edge ： <a href="https://www.microsoft.com/zh-cn/edge" target="_blank">https://www.microsoft.com/zh-cn/edge</a> <br>
        Firefox： <a href="https://www.firefox.com.cn/" target="_blank">https://www.mozilla.org/</a>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <h1>Bug 反馈与源码获取</h1>
        <v-divider />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        Github: <a href="https://github.com/PuZhiweizuishuai/HomeworkEvaluation" target="_blank">https://github.com/PuZhiweizuishuai/HomeworkEvaluation</a><br>
        <br>
        Gitee: <a href="https://gitee.com/puzhiweizuishuai/HomeworkEvaluation" target="_blank">https://gitee.com/puzhiweizuishuai/HomeworkEvaluation</a>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <h1>更新日志</h1>
        <v-divider />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <router-link to="/changelog">
          更新日志
        </router-link>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'About'
}
</script>

<style>

</style>
