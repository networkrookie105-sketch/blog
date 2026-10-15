import router from '@/router/index'
import { profile, loadSite, siteState, siteSettings } from '@/content/profile'
import { getToken } from '@/composables/cookie'
import { showMessage } from '@/composables/util'
import { showPageLoading } from '@/composables/util'
import { hidePageLoading } from '@/composables/util'
import { useUserStore } from '@/stores/user'

//全局路由前置守卫
router.beforeEach(async (to, from, next) => {
  console.log('==>全局路由前置守卫')
  //显示页面加载中
  showPageLoading()
  //若用户想访问后台 （以/admin为前缀的路由）
  //如果未登录，则强制跳转登录页
  let token = getToken()
  if (!token && to.path.startsWith('/admin')) {
    showMessage('请先登录', 'warning')
    next({ path: '/login' })
  } else if (token && to.path == '/login') {
    // 若用户已经登录，且重复访问登录页
    showMessage('请勿重复登录', 'warning')
    // 跳转后台首页
    next({ path: '/admin/index' })
  } else if (!to.path.startsWith('/admin')) {
    if (!['/login'].includes(to.path) && (to.path !== from.path || !siteState.loaded))
      await loadSite()
    next()
  } else {
    const user = useUserStore()
    await user.setUserInfo()
    if (!user.userInfo.roles?.includes('ROLE_ADMIN')) {
      user.logout()
      showMessage('此博客仅允许管理员进入后台', 'warning')
      next({ path: '/login' })
    } else next()
  }
})
// 全局路由后置守卫
router.afterEach((to, from) => {
  console.log('==>全局路由后置守卫')
  //动态设置页面标题
  let title =
    (to.meta.title ? to.meta.title : '') + ' · ' + (siteSettings.name || profile.name || '个人博客')
  document.title = title
  const description = document.querySelector('meta[name="description"]')
  if (description && !to.path.startsWith('/admin') && !['/login'].includes(to.path))
    description.setAttribute(
      'content',
      siteSettings.introduction || profile.intro || '个人主页、项目作品与技术文章'
    )
  // 隐藏页面加载 Loading
  hidePageLoading()
})
