import axios from "@/axios";

// 获取数据看板统计信息
export function getDashboardStatistics() {
    return axios.get("/admin/dashboard/statistics")
}
