package com.rookie.admin.model.vo.comment;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author kiro
 * @ClassName UpdateCommentStatusReqVO
 * @date 2026年08月05日
 */
@Data
@ApiModel(value = "评论审核 VO")
public class UpdateCommentStatusReqVO {
    @NotNull(message = "评论 ID 不能为空")
    private Long id;

    /** 目标状态：1-通过，2-拒绝 */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
