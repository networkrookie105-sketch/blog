<template>
    <section class="mt-6 rounded-[24px] border border-white/10 bg-white/[0.04] p-6 shadow-xl shadow-black/20 backdrop-blur-xl md:p-10">
        <h2 class="text-xl font-bold text-white">
            评论
            <span class="ml-2 text-sm font-normal text-white/40">{{ totalCount }} 条</span>
        </h2>

        <!-- 评论表单 -->
        <div class="mt-6 rounded-2xl border border-white/10 bg-white/[0.03] p-5">
            <div v-if="replyTo.parentId" class="mb-3 flex items-center justify-between text-sm text-white/50">
                <span>回复 <span class="font-semibold text-cyan-300">@{{ replyTo.replyNickname }}</span></span>
                <button type="button" class="text-white/40 transition hover:text-white" @click="cancelReply">取消回复</button>
            </div>
            <div class="grid gap-3 md:grid-cols-3">
                <input v-model="form.nickname" type="text" maxlength="30" placeholder="昵称 *"
                    class="comment-input" />
                <input v-model="form.email" type="text" placeholder="邮箱（选填）"
                    class="comment-input" />
                <input v-model="form.website" type="text" placeholder="网站（选填）"
                    class="comment-input" />
            </div>
            <textarea v-model="form.content" rows="4" maxlength="500" placeholder="写下你的评论..."
                class="comment-input mt-3 w-full resize-none"></textarea>
            <div class="mt-3 flex justify-end">
                <button type="button" :disabled="submitting"
                    class="btn-submit rounded-full px-5 py-2 text-sm font-medium text-white disabled:opacity-50"
                    @click="submit">
                    {{ submitting ? '提交中...' : '发表评论' }}
                </button>
            </div>
        </div>

        <!-- 评论列表 -->
        <div v-if="comments.length" class="mt-8 space-y-6">
            <div v-for="c in comments" :key="c.id" class="border-b border-white/5 pb-6 last:border-0">
                <div class="flex items-start gap-3">
                    <div class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-sm font-semibold text-white"
                        style="background: linear-gradient(120deg, #7c3aed, #22d3ee)">
                        {{ (c.nickname || '?').slice(0, 1).toUpperCase() }}
                    </div>
                    <div class="min-w-0 flex-1">
                        <div class="flex items-center gap-2 text-sm">
                            <a v-if="c.website" :href="c.website" target="_blank" rel="nofollow noopener"
                                class="font-semibold text-white hover:text-cyan-300">{{ c.nickname }}</a>
                            <span v-else class="font-semibold text-white">{{ c.nickname }}</span>
                            <span class="text-xs text-white/35">{{ formatDate(c.createTime) }}</span>
                        </div>
                        <p class="mt-1 whitespace-pre-wrap break-words text-sm leading-6 text-white/70">{{ c.content }}</p>
                        <button type="button" class="mt-1 text-xs text-white/40 transition hover:text-cyan-300"
                            @click="startReply(c.id, c.nickname)">回复</button>

                        <!-- 楼中楼 -->
                        <div v-if="c.children && c.children.length" class="mt-4 space-y-4 border-l-2 border-white/10 pl-4">
                            <div v-for="r in c.children" :key="r.id">
                                <div class="flex items-center gap-2 text-sm">
                                    <a v-if="r.website" :href="r.website" target="_blank" rel="nofollow noopener"
                                        class="font-semibold text-white hover:text-cyan-300">{{ r.nickname }}</a>
                                    <span v-else class="font-semibold text-white">{{ r.nickname }}</span>
                                    <span v-if="r.replyNickname" class="text-xs text-white/35">回复 <span class="text-cyan-300/80">@{{ r.replyNickname }}</span></span>
                                    <span class="text-xs text-white/35">{{ formatDate(r.createTime) }}</span>
                                </div>
                                <p class="mt-1 whitespace-pre-wrap break-words text-sm leading-6 text-white/70">{{ r.content }}</p>
                                <button type="button" class="mt-1 text-xs text-white/40 transition hover:text-cyan-300"
                                    @click="startReply(c.id, r.nickname)">回复</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <p v-else class="mt-8 text-center text-sm text-white/40">还没有评论，来抢沙发吧~</p>
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

<style scoped>
.comment-input {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(255, 255, 255, 0.04);
    color: #f4f4f5;
    border-radius: 12px;
    padding: 0.5rem 0.75rem;
    font-size: 0.875rem;
    outline: none;
    transition: border-color 0.25s, background 0.25s;
}
.comment-input::placeholder {
    color: rgba(255, 255, 255, 0.35);
}
.comment-input:focus {
    border-color: rgba(34, 211, 238, 0.6);
    background: rgba(255, 255, 255, 0.06);
}
.btn-submit {
    background: linear-gradient(120deg, #7c3aed, #6366f1 45%, #22d3ee);
    box-shadow: 0 10px 30px -10px rgba(124, 58, 237, 0.7);
    transition: transform 0.25s, filter 0.25s;
}
.btn-submit:hover:not(:disabled) {
    transform: translateY(-2px);
    filter: brightness(1.08);
}
</style>
