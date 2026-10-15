<template>
  <section
    class="rounded-2xl border border-white/10 bg-white/[0.04] p-5 shadow-xl shadow-black/20 backdrop-blur-xl"
  >
    <div class="mb-4 flex items-center justify-between">
      <h2 class="text-sm font-semibold uppercase tracking-[0.18em] text-white/50">标签</h2>
      <PriceTag class="h-5 w-5 text-white/30" />
    </div>
    <p v-if="loading" role="status" class="text-sm text-white/50">正在加载标签…</p>
    <div v-else-if="error" role="status" class="text-sm text-white/50">
      <p>标签暂时无法加载，请检查博客后端连接。</p>
      <button type="button" class="inline-link" @click="load">重新加载</button>
    </div>
    <p v-else-if="!tags.length" class="text-sm text-white/50">暂无标签。</p>
    <div v-else class="flex flex-wrap gap-2">
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

const loading = ref(false)
const error = ref(false)
async function load() {
  if (loading.value) return
  loading.value = true
  error.value = false
  try {
    const res = await getTagList()
    if (!res.success) throw new Error('load')
    tags.value = res.data || []
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}
load()
</script>

<style scoped>
.tag-chip {
  border: 1px solid var(--personal-line);
  color: var(--personal-muted);
  background: var(--personal-paper);
  transition:
    color 0.25s,
    border-color 0.25s,
    background 0.25s;
}
.tag-chip:hover {
  color: #fff;
  border-color: transparent;
  background: var(--personal-blue);
}
</style>
