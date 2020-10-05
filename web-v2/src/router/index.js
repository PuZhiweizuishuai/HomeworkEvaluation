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
      {
        path: '/course/tag/:tagId',
        component: () => import('@/views/home/tag-course.vue'),
        name: 'Tag',
        meta: {
          title: '课程标签'
        }
      },
      {
        path: '/notification',
        component: () => import('@/views/notification/index.vue'),
        name: 'Notification',
        meta: {
          title: '消息通知'
        }
      },
      // 新建课程
      {
        path: '/course/create',
        component: () => import('@/views/course/create/index.vue'),
        name: 'CreateCourse',
        meta: {
          title: '新建课程',
          requireAuth: true,
          tracherPower: true
        }
      },
      // 用户设置
      {
        path: '/user/setting',
        name: 'UserSetting',
        component: () => import('@/views/user/setting.vue'),
        meta: {
          title: '个人主页'
        }
      },
      // 用户主页
      {
        path: '/user/:id',
        name: 'UserHome',
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
      },
      {
        path: '/changelog',
        name: 'ChangeLog',
        component: () => import('@/views/changelog.vue'),
        meta: {
          title: '更新日志'
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
          requireAuth: true
        }
      },
      {
        path: '/course/learn/:id/score',
        name: 'Score',
        component: () => import('@/views/course/score.vue'),
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/course/learn/:id/courseware',
        name: 'Courseware',
        component: () => import('@/views/course/courseware.vue'),
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/course/learn/:id/courseware/:coursewareId',
        name: 'CoursewareInfo',
        component: () => import('@/views/course/courseware-info.vue'),
        meta: {
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
      // 作业互评
      {
        path: '/course/learn/:id/evaluation',
        name: 'Evaluation',
        component: () => import('@/views/course/evaluation.vue'),
        meta: {
          // title: '测验与作业',
          requireAuth: true
        }
      },
      // 作业互评提交的作业列表
      {
        path: '/course/learn/:id/evaluation/homework/:homeworkId',
        name: 'EvaluationList',
        component: () => import('@/views/homework/evaluation/list.vue'),
        meta: {
          requireAuth: true
        }
      },
      // 作业互评详细提交的额作业内容
      {
        path: '/course/learn/:id/evaluation/homework/:homeworkId/comment/:submitId',
        name: 'EvaluationInfo',
        component: () => import('@/views/homework/evaluation/info.vue'),
        meta: {
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
          requireAuth: true
        }
      },
      // 新建主题帖
      {
        path: '/course/learn/:id/bbs/article',
        name: 'CreateArticle',
        component: () => import('@/views/article/create.vue'),
        meta: {
          requireAuth: true
        }
      },
      // 显示主题帖
      {
        path: '/course/learn/:id/bbs/:articleId',
        name: 'ShowCourseArticle',
        component: () => import('@/views/article/show-course-article.vue'),
        meta: {
          requireAuth: true
        }
      },
      // 课程评价
      {
        path: '/course/learn/:id/comment',
        name: 'CourseComment',
        component: () => import('@/views/course/comment.vue'),
        meta: {

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

  // 管理页
  {
    path: '/admin',
    component: () => import('@/layout/admin/index.vue'),
    meta: {
      title: store.state.webInfo.name + ' - 管理系统',
      requireAuth: true,
      admin: true
    },
    children: [
      {
        path: '/admin',
        name: 'Admin',
        component: () => import('@/views/admin/index.vue'),
        meta: {
          admin: true,
          requireAuth: true
        }
      },
      {
        path: '/admin/user',
        name: 'AdminUserList',
        component: () => import('@/views/admin/user-list.vue'),
        meta: {
          admin: true,
          requireAuth: true
        }
      },
      {
        path: '/admin/user/import',
        name: 'AdminImportUserList',
        component: () => import('@/views/admin/import-user.vue'),
        meta: {
          admin: true,
          requireAuth: true
        }
      },
      {
        path: '/admin/course',
        name: 'AdminCourse',
        component: () => import('@/views/admin/course.vue'),
        meta: {
          admin: true,
          requireAuth: true
        }
      },
      {
        path: '/admin/coursetag',
        name: 'AdminCourseTag',
        component: () => import('@/views/admin/course-tag.vue'),
        meta: {
          admin: true,
          requireAuth: true
        }
      },
      {
        path: '/admin/bbs',
        name: 'AdminBBS',
        component: () => import('@/views/admin/bbs.vue'),
        meta: {
          admin: true,
          requireAuth: true
        }
      },
      {
        path: '/admin/topimg',
        name: 'AdminTopImg',
        component: () => import('@/views/admin/top-img.vue'),
        meta: {
          admin: true,
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
  },
  {
    path: '*',
    name: '404',

    component: () => import('@/views/404.vue'),
    meta: {
      title: '404'
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
  const date = new Date().getTime()
  // 已经登录
  if (store.state.userInfo != null) {
    if (store.state.userInfo.expirationTime > date) {
      if (to.path === '/login') {
        return next({ path: '/' })
      }
      if (to.meta.admin) {
        if (store.state.userInfo.role.role === 'ROLE_ADMIN') {
          return next()
        } else {
          //
          return next({ path: '/404' })
        }
      } else {
        return next()
      }
      // 登录到期
    } else {
      store.commit('setUserInfo', null)
      return next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
    // 未登录
  } else {
    if (to.meta.requireAuth) {
      return next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      return next()
    }
  }
})

export default router
