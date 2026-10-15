<template>
  <footer class="personal-footer page-width">
    <div>
      <strong v-if="personalHomepagePublic"
        >{{ profile.name
        }}<template v-if="profile.englishName"> / {{ profile.englishName }}</template></strong
      ><strong v-else>{{ siteSettings.name || '技术博客' }}</strong>
      <p v-if="siteSettings.footerText">{{ siteSettings.footerText }}</p>
    </div>
    <div class="footer-links">
      <RouterLink :to="sitePath('/articles')">阅读文章</RouterLink
      ><RouterLink v-if="personalHomepagePublic" :to="sitePath('/contact')">联系我</RouterLink
      ><RouterLink :to="getToken() ? '/admin/index' : '/login'">后台登录</RouterLink
      ><span
        >© {{ new Date().getFullYear() }}
        {{
          siteSettings.copyright ||
          (personalHomepagePublic ? profile.name : '') ||
          siteSettings.name
        }}{{ personalHomepagePublic && profile.isExample ? ' · 虚构人物示例' : '' }}</span
      >
      <span v-if="siteSettings.filing">{{ siteSettings.filing }}</span>
    </div>
  </footer>
</template>
<script setup>
import { getToken } from '@/composables/cookie'
import { profile, sitePath, siteSettings, personalHomepagePublic } from '@/content/profile'
</script>
