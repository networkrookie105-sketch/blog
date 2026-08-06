import Index from '@/pages/frontend/index.vue'
import ArchiveList from '@/pages/frontend/archive-list.vue'
import CategoryList from '@/pages/frontend/category-list.vue'
import CategoryArticleList from '@/pages/frontend/category-article-list.vue'
import TagList from '@/pages/frontend/tag-list.vue'
import TagArticleList from '@/pages/frontend/tag-article-list.vue'
import ArticleDetail from '@/pages/frontend/article-detail.vue'
import SearchResult from '@/pages/frontend/search-result.vue'
import Login from '@/pages/admin/login.vue'
import AdminIndex from '@/pages/admin/index.vue'
import AdminArticleList from '@/pages/admin/article-list.vue'
import AdminCategoryList from '@/pages/admin/category-list.vue'
import AdminTagList from '@/pages/admin/tag-list.vue'
import AdminBlogSettings from '@/pages/admin/blog-settings.vue'
import AdminCommentList from '@/pages/admin/comment-list.vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import Admin from '@/layouts/admin/admin.vue'

const routes = [
    {
        path: '/',
        component: Index,
        meta: {
            title: '首页'
        }
    },
    {
        path: '/archive/list',
        component: ArchiveList,
        meta: {
            title: '归档'
        }
    },
    {
        path: '/category/list',
        component: CategoryList,
        meta: {
            title: '分类'
        }
    },
    {
        path: '/tag/list',
        component: TagList,
        meta: {
            title: '标签'
        }
    },
    {
        path: '/tag/article/list',
        component: TagArticleList,
        meta: {
            title: '标签文章'
        }
    },
    {
        path: '/category/article/list',
        component: CategoryArticleList,
        meta: {
            title: '分类文章'
        }
    },
    {
        path: '/article/detail/:id',
        component: ArticleDetail,
        meta: {
            title: '文章详情'
        }
    },
    {
        path: '/search',
        component: SearchResult,
        meta: {
            title: '搜索'
        }
    },
    {
        path: '/login',
        component: Login,
        meta: {
            title: '登录'
        }
    },
    {
        path: '/admin',
        component: Admin,
        children: [
            {
                path: '/admin/index',
                component: AdminIndex,
                meta: {
                    title: '仪表盘'
                }
            },
            {
                path: '/admin/article/list',
                component: AdminArticleList,
                meta: {
                    title: '文章管理'
                }
            },
            {
                path: '/admin/category/list',
                component: AdminCategoryList,
                meta: {
                    title: '分类管理'
                }
            },
            {
                path: '/admin/tag/list',
                component: AdminTagList,
                meta: {
                    title: '标签管理'
                }
            },
            {
                path: '/admin/comment/list',
                component: AdminCommentList,
                meta: {
                    title: '评论管理'
                }
            },
            {
                path: '/admin/blog/settings',
                component: AdminBlogSettings,
                meta: {
                    title: '博客设置'
                }
            },
        ]
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes,
})

export default router
