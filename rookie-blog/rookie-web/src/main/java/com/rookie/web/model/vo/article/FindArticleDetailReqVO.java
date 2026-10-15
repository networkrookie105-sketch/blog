package com.rookie.web.model.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author peishunwu
 * @ClassName FindArticleDetailReqVO
 * @description: TODO
 * @date 2026年06月07日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "查询文章详情 VO")
public class FindArticleDetailReqVO {
    /**
     * 文章 ID
     */
    private Long articleId;
}
