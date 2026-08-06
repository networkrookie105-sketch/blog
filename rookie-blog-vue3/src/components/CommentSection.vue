<template>
    <section class="mt-6 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-10">
        <h2 class="text-xl font-semibold text-slate-950">
            评论
            <span class="ml-2 text-sm font-normal text-slate-400">{{ totalCount }} 条</span>
        </h2>

        <!-- 评论表单 -->
        <div class="mt-6 rounded-2xl border border-slate-200 bg-slate-50 p-5">
            <div v-if="replyTo.parentId" class="mb-3 flex items-center justify-between text-sm text-slate-500">
                <span>回复 <span class="font-semibold text-slate-800">@{{ replyTo.replyNickname }}</span></span>
                <button type="button" class="text-slate-400 hover:text-slate-700" @click="cancelReply">取消回复</button>
            </div>
            <div class="grid gap-3 md:grid-cols-3">
                <input v-model="form.nickname" type="text" maxlength="30" placeholder="昵称 *"
                    class="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-slate-900" />
                <input v-model="form.email" type="text" placeholder="邮箱（选填）"
                    class="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-slate-900" />
                <input v-model="form.website" type="text" placeholder="网站（选填）"
                    class="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-slate-900" />
            </div>
            <textarea v-model="form.content" rows="4" maxlength="500" placeholder="写下你的评论..."
                class="mt-3 w-full resize-none rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-slate-900"></textarea>
            <div class="mt-3 flex justify-end">
                <button type="button" :disabled="submitting"
                    class="rounded-full bg-slate-950 px-5 py-2 text-sm font-medium text-white transition hover:bg-slate-800 disabled:opacity-50"
                    @click="submit">
                    {{ submitting ? '提交中...' : '发表评论' }}
                </button>
            </div>
        </div>

        <!-- 评论列表 -->
        <div v-if="comments.length" class="mt-8 space-y-6">
            <div v-for="c in comments" :key="c.id" class="border-b border-slate-100 pb-6 last:border-0">
                <div class="flex items-start gap-3">
                    <div class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-slate-900 text-sm font-semibold text-white">
                        {{ (c.nickname || '?').slice(0, 1).toUpperCase() }}
                    </div>
                    <div class="min-w-0 flex-1">
                        <div class="flex items-center gap-2 text-sm">
                            <a v-if="c.website" :href="c.website" target="_blank" rel="nofollow noopener"
                                class="font-semibold text-slate-900 hover:underline">{{ c.nickname }}</a>
                            <span v-else class="font-semibold text-slate-900">{{ c.nickname }}</span>
                            <span class="text-xs text-slate-400">{{ formatDate(c.createTime) }}</span>
                        </div>
                        <p class="mt-1 whitespace-pre-wrap break-words text-sm leading-6 text-slate-700">{{ c.content }}</p>
                        <button type="button" class="mt-1 text-xs text-slate-400 hover:text-slate-700"
                            @click="startReply(c.id, c.nickname)">回复</button>

                        <!-- 楼中楼 -->
                        <div v-if="c.children && c.children.length" class="mt-4 space-y-4 border-l-2 border-slate-100 pl-4">
                            <div v-for="r in c.children" :key="r.id">
                                <div class="flex items-center gap-2 text-sm">
                                    <a v-if="r.website" :href="r.website" target="_blank" rel="nofollow noopener"
                                        class="font-semibold text-slate-900 hover:underline">{{ r.nickname }}</a>
                                    <span v-else class="font-semibold text-slate-900">{{ r.nickname }}</span>
                                    <span v-if="r.replyNickname" class="text-xs text-slate-400">回复 @{{ r.replyNickname }}</span>
                                    <span class="text-xs text-slate-400">{{ formatDate(r.createTime) }}</span>
                                </div>
                                <p class="mt-1 whitespace-pre-wrap break-words text-sm leading-6 text-slate-700">{{ r.content }}</p>
                                <button type="button" class="mt-1 text-xs text-slate-400 hover:text-slate-700"
                                    @click="startReply(c.id, r.nickname)">回复</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <p v-else class="mt-8 text-center text-sm text-slate-400">还没有评论，来抢沙发吧~</p>
    </section>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { getCommentList, publishComment } from '@/api/frontend/comment'
import { showMessage } from '@/composables/util'

const props = defineProps({
    articleId: {
        type: [String, Number],
        required: true,
    },
})

const comments = ref([])
const submitting = ref(false)
const form = reactive({
    nickname: '',
    email: '',
    website: '',
    content: '',
})
const replyTo = reactive({
    parentId: null,
    replyNickname: '',
})

const totalCount = computed(() =>
    comments.value.reduce((sum, c) => sum + 1 + (c.children ? c.children.length : 0), 0)
)

const formatDate = (value) => {
    if (!value) return ''
    return String(value).replace('T', ' ').slice(0, 16)
}

const loadComments = () => {
    if (!props.articleId) return
    getCommentList(props.articleId).then((res) => {
        if (res.success) {
            comments.value = res.data || []
        }
    })
}

const startReply = (parentId, replyNickname) => {
    replyTo.parentId = parentId
    replyTo.replyNickname = replyNickname
}

const cancelReply = () => {
    replyTo.parentId = null
    replyTo.replyNickname = ''
}

const submit = () => {
    if (!form.nickname.trim()) {
        showMessage('请输入昵称', 'warning')
        return
    }
    if (!form.content.trim()) {
        showMessage('请输入评论内容', 'warning')
        return
    }
    submitting.value = true
    publishComment({
        articleId: props.articleId,
        parentId: replyTo.parentId,
        replyNickname: replyTo.replyNickname || null,
        nickname: form.nickname,
        email: form.email || null,
        website: form.website || null,
        content: form.content,
    })
        .then((res) => {
            if (res.success) {
                showMessage('评论成功')
                form.content = ''
                cancelReply()
                loadComments()
            }
        })
        .finally(() => {
            submitting.value = false
        })
}

watch(
    () => props.articleId,
    () => loadComments(),
    { immediate: true }
)
</script>
