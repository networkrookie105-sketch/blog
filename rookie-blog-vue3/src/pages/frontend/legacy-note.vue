<template>
  <div class="public-shell">
    <Header />
    <main class="page-width inner-page">
      <p v-if="!error" role="status">正在打开文章…</p>
      <section v-else class="article-status" role="alert">
        <h1>文章暂时无法打开</h1>
        <p>{{ error }}</p>
        <button class="inline-link" @click="load">重新加载</button
        ><RouterLink to="/articles" class="inline-link">返回文章列表</RouterLink>
      </section>
    </main>
    <Footer />
  </div>
</template>
<script setup>
import { ref, watch, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/axios'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
const route = useRoute(),
  router = useRouter(),
  error = ref('')
let sequence = 0
async function load() {
  const request = ++sequence
  error.value = ''
  try {
    const res = await axios.get(`/site/legacy/${encodeURIComponent(route.params.slug)}`)
    if (request !== sequence) return
    if (!res.success) throw new Error(res.message)
    await router.replace(`/article/detail/${res.data.articleId}`)
  } catch (e) {
    if (request === sequence) error.value = e.message || '文章加载失败，请重试'
  }
}
watch(() => route.params.slug, load, { immediate: true })
onBeforeUnmount(() => {
  ++sequence
})
</script>
