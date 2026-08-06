package com.rookie.common.model;

import lombok.Data;

/**
 * @author peishunwu
 * @ClassName BasePageQuery
 * @description: TODO
 * @date 2024年04月24日
 * @version: 1.0
 */
@Data
public class BasePageQuery {
    /**
     * 当前页码, 默认第一页
     */
    private Long current = 1L;
    /**
     * 每页展示的数据数量，默认每页展示 10 条数据
     */
    private Long size = 10L;
}
