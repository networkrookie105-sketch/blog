import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createMemoryHistory, createRouter } from 'vue-router'
import ArticleList from '../../pages/frontend/article-list.vue'
import { profile, siteState } from '../../content/profile'
import { getArticlePageList, searchArticles } from '../../api/frontend/article'
vi.mock('../../api/frontend/article', () => ({
  getArticlePageList: vi.fn(),
  searchArticles: vi.fn()
}))
vi.mock('element-plus/es', async () => {
  const { defineComponent, h } = await import('vue')
  return {
    ElInput: defineComponent({
      props: ['modelValue'],
      emits: ['update:modelValue'],
      setup(props, { emit, expose }) {
        expose({ focus: vi.fn() })
        return () =>
          h('input', {
            value: props.modelValue,
            onInput: (event: Event) =>
              emit('update:modelValue', (event.target as HTMLInputElement).value)
          })
      }
    })
  }
})
const published: any = {
  success: true,
  total: 1,
  pages: 1,
  data: [{ id: 9, title: '后台文章', summary: '文章摘要', category: { name: '工程实践' } }]
}
let wrapper: ReturnType<typeof mount> | undefined
async function render() {
  Object.assign(siteState, { username: 'admin', loaded: true, loading: false, error: '' })
  profile.name = '博主'
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/articles', component: ArticleList },
      { path: '/:pathMatch(.*)*', component: { template: '<div />' } }
    ]
  })
  await router.push('/articles')
  wrapper = mount(ArticleList, {
    global: { plugins: [router], stubs: { Header: true, Footer: true } }
  })
  await flushPromises()
  return { wrapper, router }
}
beforeEach(() => {
  vi.resetAllMocks()
  vi.mocked(getArticlePageList).mockResolvedValue(published)
  vi.mocked(searchArticles).mockResolvedValue({
    success: true,
    total: 0,
    pages: 0,
    data: []
  } as any)
})
afterEach(() => wrapper?.unmount())
describe('完整博客文章数据', () => {
  it('列表和搜索来自后台接口，清除搜索恢复完整列表', async () => {
    const { wrapper, router } = await render()
    expect(getArticlePageList).toHaveBeenCalledWith({ current: 1, size: 8 })
    expect(wrapper.find('.writing-row').attributes('href')).toBe('/article/detail/9')
    expect(wrapper.find('a[href="/category/list"]').exists()).toBe(true)
    await wrapper.find('input').setValue('关键词')
    await wrapper.find('form').trigger('submit')
    await flushPromises()
    expect(searchArticles).toHaveBeenCalledWith({ current: 1, size: 8, keyword: '关键词' })
    expect(wrapper.text()).toContain('没有找到匹配的文章')
    await wrapper
      .findAll('button')
      .find((button) => button.text() === '清除搜索')!
      .trigger('click')
    await flushPromises()
    expect(router.currentRoute.value.query).toEqual({})
    expect(wrapper.text()).toContain('后台文章')
  })
  it('读取失败保留重试，不显示旧内容', async () => {
    vi.mocked(getArticlePageList).mockRejectedValueOnce(new Error('连接失败'))
    const { wrapper } = await render()
    expect(wrapper.text()).toContain('连接失败')
    expect(wrapper.findAll('.writing-row')).toHaveLength(0)
    await wrapper
      .findAll('button')
      .find((button) => button.text() === '重新加载')!
      .trigger('click')
    await flushPromises()
    expect(wrapper.text()).toContain('后台文章')
  })
  it('晚返回的旧请求不会覆盖新搜索结果', async () => {
    let finish: (response: any) => void = () => {}
    vi.mocked(getArticlePageList).mockImplementationOnce(
      () =>
        new Promise<any>((resolve) => {
          finish = resolve
        })
    )
    const { wrapper, router } = await render()
    await router.replace('/articles?q=新搜索')
    await flushPromises()
    finish(published)
    await flushPromises()
    expect(wrapper.text()).toContain('没有找到匹配的文章')
    expect(wrapper.text()).not.toContain('后台文章')
  })
})
