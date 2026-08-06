<template>
    <Header />

    <main class="mx-auto max-w-screen-xl px-4 py-8">
        <div class="grid grid-cols-1 gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
            <section class="min-w-0">
                <div class="mb-6 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-8">
                    <p class="text-sm font-semibold uppercase tracking-[0.22em] text-slate-400">Tag</p>
                    <h1 class="mt-3 text-4xl font-semibold text-slate-950"># {{ tagName || '标签文章' }}</h1>
                </div>

                <div class="space-y-4">
                    <button
                        v-for="article in articles"
                        :key="article.id"
                        type="button"
                        class="group grid w-full gap-4 rounded-2xl border border-slate-200 bg-white p-4 text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-md sm:grid-cols-[180px_1fr]"
                        @click="goArticle(article.id)"
                    >
                        <img class="h-40 w-full rounded-xl object-cover sm:h-28" :src="article.cover" alt="" />
                        <div class="min-w-0 py-1">
                            <h2 class="line-clamp-2 text-xl font-semibold leading-snug text-slate-950">{{ article.title }}</h2>
                            <p class="mt-3 line-clamp-2 text-sm leading-6 text-slate-500">{{ article.summary }}</p>
                            <div class="mt-4 text-xs text-slate-400">{{ formatDate(article.createDate || article.createTime) }}</div>
                        </div>
                    </button>
                </div>

                <div v-if="articles.length === 0" class="rounded-3xl border border-dashed border-slate-200 bg-white p-12 text-center text-sm text-slate-500">
                    这个标签下暂时还没有文章。
                </div>

                <nav v-if="pages > 1" class="mt-10 flex justify-center gap-2">
                    <button class="pager-button" :disabled="current <= 1" @click="getTagArticles(current - 1)">上一页</button>
                    <button
                        v-for="pageNo in pages"
                        :key="pageNo"
                        class="pager-button"
                        :class="{ 'bg-slate-950 text-white': pageNo === current }"
                        @click="getTagArticles(pageNo)"
                    >
                        {{ pageNo }}
                    </button>
                    <button class="pager-button" :disabled="current >= pages" @click="getTagArticles(current + 1)">下一页</button>
                </nav>
            </section>

            <aside class="space-y-5 lg:sticky lg:top-24 lg:self-start">
                <UserInfoCard />
                <CategoryListCard />
                <TagListCard />
            </aside>
        </div>
    </main>

    <Footer />
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue'
import TagListCard from '@/layouts/frontend/components/TagListCard.vue'
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue'
import { getTagArticlePageList } from '@/api/frontend/tag'

const route = useRoute()
const router = useRouter()
const articles = ref([])
const tagName = ref(route.query.name)
const tagId = ref(route.query.id)
const current = ref(1)
const size = ref(10)
const total = ref(0)
const pages = ref(0)

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 10) : '')
const goArticle = (id) => router.push(`/article/detail/${id}`)

watch(
    () => route.query,
    (query) => {
        tagName.value = query.name
        tagId.value = query.id
        current.value = 1
        getTagArticles(current.value)
    }
)

function getTagArticles(currentNo) {
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return
    getTagArticlePageList({ current: currentNo, size: size.value, id: tagId.value }).then((res) => {
        if (res.success) {
            articles.value = res.data || []
            current.value = res.current
            size.value = res.size
            total.value = res.total
            pages.value = res.pages
        }
    })
}

getTagArticles(current.value)
</script>
