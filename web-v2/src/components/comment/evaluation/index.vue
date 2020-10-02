<template>
  <v-container fluid>
    <v-row>
      <v-col cols="9" style="padding-bottom: 0px;">
        <v-tabs>
          <v-tab @click="setType(0)">评论时间</v-tab>
          <v-tab @click="setType(1)">时间倒序</v-tab>
          <v-tab>最多点赞</v-tab>
          <v-tab @click="setType(3)">最多评论</v-tab>
        </v-tabs>
      </v-col>
      <v-col cols="3" style="padding-bottom: 0px;">
        <h3> {{ total }} 个评价</h3>
      </v-col>
    </v-row>
    <!-- 分割线 -->
    <v-row>
      <v-divider />
    </v-row>
    <v-row v-for="item in commentsList" :key="item.id">
      <v-col cols="12">
        <Card :comment="item" :artice="artice" :homework="homework" :type="11" />
      </v-col>
    </v-row>
    <v-row v-if="total == 0" justify="center">
      <h3> 暂无评价 </h3>
    </v-row>

    <v-row justify="center">
      <v-pagination
        v-if="total > size"
        v-model="page"
        :length="length"
        @input="pageChange"
      />
    </v-row>
    <v-row>
      <v-col>
        <v-divider />
      </v-col>
    </v-row>
    <v-row>
      <v-col v-if="this.$store.state.userInfo" cols="12">
        <router-link :to="`/user/${this.$store.state.userInfo.userId}`">
          <v-avatar size="48" style="float: left;">
            <v-img
              :src="this.$store.state.userInfo.userAvatarUrl"
              :alt="this.$store.state.userInfo.username"
              :title="this.$store.state.userInfo.username"
            />
          </v-avatar>
          <h4
            style="margin-left: 60px;margin-top: 12px;"
          >{{ this.$store.state.userInfo.username }} </h4>
        </router-link>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <Vditor
          ref="commentVditor"
          :placeholder="'请自觉遵守互联网相关的政策法规，严禁发布色情、暴力、反动的言论。'"
          :uploadurl="uploadurl"
          :height="400"
          @vditor-input="setVditorInput"
        />
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="4">
        <img :src="verifyImageUrl" alt="验证码" title="点击刷新" style="cursor:pointer;" @click="getVerifyImage">
      </v-col>
      <v-col cols="4">
        <v-text-field
          v-model="comment.verifyCode"
          placeholder="验证码"
          label="验证码"
          :rules="[() => comment.verifyCode != null || '验证码不能为空']"
          clearable
        />
      </v-col>
      <v-col>
        <v-row justify="center">
          <v-btn depressed color="success" @click="submit">
            评论
          </v-btn>
        </v-row>
      </v-col>
    </v-row>
    <v-snackbar
      v-model="showMessage"
      :top="true"
      :timeout="3000"
    >
      {{ message }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="pink"
          text
          v-bind="attrs"
          @click="showMessage = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import Card from '@/components/comment/card.vue'
import Vditor from '@/components/vditor/vditor.vue'
export default {
  components: {
    Vditor,
    Card
  },
  props: {
    homework: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      artice: {
        authorId: -1
      },
      commentsList: [],
      verifyImageUrl: this.SERVER_API_URL + '/verifyImage',
      uploadurl: this.SERVER_API_URL + '/upload/file',
      comment: {
        submitId: 0,
        content: '',
        verifyCode: '',
        // 作业一级评论
        type: 10
      },
      message: '',
      showMessage: false,
      page: 1,
      size: 20,
      length: 0,
      total: 0,
      type: 0
    }
  },
  created() {
    this.getComment()
  },
  methods: {
    getVerifyImage() {
      this.verifyImageUrl = this.SERVER_API_URL + '/verifyImage?t=' + new Date().getTime()
    },
    getComment() {
      this.httpGet(`/evaluation/comment/list/${this.$route.params.submitId}?page=${this.page}&limit=${this.size}&sort=${this.type}`, (json) => {
        if (json.status === 200) {
          this.commentsList = json.data.list
          this.length = json.data.totalPage
          this.total = json.data.totalCount
        } else {
          //
        }
      })
    },
    pageChange(value) {
      this.page = value
      this.getComment()
    },

    setType(value) {
      this.type = value
      this.getComment()
    },
    setVditorInput(value) {
      this.comment.content = value
    },
    submit() {
      this.comment.submitId = this.$route.params.submitId
      console.log(this.comment)
      if (!this.$store.state.userInfo) {
        this.message = '请先登录后再评论！'
        this.showMessage = true
        return
      }
      if (this.comment.content == null || this.comment.content === '') {
        this.message = '评论内容不能为空！'
        this.showMessage = true
        return
      }
      if (this.comment.verifyCode == null || this.comment.verifyCode === '') {
        this.message = '验证码不能为空！'
        this.showMessage = true
        return
      }
      this.httpPost('/evaluation/comment/submit', this.comment, (json) => {
        if (json.status === 200) {
          this.message = '评论成功！'
          this.showMessage = true
          this.$refs.commentVditor.setTextValue('')
          this.commentsList.push(json.data)
        } else {
          //
          this.message = json.message
          this.showMessage = true
        }
      })
    }
  }
}
</script>

<style>

</style>
