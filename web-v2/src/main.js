import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import VueCookies from 'vue-cookies'
import HttpFetch from '@/utils/fetch.js'
import 'viewerjs/dist/viewer.css'
import Viewer from 'v-viewer'

Vue.config.productionTip = false
Vue.use(VueCookies)
Vue.use(HttpFetch)
Vue.use(Viewer)

Vue.prototype.SERVER_API_URL = '/api' // 'http://127.0.0.1:8080/api'

console.log('%c不挂高数出品 https://www.buguagaoshu.com', 'color: #1976d2;font-size:2em')
console.log('%cPowered by buguagaoshu 1.0.0 bete', 'color: #1976d2;font-size:1em')
new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app')
