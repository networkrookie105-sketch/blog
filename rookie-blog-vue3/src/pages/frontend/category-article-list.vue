<template>
    <div class="site-dark">
        <div class="aurora"><div class="grid"></div></div>
        <div class="content-layer">
            <Header />

            <main class="mx-auto max-w-screen-xl px-4 py-10">
                <div class="grid grid-cols-1 gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
                    <section class="min-w-0">
                        <div v-reveal class="mb-6 rounded-[24px] glass p-6 md:p-8">
                            <p class="text-sm font-semibold uppercase tracking-[0.22em] text-white/40">Category</p>
                            <h1 class="mt-3 text-4xl font-extrabold text-white">{{ categoryName || '分类文章' }}</h1>
                        </div>

                        <div class="space-y-4">
                            <ArticleRow v-for="article in articles" :key="article.id" :article="article" @click="goArticle(article.id)" />
                        </div>

                        <EmptyState v-if="articles.length === 0" text="这个分类下暂时还没有文章。" />
                        <Pager v-if="pages > 1" :current="current" :pages="pages" @change="getCategoryArticles" />
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
import { defineComponent, h, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue'
import TagListCard from '@/layouts/frontend/components/TagListCard.vue'
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue'
import { getCategoryArticlePageList } from '@/api/frontend/category'

const ArticleRow = defineComponent({
    props: { article: { type: Object, required: true } },
    emits: ['click'],
    setup(props, { emit }) {
        return () =>
            h('button', {
                type: 'button',
                class: 'group grid w-full gap-4 overflow-hidden rounded-[20px] border border-white/10 bg-white/[0.04] p-4 text-left shadow-xl shadow-black/20 backdrop-blur-xl transition hover:-translate-y-1 hover:border-white/20 sm:grid-cols-[180px_1fr]',
                onClick: () => emit('click'),
            }, [
                h('div', { class: 'h-40 w-full overflow-hidden rounded-xl sm:h-28' }, [
                    props.article.cover
                        ? h('img', { class: 'h-full w-full object-cover transition duration-700 group-hover:scale-105', src: props.article.cover, alt: '' })
                        : h('div', { class: 'h-full w-full', style: 'background: linear-gradient(135deg, #16162a, #22d3ee 160%)' }),
                ]),
                h('div', { class: 'min-w-0 py-1' }, [
                    h('h2', { class: 'line-clamp-2 text-xl font-bold leading-snug text-white transition group-hover:text-cyan-200' }, props.article.title),
                    h('p', { class: 'mt-3 line-clamp-2 text-sm leading-6 text-white/45' }, props.article.summary || ''),
                    h('div', { class: 'mt-4 text-xs text-white/35' }, formatDate(props.article.createDate || props.article.createTime)),
                ]),
            ])
    },
})

const EmptyState = defineComponent({
    props: { text: { type: String, required: true } },
    setup(props) {
        return () => h('div', { class: 'rounded-[20px] border border-dashed border-white/15 bg-white/[0.03] p-12 text-center text-sm text-white/40' }, props.text)
    },
})

const Pager = defineComponent({
    props: { current: Number, pages: Number },
    emits: ['change'],
    setup(props, { emit }) {
        return () => h('nav', { class: 'mt-10 flex justify-center gap-2' }, [
            h('button', { class: 'pager', disabled: props.current <= 1, onClick: () => emit('change', props.current - 1) }, '上一页'),
            ...Array.from({ length: props.pages }, (_, index) => index + 1).map((pageNo) =>
                h('button', {
                    class: ['pager', pageNo === props.current ? 'pager-active' : ''],
                    onClick: () => emit('change', pageNo),
                }, pageNo)
            ),
            h('button', { class: 'pager', disabled: props.current >= props.pages, onClick: () => emit('change', props.current + 1) }, '下一页'),
        ])
    },
})

const route = useRoute()
const router = useRouter()
const categoryName = ref(route.query.name)
const categoryId = ref(route.query.id)
const articles = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const pages = ref(0)

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 10) : '')
const goArticle = (id) => router.push(`/article/detail/${id}`)

watch(
    () => route.query,
    (query) => {
        categoryName.value = query.name
        categoryId.value = query.id
        current.value = 1
        getCategoryArticles(current.value)
    }
)

function getCategoryArticles(currentNo) {
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return
    getCategoryArticlePageList({ current: currentNo, size: size.value, id: categoryId.value }).then((res) => {
        if (res.success) {
            articles.value = res.data || []
            current.value = res.current
            size.value = res.size
            total.value = res.total
            pages.value = res.pages
        }
    })
}

getCategoryArticles(current.value)
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
