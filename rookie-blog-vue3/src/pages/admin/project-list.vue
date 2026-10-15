<template>
  <div class="admin-editor">
    <el-card shadow="never" class="editor-card"
      ><div class="editor-toolbar">
        <h1>项目管理</h1>
        <div class="editor-actions">
          <el-button @click="load" :disabled="loading || saving">刷新</el-button
          ><el-button
            type="primary"
            :disabled="loading || !!loadError || items.length >= 100"
            @click="edit()"
            >新增项目</el-button
          >
        </div>
      </div>
      <div class="editor-actions">
        <el-input
          ref="searchRef"
          v-model="keyword"
          clearable
          placeholder="按项目名称筛选"
          aria-label="筛选项目"
          style="max-width: 320px"
          @clear="searchRef?.focus()"
        /><el-button v-if="keyword" @click="clearFilter">清除筛选</el-button
        ><span class="editor-help">顺序越小越靠前，未公开项目不会出现在前台。</span>
      </div>
    </el-card>
    <el-card shadow="never"
      ><div v-if="loadError" role="alert" class="editor-error">
        {{ loadError }} <el-button @click="load">重试</el-button>
      </div>
      <el-table
        v-else
        :data="paged"
        border
        v-loading="loading"
        :empty-text="keyword ? '没有匹配的项目' : '暂无项目，点击新增项目开始记录'"
      >
        <el-table-column prop="name" label="项目名称" min-width="240" /><el-table-column
          prop="category"
          label="项目类型"
          min-width="120"
        /><el-table-column prop="year" label="年份" width="90" /><el-table-column
          prop="order"
          label="顺序"
          width="80"
        />
        <el-table-column label="公开" width="90"
          ><template #default="{ row }"
            ><el-tag :type="row.published ? 'success' : 'info'">{{
              row.published ? '已公开' : '未公开'
            }}</el-tag></template
          ></el-table-column
        >
        <el-table-column label="首页精选" width="100"
          ><template #default="{ row }">{{ row.featured ? '是' : '否' }}</template></el-table-column
        >
        <el-table-column label="操作" width="160" fixed="right"
          ><template #default="{ row }"
            ><el-button size="small" :disabled="saving" @click="edit(row)">编辑</el-button
            ><el-button type="danger" size="small" :disabled="saving" @click="remove(row)"
              >删除</el-button
            ></template
          ></el-table-column
        >
      </el-table>
      <el-pagination
        v-if="filtered.length > 10"
        v-model:current-page="page"
        :page-size="10"
        :total="filtered.length"
        layout="total, prev, pager, next"
        style="margin-top: 20px"
      />
      <p class="editor-status" role="status">{{ status || `共 ${items.length} 个项目` }}</p>
      <el-button v-if="conflict && !open" @click="load">重新加载</el-button>
    </el-card>
    <el-dialog
      class="admin-editor-dialog"
      v-model="open"
      :title="isNew ? '新增项目' : '编辑项目'"
      width="860px"
      :before-close="close"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form ref="formRef" :model="project" :rules="rules" label-position="top" novalidate>
        <div class="editor-grid">
          <el-form-item label="项目名称" prop="name"
            ><el-input v-model="project.name" maxlength="150" /></el-form-item
          ><el-form-item label="项目副标题"><el-input v-model="project.subtitle" /></el-form-item
          ><el-form-item label="项目类型"
            ><el-input
              v-model="project.category"
              placeholder="例如：业务系统、工程实践" /></el-form-item
          ><el-form-item label="年份"><el-input v-model="project.year" /></el-form-item>
        </div>
        <el-form-item label="项目摘要"
          ><el-input v-model="project.description" type="textarea" :rows="3" resize="none"
        /></el-form-item>
        <el-form-item label="技术栈（逗号分隔）"><el-input v-model="stack" /></el-form-item>
        <el-form-item v-for="part in parts" :key="part.key" :label="part.label"
          ><el-input v-model="project[part.key]" type="textarea" :rows="3" resize="none"
        /></el-form-item>
        <div class="editor-grid">
          <el-form-item label="展示顺序"
            ><el-input-number v-model="project.order" :min="0" :max="9999" /></el-form-item
          ><el-form-item label="是否公开"
            ><el-switch v-model="project.published" active-text="在前台展示" /></el-form-item
          ><el-form-item label="首页精选"
            ><el-switch v-model="project.featured" active-text="展示在首页精选项目"
          /></el-form-item>
        </div>
      </el-form>
      <p v-if="status" class="editor-status" role="status">{{ status }}</p>
      <p v-if="conflict" class="editor-help">
        编辑内容已保留。关闭编辑并重新加载最新列表后再修改，避免覆盖其他页面的保存。
      </p>
      <template #footer
        ><el-button :disabled="saving" @click="close()">取消</el-button
        ><el-button type="primary" :loading="saving" :disabled="conflict" @click="saveProject"
          >保存项目</el-button
        ></template
      >
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed, watch, onMounted, onActivated, onBeforeUnmount } from 'vue'
import { ElMessageBox } from 'element-plus'
import axios from '@/axios'
import { showMessage } from '@/composables/util'
import { useAdminLeaveGuard } from '@/composables/useSiteSection'
import { loadSite } from '@/content/profile'
import '@/assets/admin-editor.css'
const items = ref([]),
  revision = ref(0),
  loading = ref(false),
  saving = ref(false),
  loadError = ref(''),
  status = ref(''),
  conflict = ref(false)
const keyword = ref(''),
  searchRef = ref(null),
  open = ref(false),
  project = ref({}),
  formRef = ref(null),
  snapshot = ref(''),
  isNew = ref(false)
const filtered = computed(() =>
  items.value
    .filter((item) =>
      item.name.toLocaleLowerCase().includes(keyword.value.trim().toLocaleLowerCase())
    )
    .sort((a, b) => a.order - b.order)
)
const page = ref(1)
const paged = computed(() => filtered.value.slice((page.value - 1) * 10, page.value * 10))
watch([keyword, () => filtered.value.length], () => {
  page.value = 1
})
const dirty = computed(() => open.value && JSON.stringify(project.value) !== snapshot.value)
const stack = computed({
  get: () => project.value.stack?.join(', ') || '',
  set: (value) =>
    (project.value.stack = value
      .split(/[,，]/)
      .map((s) => s.trim())
      .filter(Boolean))
})
const rules = { name: [{ required: true, message: '请填写项目名称', trigger: 'blur' }] }
const parts = [
  { key: 'context', label: '项目背景' },
  { key: 'contribution', label: '我的职责' },
  { key: 'challenge', label: '难点与选择' },
  { key: 'result', label: '实践与收获' }
]
function clearFilter() {
  keyword.value = ''
  searchRef.value?.focus()
}
async function load() {
  if (loading.value) return
  loading.value = true
  loadError.value = ''
  try {
    const res = await axios.get('/admin/site')
    if (!res.success) throw new Error(res.message)
    items.value = res.data.projects
    revision.value = res.data.revision
    conflict.value = false
    status.value = ''
  } catch (e) {
    loadError.value = e.message || '项目加载失败'
  } finally {
    loading.value = false
  }
}
function edit(row) {
  isNew.value = !row
  project.value = row
    ? JSON.parse(JSON.stringify(row))
    : {
        slug: crypto.randomUUID(),
        name: '',
        subtitle: '',
        category: '',
        year: String(new Date().getFullYear()),
        description: '',
        stack: [],
        context: '',
        contribution: '',
        challenge: '',
        result: '',
        order: items.value.length,
        published: false,
        featured: false
      }
  snapshot.value = JSON.stringify(project.value)
  status.value = ''
  conflict.value = false
  open.value = true
}
async function close(done) {
  if (saving.value) return false
  if (dirty.value) {
    try {
      await ElMessageBox.confirm('丢弃未保存的项目修改？', '未保存的修改', {
        confirmButtonText: '丢弃修改',
        cancelButtonText: '继续编辑',
        type: 'warning'
      })
    } catch {
      return false
    }
  }
  open.value = false
  project.value = {}
  if (typeof done === 'function') done()
  return true
}
useAdminLeaveGuard(() => close())
async function mutate(payload) {
  saving.value = true
  status.value = ''
  try {
    const res = await axios.put('/admin/site/projects', { revision: revision.value, ...payload })
    if (!res.success) {
      conflict.value = res.errorCode === '409'
      throw new Error(res.message)
    }
    items.value = res.data.projects
    revision.value = res.data.revision
    status.value = '项目已保存，前台已更新'
    showMessage(payload.action === 'delete' ? '项目已删除' : '项目已保存')
    open.value = false
    project.value = {}
    await loadSite()
  } catch (e) {
    status.value = e.message || '保存失败，编辑内容已保留'
  } finally {
    saving.value = false
  }
}
async function saveProject() {
  if (saving.value || conflict.value || !(await formRef.value.validate().catch(() => false))) return
  await mutate({ action: 'upsert', project: project.value })
}
async function remove(row) {
  if (saving.value) return
  try {
    await ElMessageBox.confirm(
      `删除项目“${row.name}”？此操作会立即从后台及前台移除。`,
      '删除项目',
      { confirmButtonText: '删除项目', cancelButtonText: '取消', type: 'warning' }
    )
    await mutate({ action: 'delete', slug: row.slug })
  } catch {}
}
function beforeUnload(event) {
  if (dirty.value) {
    event.preventDefault()
    event.returnValue = ''
  }
}
onActivated(() => {
  if (!open.value && !saving.value && !loading.value) load()
})
onMounted(() => {
  load()
  window.addEventListener('beforeunload', beforeUnload)
})
onBeforeUnmount(() => window.removeEventListener('beforeunload', beforeUnload))
</script>
