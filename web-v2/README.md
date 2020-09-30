# 作业互评系统前端重构


## 快速运行


 运行

```
npm install
```

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


之后

```
npm run serve
```



### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
