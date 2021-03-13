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
    editCourseTag: [],
    tagMap: {
      Set: function(key, value) { this[key] = value },
      Get: function(key) { return this[key] },
      Contains: function(key) { return this.Get(key) != null },
      Remove: function(key) { delete this[key] }
    },
    tagsLength: 0
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
    },
    setTagMap(state, tags) {
      for (let i = 0; i < tags.length; i++) {
        state.tagMap.Set(tags[i].id, tags[i])
        state.tagsLength++
      }
    }
  },
  actions: {
  },
  modules: {
  }
})
