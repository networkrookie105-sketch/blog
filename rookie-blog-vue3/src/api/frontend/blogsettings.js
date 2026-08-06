import axios from "@/axios";
// 获取博客设置
export function getBlogSettingsDetail() {
  return axios.get("/blog/settings/detail");
}