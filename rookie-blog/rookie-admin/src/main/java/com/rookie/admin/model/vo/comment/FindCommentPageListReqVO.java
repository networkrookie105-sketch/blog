package com.rookie.admin.model.vo.comment;

import com.rookie.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kiro
 * @ClassName FindCommentPageListReqVO
 * @date 2026年08月05日
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "后台评论分页查询 VO")
public class FindCommentPageListReqVO extends BasePageQuery {
    /** 状态：0-待审核，1-已通过，2-已拒绝（可空，查全部） */
    private Integer status;
    /** 文章 ID（可空） */
    private Long articleId;
    /** 昵称或内容关键字（可空） */
    private String keyword;
}
