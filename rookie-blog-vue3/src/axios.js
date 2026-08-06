import axios from "axios";
import { getToken,removeToken  } from "@/composables/cookie";
import { showMessage} from '@/composables/util'
import { useUserStore } from '@/stores/user'
//创建axios实例
const instance  = axios.create({
    // baseURL: "http://localhost:8080", // api的base_url
    baseURL: "/api", // api的base_url
    timeout: 5000 // 请求超时时间
});
// 添加请求拦截器
instance.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    const token = getToken()
    console.log('统一添加请求头中的 Token:' + token)

    // 当 token 不为空时
    if (token) {
        // 添加请求头, key 为 Authorization，value 值的前缀为 'Bearer '
        config.headers['Authorization'] = 'Bearer ' + token
    }

    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error)
});

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数
    const data = response.data
    // 统一处理业务失败：后端约定 HTTP 200 + { success:false, message }
    if (data && data.success === false) {
        showMessage(data.message || '请求失败', 'error')
    }
    return data;
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数
    // 注意：网络错误 / 后端未启动 / 跨域失败时 error.response 可能为 undefined，需判空
    const response = error.response
    if (response && response.status === 401) {
        // 未授权：退出登录并刷新页面
        let userStore = useUserStore()
        userStore.logout()
        location.reload()
        return Promise.reject(error)
    }

    // 优先展示后端返回的业务错误信息，其次是 HTTP 错误，最后兜底为网络异常
    let errorMsg
    if (response && response.data && response.data.message) {
        errorMsg = response.data.message
    } else if (response && response.status) {
        errorMsg = `请求失败（${response.status}）`
    } else {
        errorMsg = (error.message || '网络异常') + ' - 无法连接服务器'
    }
    showMessage(errorMsg, 'error')

    return Promise.reject(error)
});

//暴漏出去
export default instance;