<template>
    <header class="sticky top-0 z-50 border-b border-white/10 bg-[#07070c]/70 backdrop-blur-xl">
        <nav class="mx-auto flex max-w-screen-xl items-center justify-between px-4 py-3">
            <button class="group flex items-center gap-3" type="button" @click="router.push('/')">
                <img
                    v-if="blogSettingsStore.blogSettings.logo"
                    :src="blogSettingsStore.blogSettings.logo"
                    class="h-10 w-10 rounded-full object-cover ring-1 ring-white/20"
                    alt="blog logo"
                />
                <span
                    v-else
                    class="flex h-10 w-10 items-center justify-center rounded-xl text-sm font-bold text-white shadow-lg shadow-violet-600/40"
                    style="background: linear-gradient(120deg, #7c3aed, #22d3ee)"
                >
                    R
                </span>
                <span class="brand text-lg font-bold tracking-wide">
                    {{ blogSettingsStore.blogSettings.name || 'ROOKIE BLOG' }}
                </span>
            </button>

            <div class="hidden items-center gap-7 md:flex">
                <button
                    v-for="item in navItems"
                    :key="item.path"
                    type="button"
                    class="h-nav text-sm font-medium"
                    :class="{ active: isActive(item.path) }"
                    @click="router.push(item.path)"
                >
                    {{ item.label }}
                </button>
            </div>

            <div class="hidden items-center gap-3 md:flex">
                <div class="relative">
                    <input
                        v-model="searchKeyword"
                        type="text"
                        placeholder="搜索文章..."
                        class="w-44 rounded-full border border-white/10 bg-white/5 py-2 pl-9 pr-3 text-sm text-white placeholder-white/40 outline-none transition focus:w-56 focus:border-cyan-400/60 focus:bg-white/10"
                        @keyup.enter="doSearch"
                    />
                    <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-white/40" />
                </div>
                <button
                    v-if="!isLogined"
                    type="button"
                    class="rounded-full px-4 py-2 text-sm font-medium text-white transition hover:brightness-110"
                    style="background: linear-gradient(120deg, #7c3aed, #6366f1 45%, #22d3ee); box-shadow: 0 8px 24px -8px rgba(124,58,237,0.7)"
                    @click="router.push('/login')"
                >
                    登录
                </button>
                <button
                    v-else
                    type="button"
                    class="rounded-full border border-white/15 px-4 py-2 text-sm font-medium text-white/80 transition hover:border-transparent hover:bg-violet-600/20 hover:text-white"
                    @click="router.push('/admin/index')"
                >
                    后台
                </button>
            </div>

            <button
                type="button"
                class="inline-flex h-10 w-10 items-center justify-center rounded-full border border-white/15 text-white/80 md:hidden"
                @click="mobileOpen = !mobileOpen"
            >
                <Menu v-if="!mobileOpen" class="h-5 w-5" />
                <Close v-else class="h-5 w-5" />
            </button>
        </nav>

        <div v-if="mobileOpen" class="border-t border-white/10 bg-[#07070c]/95 px-4 py-3 backdrop-blur-xl md:hidden">
            <div class="mx-auto flex max-w-screen-xl flex-col gap-2">
                <button
                    v-for="item in navItems"
                    :key="item.path"
                    type="button"
                    class="rounded-xl px-4 py-3 text-left text-sm font-medium transition"
                    :class="isActive(item.path) ? 'bg-violet-600/25 text-white' : 'text-white/70 hover:bg-white/5'"
                    @click="go(item.path)"
                >
                    {{ item.label }}
                </button>
                <button
                    type="button"
                    class="rounded-xl border border-white/15 px-4 py-3 text-left text-sm font-medium text-white/80"
                    @click="go(isLogined ? '/admin/index' : '/login')"
                >
                    {{ isLogined ? '进入后台' : '登录' }}
                </button>
            </div>
        </div>
    </header>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Menu, Close, Search } from '@element-plus/icons-vue'
import { useBlogSettingsStore } from '@/stores/blogsettings'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const mobileOpen = ref(false)
const searchKeyword = ref('')
const doSearch = () => {
    const kw = searchKeyword.value.trim()
    if (!kw) return
    router.push({ path: '/search', query: { keyword: kw } })
}
const blogSettingsStore = useBlogSettingsStore()
const userStore = useUserStore()

const navItems = [
    { path: '/', label: '首页' },
    { path: '/category/list', label: '分类' },
    { path: '/tag/list', label: '标签' },
    { path: '/archive/list', label: '归档' },
]

const isLogined = computed(() => Object.keys(userStore.userInfo || {}).length > 0)
const isActive = (path) => route.path === path || (path !== '/' && route.path.startsWith(path))
const go = (path) => {
    mobileOpen.value = false
    router.push(path)
}
</script>

<style scoped>
.brand {
    background: linear-gradient(120deg, #ffffff, #c4b5fd 50%, #67e8f9);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
}
.h-nav {
    position: relative;
    color: rgba(255, 255, 255, 0.7);
    transition: color 0.25s;
    background: transparent;
}
.h-nav::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: -6px;
    height: 2px;
    width: 100%;
    border-radius: 2px;
    background: linear-gradient(120deg, #7c3aed, #22d3ee);
    transform: scaleX(0);
    transform-origin: left;
    transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}
.h-nav:hover,
.h-nav.active {
    color: #fff;
}
.h-nav:hover::after,
.h-nav.active::after {
    transform: scaleX(1);
}
</style>
