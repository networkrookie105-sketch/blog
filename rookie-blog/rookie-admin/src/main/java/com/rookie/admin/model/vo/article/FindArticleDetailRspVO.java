package com.rookie.admin.model.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author peishunwu
 * @ClassName FindArticleDetailRspVO
 * @description: TODO
 * @date 2024年06月11日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "查询文章详情响应VO")
public class FindArticleDetailRspVO {
    private Long id;

    private String title;

    private String cover;

    private String content;

    private Long categoryId;

    private List<Long> tagIds;

    private String summary;

    /**
     * 文章状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 是否置顶
     */
    private Boolean isTop;
}
