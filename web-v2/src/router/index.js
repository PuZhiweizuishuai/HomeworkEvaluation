import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store/index.js'
import Index from '@/layout/index.vue'
import Home from '@/views/home/index.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',

    component: Index,
    meta: {
      title: store.state.webInfo.name
    },
    children: [
      {
        path: '/',
        name: 'Index',
        component: Home,
        meta: { title: store.state.webInfo.name }
      },
      {
        path: '/about',
        name: 'About',
        component: () => import('@/views/about.vue'),
        meta: {
          title: '关于'
        }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('@/views/login/index.vue')
  }

]

const router = new VueRouter({
  mode: 'history',
  routes
})

// 路由导航守卫
router.beforeEach((to, from, next) => {
  // 路由发生变化修改页面title
  if (to.meta.title) {
    document.title = to.meta.title
  }
  return next()
})

export default router
