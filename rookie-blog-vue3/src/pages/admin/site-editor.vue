<template>
  <div class="admin-editor">
    <el-card shadow="never" class="editor-card">
      <div class="editor-toolbar">
        <h1>个人资料</h1>
        <div class="editor-actions">
          <AdminPersonalPreview />
          <el-button @click="preview">预览主页</el-button>
          <el-button
            type="primary"
            :disabled="!form || loading || !!error"
            :loading="saving"
            @click="save(formRef)"
            >保存个人资料</el-button
          >
        </div>
      </div>
      <p class="editor-help">
        介绍你本人。博客名称与网站信息在“站点设置”维护，项目在“项目管理”维护。
      </p>
      <p v-if="loading" role="status">正在加载个人资料…</p>
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
        <div class="editor-grid">
          <el-form-item label="姓名 / 展示名称" prop="name"
            ><el-input v-model="form.name" maxlength="60"
          /></el-form-item>
          <el-form-item label="英文名"><el-input v-model="form.englishName" /></el-form-item>
          <el-form-item label="职业方向"><el-input v-model="form.role" /></el-form-item>
          <el-form-item label="所在地"><el-input v-model="form.location" /></el-form-item>
          <el-form-item label="公开联系邮箱" prop="email"
            ><el-input v-model="form.email" type="email"
          /></el-form-item>
          <el-form-item label="个人展示标语"
            ><el-input v-model="form.description" />
            <p class="editor-help">显示在首页自我介绍标题中。</p></el-form-item
          >
        </div>
        <el-form-item label="个人头像" prop="avatar"
          ><AdminAssetInput v-model="form.avatar" label="个人头像"
        /></el-form-item>
        <el-form-item label="个人介绍"
          ><el-input v-model="form.intro" type="textarea" :rows="4" maxlength="5000" resize="none"
        /></el-form-item>
        <el-form-item label="技术技能（逗号分隔）"><el-input v-model="skills" /></el-form-item>
        <el-form-item label="兴趣与技术之外"
          ><el-input v-model="form.interests" type="textarea" :rows="3" resize="none"
        /></el-form-item>
        <el-divider content-position="left">联系方式与个人链接</el-divider>
        <p class="editor-help">
          仅当“站点设置”中的个人主页公开时生效。关闭某项展示不会清空内容，也不会出现在公开接口或下载的简历中。
        </p>
        <div class="editor-grid">
          <el-form-item v-for="option in contactOptions" :key="option.key" :label="option.label">
            <el-switch
              v-model="form.contactVisibility[option.key]"
              active-text="展示"
              inactive-text="隐藏"
              :aria-label="option.label"
            />
          </el-form-item>
        </div>
        <div class="editor-grid">
          <el-form-item v-for="link in links" :key="link.key" :label="link.label" :prop="link.key"
            ><el-input v-model="form[link.key]" clearable placeholder="可选：填写后在联系页面展示"
          /></el-form-item>
        </div>
        <el-divider content-position="left">工作经历</el-divider>
        <el-table :data="form.experiences" border empty-text="暂无工作经历"
          ><el-table-column prop="period" label="时间" min-width="140" /><el-table-column
            prop="company"
            label="公司"
            min-width="180"
          /><el-table-column prop="role" label="岗位" min-width="140" /><el-table-column
            label="操作"
            width="150"
            ><template #default="{ row, $index }"
              ><el-button size="small" @click="editExperience(row, $index)">编辑</el-button
              ><el-button type="danger" size="small" @click="removeExperience($index)"
                >删除</el-button
              ></template
            ></el-table-column
          ></el-table
        >
        <el-button
          style="margin-top: 16px"
          :disabled="form.experiences.length >= 30"
          @click="editExperience(null, -1)"
          >添加经历</el-button
        >
        <el-form-item label="示例内容标识" style="margin-top: 24px"
          ><el-switch v-model="form.isExample" active-text="公开标注为虚构示例"
        /></el-form-item>
      </el-form>
      <p class="editor-status" role="status">
        {{ status || (dirty ? '有未保存的个人资料修改' : '') }}
      </p>
      <el-button v-if="conflict" @click="reload">重新加载资料</el-button>
    </el-card>
    <el-dialog
      class="admin-editor-dialog"
      v-model="experienceOpen"
      :title="experienceIndex < 0 ? '添加经历' : '编辑经历'"
      width="640px"
      :close-on-click-modal="false"
      :before-close="closeExperience"
      append-to-body
    >
      <el-form
        ref="experienceRef"
        :model="experience"
        :rules="experienceRules"
        label-position="top"
        novalidate
      >
        <el-form-item label="时间" prop="period"
          ><el-input v-model="experience.period" /></el-form-item
        ><el-form-item label="公司" prop="company"
          ><el-input v-model="experience.company" /></el-form-item
        ><el-form-item label="岗位" prop="role"><el-input v-model="experience.role" /></el-form-item
        ><el-form-item label="职责与贡献"
          ><el-input v-model="experience.description" type="textarea" :rows="4" resize="none"
        /></el-form-item> </el-form
      ><template #footer
        ><el-button @click="closeExperience()">取消</el-button
        ><el-button type="primary" @click="applyExperience">应用到资料</el-button></template
      >
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import AdminPersonalPreview from '@/components/AdminPersonalPreview.vue'
import AdminAssetInput from '@/components/AdminAssetInput.vue'
import { useSiteSection, urlRule, confirmPersonalDisclosure } from '@/composables/useSiteSection'
import '@/assets/admin-editor.css'
const { form, loading, saving, error, status, conflict, dirty, load, reload, save } =
  useSiteSection('profile', {
    beforeSave: (current, previous) => confirmPersonalDisclosure('profile', current, previous),
    dirty: () =>
      experienceOpen.value && JSON.stringify(experience.value) !== experienceSnapshot.value,
    discard: () => {
      experienceOpen.value = false
    }
  })
const contactOptions = [
  { key: 'email', label: '公开邮箱' },
  { key: 'location', label: '公开所在地' },
  { key: 'githubHomepage', label: '公开 GitHub 链接' },
  { key: 'giteeHomepage', label: '公开 Gitee 链接' },
  { key: 'zhihuHomepage', label: '公开知乎链接' },
  { key: 'csdnHomepage', label: '公开 CSDN 链接' }
]
const formRef = ref(null),
  router = useRouter()
const links = [
  { key: 'githubHomepage', label: 'GitHub' },
  { key: 'giteeHomepage', label: 'Gitee' },
  { key: 'zhihuHomepage', label: '知乎' },
  { key: 'csdnHomepage', label: 'CSDN' }
]
const rules = {
  name: [{ required: true, message: '请填写姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  avatar: [urlRule],
  ...Object.fromEntries(links.map((link) => [link.key, [urlRule]]))
}
const skills = computed({
  get: () => form.value.skills.join(', '),
  set: (value) =>
    (form.value.skills = value
      .split(/[,，]/)
      .map((s) => s.trim())
      .filter(Boolean))
})
const experienceOpen = ref(false),
  experienceIndex = ref(-1),
  experience = ref({}),
  experienceRef = ref(null),
  experienceSnapshot = ref('')
const experienceRules = Object.fromEntries(
  ['period', 'company', 'role'].map((key) => [
    key,
    [{ required: true, message: '请填写此项', trigger: 'blur' }]
  ])
)
function editExperience(row, index) {
  experienceIndex.value = index
  experience.value = row
    ? JSON.parse(JSON.stringify(row))
    : { period: '', company: '', role: '', description: '' }
  experienceSnapshot.value = JSON.stringify(experience.value)
  experienceOpen.value = true
}
async function closeExperience(done) {
  if (JSON.stringify(experience.value) !== experienceSnapshot.value) {
    try {
      await ElMessageBox.confirm('丢弃本次经历编辑？', '未应用的修改', {
        confirmButtonText: '丢弃修改',
        cancelButtonText: '继续编辑'
      })
    } catch {
      return
    }
  }
  experienceOpen.value = false
  if (typeof done === 'function') done()
}
async function applyExperience() {
  if (!(await experienceRef.value.validate().catch(() => false))) return
  const row = JSON.parse(JSON.stringify(experience.value))
  if (experienceIndex.value < 0) form.value.experiences.push(row)
  else form.value.experiences.splice(experienceIndex.value, 1, row)
  experienceSnapshot.value = JSON.stringify(experience.value)
  experienceOpen.value = false
  status.value = '经历已应用，请保存个人资料'
}
async function removeExperience(index) {
  try {
    await ElMessageBox.confirm('删除这条经历？保存个人资料后生效。', '删除经历', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    form.value.experiences.splice(index, 1)
  } catch {}
}
function preview() {
  router.push('/')
}
</script>
