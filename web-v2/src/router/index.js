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
      // 社区
      {
        path: '/bbs',
        name: 'IndexBBS',
        component: () => import('@/views/bbs/index.vue'),
        meta: {
          title: '社区'
        }
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
      {
        path: '/myclass',
        component: () => import('@/views/home/myclass.vue'),
        name: 'MyClass',
        meta: {
          title: '我的课程'
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
      title: '课程',
      requireAuth: true
    },
    children: [
      {
        path: '/course/learn/:id',
        name: 'Learn',
        component: () => import('@/views/course/learn.vue'),
        meta: {
          // title: '课程详情',
          requireAuth: true
        }
      },
      {
        path: '/course/learn/:id/score',
        name: 'Score',
        component: () => import('@/views/course/score.vue'),
        meta: {
          // title: '评分标准',
          requireAuth: true
        }
      },
      {
        path: '/course/learn/:id/courseware',
        name: 'Courseware',
        component: () => import('@/views/course/courseware.vue'),
        meta: {
          // title: '课件',
          requireAuth: true
        }
      },
      // 显示作业列表
      {
        path: '/course/learn/:id/exam',
        name: 'Exam',
        component: () => import('@/views/course/exam.vue'),
        meta: {
          // title: '测验与作业',
          requireAuth: true
        }
      },
      // 显示作业内容
      {
        path: '/course/learn/:id/exam/homework/:homeworkId',
        name: 'HomeworkInfo',
        component: () => import('@/views/homework/index.vue'),
        meta: {
          // title: '测验与作业',
          requireAuth: true
        }
      },
      // 讨论区
      {
        path: '/course/learn/:id/bbs',
        name: 'BBS',
        component: () => import('@/views/course/bbs.vue'),
        meta: {
          // title: '讨论区',
          requireAuth: true
        }
      },
      // 设置页面
      {
        path: '/course/learn/:id/setting',
        name: 'Setting',
        component: () => import('@/views/course/setting/index.vue'),
        meta: {
          // title: '设置',
          requireAuth: true
        }
      },
      // 创建作业
      {
        path: '/course/learn/:id/create/homework',
        name: 'CreateHomework',
        component: () => import('@/views/homework/create.vue'),
        meta: {
          requireAuth: true
        }
      },
      // 作业批改页
      {
        path: '/course/learn/:id/keeper/homework/:homeworkId',
        name: 'KeeperHomework',
        component: () => import('@/views/keeper/index.vue'),
        meta: {
          requireAuth: true
        }
      },
      // 展示用户提交的作业，并进行批改
      {
        path: '/course/learn/:id/keeper/homework/:homeworkId/correct',
        name: 'CorrectHomework',
        component: () => import('@/views/keeper/correct.vue'),
        meta: {
          requireAuth: true
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
