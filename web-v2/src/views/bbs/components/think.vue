<template>
  <v-card>
    <v-card-title>
      写想法
    </v-card-title>
    <v-card-subtitle>
      分享你此刻的想法
    </v-card-subtitle>
    <v-card-subtitle>
      <v-row>
        <v-col>
          <v-divider />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-textarea

            v-model="article.content"
            label="想法"
            hint="想法"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-file-input
            :rules="rules"
            accept="image/*, video/*"
            placeholder="选择图片或视频"
            prepend-icon="mdi-camera"
            label="选择图片或视频"
          />
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-btn color="primary">
          发布
        </v-btn>
      </v-row>
    </v-card-subtitle>
  </v-card>
</template>

<script>
export default {
  data() {
    return {
      article: {
        title: '',
        content: '',
        tag: [],
        tagId: 0,
        type: 0,
        courseId: null,
        verifyCode: '',
        atUsers: [],
        offerPoint: 0
      },
      rules: [
        value => !value || value.size < 20000000 || '上传文件大小要小于 20 MB!'
      ]
    }
  }
}
</script>

<style>

</style>
