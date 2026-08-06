<template>
    <Header />

    <main class="mx-auto max-w-screen-xl px-4 py-8">
        <section class="mb-8 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-8">
            <p class="text-sm font-semibold uppercase tracking-[0.22em] text-slate-400">Search</p>
            <h1 class="mt-3 text-3xl font-semibold text-slate-950">
                搜索 “{{ keyword }}”
            </h1>
            <p class="mt-2 text-sm text-slate-500">共找到 {{ total }} 篇相关文章</p>
        </section>

        <section class="min-w-0">
            <div v-if="loading" class="text-center text-sm text-slate-400 py-16">搜索中...</div>

            <div v-else-if="articles.length" class="space-y-4">
                <article
                    v-for="a in articles"
                    :key="a.id"
                    class="group overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
                >
                    <button type="button" class="flex w-full gap-5 p-5 text-left" @click="goArticle(a.id)">
                        <img v-if="a.cover" :src="a.cover" class="hidden h-28 w-40 shrink-0 rounded-xl object-cover sm:block" alt="" />
                        <div class="min-w-0">
                            <h2 class="line-clamp-1 text-xl font-semibold text-slate-950 group-hover:text-slate-700">
                                {{ a.title }}
                            </h2>
                            <p class="mt-2 line-clamp-2 text-sm leading-6 text-slate-500">{{ a.summary }}</p>
                            <span class="mt-3 block text-xs text-slate-400">
                                {{ formatDate(a.createTime) }}
                            </span>
                        </div>
                    </button>
                </article>

                <nav v-if="pages > 1" class="mt-10 flex justify-center gap-2">
                    <button class="pager-button" :disabled="current <= 1" @click="doSearch(current - 1)">上一页</button>
                    <button
                        v-for="pageNo in pages"
                        :key="pageNo"
                        class="pager-button"
                        :class="{ 'bg-slate-950 text-white': pageNo === current }"
                        @click="doSearch(pageNo)"
                    >
                        {{ pageNo }}
                    </button>
                    <button class="pager-button" :disabled="current >= pages" @click="doSearch(current + 1)">下一页</button>
                </nav>
            </div>

            <div v-else class="py-16 text-center text-sm text-slate-400">没有找到相关文章，换个关键词试试~</div>
        </section>
    </main>

    <Footer />
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import { searchArticles } from '@/api/frontend/article'

const route = useRoute()
const router = useRouter()
const keyword = ref('')
const articles = ref([])
const loading = ref(false)
const current = ref(1)
const size = ref(10)
const total = ref(0)
const pages = ref(0)

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 10) : '')

const goArticle = (id) => router.push(`/article/detail/${id}`)

const doSearch = (pageNo = 1) => {
    if (!keyword.value) return
    loading.value = true
    searchArticles({ keyword: keyword.value, current: pageNo, size: size.value })
        .then((res) => {
            if (res.success) {
                articles.value = res.data || []
                current.value = res.current
                size.value = res.size
                total.value = res.total
                pages.value = res.pages
            }
        })
        .finally(() => {
            loading.value = false
        })
}

watch(
    () => route.query.keyword,
    (kw) => {
        keyword.value = (kw || '').toString()
        current.value = 1
        if (keyword.value) doSearch(1)
        else {
            articles.value = []
            total.value = 0
            pages.value = 0
        }
    },
    { immediate: true }
)
</script>

<style scoped>
.pager-button {
    border: 1px solid rgb(226 232 240);
    border-radius: 9999px;
    padding: 0.4rem 0.9rem;
    font-size: 0.85rem;
    background: #fff;
    transition: all 0.2s;
}
.pager-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}
</style>
