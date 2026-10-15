import { ref, computed, onMounted, onActivated, onDeactivated, onBeforeUnmount } from 'vue'
import { onBeforeRouteLeave } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import axios from '@/axios'
import { showMessage } from '@/composables/util'
import { loadSite } from '@/content/profile'
const activeGuards = new Set()
export async function leaveAdminEditors() {
  for (const guard of activeGuards) if (!(await guard())) return false
  return true
}
export function useAdminLeaveGuard(guard) {
  onMounted(() => activeGuards.add(guard))
  onActivated(() => activeGuards.add(guard))
  onDeactivated(() => activeGuards.delete(guard))
  onBeforeUnmount(() => activeGuards.delete(guard))
  onBeforeRouteLeave(guard)
}
export function useSiteSection(section, extra = {}) {
  const form = ref(null),
    revision = ref(0),
    loading = ref(false),
    saving = ref(false)
  const error = ref(''),
    status = ref(''),
    conflict = ref(false),
    snapshot = ref('')
  const dirty = computed(
    () => (form.value && JSON.stringify(form.value) !== snapshot.value) || extra.dirty?.()
  )
  async function load() {
    loading.value = true
    error.value = ''
    status.value = ''
    try {
      const res = await axios.get('/admin/site')
      if (!res.success) throw new Error(res.message)
      form.value = JSON.parse(JSON.stringify(res.data[section]))
      revision.value = res.data.revision
      snapshot.value = JSON.stringify(form.value)
      conflict.value = false
    } catch (e) {
      error.value = e.message || '加载失败，请重试'
    } finally {
      loading.value = false
    }
  }
  async function save(formRef) {
    if (saving.value || !form.value || !(await formRef.validate().catch(() => false))) return
    saving.value = true
    status.value = ''
    conflict.value = false
    try {
      if (extra.beforeSave && !(await extra.beforeSave(form.value, JSON.parse(snapshot.value))))
        return
      const res = await axios.put(`/admin/site/${section}`, {
        revision: revision.value,
        document: form.value
      })
      if (!res.success) {
        conflict.value = res.errorCode === '409'
        throw new Error(res.message)
      }
      form.value = JSON.parse(JSON.stringify(res.data[section]))
      revision.value = res.data.revision
      snapshot.value = JSON.stringify(form.value)
      status.value = '保存成功，前台已更新'
      showMessage('保存成功')
      await loadSite()
    } catch (e) {
      status.value = e.message || '保存失败，修改仍保留在此页面'
    } finally {
      saving.value = false
    }
  }
  async function canLeave() {
    if (saving.value) return false
    if (!dirty.value) return true
    try {
      await ElMessageBox.confirm('有未保存的修改，离开会丢弃，是否继续？', '未保存的修改', {
        confirmButtonText: '丢弃修改',
        cancelButtonText: '继续编辑',
        type: 'warning'
      })
      form.value = JSON.parse(snapshot.value)
      extra.discard?.()
      return true
    } catch {
      return false
    }
  }
  async function reload() {
    if (await canLeave()) await load()
  }
  useAdminLeaveGuard(canLeave)
  function beforeUnload(event) {
    if (dirty.value) {
      event.preventDefault()
      event.returnValue = ''
    }
  }
  onActivated(() => {
    if (!dirty.value && !saving.value && !loading.value) load()
  })
  onMounted(() => {
    load()
    window.addEventListener('beforeunload', beforeUnload)
  })
  onBeforeUnmount(() => window.removeEventListener('beforeunload', beforeUnload))
  return { form, revision, loading, saving, error, status, conflict, dirty, load, reload, save }
}
export const urlRule = {
  validator: (_rule, value, done) =>
    done(
      !value || /^(https?:\/\/[^\s]+|\/[^/\s][^\s]*)$/.test(value)
        ? undefined
        : new Error('请输入 http/https 地址或站内路径')
    ),
  trigger: 'blur'
}

export async function confirmPersonalDisclosure(section, current, previous) {
  const enablingHomepage =
    section === 'settings' &&
    current.personalHomepagePublic === true &&
    previous.personalHomepagePublic !== true
  const labels = {
    email: '邮箱',
    location: '所在地',
    githubHomepage: 'GitHub',
    giteeHomepage: 'Gitee',
    zhihuHomepage: '知乎',
    csdnHomepage: 'CSDN'
  }
  const newlyVisible =
    section === 'profile'
      ? Object.keys(labels).filter(
          (key) =>
            current.contactVisibility?.[key] === true && previous.contactVisibility?.[key] !== true
        )
      : []
  if (!enablingHomepage && !newlyVisible.length) return true
  try {
    await ElMessageBox.confirm(
      enablingHomepage
        ? '开启后，访客将可以查看个人介绍、技能、工作经历、已公开项目和允许展示的联系方式。确认公开个人主页？'
        : `保存后，个人主页公开时访客可查看这些信息：${newlyVisible
            .map((key) => labels[key])
            .join('、')}。确认展示？`,
      '确认公开范围',
      { confirmButtonText: '确认公开并保存', cancelButtonText: '继续编辑', type: 'warning' }
    )
    return true
  } catch {
    return false
  }
}
