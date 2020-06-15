<template>
  <a-layout id="components-layout-demo-fixed-sider">
    <a-layout-sider theme="light" :style="{ overflow: 'auto', height: '100vh', position: 'fixed', left: 0 }">
      <div class="logo">
        <router-link to="/">
          <img src="/logo.png" width="100px" style="margin-left: 25px;" @click="clickLogo">
        </router-link>
      </div>
      <a-card hoverable style="width: 90%;margin-top: 40px;margin-left: 5%;">
        <img
          slot="cover"
          :title="curriculumInfo.curriculumName"
          :alt="curriculumInfo.curriculumName"
          :src="curriculumInfo.curriculumImageUrl"
        >
      </a-card>
      <a-menu theme="light" mode="inline" :default-selected-keys="['公告']" style="margin-top: 24px">

        <a-menu-item key="公告">
          <router-link :to="`/curriculum/learn/${id}`">
            <a-icon type="exclamation-circle" />
            <span class="nav-text">公告</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="评分标准">
          <a-icon type="line-chart" />
          <span class="nav-text">评分标准</span>
        </a-menu-item>
        <a-menu-item key="课件">
          <a-icon type="video-camera" />
          <span class="nav-text">课件</span>
        </a-menu-item>
        <a-menu-item key="测验与作业">
          <router-link :to="`/curriculum/learn/${id}/homework`">
            <a-icon type="read" />
            <span class="nav-text">测验与作业</span>
          </router-link>
        </a-menu-item>
        <!-- <a-menu-item key="考试">
          <a-icon type="carry-out" />
          <span class="nav-text">考试</span>
        </a-menu-item> -->
        <a-menu-item key="讨论区">
          <a-icon type="appstore-o" />
          <span class="nav-text">讨论区</span>
        </a-menu-item>
        <a-menu-item key="课程管理">
          <router-link :to="`/curriculum/learn/${id}/setting`">
            <a-icon type="setting" />
            <span class="nav-text">课程管理</span>
          </router-link>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout :style="{ marginLeft: '200px' }">
      <Header :position="'static'" :logo-is-show="false" />
      <a-page-header
        style="border: 1px solid rgb(235, 237, 240)"
        :title="curriculumInfo.curriculumName"
        :sub-title="curriculumInfo.simpleInfo"
        :tags="curriculumInfo.teacherName"
        @back="() => null"
      />
      <a-layout-content :style="{ margin: '0px 16px 0', overflow: 'initial' }">
        <div :style="{ padding: '24px', background: '#fff' }">
          <router-view />
        </div>
      </a-layout-content>
      <Footer />
    </a-layout>
  </a-layout>
</template>
<script>
import Header from '@/layout/components/Header.vue'
import Footer from '@/layout/components/Footer.vue'

export default {
  name: 'CurriculumLayout',
  components: { Header, Footer },
  data() {
    return {
      id: 0,
      curriculumInfo: []
    }
  },
  created() {
    this.id = this.$route.params.id
    this.judgeAccessPower()
  },
  methods: {
    clickLogo() {
      window.sessionStorage.setItem('headerSelectKey', 'bbs')
    },
    /**
     * 向服务器请求判断，有没有进入这门课程的权力
     * 服务器返回详细课程信息
     */
    judgeAccessPower() {
      // 获取课程 ID
      const id = this.$route.params.id
      fetch(this.SERVER_API_URL + `/curriculum/learn/${id}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          if (json.status === 200) {
            this.curriculumInfo = json.data
          } else {
            this.$router.push('/curriculum/info/' + id)
          }
        })
        .catch(e => {
          return null
        })
    }
  }
}
</script>

<style>
#components-layout-demo-fixed-sider .logo {
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
  margin: 16px;
}
</style>
