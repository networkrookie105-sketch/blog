<template>
    <section v-if="categories.length > 0" class="rounded-2xl border border-white/10 bg-white/[0.04] p-5 shadow-xl shadow-black/20 backdrop-blur-xl">
        <div class="mb-4 flex items-center justify-between">
            <h2 class="text-sm font-semibold uppercase tracking-[0.18em] text-white/50">Categories</h2>
            <FolderOpened class="h-5 w-5 text-white/30" />
        </div>
        <div class="space-y-1.5">
            <button
                v-for="category in categories"
                :key="category.id"
                type="button"
                class="group flex w-full items-center justify-between rounded-xl px-3 py-2 text-left text-sm text-white/60 transition hover:bg-white/5 hover:text-white"
                @click="goCategoryArticleListPage(category.id, category.name)"
            >
                <span>{{ category.name }}</span>
                <ArrowRight class="h-4 w-4 -translate-x-1 opacity-0 transition group-hover:translate-x-0 group-hover:opacity-100" style="color: #22d3ee" />
            </button>
        </div>
    </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, FolderOpened } from '@element-plus/icons-vue'
import { getCategoryList } from '@/api/frontend/category'

const router = useRouter()
const categories = ref([])

const goCategoryArticleListPage = (id, name) => {
    router.push({ path: '/category/article/list', query: { id, name } })
}

getCategoryList().then((res) => {
    if (res.success) {
        categories.value = res.data || []
    }
})
</script>
