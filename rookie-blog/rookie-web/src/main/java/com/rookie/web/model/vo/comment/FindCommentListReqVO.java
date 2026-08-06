package com.rookie.web.model.vo.comment;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author kiro
 * @ClassName FindCommentListReqVO
 * @date 2026年08月05日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询文章评论 VO")
public class FindCommentListReqVO {
    @NotNull(message = "文章 ID 不能为空")
    private Long articleId;
}
