<template>
  <section
    v-if="personalHomepagePublic"
    class="hero-note"
    style="transform: none; box-shadow: none"
  >
    <p class="eyebrow">ABOUT THE AUTHOR</p>
    <img
      v-if="profile.avatar"
      :src="profile.avatar"
      alt="作者头像"
      style="width: 64px; height: 64px; object-fit: cover; border-radius: 50%; margin-top: 16px"
    />
    <h2 style="font-size: 26px; margin: 15px 0 8px; font-weight: 600">
      {{ profile.name || '个人主页' }}
    </h2>
    <p style="font-size: 13px; color: var(--personal-muted)">{{ profile.role }}</p>
    <p style="font-size: 13px; line-height: 1.9; margin: 18px 0">{{ profile.intro }}</p>
    <RouterLink :to="sitePath('/about')" class="inline-link">更多关于我 <Right /></RouterLink>
    <p v-if="profile.isExample" class="sample-caption">虚构人物示例</p>
  </section>
</template>
<script setup>
import { Right } from '@element-plus/icons-vue'
import { profile, sitePath, personalHomepagePublic } from '@/content/profile'
</script>
