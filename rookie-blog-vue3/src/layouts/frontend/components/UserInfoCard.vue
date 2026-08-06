<template>
    <section class="rounded-2xl border border-white/10 bg-white/[0.04] p-6 shadow-xl shadow-black/20 backdrop-blur-xl">
        <div class="flex flex-col items-center text-center">
            <div class="mb-4 rounded-full p-[2px]" style="background: linear-gradient(120deg, #7c3aed, #22d3ee)">
                <img
                    v-if="blogSettingsStore.blogSettings.avatar"
                    class="h-20 w-20 rounded-full object-cover"
                    :src="blogSettingsStore.blogSettings.avatar"
                    alt="author avatar"
                />
                <div v-else class="flex h-20 w-20 items-center justify-center rounded-full bg-[#0c0c15] text-xl font-semibold text-white">
                    R
                </div>
            </div>
            <h2 class="text-lg font-bold text-white">
                {{ blogSettingsStore.blogSettings.author || 'Rookie' }}
            </h2>
            <p class="mt-2 text-sm leading-6 text-white/50">
                {{ blogSettingsStore.blogSettings.introduction || '记录技术、生活与持续成长。' }}
            </p>
            <div class="mt-5 flex flex-wrap justify-center gap-2">
                <button
                    v-for="link in links"
                    :key="link.label"
                    v-show="link.url"
                    type="button"
                    class="rounded-full border border-white/10 px-3 py-1 text-xs font-medium text-white/60 transition hover:border-transparent hover:bg-violet-600/25 hover:text-white"
                    @click="jump(link.url)"
                >
                    {{ link.label }}
                </button>
            </div>
        </div>
    </section>
</template>

<script setup>
import { computed } from 'vue'
import { useBlogSettingsStore } from '@/stores/blogsettings'

const blogSettingsStore = useBlogSettingsStore()
const links = computed(() => [
    { label: 'GitHub', url: blogSettingsStore.blogSettings.githubHomepage },
    { label: 'Gitee', url: blogSettingsStore.blogSettings.giteeHomepage },
    { label: '知乎', url: blogSettingsStore.blogSettings.zhihuHomepage },
    { label: 'CSDN', url: blogSettingsStore.blogSettings.csdnHomepage },
])

const jump = (url) => {
    window.open(url, '_blank')
}
</script>
