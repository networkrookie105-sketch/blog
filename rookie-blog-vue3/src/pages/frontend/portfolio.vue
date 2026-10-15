<template>
  <div class="public-shell">
    <Header />
    <SiteStatus />
    <main v-if="siteState.loaded" id="main-content" class="page-width inner-page">
      <template v-if="!personalHomepagePublic">
        <h1 class="page-title">个人主页暂未公开。</h1>
        <p class="page-intro">你可以继续阅读博客中的文章与技术笔记。</p>
        <RouterLink to="/articles" class="primary-action">阅读文章 <Right /></RouterLink>
      </template>
      <template v-else-if="route.path.endsWith('/projects')"
        ><p class="eyebrow">PROJECT JOURNAL</p>
        <h1 class="page-title">把想法，做成作品。</h1>
        <p class="page-intro">记录项目背后的问题、选择与实践。</p>
        <div class="project-grid">
          <RouterLink
            v-for="item in projects"
            :key="item.slug"
            :to="sitePath(`/projects/${item.slug}`)"
            class="project-card"
            ><div class="project-top">
              <span>{{ item.category }}</span
              ><TopRight />
            </div>
            <p class="project-year">{{ item.year }}</p>
            <h2>{{ item.name }}</h2>
            <p>{{ item.description }}</p>
            <div class="skill-chips">
              <span v-for="skill in item.stack" :key="skill">{{ skill }}</span>
            </div>
            <span class="project-detail">阅读项目案例 <Right /></span
          ></RouterLink></div
      ></template>
      <template v-else-if="project"
        ><RouterLink :to="sitePath('/projects')" class="inline-link">← 返回项目</RouterLink>
        <p class="eyebrow detail-eyebrow">
          {{ project.category }} / {{ project.year }} {{ profile.isExample ? '/ 示例案例' : '' }}
        </p>
        <h1 class="page-title">{{ project.name }}</h1>
        <p class="page-intro">{{ project.subtitle }}</p>
        <div class="skill-chips">
          <span v-for="skill in project.stack" :key="skill">{{ skill }}</span>
        </div>
        <div class="case-body">
          <section v-for="part in caseParts" :key="part.key">
            <h2>{{ part.label }}</h2>
            <p>{{ project[part.key] }}</p>
          </section>
        </div></template
      >
      <template v-else-if="route.path.endsWith('/about')"
        ><p class="eyebrow">A LITTLE ABOUT ME</p>
        <h1 class="page-title">认真构建，<br />保持好奇。</h1>
        <p class="page-intro">
          我是{{ profile.name }}，一名{{ profile.role }}。{{ profile.intro }}
        </p>
        <p v-if="profile.isExample" class="sample-caption">
          以下人物、公司与工作经历均为虚构示例。
        </p>
        <section class="about-section">
          <h2>走过的路</h2>
          <div
            v-for="experience in profile.experiences"
            :key="experience.period"
            class="experience-row"
          >
            <span>{{ experience.period }}</span>
            <div>
              <h3>{{ experience.company }}</h3>
              <p class="experience-role">{{ experience.role }}</p>
              <p>{{ experience.description }}</p>
            </div>
          </div>
        </section>
        <section class="about-section">
          <h2>技术之外</h2>
          <p class="page-intro">
            {{ profile.interests || '还没有填写兴趣介绍。' }}
          </p>
          <button class="primary-action" @click="downloadResume">
            下载简历 <Download />
          </button></section
      ></template>
      <template v-else-if="route.path.endsWith('/contact')"
        ><p class="eyebrow">SAY HELLO</p>
        <h1 class="page-title">聊聊你的想法。</h1>
        <p class="page-intro">
          无论是技术交流、工作机会，还是一个正在酝酿的项目，都可以从简单的介绍开始。
        </p>
        <section v-if="profile.email" class="contact-card">
          <p class="eyebrow">EMAIL</p>
          <p class="email-display">{{ profile.email || '暂未填写邮箱' }}</p>
          <p>
            {{
              profile.isExample
                ? '这是用于展示的邮箱，请替换为真实联系方式。'
                : '欢迎通过邮件与我交流。'
            }}
          </p>
          <button class="primary-action" :disabled="!profile.email" @click="copyEmail">
            复制邮箱 <CopyDocument />
          </button>
          <p class="copy-status" role="status">{{ copyStatus }}</p>
        </section>
        <p v-if="!profile.email && !socialLinks.length" class="page-intro">暂未公开联系方式。</p>
        <div v-if="socialLinks.length" class="action-row" style="margin: 24px 0">
          <a
            v-for="link in socialLinks"
            :key="link.key"
            :href="link.url"
            target="_blank"
            rel="noopener noreferrer"
            class="inline-link"
            >{{ link.label }} <TopRight
          /></a>
        </div>
        <button class="text-action" @click="downloadResume">
          先了解我的经历 · 下载简历 <Download /></button
      ></template>
      <template v-else
        ><h1 class="page-title">没有找到这篇内容。</h1>
        <RouterLink :to="sitePath('/')" class="primary-action">返回首页 <Right /></RouterLink
      ></template>
    </main>
    <Footer />
  </div>
</template>
<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { TopRight, Right, Download, CopyDocument } from '@element-plus/icons-vue'
import Header from '@/layouts/frontend/components/Header.vue'
import SiteStatus from '@/components/SiteStatus.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import {
  profile,
  projects,
  downloadResume,
  sitePath,
  siteState,
  personalHomepagePublic
} from '@/content/profile'
const route = useRoute()
const project = computed(() =>
  route.path.includes('/projects/') ? projects.find((p) => p.slug === route.params.slug) : null
)
const socialLinks = computed(() =>
  [
    { key: 'githubHomepage', label: 'GitHub' },
    { key: 'giteeHomepage', label: 'Gitee' },
    { key: 'zhihuHomepage', label: '知乎' },
    { key: 'csdnHomepage', label: 'CSDN' }
  ]
    .map((link) => ({ ...link, url: profile[link.key] }))
    .filter((link) => link.url)
)
const caseParts = [
  { key: 'context', label: '项目背景' },
  { key: 'contribution', label: '我的职责' },
  { key: 'challenge', label: '难点与选择' },
  { key: 'result', label: '实践与收获' }
]
const copyStatus = ref('')
async function copyEmail() {
  try {
    await navigator.clipboard.writeText(profile.email)
    copyStatus.value = '邮箱已复制。'
  } catch {
    copyStatus.value = '复制未成功，请选中上方邮箱手动复制。'
  }
}
</script>
