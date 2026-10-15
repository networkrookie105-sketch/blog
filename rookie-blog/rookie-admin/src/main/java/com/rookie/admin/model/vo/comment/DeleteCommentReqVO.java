package com.rookie.admin.model.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author kiro
 * @ClassName DeleteCommentReqVO
 * @date 2026年08月05日
 */
@Data
@Schema(description = "删除评论 VO")
public class DeleteCommentReqVO {
    @NotNull(message = "评论 ID 不能为空")
    private Long id;
}
