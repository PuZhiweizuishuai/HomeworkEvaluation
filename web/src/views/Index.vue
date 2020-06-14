<template>
  <div>
    <a-layout>
      <Header />
    </a-layout>
    <a-layout>
      <a-layout-content :style="{ padding: '0 50px', marginTop: '64px' }">
        <a-breadcrumb :style="{ margin: '16px 0' }">
          <a-breadcrumb-item>Home</a-breadcrumb-item>
          <a-breadcrumb-item>List</a-breadcrumb-item>
          <a-breadcrumb-item>App</a-breadcrumb-item>
        </a-breadcrumb>

        <div :style="{ background: '#fff', padding: '24px', minHeight: '380px' }">
          <router-view />
        </div>
      </a-layout-content>

      <Footer />
    </a-layout>
  </div>
</template>

<script>
import Header from '@/layout/components/Header.vue'
import Footer from '@/layout/components/Footer.vue'

export default {
  name: 'Index',
  components: { Header, Footer },
  deta() {
    return {
      width: 0
    }
  },
  watch: {
    getWidth() {
      console.log(document.body.clientWidth)
    }
  }
}
</script>

<style>

</style>
