<template>
    <Header />

    <main class="mx-auto max-w-screen-xl px-4 py-8">
        <div class="grid grid-cols-1 gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
            <section class="min-w-0">
                <div class="mb-6 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-8">
                    <p class="text-sm font-semibold uppercase tracking-[0.22em] text-slate-400">Category</p>
                    <h1 class="mt-3 text-4xl font-semibold text-slate-950">{{ categoryName || '分类文章' }}</h1>
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
                class: 'group grid w-full gap-4 rounded-2xl border border-slate-200 bg-white p-4 text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-md sm:grid-cols-[180px_1fr]',
                onClick: () => emit('click'),
            }, [
                h('img', { class: 'h-40 w-full rounded-xl object-cover sm:h-28', src: props.article.cover, alt: '' }),
                h('div', { class: 'min-w-0 py-1' }, [
                    h('h2', { class: 'line-clamp-2 text-xl font-semibold leading-snug text-slate-950' }, props.article.title),
                    h('p', { class: 'mt-3 line-clamp-2 text-sm leading-6 text-slate-500' }, props.article.summary || ''),
                    h('div', { class: 'mt-4 text-xs text-slate-400' }, formatDate(props.article.createDate || props.article.createTime)),
                ]),
            ])
    },
})

const EmptyState = defineComponent({
    props: { text: { type: String, required: true } },
    setup(props) {
        return () => h('div', { class: 'rounded-3xl border border-dashed border-slate-200 bg-white p-12 text-center text-sm text-slate-500' }, props.text)
    },
})

const Pager = defineComponent({
    props: { current: Number, pages: Number },
    emits: ['change'],
    setup(props, { emit }) {
        return () => h('nav', { class: 'mt-10 flex justify-center gap-2' }, [
            h('button', { class: 'pager-button', disabled: props.current <= 1, onClick: () => emit('change', props.current - 1) }, '上一页'),
            ...Array.from({ length: props.pages }, (_, index) => index + 1).map((pageNo) =>
                h('button', {
                    class: ['pager-button', pageNo === props.current ? 'bg-slate-950 text-white' : ''],
                    onClick: () => emit('change', pageNo),
                }, pageNo)
            ),
            h('button', { class: 'pager-button', disabled: props.current >= props.pages, onClick: () => emit('change', props.current + 1) }, '下一页'),
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
