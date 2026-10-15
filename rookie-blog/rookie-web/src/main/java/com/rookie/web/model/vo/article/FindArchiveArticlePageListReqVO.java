package com.rookie.web.model.vo.article;

import com.rookie.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author peishunwu
 * @ClassName FindArchiveArticlePageListReqVO
 * @description: 文章回档查询（仅继承分页参数 current/size，通过 @RequestBody 绑定）
 * @date 2025年08月07日
 * @version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "文章归档分页 VO")
public class FindArchiveArticlePageListReqVO extends BasePageQuery {
}
