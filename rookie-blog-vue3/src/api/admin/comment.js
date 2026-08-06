import axios from "@/axios";

// 评论分页查询
export function getCommentPageList(data) {
    return axios.post("/admin/comment/list", data)
}

// 评论审核（status: 1-通过, 2-拒绝）
export function updateCommentStatus(id, status) {
    return axios.post("/admin/comment/update/status", { id, status })
}

// 删除评论
export function deleteComment(id) {
    return axios.post("/admin/comment/delete", { id })
}
