<template>
    <div class="login-wrap site-dark">
        <div class="aurora"><div class="grid"></div></div>

        <div class="content-layer grid min-h-screen grid-cols-1 md:grid-cols-2">
            <!-- 左侧品牌区 -->
            <div class="relative order-2 hidden flex-col items-center justify-center p-10 md:order-1 md:flex">
                <div class="animate__animated animate__fadeInLeft animate__fast flex flex-col items-center text-center">
                    <h2 class="mb-5 text-4xl font-extrabold">
                        <span class="brand">ROOKIE BLOG</span>
                    </h2>
                    <p class="max-w-sm text-sm leading-7 text-white/50">
                        一款由 Spring Boot + MyBatis Plus + Vue 3.2 + Vite 4 打造的前后端分离博客系统。
                    </p>
                    <img src="@/assets/rookie.png" class="mt-8 w-1/2 opacity-90 drop-shadow-[0_10px_40px_rgba(124,58,237,0.4)]" />
                </div>
            </div>

            <!-- 右侧登录表单 -->
            <div class="order-1 flex items-center justify-center p-6 md:order-2">
                <div class="login-card animate__animated animate__fadeInUp animate__fast w-full max-w-md rounded-[24px] border border-white/10 bg-white/[0.04] p-8 shadow-2xl shadow-black/40 backdrop-blur-xl md:p-10">
                    <h1 class="text-3xl font-extrabold text-white">欢迎回来</h1>
                    <div class="mb-8 mt-3 flex items-center gap-3 text-sm text-white/40">
                        <span class="h-px flex-1 bg-white/10"></span>
                        账号密码登录
                        <span class="h-px flex-1 bg-white/10"></span>
                    </div>

                    <el-form ref="formRef" :rules="rules" :model="form">
                        <el-form-item prop="username">
                            <el-input size="large" v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" clearable />
                        </el-form-item>
                        <el-form-item prop="password">
                            <el-input size="large" type="password" v-model="form.password" placeholder="请输入密码" :prefix-icon="Lock" clearable show-password />
                        </el-form-item>
                        <el-form-item>
                            <button
                                type="button"
                                class="login-btn mt-2 w-full rounded-xl py-3 text-sm font-semibold text-white"
                                :disabled="loading"
                                @click="onSubmit"
                            >
                                {{ loading ? '登录中...' : '登 录' }}
                            </button>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/admin/user'
import { useRouter } from "vue-router";
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { showMessage } from '@/composables/util'
import { setToken } from '@/composables/cookie'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const form = reactive({
    username: '',
    password: ''
})
const router = useRouter()
const loading = ref(false)
const formRef = ref(null)
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
    ]
}
const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) {
            return false
        }
        loading.value = true
        login(form.username, form.password).then((res) => {
            if (res.success == true) {
                showMessage('登录成功')
                let token = res.data.token
                setToken(token)
                userStore.setUserInfo()
                router.push('/admin/index')
            } else {
                showMessage(res.message, 'error')
            }
        })
        .finally(() => {
            loading.value = false
        })
    })
}
function onKeyUp(e) {
    if (e.key == "Enter") {
        onSubmit()
    }
}
onMounted(() => {
    document.addEventListener('keyup', onKeyUp)
})
onBeforeUnmount(() => {
    document.removeEventListener('keyup', onKeyUp)
})
</script>

<style scoped>
.login-wrap {
    min-height: 100vh;
}
.brand {
    background: linear-gradient(120deg, #ffffff, #c4b5fd 50%, #67e8f9);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
}
.login-btn {
    background: linear-gradient(120deg, #7c3aed, #6366f1 45%, #22d3ee);
    box-shadow: 0 12px 30px -10px rgba(124, 58, 237, 0.7);
    transition: transform 0.25s, filter 0.25s;
}
.login-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    filter: brightness(1.08);
}
.login-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

/* Element Plus 输入框深色覆盖 */
.login-card :deep(.el-input__wrapper) {
    background-color: rgba(255, 255, 255, 0.05);
    box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.12) inset;
    border-radius: 12px;
}
.login-card :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.6) inset;
}
.login-card :deep(.el-input__inner) {
    color: #fff;
}
.login-card :deep(.el-input__inner::placeholder) {
    color: rgba(255, 255, 255, 0.4);
}
.login-card :deep(.el-input__prefix),
.login-card :deep(.el-input__suffix) {
    color: rgba(255, 255, 255, 0.5);
}
</style>
