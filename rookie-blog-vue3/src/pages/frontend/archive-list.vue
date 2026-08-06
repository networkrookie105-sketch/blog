<template>
    <div class="site-dark">
        <div class="aurora"><div class="grid"></div></div>
        <div class="content-layer">
            <Header />

            <main class="mx-auto max-w-screen-xl px-4 py-10">
                <div class="grid grid-cols-1 gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
                    <section class="min-w-0">
                        <div v-reveal class="mb-6 rounded-[24px] glass p-6 md:p-8">
                            <p class="text-sm font-semibold uppercase tracking-[0.22em] text-white/40">Archive</p>
                            <h1 class="mt-3 text-4xl font-extrabold text-white">时间线</h1>
                        </div>

                        <div class="space-y-6">
                            <section v-for="archive in archives" :key="archive.month" v-reveal class="rounded-[20px] glass p-5">
                                <time class="inline-flex items-center gap-2 text-lg font-bold text-white">
                                    <span class="h-2 w-2 rounded-full" style="background: #22d3ee; box-shadow: 0 0 8px #22d3ee"></span>
                                    {{ archive.month }}
                                </time>
                                <div class="mt-4 space-y-2">
                                    <button
                                        v-for="article in archive.articles"
                                        :key="article.id"
                                        type="button"
                                        class="group grid w-full gap-4 rounded-2xl p-3 text-left transition hover:bg-white/5 sm:grid-cols-[150px_1fr]"
                                        @click="goArticle(article.id)"
                                    >
                                        <div class="h-36 w-full overflow-hidden rounded-xl sm:h-24">
                                            <img v-if="article.cover" class="h-full w-full object-cover transition duration-700 group-hover:scale-105" :src="article.cover" alt="" />
                                            <div v-else class="h-full w-full" style="background: linear-gradient(135deg, #16162a, #7c3aed 160%)"></div>
                                        </div>
                                        <div class="min-w-0">
                                            <h2 class="line-clamp-2 text-lg font-semibold leading-snug text-white transition group-hover:text-cyan-200">{{ article.title }}</h2>
                                            <div class="mt-3 text-xs text-white/35">{{ formatDate(article.createDate || article.createTime) }}</div>
                                        </div>
                                    </button>
                                </div>
                            </section>
                        </div>

                        <div v-if="archives.length === 0" class="rounded-[20px] border border-dashed border-white/15 bg-white/[0.03] p-12 text-center text-sm text-white/40">
                            暂时还没有归档文章。
                        </div>

                        <nav v-if="pages > 1" class="mt-10 flex justify-center gap-2">
                            <button class="pager" :disabled="current <= 1" @click="getArchives(current - 1)">上一页</button>
                            <button
                                v-for="pageNo in pages"
                                :key="pageNo"
                                class="pager"
                                :class="{ 'pager-active': pageNo === current }"
                                @click="getArchives(pageNo)"
                            >
                                {{ pageNo }}
                            </button>
                            <button class="pager" :disabled="current >= pages" @click="getArchives(current + 1)">下一页</button>
                        </nav>
                    </section>

                    <aside class="space-y-5 lg:sticky lg:top-24 lg:self-start">
                        <UserInfoCard />
                        <TagListCard />
                    </aside>
                </div>
            </main>

            <Footer />
        </div>
    </div>
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
