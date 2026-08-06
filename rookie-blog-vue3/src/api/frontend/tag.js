import axios from "@/axios";

// 获取标签列表
export function getTagList(data) {
    return axios.post("/tag/list", data)
}
// 获取标签下的文章列表
export function getTagArticlePageList(data) {
    return axios.post("/tag/article/list", data)
}