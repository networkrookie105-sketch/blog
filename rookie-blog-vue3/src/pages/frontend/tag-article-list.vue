<template>
    <div class="site-dark">
        <div class="aurora"><div class="grid"></div></div>
        <div class="content-layer">
            <Header />

            <main class="mx-auto max-w-screen-xl px-4 py-10">
                <div class="grid grid-cols-1 gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
                    <section class="min-w-0">
                        <div v-reveal class="mb-6 rounded-[24px] glass p-6 md:p-8">
                            <p class="text-sm font-semibold uppercase tracking-[0.22em] text-white/40">Tag</p>
                            <h1 class="mt-3 text-4xl font-extrabold text-white"># {{ tagName || '标签文章' }}</h1>
                        </div>

                        <div class="space-y-4">
                            <button
                                v-for="(article, i) in articles"
                                :key="article.id"
                                v-reveal="i * 70"
                                type="button"
                                class="group grid w-full gap-4 overflow-hidden rounded-[20px] glass glass-hover p-4 text-left sm:grid-cols-[180px_1fr]"
                                @click="goArticle(article.id)"
                            >
                                <div class="h-40 w-full overflow-hidden rounded-xl sm:h-28">
                                    <img v-if="article.cover" class="h-full w-full object-cover transition duration-700 group-hover:scale-105" :src="article.cover" alt="" />
                                    <div v-else class="h-full w-full" style="background: linear-gradient(135deg, #16162a, #7c3aed 160%)"></div>
                                </div>
                                <div class="min-w-0 py-1">
                                    <h2 class="line-clamp-2 text-xl font-bold leading-snug text-white transition group-hover:text-cyan-200">{{ article.title }}</h2>
                                    <p class="mt-3 line-clamp-2 text-sm leading-6 text-white/45">{{ article.summary }}</p>
                                    <div class="mt-4 text-xs text-white/35">{{ formatDate(article.createDate || article.createTime) }}</div>
                                </div>
                            </button>
                        </div>

                        <div v-if="articles.length === 0" class="rounded-[20px] border border-dashed border-white/15 bg-white/[0.03] p-12 text-center text-sm text-white/40">
                            这个标签下暂时还没有文章。
                        </div>

                        <nav v-if="pages > 1" class="mt-10 flex justify-center gap-2">
                            <button class="pager" :disabled="current <= 1" @click="getTagArticles(current - 1)">上一页</button>
                            <button
                                v-for="pageNo in pages"
                                :key="pageNo"
                                class="pager"
                                :class="{ 'pager-active': pageNo === current }"
                                @click="getTagArticles(pageNo)"
                            >
                                {{ pageNo }}
                            </button>
                            <button class="pager" :disabled="current >= pages" @click="getTagArticles(current + 1)">下一页</button>
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
        </div>
    </div>
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
