package com.rookie.common.enums;

import com.rookie.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author peishunwu
 * @ClassName ResponseCodeEnum
 * @description: TODO
 * @date 2023年11月25日
 * @version: 1.0
 */
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {
    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),
    // ----------- 业务异常状态码 -----------
    PRODUCT_NOT_FOUND("20000", "该产品不存在（测试使用）"),

    PARAM_NOT_VALID("10001","参数异常"),
    LOGIN_FAIL("20000", "登录失败"),
    USERNAME_OR_PWD_ERROR("20001", "用户名或密码错误"),
    UNAUTHORIZED("20002", "无访问权限，请先登录！"),
    FORBIDDEN("20004", "演示账号仅支持查询操作！"),

    USERNAME_NOT_FOUND("20003", "用户名不存在"),

    CATEGORY_NAME_IS_EXISTED("20005", "分类名称已存在"),

    TAG_NAME_IS_EXISTED("20006", "标签名称已存在"),

    FILE_UPLOAD_FAILED("20008", "文件上传失败！"),

    CATEGORY_NOT_EXISTED("20009", "分类不存在"),
    ARTICLE_NOT_FOUND("20010", "该文章不存在！"),

    CATEGORY_CAN_NOT_DELETE("20011", "该分类下包含文章，请先删除对应文章，才能删除！"),

    TAG_CAN_NOT_DELETE("20012", "该标签下包含文章，请先删除对应文章，才能删除！"),

    TAG_NOT_EXISTED("20012", "该标签不存在！"),

    COMMENT_PARENT_NOT_EXISTED("20013", "回复的评论不存在！"),

    COMMENT_NOT_EXISTED("20014", "该评论不存在！"),


    ;

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;
}
