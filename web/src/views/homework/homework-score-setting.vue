<template>
  <a-layout>
    <!-- <a-layout-header> -->
    <template>
      <a-page-header
        style="border: 1px solid rgb(235, 237, 240)"
        title="作业批改与评价系统"
        sub-title="当前已批改"
        @back="() => close()"
      />
    </template>
    <!-- </a-layout-header> -->

    <a-layout-content :style="{ padding: '0 50px' }">
      <!-- 基本信息 -->、
      <!-- 添加数据分析页面，成绩排名，正确率 -->
      <a-card>
        <a-descriptions title="当前作业基本情况">
          <a-descriptions-item label="提交数/未提交">
            Zhou Maomao
          </a-descriptions-item>
          <a-descriptions-item label="批改数/待批改">
            1810000000
          </a-descriptions-item>
          <a-descriptions-item label="作业状态">
            Hangzhou, Zhejiang
          </a-descriptions-item>
          <a-descriptions-item label="Remark">
            empty
          </a-descriptions-item>
          <a-descriptions-item label="Address">
            No. 18, Wantang Road, Xihu District, Hangzhou, Zhejiang, China
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!-- 基本操作，比如批改完成后开启互评 -->

      <!-- 待批改列表 -->

      <!-- 批改完成列表 -->

      <!--  -->
    </a-layout-content>
    <a-layout-footer>
      <Footer />
    </a-layout-footer>
  </a-layout>
</template>

<script>
import Footer from '@/layout/components/Footer.vue'
export default {
  name: '',
  components: {
    Footer
  },
  data() {
    return {
      id: 0
    }
  },
  created() {
    this.id = this.$route.params.id
    this.checkPower()
  },
  methods: {
    // 检查是否有批改权限
    checkPower() {
      fetch(`${this.SERVER_API_URL}/homework/setting/power/${this.id}`, {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          if (json.status !== 200) {
            window.location.href = '/'
            return
          }
          if (!json.data) {
            window.location.href = '/'
            return
          }
        })
        .catch(e => {
          return null
        })
    },
    close() {
      window.opener = null
      window.open('', '_self')
      window.close()
    }
  }
}
</script>

<style>

</style>
