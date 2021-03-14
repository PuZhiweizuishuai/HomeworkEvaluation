import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import VueCookies from 'vue-cookies'
import HttpFetch from '@/utils/fetch.js'

Vue.config.productionTip = false
Vue.use(VueCookies)
Vue.use(HttpFetch)

Vue.prototype.SERVER_API_URL = '/api' // 'http://127.0.0.1:8080/api'

new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app')
