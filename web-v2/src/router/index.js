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
      // 课程详情页
      {
        path: '/course/info/:id',
        component: () => import('@/views/course/info.vue'),
        name: 'Info',
        meta: {
          title: '课程详情'
        }
      },
      // 用户主页
      {
        path: '/user/:id',
        name: 'CourseInfo',
        component: () => import('@/views/user/index.vue'),
        meta: {
          title: '个人主页'
        }
      },
      // 关于
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
  // 课程主页
  {
    path: '/course/learn/:id',
    component: () => import('@/layout/course/index.vue'),
    meta: {
      title: '课程'
    },
    children: [
      {
        path: '/course/learn/:id',
        name: 'Learn',
        component: () => import('@/views/course/learn.vue'),
        meta: {
          title: '课程详情'
        }
      },
      {
        path: '/course/learn/:id/score',
        name: 'Score',
        component: () => import('@/views/course/score.vue'),
        meta: {
          title: '评分标准'
        }
      },
      {
        path: '/course/learn/:id/courseware',
        name: 'Courseware',
        component: () => import('@/views/course/courseware.vue'),
        meta: {
          title: '课件'
        }
      },
      {
        path: '/course/learn/:id/exam',
        name: 'Exam',
        component: () => import('@/views/course/exam.vue'),
        meta: {
          title: '测验与作业'
        }
      },
      {
        path: '/course/learn/:id/bbs',
        name: 'BBS',
        component: () => import('@/views/course/bbs.vue'),
        meta: {
          title: '讨论区'
        }
      },
      {
        path: '/course/learn/:id/setting',
        name: 'Setting',
        component: () => import('@/views/course/setting/index.vue'),
        meta: {
          title: '设置'
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
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: store.state.webInfo.name + '- 登录'
    }
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
