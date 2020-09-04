import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    /**
     * 主页及网页框架部分的路由
     */
    path: '/',
    name: 'Index',
    component: () => import('@/views/Index.vue'),

    meta: { title: '首页' },
    children: [
      {
        path: '/',
        name: 'Home',
        component: () => import('@/views/curriculum/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/bbs',
        name: 'BBS',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '社区' }
      },
      {
        path: '/about',
        name: 'About',
        component: () => import('@/views/About.vue'),
        meta: { title: '关于' }
      },
      {
        path: '/curriculum/info/:id',
        name: 'CourseInfo',
        component: () => import('@/views/curriculum/CourseInfo.vue'),
        meta: { title: '课程信息' }
      }
    ]
  },
  /**
   * 管理员，教师页的路由
   */
  {
    path: '/admin',
    name: 'Admin',
    // @/views/admin/index.vue  @/layout/admin/index.vue
    component: () => import('@/layout/admin/index.vue'),
    meta: { title: '管理员页' },
    redirect: '/admin/home',
    children: [
      {
        path: '/admin/user/list',
        name: 'UserList',
        component: () => import('@/views/admin/user-list-table.vue'),
        meta: { title: '用户列表' }

      },
      {
        path: '/admin/user/import',
        name: 'ImportUser',
        component: () => import('@/views/admin/import-user-list.vue'),
        meta: { title: '导入用户' }

      },
      {
        path: '/admin/curriculum/create',
        name: 'AddCurriculum',
        component: () => import('@/views/curriculum/add.vue'),
        meta: { title: '新建课程' }

      },
      {
        path: '/admin/curriculum/tag',
        name: 'CourseTag',
        component: () => import('@/views/curriculum/CourseTag.vue'),
        meta: { title: '课程分类列表' }
      },
      {
        path: '/admin/curriculum/my-create',
        name: 'MyCreate',
        component: () => import('@/views/curriculum/MyCreateCurriculum.vue'),
        meta: { title: '我创建的课程' }
      },
      {
        path: '/admin/home',
        name: 'AdminHome',
        component: () => import('@/views/admin/index.vue'),
        meta: { title: '管理员主页' }
      },
      {
        path: '/admin/ad',
        name: 'AD',
        component: () => import('@/views/admin/ad.vue'),
        meta: { title: '广告及大图管理' }
      },
      {
        path: '/admin/curriculum/question',
        name: 'Question',
        component: () => import('@/views/admin/question.vue'),
        meta: { title: '问题管理' }
      }
    ]
  },
  /**
   * 登陆注册页路由
   */
  {
    path: '/login',
    name: 'LoginWithLoginOn',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登陆或注册' }
  },
  /**
   * 课程页面的路由
   * 页面设计参照中国大学 MOOC
   */
  {
    path: '/curriculum/learn/:id',
    name: 'CourseLayout',
    component: () => import('@/layout/curriculum/index.vue'),
    meta: { title: '课程' },
    children: [
      {
        path: '/curriculum/learn/:id/setting',
        name: 'Setting',
        component: () => import('@/views/curriculum/setting.vue'),
        meta: { title: '课程设置' }
      },
      {
        path: '/curriculum/learn/:id/add-homework',
        name: 'AddHomework',
        component: () => import('@/views/homework/add-homework.vue'),
        meta: { title: '添加作业' }
      },
      {
        path: '/curriculum/learn/:id/homework',
        name: 'HomeworkIndex',
        component: () => import('@/views/homework/homework-index.vue'),
        meta: { title: '作业' }
      },
      {
        path: '/curriculum/learn/:id/homework/:hid',
        name: 'ShowHomework',
        component: () => import('@/views/homework/show-homework.vue'),
        meta: { title: '做作业' }
      }
    ]
  },
  /**
   * 作业批改，检查，评分独立页面
   * id 是作业 ID
   */
  {
    path: '/curriculum/keeper/homework/:id',
    meta: { title: '作业批改' },
    component: () => import('@/views/homework/keeper/homework-keeper.vue'),
    name: 'HomeworkScoreKeeper'
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

// 路由导航守卫
router.beforeEach((to, from, next) => {
  // const token = window.localStorage.getItem('user')
  // 路由发生变化修改页面title
  if (to.meta.title) {
    document.title = to.meta.title
  }
  return next()
})

export default router
