<template>
  <div>
    <a-layout id="components-layout-demo-side" style="min-height: 100vh">
      <a-layout-sider v-model="collapsed" theme="light" style="background: white;" collapsible>
        <div class="logo">
          <router-link to="/">
            <h2 v-if="collapsed" style="color: #0e85ff;font-size: 24px;" @click="clickLogo">管理</h2>
            <img v-if="collapsed === false" src="/logo.png" width="100px" style="margin-left: 25px;" @click="clickLogo">
          </router-link>
        </div>
        <a-menu theme="light" :default-selected-keys="nowSelect" mode="inline" @click="nowSelectKey">

          <a-menu-item key="首页">
            <router-link to="/admin/home">
              <a-icon type="dashboard" />
              <span>首页</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="用户列表">
            <router-link to="/admin/user/list">
              <a-icon type="user" />
              <span>用户列表</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="我创建的课程">
            <router-link to="/admin/curriculum/my-create">
              <a-icon type="book" />
              <span>我创建的课程</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="添加课程">
            <router-link to="/admin/curriculum/create">
              <a-icon type="plus" />
              <span>添加课程</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="课程分类修改">
            <router-link to="/admin/curriculum/tag">
              <a-icon type="tool" />
              <span>课程分类修改</span>
            </router-link>
          </a-menu-item>
          <a-menu-item key="广告及大图管理">
            <router-link to="/admin/ad">
              <a-icon type="shopping-cart" />
              <span>广告及大图管理</span>
            </router-link>
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
      <a-layout>
        <a-layout-header style="background: #fff; padding: 0">
          <!-- {{ nowSelect[0] }} -->
          <HeaderAvatar style="margin-right:50px" />
        </a-layout-header>
        <a-breadcrumb style="margin: 16px 0" />
        <a-layout-content style="margin: 0 16px">
          <div :style="{ padding: '24px', background: '#fff', minHeight: '360px' }">
            <router-view />
          </div>
        </a-layout-content>
        <Footer />
      </a-layout>
    </a-layout>
    <div>
      <a-back-top />
      <strong style="color: rgba(64, 64, 64, 0.6)" />
    </div>
  </div>
</template>
<script>
import Footer from '@/layout/components/Footer.vue'
import HeaderAvatar from '@/layout/components/HeaderAvatar.vue'

export default {
  name: 'Admin',
  components: { Footer, HeaderAvatar },
  data() {
    return {
      collapsed: false,
      nowSelect: ['首页']
    }
  },
  created() {
    const key = sessionStorage.getItem('adminSelect')
    if (key != null && key !== '') {
      this.nowSelect = [key]
    } else {
      this.nowSelect = ['用户列表']
    }
  },
  methods: {
    nowSelectKey(data) {
      this.nowSelec = [data.key]
      sessionStorage.setItem('adminSelect', data.key)
    },
    showTitle() {
      return this.nowSelec[0]
    },
    clickLogo() {
      window.sessionStorage.setItem('headerSelectKey', 'bbs')
      this.selectKey = ['bbs']
    }
  }
}
</script>
<style>
#components-layout-demo-custom-trigger .trigger {
  font-size: 18px;
  line-height: 64px;
  padding: 0 24px;
  cursor: pointer;
  transition: color 0.3s;
}

#components-layout-demo-custom-trigger .trigger:hover {
  color: #1890ff;
}

#components-layout-demo-side .logo {
  height: 32px;
  background: white;
  margin: 16px;
}
#collapsed-table {
    background: white;
}
</style>
