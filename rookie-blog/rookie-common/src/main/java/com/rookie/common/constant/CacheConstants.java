package com.rookie.common.constant;

/**
 * 本地缓存名称常量。
 * <p>
 * 统一管理缓存名，避免 {@code @Cacheable}/{@code @CacheEvict} 中出现魔法字符串导致失配。
 *
 * @author kiro
 * @ClassName CacheConstants
 * @date 2026年08月05日
 */
public final class CacheConstants {

    private CacheConstants() {
    }

    /** 前台分类列表 */
    public static final String CATEGORY_LIST = "categoryList";

    /** 前台标签列表 */
    public static final String TAG_LIST = "tagList";

    /** 博客基础设置 */
    public static final String BLOG_SETTINGS = "blogSettings";
}
