import { useCookies } from '@vueuse/integrations/useCookies'

const cookie = useCookies()

//=========================================================Token令牌操作=========================================================
//存储在Cookie中的token名称的key
const TOKEN_KEY = 'Authorization'
//获取token
export function getToken() {
  return cookie.get(TOKEN_KEY)
}
//设置token到cookie中
export function setToken(token) {
  return cookie.set(TOKEN_KEY, token)
}
//删除cookie中的token
export function removeToken() {
  return cookie.remove(TOKEN_KEY)
}

// ======================================================= 标签页 =======================================================

// 存储在 Cookie 中的标签页数据的 key
const TAB_LIST_KEY = 'tabList'

// 获取 TabList
export function getTabList() {
    return cookie.get(TAB_LIST_KEY)
}

// 存储 TabList 到 Cookie 中
export function setTabList(tabList) {
    return cookie.set(TAB_LIST_KEY, tabList)
}