# 作业互评系统前端重构


## 快速运行


### 运行

```
npm install
```


### 构建准备

之后请删除 `node_modules\vuetify\src\styles\elements\_code.sass`下的对于code样式的设置，避免对 vditor 样式造成干扰

删除后结果：

```
.v-application
  code, kbd


  code


  kbd

```

然后配置`main.js`下的 `Vue.prototype.SERVER_API_URL`值

与`src\store\index.js`下的web基本信息



因为本项目需要在局域网环境下进行访问，所以对于前端Markdown编辑器Vditor的CDN配置进行了修改，将其默认的CND地址更改为了本地地址。如果你没有局域网访问需求，可以将CDN配置删除，采用Vditor的默认CDN jsdelivr

修改以下文件目录


- src\components\vditor\comment.vue

- src\components\vditor\show-markdown.vue

- src\components\vditor\vditor.vue

- src\views\course\info.vue

- src\views\changelog.vue

- src\views\article\show-course-article.vue


将其中的

```js
          cdn: '/vditor',
          theme: {
            path: '/vditor/dist/css/content-theme'
          },
```

删除即可。


另外由于Vditor全部文件较大，所以在git提交时，我并没有提交Vditor的文件，如果没有删除Vditor本地CDN配置的情况下，请到https://www.jsdelivr.com/package/npm/vditor   下载你所需要的Vditor版本到/web-v2/public/vditor目录下然后再运行


### 最后

```
npm run serve
```


### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
