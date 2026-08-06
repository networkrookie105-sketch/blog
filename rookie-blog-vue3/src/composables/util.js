import nprogress from "nprogress";

export function showMessage(message = "提示内容", type = "success",customClass = "") {
  return ElMessage({
    type: type,
    message: message,
    customClass,
  });
}
//显示页面加载中
export function showPageLoading() {
  nprogress.start();
}
//隐藏页面加载中
export function hidePageLoading() {
  nprogress.done();
}

// 弹出确认框
export function showModel(content = '提示内容', type = 'warning', title = '') {
    return ElMessageBox.confirm(
        content,
        title,
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type,
        }
    )
}