package com.rookie.web.model.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author kiro
 * @ClassName FindCommentListRspVO
 * @date 2026年08月05日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "文章评论响应 VO")
public class FindCommentListRspVO {
    private Long id;
    private String nickname;
    private String website;
    private String content;
    private String replyNickname;
    private LocalDateTime createTime;
    /** 楼中楼回复列表 */
    private List<FindCommentListRspVO> children;
}
