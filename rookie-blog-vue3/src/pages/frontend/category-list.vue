<template>
    <div class="site-dark">
        <div class="aurora"><div class="grid"></div></div>
        <div class="content-layer">
            <Header />

            <main class="mx-auto max-w-screen-xl px-4 py-10">
                <div class="grid grid-cols-1 gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
                    <section class="min-w-0">
                        <div v-reveal class="mb-6 rounded-[24px] glass p-6 md:p-8">
                            <p class="text-sm font-semibold uppercase tracking-[0.22em] text-white/40">Categories</p>
                            <h1 class="mt-3 text-4xl font-extrabold text-white">全部<span class="gradient-text">分类</span></h1>
                        </div>
                        <div v-reveal>
                            <CategoryListCard />
                        </div>
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
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import UserInfoCard from '@/layouts/frontend/components/UserInfoCard.vue'
import TagListCard from '@/layouts/frontend/components/TagListCard.vue'
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue'
</script>
