package com.rookie.web.model.vo.article;

import com.rookie.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

/**
 * @author kiro
 * @ClassName SearchArticlePageListReqVO
 * @date 2026年08月05日
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "文章搜索分页 VO")
public class SearchArticlePageListReqVO extends BasePageQuery {
    @NotBlank(message = "搜索关键字不能为空")
    private String keyword;
}
