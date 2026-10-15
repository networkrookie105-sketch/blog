<template>
  <div class="public-shell">
    <Header />
    <SiteStatus />
    <main v-if="siteState.loaded" id="main-content" class="page-width inner-page">
      <p class="eyebrow">WRITING & THINKING</p>
      <h1 class="page-title">
        {{
          personalHomepagePublic && profile.name ? profile.name + '的文章与笔记' : '文章与技术笔记'
        }}
      </h1>
      <p class="page-intro">技术实践、问题复盘，以及值得再想一次的细节。</p>
      <form novalidate class="article-tools" @submit.prevent="search">
        <el-input
          ref="searchInput"
          v-model="keyword"
          type="search"
          clearable
          aria-label="搜索当前站点文章"
          placeholder="搜索文章标题或正文"
          @clear="clearSearch"
          @compositionstart="composing = true"
          @compositionend="composing = false"
        />
        <button
          v-if="keyword"
          class="inline-link"
          type="button"
          aria-label="清除搜索"
          @click="clearSearch"
        >
          清除搜索
        </button>
        <button class="primary-action" type="submit">搜索文章 <Search /></button>
      </form>
      <nav class="article-tools" aria-label="文章浏览方式">
        <RouterLink to="/category/list" class="inline-link">分类</RouterLink>
        <RouterLink to="/tag/list" class="inline-link">标签</RouterLink>
        <RouterLink to="/archive/list" class="inline-link">归档</RouterLink>
      </nav>
      <p v-if="loading" class="article-status" role="status">正在加载文章…</p>
      <section v-else-if="error" class="article-status" role="alert">
        <p>{{ error }}</p>
        <button class="inline-link" @click="loadArticles">重新加载</button>
      </section>
      <p v-else class="sample-caption" role="status">
        共 {{ total }} 篇{{ query ? '匹配的' : '公开' }}文章
      </p>
      <p v-if="!loading && !error && !articles.length && !query" class="article-status">
        还没有公开文章。
      </p>
      <div v-else-if="!loading && !error && !articles.length && query" class="article-status">
        <p>没有找到匹配的文章。</p>
        <button class="inline-link" type="button" @click="clearSearch">清除搜索</button>
      </div>
      <template v-else-if="!loading && !error && articles.length">
        <div class="note-list">
          <RouterLink
            v-for="note in articles"
            :key="note.id"
            :to="`/article/detail/${note.id}`"
            class="writing-row"
          >
            <span class="writing-date"
              >{{ note.createTime?.slice(0, 10)
              }}<small>{{ note.category?.name || '技术笔记' }}</small></span
            >
            <div>
              <h3>{{ note.title }}</h3>
              <p>{{ note.summary }}</p>
            </div>
            <TopRight />
          </RouterLink>
        </div>
        <nav v-if="pages > 1" class="article-pagination" aria-label="文章分页">
          <button :disabled="loading || current <= 1" @click="changePage(current - 1)">
            上一页
          </button>
          <span>{{ current }} / {{ pages }}</span>
          <button :disabled="loading || current >= pages" @click="changePage(current + 1)">
            下一页
          </button>
        </nav>
      </template>
    </main>
    <Footer />
  </div>
</template>
<script setup>
import { ref, computed, watch, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, TopRight } from '@element-plus/icons-vue'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import SiteStatus from '@/components/SiteStatus.vue'
import { profile, personalHomepagePublic, siteState } from '@/content/profile'

import { getArticlePageList, searchArticles } from '@/api/frontend/article'

const articles = ref([]),
  total = ref(0),
  pages = ref(0),
  loading = ref(false),
  error = ref('')
let requestSequence = 0
const router = useRouter()
const route = useRoute()
const keyword = ref('')
const composing = ref(false)
const searchInput = ref(null)
const query = computed(() => (typeof route.query.q === 'string' ? route.query.q.trim() : ''))
watch(
  query,
  (value) => {
    keyword.value = value
  },
  { immediate: true }
)
const current = computed(() => {
  const page = Number(route.query.page)
  return Number.isInteger(page) && page > 0 ? page : 1
})
async function loadArticles() {
  const request = ++requestSequence
  loading.value = true
  error.value = ''
  articles.value = []
  try {
    const params = { current: current.value, size: 8 }
    const res = query.value
      ? await searchArticles({ ...params, keyword: query.value })
      : await getArticlePageList(params)
    if (request !== requestSequence) return
    if (!res.success) throw new Error(res.message || '文章加载失败')
    articles.value = res.data || []
    total.value = res.total || 0
    pages.value = res.pages || 0
    if (pages.value && current.value > pages.value) changePage(pages.value)
  } catch (e) {
    if (request === requestSequence) error.value = e.message || '文章加载失败，请重试'
  } finally {
    if (request === requestSequence) loading.value = false
  }
}
watch(() => [query.value, current.value], loadArticles, { immediate: true })
onBeforeUnmount(() => {
  ++requestSequence
})
function search() {
  if (composing.value) return
  router.replace({
    path: route.path,
    query: keyword.value.trim() ? { q: keyword.value.trim() } : {}
  })
}
function clearSearch() {
  keyword.value = ''
  router.replace({ path: route.path, query: {} })
  searchInput.value?.focus()
}
function changePage(page) {
  router.replace({ path: route.path, query: { ...(query.value ? { q: query.value } : {}), page } })
}
</script>
<style scoped>
.article-tools :deep(.el-input) {
  flex: 1;
  min-width: 180px;
}
.article-tools :deep(.el-input__wrapper) {
  background: var(--personal-paper);
  box-shadow: 0 0 0 1px var(--personal-line) inset;
}
.article-tools :deep(.el-input__inner) {
  color: var(--personal-ink);
  height: 44px;
  padding: 0;
  border: none;
  border-radius: 0;
  background: transparent;
}
.article-tools :deep(.el-input__inner::placeholder) {
  color: var(--personal-muted);
}
.article-tools :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--personal-blue) inset;
}
</style>
