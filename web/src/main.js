import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/antd.css'
import VueCookie from 'vue-cookies'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import { LayoutPlugin } from 'bootstrap-vue'

Vue.use(LayoutPlugin)

Vue.config.productionTip = false
Vue.config.devtools = true
Vue.use(ElementUI)
Vue.use(VueCookie)
Vue.use(Antd)

Vue.prototype.SERVER_API_URL = 'http://127.0.0.1:8080/api'

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
