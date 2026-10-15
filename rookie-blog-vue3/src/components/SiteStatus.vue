<template>
  <div v-if="siteState.loading || siteState.error" class="page-width inner-page" role="status">
    <p class="article-status" v-if="siteState.loading">正在加载个人主页…</p>
    <section v-else class="article-status">
      <h1>主页暂时无法加载</h1>
      <p>{{ siteState.error }}</p>
      <button class="inline-link" @click="loadSite(siteState.username)">重新加载</button>
    </section>
  </div>
</template>
<script setup>
import { siteState, loadSite } from '@/content/profile'
</script>
