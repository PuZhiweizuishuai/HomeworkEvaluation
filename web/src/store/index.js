import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    userInfo: (localStorage.getItem('user') != null &&
    localStorage.getItem('user') !== 'undefined' &&
    localStorage.getItem('user') !== '')
      ? JSON.parse(localStorage.getItem('user')) : null
  },
  mutations: {
    setUserInfo(state, userInfo) {
      // 将传递的数据先保存到 localStorage 中
      localStorage.setItem('user', JSON.stringify(userInfo))
      // 之后才是修改state中的状态
      state.userInfo = userInfo
    }
  },
  actions: {
  },
  modules: {
  }
})
