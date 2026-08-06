import axios from "@/axios";

// 发表评论
export function publishComment(data) {
    return axios.post("/comment/publish", data)
}

// 获取某文章的评论列表（楼中楼树形）
export function getCommentList(articleId) {
    return axios.post("/comment/list", { articleId })
}
