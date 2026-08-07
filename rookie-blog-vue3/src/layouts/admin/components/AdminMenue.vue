<template>
    <div class="fixed overflow-y-auto h-screen text-white menu-container border-r border-white/10 transition-all duration-300" style="background: #0a0a12" :style="{ width: menuStore.menuWidth }">
        <!-- 顶部 Logo, 指定高度为 64px, 和右边的 Header 头保持一样高 -->
        <div class="flex items-center justify-center h-[64px] border-b border-white/10">
            <span v-if="menuStore.menuWidth == '250px'" class="brand-logo text-xl font-extrabold tracking-wide">ROOKIE ADMIN</span>
            <span v-else class="flex h-9 w-9 items-center justify-center rounded-xl text-sm font-bold text-white" style="background: linear-gradient(120deg, #7c3aed, #22d3ee)">R</span>
        </div>

        <!-- 下方菜单 -->
        <el-menu :default-active="defaultActive" @select="handleSelect" :collapse="isCollapse" :collapse-transition="false">
            <template v-for="(item, index) in menus" :key="index">
                <el-menu-item :index="item.path">
                    <el-icon>
                        <!-- 动态图标 -->
                        <component :is="item.icon"></component>
                    </el-icon>
                    <span>{{ item.name }}</span>
                </el-menu-item>
            </template>
        </el-menu>
</div></template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMenuStore } from '@/stores/menu'

const menuStore = useMenuStore()

const route = useRoute()
const router = useRouter()

// 是否折叠
const isCollapse = computed(() =>  !(menuStore.menuWidth == '250px'))

// 根据路由地址判断哪个菜单被选中
const defaultActive = ref(route.path)

// 菜单选择事件
const handleSelect = (path) => {
    router.push(path)
}

const menus = [
    {
        'name': '仪表盘',
        'icon': 'Monitor',
        'path': '/admin/index'
    },
    {
        'name': '文章管理',
        'icon': 'Document',
        'path': '/admin/article/list',
    },
    {
        'name': '分类管理',
        'icon': 'FolderOpened',
        'path': '/admin/category/list',
    },
    {
        'name': '标签管理',
        'icon': 'PriceTag',
        'path': '/admin/tag/list',
    },
    {
        'name': '评论管理',
        'icon': 'ChatDotSquare',
        'path': '/admin/comment/list',
    },
    {
        'name': '博客设置',
        'icon': 'Setting',
        'path': '/admin/blog/settings',
    },
]
</script>

<style scoped>
.brand-logo {
    background: linear-gradient(120deg, #ffffff, #c4b5fd 50%, #67e8f9);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
}
</style>

<style>
.menu-container .el-menu {
    background-color: transparent;
    border-right: 0;
}

.menu-container .el-sub-menu__title {
    color: rgba(255, 255, 255, 0.75);
}

.menu-container .el-sub-menu__title:hover {
    background-color: #ffffff0d;
}

.menu-container .el-menu-item {
    color: rgba(255, 255, 255, 0.7);
}

.menu-container .el-menu-item:hover {
    background-color: #ffffff0d;
    color: #fff;
}

.menu-container .el-menu-item.is-active {
    background-color: rgba(124, 58, 237, 0.15);
    color: #fff;
}

.menu-container .el-menu-item.is-active:before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 3px;
    height: 100%;
    background: linear-gradient(180deg, #7c3aed, #22d3ee);
}
</style>