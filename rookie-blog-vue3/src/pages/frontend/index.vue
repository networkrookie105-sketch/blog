<template>
  <div class="public-shell">
    <Header />
    <SiteStatus />
    <main v-if="siteState.loaded" id="main-content" class="page-width">
      <section v-if="personalHomepagePublic" class="personal-hero">
        <div class="hero-copy">
          <p class="eyebrow">{{ [profile.role, profile.location].filter(Boolean).join(' · ') }}</p>
          <h1>
            你好，我是<span>{{ profile.name }}。</span><br /><span class="accent-word">{{
              profile.description
            }}</span>
          </h1>
          <p class="hero-description">{{ profile.intro }}</p>
          <div class="action-row">
            <RouterLink :to="sitePath('/projects')" class="primary-action"
              >查看我的项目 <TopRight /></RouterLink
            ><button class="text-action" @click="downloadResume">下载简历 <Download /></button>
          </div>
          <p v-if="profile.isExample" class="sample-caption">
            人物与经历为虚构示例，等待写入你的故事。
          </p>
        </div>
        <aside class="hero-note">
          <div class="note-heading">
            <span>个人工作笔记</span><span class="status-dot">持续构建中</span>
          </div>
          <p class="note-name">
            {{ profile.englishName }}<span>{{ profile.role }}</span>
          </p>
          <div class="note-rule"></div>
          <p class="note-label">我关心的三件事</p>
          <ul>
            <li>
              <span>系统设计</span>
              <p>让边界清晰，让变化有处安放。</p>
            </li>
            <li>
              <span>工程质量</span>
              <p>用测试与观测，让可靠可以验证。</p>
            </li>
            <li>
              <span>持续表达</span>
              <p>把实践写下来，也把思考留下来。</p>
            </li>
          </ul>
          <div class="note-bottom">
            <span>{{ profile.skills.join(' · ') }}</span
            ><span>{{ profile.location }}</span>
          </div>
        </aside>
      </section>
      <section v-else class="portfolio-section">
        <p class="eyebrow">TECHNICAL NOTES</p>
        <h1 class="page-title">{{ siteSettings.name || '技术博客' }}</h1>
        <p v-if="siteSettings.introduction" class="page-intro">{{ siteSettings.introduction }}</p>
        <RouterLink to="/articles" class="primary-action">阅读文章 <Right /></RouterLink>
      </section>
      <section v-if="personalHomepagePublic" class="portfolio-section">
        <div class="section-heading">
          <div>
            <p class="eyebrow">SELECTED WORK</p>
            <h2>用作品，介绍自己。</h2>
          </div>
          <RouterLink :to="sitePath('/projects')" class="inline-link"
            >全部项目 <Right
          /></RouterLink>
        </div>
        <p v-if="!featuredProjects.length" class="article-status">还没有公开项目。</p>
        <div v-else class="project-grid">
          <RouterLink
            v-for="project in featuredProjects"
            :key="project.slug"
            :to="sitePath(`/projects/${project.slug}`)"
            class="project-card"
            ><div class="project-top">
              <span>{{ project.category }}</span
              ><TopRight />
            </div>
            <p class="project-year">{{ project.year }}</p>
            <h3>{{ project.name }}</h3>
            <p>{{ project.description }}</p>
            <div class="skill-chips">
              <span v-for="skill in project.stack" :key="skill">{{ skill }}</span>
            </div>
            <span class="project-detail">阅读项目案例 <Right /></span
          ></RouterLink>
        </div>
      </section>
      <section v-if="personalHomepagePublic" class="expertise-strip">
        <div>
          <p class="eyebrow">HOW I WORK</p>
          <h2>写代码，也解决问题。</h2>
        </div>
        <div>
          <p>
            从业务理解出发，关注服务边界、数据一致性与维护成本。每一个技术选择，都应该有可以说清楚的理由。
          </p>
          <div class="skill-chips">
            <span v-for="skill in profile.skills" :key="skill">{{ skill }}</span>
          </div>
        </div>
      </section>
      <section class="portfolio-section">
        <div class="section-heading">
          <div>
            <p class="eyebrow">FIELD NOTES</p>
            <h2>实践之后，留下一些思考。</h2>
          </div>
          <RouterLink :to="sitePath('/articles')" class="inline-link"
            >进入博客 <Right
          /></RouterLink>
        </div>
        <p v-if="articlesLoading" class="article-status" role="status">正在加载文章…</p>
        <div v-else-if="articlesError" class="article-status" role="alert">
          文章暂时无法加载。<button class="inline-link" @click="loadRecentArticles">重试</button>
        </div>
        <p v-else-if="!notes.length" class="article-status">还没有公开文章。</p>
        <div v-else class="note-list">
          <RouterLink
            v-for="note in notes"
            :key="note.id"
            :to="`/article/detail/${note.id}`"
            class="writing-row"
            ><span class="writing-date"
              >{{ note.createTime?.slice(0, 10)
              }}<small>{{ note.category?.name || '技术笔记' }}</small></span
            >
            <div>
              <h3>{{ note.title }}</h3>
              <p>{{ note.summary }}</p>
            </div>
            <TopRight
          /></RouterLink>
        </div>
      </section>
      <section v-if="personalHomepagePublic" class="contact-band">
        <div>
          <p class="eyebrow">LET’S CONNECT</p>
          <h2>也许，下一个好项目<br />从一次交流开始。</h2>
        </div>
        <RouterLink :to="sitePath('/contact')" class="primary-action"
          >认识一下 <TopRight
        /></RouterLink>
      </section>
    </main>
    <Footer />
  </div>
</template>
<script setup>
import Header from '@/layouts/frontend/components/Header.vue'
import SiteStatus from '@/components/SiteStatus.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import { TopRight, Right, Download } from '@element-plus/icons-vue'
import {
  profile,
  projects,
  downloadResume,
  sitePath,
  siteState,
  siteSettings,
  personalHomepagePublic
} from '@/content/profile'
import { ref, onMounted, computed } from 'vue'
import { getArticlePageList } from '@/api/frontend/article'
const featuredProjects = computed(() => projects.filter((project) => project.featured !== false))
const notes = ref([]),
  articlesLoading = ref(false),
  articlesError = ref(false)
async function loadRecentArticles() {
  if (articlesLoading.value) return
  articlesLoading.value = true
  articlesError.value = false
  try {
    const res = await getArticlePageList({ current: 1, size: 3 })
    if (!res.success) throw new Error(res.message)
    notes.value = res.data || []
  } catch {
    articlesError.value = true
  } finally {
    articlesLoading.value = false
  }
}
onMounted(loadRecentArticles)
</script>
