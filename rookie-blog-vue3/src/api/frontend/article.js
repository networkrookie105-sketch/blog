import axios from "@/axios";

// 获取文章列表
export function getArticlePageList(data) {
    return axios.post("/article/list", data)
}

export function getArticleDetail(articleId) {
    return axios.post("/detail", { articleId })
}

// 全文搜索文章
export function searchArticles(data) {
    return axios.post("/search", data)
}
