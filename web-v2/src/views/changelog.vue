<template>
  <v-container>
    <v-row>
      <v-col>
        <h3>2020-10-2</h3>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        发布 0.1 beta 版本，完成大部分基本功能！<br>
        <h4>已知问题：</h4>
        <li>在课件详情页点击底部的上一章，下一章无法进行课件切换，需要在顶部选择。</li>
        <li>提交评论或发帖偶尔会出现空指针的异常，此时刷新验证码即可解决！造成此bug的原因是后台在获取Session ID时，无法正确获取所出现的bug，需要下个版本修复。</li>

        <h4>待完成：</h4>
        <li>完整的社区系统。</li>
        <li>完善的数据分析系统。</li>
        <li>用户主页完善。</li>
        <li>课程评分功能。</li>
        <li>点赞收藏功能。</li>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {

}
</script>

<style>

</style>
