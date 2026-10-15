<template>
  <header class="personal-header" @keydown.esc="open = false">
    <nav class="page-width header-inner" aria-label="主导航">
      <RouterLink :to="sitePath('/')" class="personal-brand"
        ><span class="brand-mark">{{
          (personalHomepagePublic ? profile.name : siteSettings.name)?.slice(0, 1) || '博'
        }}</span
        ><span
          >{{ siteSettings.name || (personalHomepagePublic ? profile.name : '') || '技术博客'
          }}<small>构建 · 记录 · 分享</small></span
        ></RouterLink
      >
      <div class="desktop-nav">
        <RouterLink
          v-for="item in items"
          :key="item.path"
          :to="sitePath(item.path)"
          :class="{ active: active(item.path) }"
          >{{ item.label }}</RouterLink
        >
      </div>
      <RouterLink v-if="personalHomepagePublic" class="header-contact" :to="sitePath('/contact')"
        >聊聊合作 <ArrowUpRight
      /></RouterLink>
      <button
        class="mobile-toggle"
        :aria-expanded="open"
        aria-controls="mobile-nav"
        :aria-label="open ? '关闭导航' : '打开导航'"
        @click="open = !open"
      >
        <Close v-if="open" /><Menu v-else />
      </button>
    </nav>
    <nav v-if="open" id="mobile-nav" class="mobile-nav page-width" aria-label="移动导航">
      <RouterLink
        v-for="item in items"
        :key="item.path"
        :to="sitePath(item.path)"
        @click="open = false"
        >{{ item.label }}</RouterLink
      >
    </nav>
  </header>
</template>
<script setup>
import { ref, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { TopRight as ArrowUpRight, Menu, Close } from '@element-plus/icons-vue'
import { profile, sitePath, siteSettings, personalHomepagePublic } from '@/content/profile'
const route = useRoute()
const open = ref(false)
const items = computed(() =>
  [
    { path: '/', label: '首页' },
    { path: '/projects', label: '项目' },
    { path: '/articles', label: '文章' },
    { path: '/about', label: '关于我' }
  ].filter((item) => personalHomepagePublic.value || !['/projects', '/about'].includes(item.path))
)
const active = (path) =>
  path === '/'
    ? route.path === sitePath('/')
    : path === '/articles'
      ? ['/articles', '/article', '/notes', '/search', '/category', '/tag', '/archive'].some((p) =>
          route.path.startsWith(sitePath(p))
        )
      : route.path.startsWith(sitePath(path))
watch(
  () => route.fullPath,
  () => {
    open.value = false
  }
)
</script>
