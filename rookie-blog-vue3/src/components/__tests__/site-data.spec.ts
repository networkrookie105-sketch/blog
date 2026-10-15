import { describe, it, expect, vi } from 'vitest'
import axios from 'axios'
import {
  loadSite,
  profile,
  projects,
  siteState,
  sitePath,
  siteSettings
} from '../../content/profile'
vi.mock('axios', () => ({ default: { get: vi.fn() } }))
const response = (name: string) => ({
  data: {
    success: true,
    data: {
      username: 'admin',
      settings: { personalHomepagePublic: true },
      profile: { name, skills: [], experiences: [] },
      projects: [],
      notes: [{ slug: name, title: name }]
    }
  }
})
describe('个人博客资料加载', () => {
  it('较晚返回的旧请求不能覆盖较新的博客资料', async () => {
    let finishOld: (value: any) => void = () => {}
    vi.mocked(axios.get)
      .mockImplementationOnce(
        () =>
          new Promise((resolve) => {
            finishOld = resolve
          })
      )
      .mockResolvedValueOnce(response('new-owner'))
    const old = loadSite()
    await loadSite()
    finishOld(response('old-owner'))
    await old
    expect(profile.name).toBe('new-owner')
    expect(siteState.username).toBe('admin')
    expect(sitePath('/projects')).toBe('/projects')
  })
  it('资料读取失败时不显示旧资料或虚构回退内容', async () => {
    vi.mocked(axios.get).mockRejectedValueOnce(new Error('主页不存在'))
    await loadSite()
    expect(profile.name).toBe('')
    expect(projects.length).toBe(0)
    expect(siteState.loaded).toBe(false)
    expect(siteState.error).toBe('主页不存在')
  })
  it('站点名称、个人姓名及浏览器图标分别读取，不混用字段', async () => {
    const data = response('作者姓名')
    Object.assign(data.data.data, {
      settings: {
        personalHomepagePublic: true,
        name: '技术笔记',
        introduction: '网站简介',
        footerText: '页脚标语',
        favicon: '/custom.ico'
      }
    })
    const icon = document.createElement('link')
    icon.rel = 'icon'
    document.head.appendChild(icon)
    vi.mocked(axios.get).mockResolvedValueOnce(data)
    await loadSite()
    expect(profile.name).toBe('作者姓名')
    expect(siteSettings.name).toBe('技术笔记')
    expect(siteSettings.introduction).toBe('网站简介')
    expect(icon.getAttribute('href')).toBe('/custom.ico')
    icon.remove()
  })
  it('关闭个人主页后清空旧个人数据，迟到的公开响应也不能恢复它', async () => {
    vi.mocked(axios.get).mockResolvedValueOnce(response('公开作者'))
    await loadSite()
    expect(profile.name).toBe('公开作者')
    let finishOld: (value: any) => void = () => {}
    vi.mocked(axios.get).mockImplementationOnce(
      () =>
        new Promise((resolve) => {
          finishOld = resolve
        })
    )
    const old = loadSite()
    const closed = response('不应显示的姓名')
    closed.data.data.settings.personalHomepagePublic = false
    vi.mocked(axios.get).mockResolvedValueOnce(closed)
    await loadSite()
    finishOld(response('迟到的公开姓名'))
    await old
    expect(profile.name).toBe('')
    expect(projects).toHaveLength(0)
    expect(siteSettings.personalHomepagePublic).toBe(false)
    expect(siteState.loaded).toBe(true)
  })
})
