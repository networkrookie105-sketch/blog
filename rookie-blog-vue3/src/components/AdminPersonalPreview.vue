<template>
  <el-button :loading="loading" @click="openPreview">预览已保存内容</el-button>
  <el-dialog
    v-model="open"
    title="个人内容预览"
    class="admin-editor-dialog"
    width="760px"
    append-to-body
  >
    <p class="editor-help">
      仅在后台查看，不会公开内容。这里展示已保存资料；页面中尚未保存的修改不包含在内。
    </p>
    <p v-if="loading" role="status">正在加载预览…</p>
    <p v-else-if="error" role="alert">{{ error }} <el-button @click="load">重试</el-button></p>
    <template v-else-if="data">
      <el-alert
        :closable="false"
        :title="
          data.settings.personalHomepagePublic
            ? '个人主页当前已公开'
            : '个人主页当前未公开，以下是开启后的内容预览'
        "
        type="info"
      />
      <img
        v-if="data.profile.avatar"
        :src="data.profile.avatar"
        alt="个人头像预览"
        class="asset-preview"
      />
      <h2>
        {{ data.profile.name }} <small>{{ data.profile.englishName }}</small>
      </h2>
      <p>{{ data.profile.description }}</p>
      <p>
        {{ data.profile.role
        }}<template v-if="visible('location') && data.profile.location">
          · {{ data.profile.location }}</template
        >
      </p>
      <p>{{ data.profile.intro }}</p>
      <p>{{ data.profile.skills.join(' · ') }}</p>
      <p v-if="visible('email') && data.profile.email">邮箱：{{ data.profile.email }}</p>
      <p v-for="link in links" :key="link">{{ link }}</p>
      <p v-if="data.profile.interests">{{ data.profile.interests }}</p>
      <h3>工作经历</h3>
      <section v-for="item in data.profile.experiences" :key="item.period + item.company">
        <h4>{{ item.company }} · {{ item.role }}</h4>
        <p>{{ item.period }}</p>
        <p>{{ item.description }}</p>
      </section>
      <h3>公开项目</h3>
      <p v-if="!projects.length">暂无公开项目。</p>
      <section v-for="item in projects" :key="item.slug">
        <h4>{{ item.name }}</h4>
        <p>{{ item.description }}</p>
      </section>
    </template>
    <template #footer><el-button @click="open = false">关闭预览</el-button></template>
  </el-dialog>
</template>
<script setup>
import { ref, computed } from 'vue'
import axios from '@/axios'
const open = ref(false),
  loading = ref(false),
  error = ref(''),
  data = ref(null)
const visible = (key) => data.value?.profile.contactVisibility[key] === true
const links = computed(() =>
  ['githubHomepage', 'giteeHomepage', 'zhihuHomepage', 'csdnHomepage']
    .filter(visible)
    .map((key) => data.value.profile[key])
    .filter(Boolean)
)
const projects = computed(() =>
  (data.value?.projects || []).filter((item) => item.published).sort((a, b) => a.order - b.order)
)
async function load() {
  loading.value = true
  error.value = ''
  data.value = null
  try {
    const res = await axios.get('/admin/site')
    if (!res.success) throw new Error(res.message)
    data.value = res.data
  } catch (e) {
    error.value = e.message || '预览加载失败，请重试'
  } finally {
    loading.value = false
  }
}
function openPreview() {
  open.value = true
  load()
}
</script>
