<template>
    <div>
        <!-- 查询条件 -->
        <el-card shadow="never" class="mb-5">
            <div class="flex items-center">
                <el-text>关键字</el-text>
                <div class="ml-3 w-52 mr-5">
                    <el-input v-model="keyword" placeholder="昵称/内容（模糊）" clearable />
                </div>
                <el-text>状态</el-text>
                <div class="ml-3 w-40 mr-5">
                    <el-select v-model="status" placeholder="全部" clearable>
                        <el-option label="待审核" :value="0" />
                        <el-option label="已通过" :value="1" />
                        <el-option label="已拒绝" :value="2" />
                    </el-select>
                </div>
                <el-button type="primary" :icon="Search" @click="getTableData">查询</el-button>
                <el-button :icon="RefreshRight" @click="reset">重置</el-button>
            </div>
        </el-card>

        <el-card shadow="never">
            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="nickname" label="昵称" width="140" />
                <el-table-column prop="content" label="内容" min-width="280" show-overflow-tooltip />
                <el-table-column label="回复对象" width="140">
                    <template #default="scope">
                        <span v-if="scope.row.replyNickname">@{{ scope.row.replyNickname }}</span>
                        <span v-else class="text-gray-400">-</span>
                    </template>
                </el-table-column>
                <el-table-column prop="articleId" label="文章ID" width="90" />
                <el-table-column label="状态" width="100">
                    <template #default="scope">
                        <el-tag v-if="scope.row.status === 1" type="success">已通过</el-tag>
                        <el-tag v-else-if="scope.row.status === 0" type="warning">待审核</el-tag>
                        <el-tag v-else type="info">已拒绝</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="时间" width="180" />
                <el-table-column label="操作" width="240">
                    <template #default="scope">
                        <el-button v-if="scope.row.status !== 1" size="small" type="success"
                            @click="review(scope.row, 1)">通过</el-button>
                        <el-button v-if="scope.row.status !== 2" size="small" type="warning"
                            @click="review(scope.row, 2)">拒绝</el-button>
                        <el-button size="small" type="danger" @click="remove(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="mt-10 flex justify-center">
                <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[10, 20, 50]"
                    :background="true" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="getTableData" />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { ref } from 'vue'
import { getCommentPageList, updateCommentStatus, deleteComment } from '@/api/admin/comment'
import { showMessage, showModel } from '@/composables/util'

const keyword = ref('')
const status = ref(null)
const tableLoading = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)

function getTableData() {
    tableLoading.value = true
    getCommentPageList({ current: current.value, size: size.value, keyword: keyword.value, status: status.value })
        .then((res) => {
            if (res.success) {
                tableData.value = res.data
                current.value = res.current
                size.value = res.size
                total.value = res.total
            }
        })
        .finally(() => {
            tableLoading.value = false
        })
}
getTableData()

const handleSizeChange = (chooseSize) => {
    size.value = chooseSize
    getTableData()
}

const reset = () => {
    keyword.value = ''
    status.value = null
    current.value = 1
    getTableData()
}

const review = (row, targetStatus) => {
    updateCommentStatus(row.id, targetStatus).then((res) => {
        if (res.success) {
            showMessage('操作成功')
            getTableData()
        }
    })
}

const remove = (row) => {
    showModel('是否确认删除该评论（含其回复）？').then(() => {
        deleteComment(row.id).then((res) => {
            if (res.success) {
                showMessage('删除成功')
                getTableData()
            }
        })
    }).catch(() => {
        showMessage('取消删除')
    })
}
</script>
