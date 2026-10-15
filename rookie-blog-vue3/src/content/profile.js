import { reactive, computed } from 'vue'
import axios from 'axios'
export const siteSettings = reactive({
  personalHomepagePublic: false,
  name: '',
  logo: '',
  favicon: '',
  introduction: '',
  footerText: '',
  copyright: '',
  filing: ''
})
export const personalHomepagePublic = computed(() => siteSettings.personalHomepagePublic === true)
const defaultFavicon =
  document.querySelector('link[rel~=icon]')?.getAttribute('href') || '/favicon.ico'
export const profile = reactive({
  name: '',
  englishName: '',
  role: '',
  location: '',
  email: '',
  description: '',
  intro: '',
  interests: '',
  skills: [],
  experiences: [],
  avatar: '',
  githubHomepage: '',
  giteeHomepage: '',
  zhihuHomepage: '',
  csdnHomepage: '',
  isExample: false
})
export const projects = reactive([])
export const siteState = reactive({ username: 'admin', loading: false, error: '', loaded: false })
let sequence = 0
export function sitePath(path = '/') {
  return path
}
export async function loadSite() {
  const request = ++sequence
  siteState.username = 'admin'
  siteState.loading = true
  siteState.error = ''
  siteState.loaded = false
  Object.assign(profile, {
    name: '',
    englishName: '',
    role: '',
    location: '',
    email: '',
    description: '',
    intro: '',
    interests: '',
    skills: [],
    experiences: [],
    avatar: '',
    githubHomepage: '',
    giteeHomepage: '',
    zhihuHomepage: '',
    csdnHomepage: '',
    isExample: false
  })
  Object.assign(siteSettings, {
    personalHomepagePublic: false,
    name: '',
    logo: '',
    favicon: '',
    introduction: '',
    footerText: '',
    copyright: '',
    filing: ''
  })
  projects.splice(0)
  try {
    const { data } = await axios.get('/api/site')
    if (request !== sequence) return
    if (!data.success) throw new Error(data.message || '主页暂时无法加载')
    Object.assign(siteSettings, data.data.settings || {})
    if (personalHomepagePublic.value) Object.assign(profile, data.data.profile || {})
    const favicon = document.querySelector('link[rel~=icon]')
    if (favicon) favicon.setAttribute('href', siteSettings.favicon || defaultFavicon)
    if (personalHomepagePublic.value)
      projects.splice(0, projects.length, ...(data.data.projects || []))
    siteState.username = data.data.username || 'admin'
    siteState.loaded = true
  } catch (error) {
    if (request === sequence) siteState.error = error.message || '主页暂时无法加载'
  } finally {
    if (request === sequence) siteState.loading = false
  }
}
export function downloadResume() {
  if (!personalHomepagePublic.value || !siteState.loaded) return
  const text = `# ${profile.name}｜${profile.role}\n\n${
    profile.isExample ? '> 虚构示例简历\n\n' : ''
  }${profile.location}\n${profile.email}\n\n## 个人介绍\n${
    profile.intro
  }\n\n## 技术方向\n${profile.skills.join(' / ')}\n\n## 工作经历\n${profile.experiences
    .map((e) => `### ${e.company} · ${e.role}\n${e.period}\n${e.description}`)
    .join('\n\n')}\n\n## 项目\n${projects
    .map((p) => `### ${p.name}\n${p.description}\n${p.contribution}\n${p.result}`)
    .join('\n\n')}\n`
  const url = URL.createObjectURL(new Blob([text], { type: 'text/markdown;charset=utf-8' }))
  const a = document.createElement('a')
  a.href = url
  a.download = `${profile.name || '个人'}-简历.md`
  a.click()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}
