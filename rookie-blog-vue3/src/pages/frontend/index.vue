<template>
    <div class="site-dark">
        <!-- 极光氛围背景 -->
        <div class="aurora"><div class="grid"></div></div>

        <div class="content-layer">
            <Header />

            <main class="mx-auto max-w-screen-xl px-4 py-10">
                <!-- Hero -->
                <section v-reveal class="relative mb-10 overflow-hidden rounded-[28px] glass p-8 md:p-14">
                    <div class="flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
                        <div>
                            <span class="inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs font-medium tracking-[0.22em] text-white/60">
                                <span class="h-1.5 w-1.5 rounded-full" style="background: #22d3ee; box-shadow: 0 0 8px #22d3ee"></span>
                                LATEST NOTES
                            </span>
                            <h1 class="mt-5 max-w-3xl text-4xl font-extrabold leading-tight text-white md:text-6xl">
                                写给当下，<br class="hidden md:block" />也写给<span class="gradient-text">未来回看</span>的自己。
                            </h1>
                            <p class="mt-5 max-w-xl text-sm leading-7 text-white/50 md:text-base">
                                这里收集技术实践、问题复盘，以及生活里那些一闪而过的灵感碎片。
                            </p>
                        </div>
                        <div class="flex shrink-0 gap-8 lg:flex-col lg:items-end lg:text-right">
                            <div>
                                <div class="text-3xl font-bold text-white md:text-4xl">{{ total }}</div>
                                <div class="mt-1 text-xs tracking-widest text-white/40">ARTICLES</div>
                            </div>
                        </div>
                    </div>
                </section>

                <div class="grid grid-cols-1 gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
                    <section class="min-w-0">
                        <!-- Featured -->
                        <article
                            v-if="featuredArticle"
                            v-reveal
                            class="group mb-6 overflow-hidden rounded-[24px] glass glass-hover"
                        >
                            <button type="button" class="grid w-full text-left md:grid-cols-[44%_1fr]" @click="goArticle(featuredArticle.id)">
                                <div class="relative h-64 w-full overflow-hidden md:h-full">
                                    <img
                                        v-if="featuredArticle.cover"
                                        class="h-full w-full object-cover transition duration-700 group-hover:scale-105"
                                        :src="featuredArticle.cover"
                                        alt=""
                                    />
                                    <div v-else class="h-full w-full" style="background: linear-gradient(135deg, #1a1a2e, #7c3aed 120%)"></div>
                                    <div class="pointer-events-none absolute inset-0 bg-gradient-to-t from-[#07070c] via-transparent to-transparent md:bg-gradient-to-r"></div>
                                </div>
                                <div class="flex flex-col justify-between p-6 md:p-8">
                                    <div>
                                        <span class="rounded-full px-3 py-1 text-xs font-semibold text-white" style="background: linear-gradient(120deg, #7c3aed, #22d3ee)">
                                            精选
                                        </span>
                                        <h2 class="mt-5 text-2xl font-bold leading-tight text-white transition group-hover:text-cyan-200 md:text-4xl">
                                            {{ featuredArticle.title }}
                                        </h2>
                                        <p class="mt-4 line-clamp-3 text-sm leading-7 text-white/50">
                                            {{ featuredArticle.summary }}
                                        </p>
                                    </div>
                                    <ArticleMeta class="mt-8" :article="featuredArticle" />
                                </div>
                            </button>
                        </article>

                        <!-- 文章网格 -->
                        <div class="grid gap-5 md:grid-cols-2">
                            <article
                                v-for="(article, i) in restArticles"
                                :key="article.id"
                                v-reveal="i * 80"
                                class="group overflow-hidden rounded-[20px] glass glass-hover"
                            >
                                <button type="button" class="block w-full text-left" @click="goArticle(article.id)">
                                    <div class="relative h-44 w-full overflow-hidden">
                                        <img
                                            v-if="article.cover"
                                            class="h-full w-full object-cover transition duration-700 group-hover:scale-105"
                                            :src="article.cover"
                                            alt=""
                                        />
                                        <div v-else class="h-full w-full" style="background: linear-gradient(135deg, #16162a, #22d3ee 160%)"></div>
                                        <div class="pointer-events-none absolute inset-0 bg-gradient-to-t from-[#0c0c15]/80 to-transparent"></div>
                                    </div>
                                    <div class="p-5">
                                        <div class="mb-3 flex flex-wrap gap-2">
                                            <span
                                                v-for="tag in article.tags || []"
                                                :key="tag.id"
                                                class="chip rounded-full px-2.5 py-1 text-xs font-medium"
                                            >
                                                {{ tag.name }}
                                            </span>
                                        </div>
                                        <h2 class="line-clamp-2 text-lg font-bold leading-snug text-white transition group-hover:text-cyan-200">
                                            {{ article.title }}
                                        </h2>
                                        <p class="mt-3 line-clamp-2 text-sm leading-6 text-white/45">
                                            {{ article.summary }}
                                        </p>
                                        <ArticleMeta class="mt-5" :article="article" />
                                    </div>
                                </button>
                            </article>
                        </div>

                        <!-- 分页 -->
                        <nav v-if="pages > 1" class="mt-10 flex justify-center gap-2">
                            <button class="pager" :disabled="current <= 1" @click="getArticles(current - 1)">上一页</button>
                            <button
                                v-for="pageNo in pages"
                                :key="pageNo"
                                class="pager"
                                :class="{ 'pager-active': pageNo === current }"
                                @click="getArticles(pageNo)"
                            >
                                {{ pageNo }}
                            </button>
                            <button class="pager" :disabled="current >= pages" @click="getArticles(current + 1)">下一页</button>
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
import { computed, defineComponent, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue'
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue'
import TagListCard from '@/layouts/frontend/components/TagListCard.vue'
import { getArticlePageList } from '@/api/frontend/article'

const ArticleMeta = defineComponent({
    props: {
        article: {
            type: Object,
            required: true,
        },
    },
    setup(props, { attrs }) {
        return () =>
            h('div', { ...attrs, class: ['flex flex-wrap items-center gap-3 text-xs text-white/35', attrs.class] }, [
                h('span', props.article.createTime ? String(props.article.createTime).replace('T', ' ').slice(0, 10) : ''),
                props.article.category?.name ? h('span', `· ${props.article.category.name}`) : null,
            ])
    },
})

const router = useRouter()
const articles = ref([])
const current = ref(1)
const size = ref(5)
const total = ref(0)
const pages = ref(0)

const featuredArticle = computed(() => articles.value[0])
const restArticles = computed(() => articles.value.slice(1))

const goArticle = (id) => {
    router.push(`/article/detail/${id}`)
}

function getArticles(currentNo) {
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return
    getArticlePageList({ current: currentNo, size: size.value }).then((res) => {
        if (res.success) {
            articles.value = res.data || []
            current.value = res.current
            size.value = res.size
            total.value = res.total
            pages.value = res.pages
        }
    })
}

getArticles(current.value)
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
