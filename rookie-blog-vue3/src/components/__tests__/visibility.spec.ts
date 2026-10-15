import { afterEach, describe, expect, it, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createMemoryHistory } from 'vue-router'
import Home from '../../pages/frontend/index.vue'
import Portfolio from '../../pages/frontend/portfolio.vue'
import { siteSettings, profile, projects, siteState, downloadResume } from '../../content/profile'
import { getArticlePageList } from '../../api/frontend/article'
vi.mock('../../api/frontend/article', () => ({ getArticlePageList: vi.fn() }))
const wrappers: ReturnType<typeof mount>[] = []
afterEach(() => wrappers.splice(0).forEach((w) => w.unmount()))
async function render(path: string) {
  siteState.loaded = true
  siteSettings.personalHomepagePublic = false
  siteSettings.name = '技术博客'
  profile.name = '不应公开的姓名'
  profile.email = 'private@example.com'
  projects.splice(0, projects.length, { slug: 'private-project', name: '不应公开的项目' } as any)
  vi.mocked(getArticlePageList).mockResolvedValue({
    success: true,
    data: [{ id: 1, title: '公开文章' }]
  } as any)
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/', component: Home },
      { path: '/:pathMatch(.*)*', component: Portfolio }
    ]
  })
  await router.push(path)
  const wrapper = mount(path === '/' ? Home : Portfolio, { global: { plugins: [router] } })
  wrappers.push(wrapper)
  await flushPromises()
  return wrapper
}
describe('个人主页公开控制', () => {
  it('关闭后首页展示文章，导航和页脚不暴露个人入口与内容', async () => {
    const w = await render('/')
    expect(w.text()).toContain('公开文章')
    expect(w.text()).not.toContain('不应公开的姓名')
    expect(w.text()).not.toContain('不应公开的项目')
    expect(w.find('a[href="/about"]').exists()).toBe(false)
    expect(w.find('a[href="/projects"]').exists()).toBe(false)
    expect(w.find('a[href="/contact"]').exists()).toBe(false)
    expect(w.find('a[href="/articles"]').exists()).toBe(true)
    expect(w.find('.footer-links').text()).toContain('后台登录')
  })
  it.each(['/about', '/contact', '/projects', '/projects/private-project'])(
    '关闭后直接访问 %s 也不显示内容',
    async (path) => {
      const w = await render(path)
      expect(w.text()).toContain('个人主页暂未公开')
      expect(w.text()).not.toContain('不应公开的姓名')
      expect(w.text()).not.toContain('private@example.com')
      expect(w.text()).not.toContain('不应公开的项目')
    }
  )
  it('关闭后不能下载个人简历', async () => {
    await render('/')
    const create = vi.fn()
    vi.stubGlobal('URL', { createObjectURL: create })
    downloadResume()
    expect(create).not.toHaveBeenCalled()
    vi.unstubAllGlobals()
  })
})
