<template>
    <section v-if="tags.length > 0" class="rounded-2xl border border-white/10 bg-white/[0.04] p-5 shadow-xl shadow-black/20 backdrop-blur-xl">
        <div class="mb-4 flex items-center justify-between">
            <h2 class="text-sm font-semibold uppercase tracking-[0.18em] text-white/50">Tags</h2>
            <PriceTag class="h-5 w-5 text-white/30" />
        </div>
        <div class="flex flex-wrap gap-2">
            <button
                v-for="tag in tags"
                :key="tag.id"
                type="button"
                class="tag-chip rounded-full px-3 py-1 text-xs font-medium"
                @click="goTagArticleListPage(tag.id, tag.name)"
            >
                {{ tag.name }}
            </button>
        </div>
    </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { PriceTag } from '@element-plus/icons-vue'
import { getTagList } from '@/api/frontend/tag'

const router = useRouter()
const tags = ref([])

const goTagArticleListPage = (id, name) => {
    router.push({ path: '/tag/article/list', query: { id, name } })
}

getTagList().then((res) => {
    if (res.success) {
        tags.value = res.data || []
    }
})
</script>

<style scoped>
.tag-chip {
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.6);
    background: rgba(255, 255, 255, 0.03);
    transition: color 0.25s, border-color 0.25s, background 0.25s;
}
.tag-chip:hover {
    color: #fff;
    border-color: transparent;
    background: linear-gradient(120deg, #7c3aed, #22d3ee);
}
</style>
