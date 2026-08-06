<template>
    <div class="site-dark">
        <div class="aurora"><div class="grid"></div></div>
        <div class="content-layer">
            <Header />

            <main class="mx-auto max-w-screen-xl px-4 py-10">
                <div class="grid grid-cols-1 gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
                    <section class="min-w-0">
                        <div v-reveal class="mb-6 rounded-[24px] glass p-6 md:p-8">
                            <p class="text-sm font-semibold uppercase tracking-[0.22em] text-white/40">Tags</p>
                            <h1 class="mt-3 text-4xl font-extrabold text-white">全部<span class="gradient-text">标签</span></h1>
                        </div>

                        <div v-if="tags && tags.length > 0" v-reveal class="rounded-[20px] glass p-6">
                            <div class="flex flex-wrap gap-3">
                                <button
                                    v-for="tag in tags"
                                    :key="tag.id"
                                    type="button"
                                    class="tag-pill rounded-full px-5 py-2.5 text-base font-medium"
                                    @click="goTagArticleListPage(tag.id, tag.name)"
                                >
                                    # {{ tag.name }}
                                </button>
                            </div>
                        </div>
                    </section>

                    <aside class="space-y-5 lg:sticky lg:top-24 lg:self-start">
                        <UserInfoCard />
                        <CategoryListCard />
                    </aside>
                </div>
            </main>

            <Footer />
        </div>
    </div>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue'
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue'
import { getTagList } from '@/api/frontend/tag'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const tags = ref([])
getTagList().then((res) => {
    if (res.success) {
        tags.value = res.data
    }
})

const goTagArticleListPage = (id, name) => {
    router.push({ path: '/tag/article/list', query: { id, name } })
}
</script>

<style scoped>
.tag-pill {
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.7);
    background: rgba(255, 255, 255, 0.03);
    transition: color 0.25s, border-color 0.25s, background 0.25s, transform 0.25s;
}
.tag-pill:hover {
    color: #fff;
    border-color: transparent;
    background: linear-gradient(120deg, #7c3aed, #22d3ee);
    transform: translateY(-2px);
}
</style>
