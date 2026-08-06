package com.rookie.admin.model.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author kiro
 * @ClassName FindCommentPageListRspVO
 * @date 2026年08月05日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCommentPageListRspVO {
    private Long id;
    private Long articleId;
    private Long parentId;
    private String nickname;
    private String email;
    private String website;
    private String content;
    private String replyNickname;
    /** 状态：0-待审核，1-已通过，2-已拒绝 */
    private Integer status;
    private LocalDateTime createTime;
}
