<template>
  <div class="admin-editor">
    <el-card shadow="never">
      <div class="editor-toolbar">
        <h1>站点设置</h1>
        <div class="editor-actions">
          <AdminPersonalPreview />
          <el-button @click="router.push('/')">预览网站</el-button
          ><el-button
            type="primary"
            :disabled="!form || loading || !!error"
            :loading="saving"
            @click="save(formRef)"
            >保存站点设置</el-button
          >
        </div>
      </div>
      <p class="editor-help">
        配置网站品牌、浏览器信息与页脚。姓名、头像、个人介绍及社交链接请在“个人资料”维护。
      </p>
      <p v-if="loading" role="status">正在加载站点设置…</p>
      <div v-else-if="error" role="alert" class="editor-error">
        {{ error }} <el-button @click="load">重试</el-button>
      </div>
      <el-form
        v-else-if="form"
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        novalidate
      >
        <el-form-item label="公开个人主页">
          <el-switch
            v-model="form.personalHomepagePublic"
            active-text="公开个人内容"
            inactive-text="仅展示博客"
            aria-label="公开个人主页"
          />
          <p class="editor-help">
            保存后生效。开启时展示个人介绍、技能、经历和已公开项目；关闭时隐藏这些内容及个人页面，文章、分类、标签与归档仍可访问。
          </p>
          <p class="editor-help">
            邮箱、所在地与个人链接按“个人资料”中的展示选项公开。博客名称、Logo、网站简介和页脚始终公开，请勿填写不希望展示的信息。
          </p>
        </el-form-item>
        <el-form-item label="博客名称" prop="name"
          ><el-input v-model="form.name" maxlength="80" />
          <p class="editor-help">
            显示在前台导航和浏览器标题，例如“林序的技术笔记”。
          </p></el-form-item
        >
        <div class="editor-grid">
          <el-form-item label="网站 Logo（可选）" prop="logo"
            ><AdminAssetInput
              v-model="form.logo"
              label="网站 Logo"
              placeholder="留空使用文字标识" /></el-form-item
          ><el-form-item label="浏览器图标（可选）" prop="favicon"
            ><AdminAssetInput
              v-model="form.favicon"
              label="浏览器图标"
              placeholder="留空使用默认图标"
          /></el-form-item>
        </div>
        <el-form-item label="网站简介"
          ><el-input v-model="form.introduction" type="textarea" :rows="3" resize="none" />
          <p class="editor-help">
            用于网页 description，描述网站主题；与个人介绍分开保存。
          </p></el-form-item
        >
        <el-divider content-position="left">页脚信息</el-divider>
        <el-form-item label="页脚标语"
          ><el-input v-model="form.footerText" clearable
        /></el-form-item>
        <el-form-item label="版权署名"
          ><el-input
            v-model="form.copyright"
            clearable
            placeholder="留空使用公开姓名或博客名称，年份自动更新"
        /></el-form-item>
        <el-form-item label="备案信息（可选）"
          ><el-input v-model="form.filing" clearable
        /></el-form-item>
      </el-form>
      <p class="editor-status" role="status">{{ status || (dirty ? '有未保存的站点设置' : '') }}</p>
      <el-button v-if="conflict" @click="reload">重新加载设置</el-button>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AdminPersonalPreview from '@/components/AdminPersonalPreview.vue'
import AdminAssetInput from '@/components/AdminAssetInput.vue'
import { useSiteSection, urlRule, confirmPersonalDisclosure } from '@/composables/useSiteSection'
import '@/assets/admin-editor.css'
const { form, loading, saving, error, status, conflict, dirty, load, reload, save } =
  useSiteSection('settings', {
    beforeSave: (current, previous) => confirmPersonalDisclosure('settings', current, previous)
  })
const formRef = ref(null),
  router = useRouter()
const rules = {
  name: [{ required: true, message: '请填写博客名称', trigger: 'blur' }],
  logo: [urlRule],
  favicon: [urlRule]
}
</script>
