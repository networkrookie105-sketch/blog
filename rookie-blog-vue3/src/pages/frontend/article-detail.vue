<template>
    <Header />

    <main class="mx-auto max-w-screen-xl px-4 py-8">
        <div class="grid grid-cols-1 gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
            <article class="min-w-0">
                <section class="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-10">
                    <div v-if="loading" class="space-y-5">
                        <div class="h-5 w-28 rounded-full bg-slate-100"></div>
                        <div class="h-12 w-3/4 rounded-2xl bg-slate-100"></div>
                        <div class="h-4 w-1/2 rounded-full bg-slate-100"></div>
                        <div class="h-80 rounded-3xl bg-slate-100"></div>
                    </div>

                    <div v-else-if="errorText" class="flex min-h-[360px] flex-col items-center justify-center text-center">
                        <DocumentDelete class="h-14 w-14 text-slate-300" />
                        <h1 class="mt-5 text-2xl font-semibold text-slate-950">文章暂时无法打开</h1>
                        <p class="mt-3 max-w-md text-sm leading-6 text-slate-500">{{ errorText }}</p>
                        <button
                            type="button"
                            class="mt-6 rounded-full bg-slate-950 px-5 py-2 text-sm font-medium text-white transition hover:bg-slate-800"
                            @click="router.push('/')"
                        >
                            返回首页
                        </button>
                    </div>

                    <template v-else>
                        <div class="mb-7 flex flex-wrap items-center gap-3 text-sm text-slate-500">
                            <button
                                v-if="article.categoryName"
                                type="button"
                                class="rounded-full bg-slate-950 px-3 py-1 text-xs font-semibold text-white"
                                @click="goCategory"
                            >
                                {{ article.categoryName }}
                            </button>
                            <span>{{ formatDate(article.createTime) }}</span>
                            <span v-if="article.readNum !== undefined">阅读 {{ article.readNum || 0 }}</span>
                        </div>

                        <h1 class="max-w-4xl text-4xl font-semibold leading-tight tracking-normal text-slate-950 md:text-5xl">
                            {{ article.title }}
                        </h1>

                        <div v-if="article.tags && article.tags.length" class="mt-6 flex flex-wrap gap-2">
                            <button
                                v-for="tag in article.tags"
                                :key="tag.id"
                                type="button"
                                class="rounded-full border border-slate-200 px-3 py-1 text-xs font-medium text-slate-600 transition hover:border-slate-950 hover:text-slate-950"
                                @click="router.push({ path: '/tag/article/list', query: { id: tag.id, name: tag.name } })"
                            >
                                # {{ tag.name }}
                            </button>
                        </div>

                        <div class="mt-10 border-t border-slate-200 pt-10">
                            <div class="article-content" v-html="article.content"></div>
                        </div>
                    </template>
                </section>

                <section v-if="!loading && !errorText" class="mt-6 grid gap-4 md:grid-cols-2">
                    <button
                        type="button"
                        class="group rounded-2xl border border-slate-200 bg-white p-5 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-slate-300 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50"
                        :disabled="!article.preArticle"
                        @click="goSibling(article.preArticle)"
                    >
                        <span class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">上一篇</span>
                        <strong class="mt-2 block line-clamp-2 text-base font-semibold text-slate-900 group-hover:text-slate-700">
                            {{ article.preArticle?.articleTitle || '没有上一篇了' }}
                        </strong>
                    </button>
                    <button
                        type="button"
                        class="group rounded-2xl border border-slate-200 bg-white p-5 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-slate-300 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50"
                        :disabled="!article.nextArticle"
                        @click="goSibling(article.nextArticle)"
                    >
                        <span class="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">下一篇</span>
                        <strong class="mt-2 block line-clamp-2 text-base font-semibold text-slate-900 group-hover:text-slate-700">
                            {{ article.nextArticle?.articleTitle || '没有下一篇了' }}
                        </strong>
                    </button>
                </section>

                <!-- 评论区 -->
                <CommentSection v-if="!loading && !errorText" :article-id="route.params.id" />
            </article>

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
import { DocumentDelete } from '@element-plus/icons-vue'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue'
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue'
import TagListCard from '@/layouts/frontend/components/TagListCard.vue'
import CommentSection from '@/components/CommentSection.vue'
import { getArticleDetail } from '@/api/frontend/article'

const route = useRoute()
const router = useRouter()
const article = ref({})
const loading = ref(false)
const errorText = ref('')

const formatDate = (value) => {
    if (!value) return ''
    return String(value).replace('T', ' ').slice(0, 16)
}

const loadArticle = (id) => {
    if (!id) {
        errorText.value = '缺少文章 ID。'
        return
    }
    loading.value = true
    errorText.value = ''
    getArticleDetail(id)
        .then((res) => {
            if (res.success) {
                article.value = res.data || {}
                document.title = `${article.value.title || '文章详情'} - ROOKIE`
            } else {
                errorText.value = res.message || '后端没有返回有效文章。'
            }
        })
        .catch(() => {
            errorText.value = '文章详情接口请求失败，请确认后端服务和文章数据是否正常。'
        })
        .finally(() => {
            loading.value = false
        })
}

const goSibling = (target) => {
    if (!target?.articleId) return
    router.push(`/article/detail/${target.articleId}`)
}

const goCategory = () => {
    router.push({
        path: '/category/article/list',
        query: {
            id: article.value.categoryId,
            name: article.value.categoryName,
        },
    })
}

watch(
    () => route.params.id,
    (id) => loadArticle(id),
    { immediate: true }
)
</script>
