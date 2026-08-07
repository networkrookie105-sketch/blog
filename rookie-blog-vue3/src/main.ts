import '@/assets/main.css'
import 'animate.css';
import 'nprogress/nprogress.css'
// Element Plus 暗色主题变量（配合 html.dark 生效）
import 'element-plus/theme-chalk/dark/css-vars.css'
import { createApp } from 'vue'
// 引入全局状态管理 Pinia
import pinia from '@/stores'
import App from '@/App.vue'
// 导入路由
import router from '@/router'
//导入全局路由守卫
import '@/permission'
// 导入 Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 启用 Element Plus 暗色模式（全站深色风格）
document.documentElement.classList.add('dark')

const app = createApp(App)

// 应用路由
app.use(router)
// 应用 Pinia
app.use(pinia)

// 引入图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 全局滚动进场指令 v-reveal：元素进入视口时淡入上移，支持 v-reveal="延迟毫秒"
app.directive('reveal', {
    mounted(el: HTMLElement, binding) {
        el.classList.add('reveal')
        const delay = Number(binding.value) || 0
        if (delay) el.style.transitionDelay = `${delay}ms`
        const io = new IntersectionObserver(
            (entries) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        el.classList.add('in')
                        io.unobserve(el)
                    }
                })
            },
            { threshold: 0.08 }
        )
        io.observe(el)
    },
})

app.mount('#app')
