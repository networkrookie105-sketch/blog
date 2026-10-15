<template>
  <div style="width: 100%">
    <div class="admin-asset-row">
      <el-input
        :model-value="modelValue"
        @update:model-value="$emit('update:modelValue', $event)"
        clearable
        :placeholder="placeholder"
        :aria-label="label"
        :disabled="uploading"
      /><el-upload
        :show-file-list="false"
        :auto-upload="false"
        :on-change="upload"
        accept="image/*"
        :disabled="uploading"
        ><el-button :loading="uploading">上传图片</el-button></el-upload
      >
    </div>
    <img
      v-if="modelValue && !previewFailed && valid"
      :src="modelValue"
      :alt="label + '预览'"
      class="asset-preview"
      @error="previewFailed = true"
    />
    <p v-if="error || previewFailed" role="alert" class="editor-error">
      {{ error || label + '无法预览，请检查图片地址' }}
    </p>
  </div>
</template>
<script setup>
import { ref, computed, watch } from 'vue'
import { uploadFile } from '@/api/admin/file'
const props = defineProps({
  modelValue: { type: String, default: '' },
  label: { type: String, default: '图片' },
  placeholder: { type: String, default: '可选：图片地址' }
})
const emit = defineEmits(['update:modelValue'])
const uploading = ref(false),
  error = ref(''),
  previewFailed = ref(false)
const valid = computed(() => /^(https?:\/\/[^\s]+|\/[^/\s][^\s]*)$/.test(props.modelValue))
watch(
  () => props.modelValue,
  () => {
    previewFailed.value = false
    error.value = ''
  }
)
async function upload(file) {
  if (uploading.value) return
  if (!file.raw?.type.startsWith('image/')) {
    error.value = '请选择图片文件'
    return
  }
  if (file.raw.size > 10 * 1024 * 1024) {
    error.value = '图片大小不能超过10MB'
    return
  }
  uploading.value = true
  error.value = ''
  try {
    const body = new FormData()
    body.append('file', file.raw)
    const res = await uploadFile(body)
    if (!res.success || !res.data?.url) throw new Error(res.message || '上传响应缺少图片地址')
    emit('update:modelValue', res.data.url)
  } catch (e) {
    error.value = e.message || '上传失败，请重试或填写图片地址'
  } finally {
    uploading.value = false
  }
}
</script>
<style scoped>
.admin-asset-row {
  display: flex;
  gap: 12px;
  align-items: center;
}
.admin-asset-row .el-input {
  min-width: 0;
  flex: 1;
}
.asset-preview {
  max-width: 100px;
  max-height: 80px;
  object-fit: contain;
  display: block;
  margin-top: 12px;
}
.editor-error {
  font-size: 13px;
  margin-top: 8px;
  color: var(--el-color-danger);
}
</style>
