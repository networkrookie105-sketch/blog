<template>
    <div v-loading="loading">
        <!-- 统计卡片 -->
        <el-row :gutter="20">
            <el-col :xs="12" :sm="12" :md="8" :lg="rsp" v-for="card in cards" :key="card.label" class="mb-5">
                <el-card shadow="hover" class="stat-card">
                    <div class="flex items-center">
                        <div class="stat-icon" :style="{ backgroundColor: card.color }">
                            <el-icon :size="24" color="#fff">
                                <component :is="card.icon" />
                            </el-icon>
                        </div>
                        <div class="ml-4">
                            <div class="text-2xl font-bold text-slate-800">{{ card.value }}</div>
                            <div class="text-sm text-slate-400 mt-1">{{ card.label }}</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 近 7 天发布趋势 -->
        <el-card shadow="never" class="mt-2">
            <template #header>
                <span class="font-semibold">近 7 天文章发布趋势</span>
            </template>
            <div class="trend-chart">
                <div v-for="(date, i) in stat.trendDates" :key="date" class="trend-col">
                    <span class="trend-count">{{ stat.trendCounts[i] }}</span>
                    <div class="trend-bar-wrap">
                        <div class="trend-bar" :style="{ height: barHeight(stat.trendCounts[i]) }"></div>
                    </div>
                    <span class="trend-date">{{ date.slice(5) }}</span>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { getDashboardStatistics } from '@/api/admin/dashboard'

const loading = ref(false)
const rsp = ref(4) // 5 张卡片，lg 下每张约占 24/5，用近似值
const stat = reactive({
    articleCount: 0,
    categoryCount: 0,
    tagCount: 0,
    commentCount: 0,
    totalReadNum: 0,
    trendDates: [],
    trendCounts: [],
})

const cards = computed(() => [
    { label: '文章总数', value: stat.articleCount, icon: 'Document', color: '#409eff' },
    { label: '分类总数', value: stat.categoryCount, icon: 'FolderOpened', color: '#67c23a' },
    { label: '标签总数', value: stat.tagCount, icon: 'PriceTag', color: '#e6a23c' },
    { label: '评论总数', value: stat.commentCount, icon: 'ChatDotSquare', color: '#f56c6c' },
    { label: '总阅读量', value: stat.totalReadNum, icon: 'View', color: '#909399' },
])

const maxCount = computed(() => {
    const max = Math.max(...(stat.trendCounts.length ? stat.trendCounts : [0]))
    return max > 0 ? max : 1
})

const barHeight = (count) => {
    const ratio = (count || 0) / maxCount.value
    // 最小 4px，最高 160px
    return Math.max(4, Math.round(ratio * 160)) + 'px'
}

const loadStatistics = () => {
    loading.value = true
    getDashboardStatistics()
        .then((res) => {
            if (res.success && res.data) {
                Object.assign(stat, res.data)
            }
        })
        .finally(() => {
            loading.value = false
        })
}
loadStatistics()
</script>

<style scoped>
.stat-card :deep(.el-card__body) {
    padding: 20px;
}
.stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
}
.trend-chart {
    display: flex;
    align-items: flex-end;
    justify-content: space-around;
    height: 220px;
    padding: 10px 0;
}
.trend-col {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
}
.trend-count {
    font-size: 12px;
    color: #909399;
    margin-bottom: 6px;
}
.trend-bar-wrap {
    display: flex;
    align-items: flex-end;
    height: 160px;
}
.trend-bar {
    width: 32px;
    border-radius: 6px 6px 0 0;
    background: linear-gradient(180deg, #409eff 0%, #79bbff 100%);
    transition: height 0.4s ease;
}
.trend-date {
    font-size: 12px;
    color: #909399;
    margin-top: 8px;
}
</style>
