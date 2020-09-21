import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    // 网站基本信息
    webInfo: {
      name: '作业互评系统',
      logo: '/logo.png',
      openInvitationRegister: 1
    },
    // 用户信息
    userInfo: (localStorage.getItem('user') != null &&
    localStorage.getItem('user') !== 'undefined' &&
    localStorage.getItem('user') !== '')
      ? JSON.parse(localStorage.getItem('user')) : null,

    // 编辑课程时需要的分类信息
    editCourseTag: []
  },
  mutations: {
    setUserInfo(state, userInfo) {
      // 将传递的数据先保存到 localStorage 中
      localStorage.setItem('user', JSON.stringify(userInfo))
      // 之后才是修改state中的状态
      state.userInfo = userInfo
    },
    setEditCourseInfoTag(state, tag) {
      state.editCourseTag = tag
    }
  },
  actions: {
  },
  modules: {
  }
})
