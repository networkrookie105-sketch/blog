package com.rookie.admin.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author peishunwu
 * @ClassName FindArticlePageListRspVO
 * @description: TODO
 * @date 2024年06月07日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArticlePageListRspVO {
    private Long id;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章封面
     */
    private String cover;
    /**
     * 文章发布时间
     */
    private LocalDateTime createTime;
    /**
     * 文章状态：0-草稿，1-已发布
     */
    private Integer status;
    /**
     * 是否置顶
     */
    private Boolean isTop;
}
