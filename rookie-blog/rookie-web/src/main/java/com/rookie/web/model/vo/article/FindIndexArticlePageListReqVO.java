package com.rookie.web.model.vo.article;

import com.rookie.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author peishunwu
 * @ClassName FindIndexArticlePageListReqVO
 * @description: TODO
 * @date 2024年08月31日
 * @version: 1.0
 */
@Data
@Builder
@Schema(description = "首页查询文章分页 VO")
public class FindIndexArticlePageListReqVO extends BasePageQuery {
}
