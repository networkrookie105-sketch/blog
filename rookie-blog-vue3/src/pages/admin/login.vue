<template>
  <div class="login-wrap site-dark">
    <div class="aurora"><div class="grid"></div></div>

    <div class="content-layer grid min-h-screen grid-cols-1 md:grid-cols-2">
      <!-- 左侧品牌区 -->
      <div
        class="relative order-2 hidden flex-col justify-center overflow-hidden p-14 md:order-1 md:flex"
      >
        <!-- 装饰：旋转光环 + 光球 -->
        <div class="orb-deco" aria-hidden="true">
          <span class="ring ring-1"></span>
          <span class="ring ring-2"></span>
          <span class="ring ring-3"></span>
          <span class="core"></span>
        </div>

        <div class="animate__animated animate__fadeInLeft animate__fast relative z-10 max-w-md">
          <span
            class="inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs font-medium tracking-[0.28em] text-white/60"
          >
            <span
              class="h-1.5 w-1.5 rounded-full"
              style="background: #22d3ee; box-shadow: 0 0 8px #22d3ee"
            ></span>
            PERSONAL SPACE
          </span>
          <h2 class="mt-6 text-6xl font-extrabold leading-[1.05]">
            <span class="brand">ROOKIE</span><br />
            <span class="text-white">BLOG</span>
          </h2>
          <p class="mt-6 max-w-sm text-sm leading-7 text-white/50">
            记录自己的成长，维护个人资料、项目作品与博客文章。
          </p>

          <ul class="mt-8 space-y-3.5">
            <li class="flex items-center gap-3 text-sm text-white/75">
              <span class="feat-dot"></span> 个人资料 · 工作经历
            </li>
            <li class="flex items-center gap-3 text-sm text-white/75">
              <span class="feat-dot"></span> 项目作品 · 技术文章
            </li>
            <li class="flex items-center gap-3 text-sm text-white/75">
              <span class="feat-dot"></span> 独立主页 · 随时更新
            </li>
          </ul>

          <div class="mt-10 flex flex-wrap gap-2">
            <span class="stack-chip">Spring Boot</span>
            <span class="stack-chip">Vue 3</span>
            <span class="stack-chip">MyBatis-Plus</span>
            <span class="stack-chip">Element Plus</span>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="order-1 flex items-center justify-center p-6 md:order-2">
        <div
          class="login-card animate__animated animate__fadeInUp animate__fast w-full max-w-md rounded-[24px] border border-white/10 bg-white/[0.04] p-8 shadow-2xl shadow-black/40 backdrop-blur-xl md:p-10"
        >
          <h1 class="text-3xl font-extrabold text-white">博客后台登录</h1>
          <div class="mb-8 mt-3 flex items-center gap-3 text-sm text-white/40">
            <span class="h-px flex-1 bg-white/10"></span>
            使用管理员账号登录
            <span class="h-px flex-1 bg-white/10"></span>
          </div>

          <el-form
            ref="formRef"
            :rules="rules"
            :model="form"
            label-position="top"
            novalidate
            @submit.prevent="onSubmit"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                size="large"
                v-model="form.username"
                placeholder="请输入用户名"
                autocomplete="username"
                aria-label="用户名"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input
                size="large"
                type="password"
                v-model="form.password"
                placeholder="请输入密码"
                autocomplete="current-password"
                aria-label="密码"
                :prefix-icon="Lock"
                clearable
                show-password
              />
            </el-form-item>
            <p v-if="error" class="form-error" role="alert">{{ error }}</p>
            <el-form-item>
              <button
                type="submit"
                class="login-btn mt-2 w-full rounded-xl py-3 text-sm font-semibold text-white"
                :disabled="loading"
              >
                {{ loading ? '登录中...' : '登录后台' }}
              </button>
            </el-form-item>
          </el-form>
          <RouterLink to="/" class="inline-link">返回博客首页</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/admin/user'
import { useRouter } from 'vue-router'
import { ref, reactive } from 'vue'
import { showMessage } from '@/composables/util'
import { setToken, removeToken } from '@/composables/cookie'
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
const router = useRouter()
const form = reactive({ username: '', password: '' })
const loading = ref(false),
  error = ref(''),
  formRef = ref(null)
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
async function onSubmit() {
  if (loading.value || !(await formRef.value.validate().catch(() => false))) return
  loading.value = true
  error.value = ''
  try {
    const res = await login(form.username, form.password)
    if (!res.success) {
      error.value = res.message || '登录失败'
      return
    }
    setToken(res.data.token)
    await userStore.setUserInfo()
    if (!userStore.userInfo.roles?.includes('ROLE_ADMIN')) {
      userStore.logout()
      error.value = '此博客仅允许管理员进入后台'
      return
    }
    showMessage('登录成功')
    await router.push('/admin/index')
  } catch {
    removeToken()
    error.value = '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-links {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 24px;
  justify-content: space-between;
}
.form-hint {
  color: var(--personal-muted);
  font-size: 13px;
  line-height: 1.7;
  margin: 8px 0 16px;
}
.form-error {
  color: #b42318;
  font-size: 14px;
  line-height: 1.7;
  margin: 12px 0;
}

.login-wrap {
  min-height: 100vh;
}
.brand {
  background: linear-gradient(120deg, #ffffff, #c4b5fd 50%, #67e8f9);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* 左侧装饰：同心旋转光环 + 呼吸光球 */
.orb-deco {
  position: absolute;
  right: -120px;
  top: 50%;
  width: 520px;
  height: 520px;
  transform: translateY(-50%);
  pointer-events: none;
  z-index: 0;
}
.orb-deco .ring {
  position: absolute;
  inset: 0;
  margin: auto;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.06);
}
.orb-deco .ring-1 {
  width: 520px;
  height: 520px;
  border-top-color: rgba(124, 58, 237, 0.55);
  border-left-color: rgba(34, 211, 238, 0.35);
  animation: orb-spin 42s linear infinite;
}
.orb-deco .ring-2 {
  width: 380px;
  height: 380px;
  border-top-color: rgba(34, 211, 238, 0.55);
  animation: orb-spin 30s linear infinite reverse;
}
.orb-deco .ring-3 {
  width: 250px;
  height: 250px;
  border-right-color: rgba(124, 58, 237, 0.6);
  animation: orb-spin 22s linear infinite;
}
.orb-deco .core {
  position: absolute;
  inset: 0;
  margin: auto;
  width: 130px;
  height: 130px;
  border-radius: 50%;
  background: radial-gradient(
    circle at 32% 30%,
    rgba(124, 58, 237, 0.95),
    rgba(34, 211, 238, 0.5) 58%,
    transparent 74%
  );
  filter: blur(4px);
  animation: orb-pulse 6s ease-in-out infinite;
}
@keyframes orb-spin {
  to {
    transform: rotate(360deg);
  }
}
@keyframes orb-pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.12);
    opacity: 1;
  }
}

/* 功能亮点小圆点 */
.feat-dot {
  width: 8px;
  height: 8px;
  flex: none;
  border-radius: 50%;
  background: linear-gradient(120deg, #7c3aed, #22d3ee);
  box-shadow: 0 0 10px rgba(124, 58, 237, 0.7);
}

/* 技术栈胶囊 */
.stack-chip {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.03);
  color: rgba(255, 255, 255, 0.6);
  border-radius: 9999px;
  padding: 0.35rem 0.8rem;
  font-size: 0.72rem;
  letter-spacing: 0.02em;
}

@media (prefers-reduced-motion: reduce) {
  .orb-deco .ring,
  .orb-deco .core {
    animation: none;
  }
}
.login-btn {
  background: linear-gradient(120deg, #7c3aed, #6366f1 45%, #22d3ee);
  box-shadow: 0 12px 30px -10px rgba(124, 58, 237, 0.7);
  transition:
    transform 0.25s,
    filter 0.25s;
}
.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  filter: brightness(1.08);
}
.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 登录页使用浅色主题，避免继承旧深色输入框的白色文字。 */
.login-card {
  background: var(--personal-paper);
  border-color: var(--personal-line);
  box-shadow: 0 16px 48px rgba(21, 37, 61, 0.1);
}
.brand {
  background: none;
  color: var(--personal-blue);
}
.stack-chip {
  color: var(--personal-muted);
  border-color: var(--personal-line);
  background: var(--personal-paper);
}
.login-card :deep(.el-form-item__label) {
  color: var(--personal-ink);
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}
.login-card :deep(.el-input__wrapper) {
  background-color: var(--personal-paper);
  box-shadow: 0 0 0 1px #8d9fb7 inset;
  border-radius: 8px;
  padding: 4px 14px;
}
.login-card :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--personal-blue) inset;
}
.login-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--personal-blue) inset;
}
.login-card :deep(.el-input__inner) {
  color: var(--personal-ink);
  -webkit-text-fill-color: var(--personal-ink);
  font-size: 16px;
}
.login-card :deep(.el-input__inner::placeholder) {
  color: var(--personal-muted);
  -webkit-text-fill-color: var(--personal-muted);
  opacity: 1;
}
.login-card :deep(.el-input__prefix),
.login-card :deep(.el-input__suffix) {
  color: var(--personal-muted);
}
.login-card :deep(.el-form-item__error) {
  color: #b42318;
}
.login-card .login-btn {
  background: var(--personal-blue);
  color: #fff;
  box-shadow: none;
  cursor: pointer;
}
.login-card .login-btn:hover:not(:disabled) {
  background: #1845a8;
  transform: none;
}
.login-card .login-btn:disabled {
  cursor: not-allowed;
}
</style>
