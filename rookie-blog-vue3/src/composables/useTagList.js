import { ref, watch } from 'vue'
import { useMenuStore } from '@/stores/menu'
import { useRoute, useRouter } from 'vue-router'
import { setTabList, getTabList } from '@/composables/cookie'
export function useTabList() {
  const menuStore = useMenuStore(),
    route = useRoute(),
    router = useRouter(),
    activeTab = ref(route.path)
  const saved = getTabList() || [{ title: '仪表盘', path: '/admin/index' }]
  const normalized = saved
    .map((tab) => {
      const path = tab.path === '/admin/site' ? '/admin/profile' : tab.path
      return { path, title: router.resolve(path).meta.title }
    })
    .filter((tab) => tab.title)
  const tabList = ref(
    normalized.filter(
      (tab, index) => normalized.findIndex((item) => item.path === tab.path) === index
    )
  )
  function persist() {
    setTabList(tabList.value)
  }
  watch(
    () => route.path,
    (path) => {
      activeTab.value = path
      const existing = tabList.value.find((tab) => tab.path === path)
      if (existing) existing.title = route.meta.title
      else tabList.value.push({ path, title: route.meta.title })
      persist()
    },
    { immediate: true }
  )
  async function tabChange(path) {
    await router.push(path)
    activeTab.value = route.path
  }
  async function removeTab(path) {
    if (path === activeTab.value) {
      const index = tabList.value.findIndex((tab) => tab.path === path)
      const next = tabList.value[index + 1] || tabList.value[index - 1]
      if (!next) return
      await router.push(next.path)
      if (route.path !== next.path) {
        activeTab.value = route.path
        return
      }
    }
    tabList.value = tabList.value.filter((tab) => tab.path !== path)
    activeTab.value = route.path
    persist()
  }
  async function handleCloseTab(command) {
    if (command === 'closeAll') {
      await router.push('/admin/index')
      if (route.path !== '/admin/index') return
      tabList.value = tabList.value.filter((tab) => tab.path === '/admin/index')
    } else if (command === 'closeOthers')
      tabList.value = tabList.value.filter(
        (tab) => tab.path === '/admin/index' || tab.path === route.path
      )
    activeTab.value = route.path
    persist()
  }
  return { menuStore, activeTab, tabList, tabChange, removeTab, handleCloseTab }
}
