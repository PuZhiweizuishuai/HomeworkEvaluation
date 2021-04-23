<template>
  <v-container>
    <v-row no-gutters>
      <v-col
        v-for="item in items"
        :key="item.id"
      >
        <Card :item="item" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Card from '@/views/tools/components/item-card.vue'
export default {
  components: {
    Card
  },
  data() {
    return {
      items: [
        { id: 0, title: '笔记本', icon: '🗺️', src: '/tools/notebook', type: true, color: '#75cfb8', text: '在线云笔记本，做笔记才能学的更好', btn: '进入' },
        { id: 1, title: '在线答题统计', icon: 'mdi-sort', src: '/tools/question', type: true, color: '#a4ebf3', text: '线上做题，实时分析题目正确率，帮助教师发现学生的薄弱点，当堂解决！', btn: '进入' },
        { id: 2, title: '来排序', icon: 'mdi-sort', src: '/tools/sort', type: true, color: '#845ec2', text: '输入主题并选择人数，分享页面给学生，系统随机分配各个同学的顺序。', btn: '进入' },
        { id: 3, title: '生成二维码', icon: 'mdi-qrcode-edit', src: '/tools/qr-code', type: true, color: '#f9f871', text: '输入文本信息，生成对应的二维码。', btn: '进入' },
        { id: 4, title: '电视直播', icon: 'mdi-tv', src: '/tvlive', type: true, color: '#e7e6e1', text: '观看电视直播，了解家国大事。', btn: '进入' },
        // { id: 9, title: '图片与Base64编码互转', icon: 'mdi-image', src: '/tools/image-to-base64', type: true, color: '#ffc478', text: '将图片转成Base64编码形式或将Base64编码图片进行复原。', btn: '进入' },
        // { id: 4, title: '加密解密', icon: 'mdi-lock', src: '/tools/aes', type: true, color: '#f1d1d0', text: '使用AES对文本进行加密解密，便于分享私密内容！', btn: '进入' },
        // { id: 5, title: '狗屁不通文章生成器', icon: 'mdi-book', src: '/tools/bullshit-generator', type: true, color: '#a4ebf3', text: '随机生成一篇谁也看不懂的文章！！！', btn: '进入' },
        // { id: 6, title: '计算器', icon: 'mdi-calculator', src: '/tools/calculator', type: true, color: '#e7e6e1', text: '一个普通的计算器', btn: '进入' },
        // { id: 7, title: 'Spring项目初始化', icon: 'mdi-alpha-s', src: 'https://spring.buguagaoshu.com', type: false, color: '#6db33f', text: 'https://start.spring.io/ 的反向代理，在直接访问原网站速度不够时可用！', btn: '进入' }

        { id: 100, title: '......', icon: 'mdi-gavel', src: '', type: true, color: '#ff5e78', text: '其它工具开发中！', btn: '施工中' }
      ]
    }
  }

}
</script>

<style>

</style>
