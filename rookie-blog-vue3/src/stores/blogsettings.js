import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getBlogSettingsDetail } from '@/api/frontend/blogsettings'

export const useBlogSettingsStore = defineStore('blogsettings', () => {
  // 博客设置信息
  const blogSettings = ref({})

  // 获取博客设置信息
  function getBlogSettings() {
    // 调用后台获取博客设置信息接口
    console.log('获取博客设置信息')
    getBlogSettingsDetail()
      .then((res) => {
        if (res.success) {
          blogSettings.value = res.data
        }
      })
      .catch(() => {
        /* 接口不可用时保留既有设置，页面继续可用。 */
      })
  }

  return { blogSettings, getBlogSettings }
})
