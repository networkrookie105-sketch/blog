<template>
    <div class="site-dark">
        <div class="aurora"><div class="grid"></div></div>
        <div class="content-layer">
            <Header />

            <main class="mx-auto max-w-screen-xl px-4 py-10">
                <section v-reveal class="mb-8 rounded-[24px] glass p-6 md:p-8">
                    <p class="text-sm font-semibold uppercase tracking-[0.22em] text-white/40">Search</p>
                    <h1 class="mt-3 text-3xl font-extrabold text-white">
                        搜索 <span class="gradient-text">“{{ keyword }}”</span>
                    </h1>
                    <p class="mt-2 text-sm text-white/50">共找到 {{ total }} 篇相关文章</p>
                </section>

                <section class="min-w-0">
                    <div v-if="loading" class="py-16 text-center text-sm text-white/40">搜索中...</div>

                    <div v-else-if="articles.length" class="space-y-4">
                        <article
                            v-for="(a, i) in articles"
                            :key="a.id"
                            v-reveal="i * 70"
                            class="group overflow-hidden rounded-[20px] glass glass-hover"
                        >
                            <button type="button" class="flex w-full gap-5 p-5 text-left" @click="goArticle(a.id)">
                                <div class="hidden h-28 w-40 shrink-0 overflow-hidden rounded-xl sm:block">
                                    <img v-if="a.cover" :src="a.cover" class="h-full w-full object-cover transition duration-700 group-hover:scale-105" alt="" />
                                    <div v-else class="h-full w-full" style="background: linear-gradient(135deg, #16162a, #22d3ee 160%)"></div>
                                </div>
                                <div class="min-w-0">
                                    <h2 class="line-clamp-1 text-xl font-bold text-white transition group-hover:text-cyan-200">
                                        {{ a.title }}
                                    </h2>
                                    <p class="mt-2 line-clamp-2 text-sm leading-6 text-white/45">{{ a.summary }}</p>
                                    <span class="mt-3 block text-xs text-white/35">
                                        {{ formatDate(a.createTime) }}
                                    </span>
                                </div>
                            </button>
                        </article>

                        <nav v-if="pages > 1" class="mt-10 flex justify-center gap-2">
                            <button class="pager" :disabled="current <= 1" @click="doSearch(current - 1)">上一页</button>
                            <button
                                v-for="pageNo in pages"
                                :key="pageNo"
                                class="pager"
                                :class="{ 'pager-active': pageNo === current }"
                                @click="doSearch(pageNo)"
                            >
                                {{ pageNo }}
                            </button>
                            <button class="pager" :disabled="current >= pages" @click="doSearch(current + 1)">下一页</button>
                        </nav>
                    </div>

                    <div v-else class="py-16 text-center text-sm text-white/40">没有找到相关文章，换个关键词试试~</div>
                </section>
            </main>

            <Footer />
        </div>
    </div>
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
.pager {
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 9999px;
    padding: 0.45rem 0.95rem;
    font-size: 0.85rem;
    color: rgba(255, 255, 255, 0.7);
    background: rgba(255, 255, 255, 0.03);
    transition: all 0.25s;
}
.pager:hover:not(:disabled) {
    color: #fff;
    border-color: transparent;
    background: rgba(124, 58, 237, 0.25);
}
.pager:disabled {
    opacity: 0.4;
    cursor: not-allowed;
}
.pager-active {
    color: #fff;
    border-color: transparent;
    background: linear-gradient(120deg, #7c3aed, #22d3ee);
}
</style>
