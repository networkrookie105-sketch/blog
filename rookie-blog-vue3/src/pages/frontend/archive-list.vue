<template>
    <Header />

    <main class="mx-auto max-w-screen-xl px-4 py-8">
        <div class="grid grid-cols-1 gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
            <section class="min-w-0">
                <div class="mb-6 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-8">
                    <p class="text-sm font-semibold uppercase tracking-[0.22em] text-slate-400">Archive</p>
                    <h1 class="mt-3 text-4xl font-semibold text-slate-950">时间线</h1>
                </div>

                <div class="space-y-6">
                    <section v-for="archive in archives" :key="archive.month" class="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
                        <time class="text-lg font-semibold text-slate-950">{{ archive.month }}</time>
                        <div class="mt-4 space-y-3">
                            <button
                                v-for="article in archive.articles"
                                :key="article.id"
                                type="button"
                                class="group grid w-full gap-4 rounded-2xl p-3 text-left transition hover:bg-slate-50 sm:grid-cols-[150px_1fr]"
                                @click="goArticle(article.id)"
                            >
                                <img class="h-36 w-full rounded-xl object-cover sm:h-24" :src="article.cover" alt="" />
                                <div class="min-w-0">
                                    <h2 class="line-clamp-2 text-lg font-semibold leading-snug text-slate-950">{{ article.title }}</h2>
                                    <div class="mt-3 text-xs text-slate-400">{{ formatDate(article.createDate || article.createTime) }}</div>
                                </div>
                            </button>
                        </div>
                    </section>
                </div>

                <div v-if="archives.length === 0" class="rounded-3xl border border-dashed border-slate-200 bg-white p-12 text-center text-sm text-slate-500">
                    暂时还没有归档文章。
                </div>

                <nav v-if="pages > 1" class="mt-10 flex justify-center gap-2">
                    <button class="pager-button" :disabled="current <= 1" @click="getArchives(current - 1)">上一页</button>
                    <button
                        v-for="pageNo in pages"
                        :key="pageNo"
                        class="pager-button"
                        :class="{ 'bg-slate-950 text-white': pageNo === current }"
                        @click="getArchives(pageNo)"
                    >
                        {{ pageNo }}
                    </button>
                    <button class="pager-button" :disabled="current >= pages" @click="getArchives(current + 1)">下一页</button>
                </nav>
            </section>

            <aside class="space-y-5 lg:sticky lg:top-24 lg:self-start">
                <UserInfoCard />
                <TagListCard />
            </aside>
        </div>
    </main>

    <Footer />
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue'
import TagListCard from '@/layouts/frontend/components/TagListCard.vue'
import { getArchivePageList } from '@/api/frontend/archive'

const router = useRouter()
const archives = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const pages = ref(0)

const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 10) : '')
const goArticle = (id) => router.push(`/article/detail/${id}`)

function getArchives(currentNo) {
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return
    getArchivePageList({ current: currentNo, size: size.value }).then((res) => {
        if (res.success) {
            archives.value = res.data || []
            current.value = res.current
            size.value = res.size
            total.value = res.total
            pages.value = res.pages
        }
    })
}

getArchives(current.value)
</script>
