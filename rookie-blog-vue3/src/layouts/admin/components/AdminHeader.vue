<template>
    <el-affix :offset="0">
            <!-- 通过flex指定水平布局 --><!-- 设置背景色为白色、高度为 64px，padding-right 为 4， border-bottom 为 slate 200 -->
        <div class="h-[64px] flex pr-4 border-b border-white/10 backdrop-blur-xl" style="background: rgba(12,12,21,0.8)">
            <!-- 左边栏收缩、展开 -->
            <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center text-white/70 hover:bg-white/10 hover:text-white" @click="handleMenuWidth">
                <el-icon>
                    <Fold v-if="menuStore.menuWidth == '250px'"/>
                    <Expand v-else />
                </el-icon>
            </div>
            <!-- 右边容器，通过ml-auto 让其在父容器的右边 -->
            <div class="ml-auto flex">
                <!-- 点击刷新页面 -->
                <el-tooltip class="box-item" effect="dark" content="刷新" placement="bottom">
                    <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center text-white/70 hover:bg-white/10 hover:text-white"
                        @click="handleRefresh">
                        <el-icon>
                            <Refresh />
                        </el-icon>
                    </div>
                </el-tooltip>
                <el-tooltip class="box-item" effect="dark" content="全屏" placement="bottom">
                    <!-- 点击全屏展示 -->
                    <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center text-white/70 mr-2 hover:bg-white/10 hover:text-white" @click="toggle">
                        <el-icon>
                            <FullScreen v-if="!isFullscreen"/>
                            <Aim v-else/>
                        </el-icon>
                    </div>
                </el-tooltip>
                
                 <!-- 登录用户头像 -->
                 <el-dropdown class="flex items-center justify-center" @command="handleCommand">
                    <span class="el-dropdown-link flex items-center justify-center text-white/80 text-xs">
                        <!-- 头像 Avatar -->
                        <el-avatar :size="25" src="https://pics1.baidu.com/feed/e1fe9925bc315c60e30f31f30f8b1818485477de.jpeg@f_auto?token=eef4f0c8d51019e4035466504f1f0ced" />
                        {{ userStore.userInfo.username }}
                        <el-icon class="el-icon--right">
                            <arrow-down />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="updatePassword" @click="updateAdminPasswordBtnClick">修改密码</el-dropdown-item>
                            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                 </el-dropdown>
            </div>
            
        </div>
        <!-- 修改密码 -->
        <!-- <el-dialog v-model="dialogVisible" title="修改密码" width="40%" :draggable ="true" :close-on-click-modal="false" :close-on-press-escape="false">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
                <el-form-item label="用户名" prop="username" label-width="120px">
                            <el-input size="large" v-model="form.username" placeholder="请输入用户名" clearable disabled />
                </el-form-item>
                <el-form-item label="密码" prop="password" label-width="120px">
                            <el-input size="large" type="password" v-model="form.password" placeholder="请输入密码"
                                 clearable show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="rePassword" label-width="120px">
                            <el-input size="large" type="password" v-model="form.rePassword" placeholder="请确认密码"
                                 clearable show-password />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary"  @click="onSubmit">
                        提交
                    </el-button>
                </span>
            </template>
        </el-dialog> -->
        <FormDialog ref="formDialogRef" title="修改密码" destroyOnClose @submit="onSubmit">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
                <el-form-item label="用户名" prop="username" label-width="120px">
                            <el-input size="large" v-model="form.username" placeholder="请输入用户名" clearable disabled />
                </el-form-item>
                <el-form-item label="密码" prop="password" label-width="120px">
                            <el-input size="large" type="password" v-model="form.password" placeholder="请输入密码"
                                 clearable show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="rePassword" label-width="120px">
                            <el-input size="large" type="password" v-model="form.rePassword" placeholder="请确认密码"
                                 clearable show-password />
                </el-form-item>
            </el-form>
        </FormDialog>
    </el-affix>
</template>

<script setup>
import { useMenuStore } from '@/stores/menu'
 // 引入 useFullscreen
import { useFullscreen } from '@vueuse/core'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { showModel } from '@/composables/util'
import { showMessage} from '@/composables/util'
import { ref,reactive,watch } from 'vue'
import { updateAdminPassword } from '@/api/admin/user'
import FormDialog from '@/components/FormDialog.vue'
 // 引入了用户 Store
 const userStore = useUserStore()
 const router = useRouter()

// isFullscreen 表示当前是否处于全屏；toggle 用于动态切换全屏、非全屏
const { isFullscreen, toggle } = useFullscreen()

// 刷新页面
const handleRefresh = () => location.reload()

// 引入了菜单
const menuStore = useMenuStore()

// icon 点击事件
const handleMenuWidth = () => {
    menuStore.handleMenuWidth()
}
// 对话框是否显示
const dialogVisible = ref(false)
// 表单引用
const formRef = ref(null)
// 修改用户密码表单对象
const form = reactive({
        username: userStore.userInfo.username || '',
        password: '',
        rePassword: ''
    })
// 规则校验
const rules = {
    username: [
        {
            required: true,
            message: '用户名不能为空',
            trigger: 'blur'
        }
    ],
    password: [
        {
            required: true,
            message: '密码不能为空',
            trigger: 'blur',
        },
    ],
    rePassword: [
        {
            required: true,
            message: '确认密码不能为空',
            trigger: 'blur',
        },
    ]
}
//下拉菜单事件处理
const handleCommand = (command) => {
    if (command === 'updatePassword') {
        // 修改密码
        // 显示修改密码对话框
        dialogVisible.value = true
    } else if (command === 'logout') {
        // 退出登录
        logout()
    }
}

// 退出登录
function logout() {
    showModel('是否确认要退出登录？').then(() => {
        userStore.logout()
        showMessage('退出登录成功！')
        // 跳转登录页
        router.push('/login')
    })
}
const formDialogRef = ref(null)
// 修改密码钮点击事件
const updateAdminPasswordBtnClick = () => {
    formDialogRef.value.open()
}
const onSubmit = () => {
    // 先验证 form 表单字段
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('表单验证不通过')
            return false
        }
        if (form.password !== form.rePassword) {
            showMessage('两次密码输入不一致', 'error')
            return false
        }
        // 显示提交按钮 loading
        formDialogRef.value.showBtnLoading()
        // 调用登录接口
        updateAdminPassword(form).then((res) => {
            console.log(res)
            // 判断是否成功
            if (res.success == true) {
                // 提示登录成功
                showMessage('密码重置成功，请重新登录！')
                //退出登录
                userStore.logout()
                // 关闭对话框
                formDialogRef.value.close()
                // 跳转登录页
                router.push('/login')
            } else {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示消息
                showMessage(message, 'error')
            }
        }).finally(() => {
            // 隐藏提交按钮 loading
            formDialogRef.value.closeBtnLoading()
        })
    })
}
// 监听用户信息变化
watch(() => userStore.userInfo.username, (newValue,oldValue) => {
    // 在这里处理变化后的值
    console.log('新值:', newValue);
    console.log('旧值:', oldValue);
    // 可以在这里执行任何你需要的逻辑
      // 重新将新的值，设置会 form 对象中
    form.username = newValue
})
</script>