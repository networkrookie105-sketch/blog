<template>
  <!-- 设置语言为中文 -->
  <el-config-provider :locale="locale">
      <router-view></router-view>
   </el-config-provider>
</template>

<script setup>
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
const locale = zhCn
</script>

<style>
/* 自定义顶部加载 Loading 颜色（主色渐变） */
#nprogress .bar {
   background: linear-gradient(120deg, #7c3aed, #6366f1 45%, #22d3ee) !important;
   height: 3px !important;
}
#nprogress .peg {
   box-shadow: 0 0 10px #22d3ee, 0 0 5px #7c3aed !important;
}
</style>