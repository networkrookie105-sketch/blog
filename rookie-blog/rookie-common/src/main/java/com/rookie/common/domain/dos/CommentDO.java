package com.rookie.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论实体。
 * <p>
 * 采用「一级评论 + 楼中楼回复」模型：{@code parentId} 为空表示一级评论，
 * 非空则指向其所属的一级评论，{@code replyNickname} 记录被 @ 回复者昵称。
 *
 * @author kiro
 * @ClassName CommentDO
 * @date 2026年08月05日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_comment")
public class CommentDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属文章 ID */
    private Long articleId;

    /** 父评论 ID：null 表示一级评论，非 null 指向所属的一级评论 */
    private Long parentId;

    /** 被回复者昵称（楼中楼 @ 用） */
    private String replyNickname;

    /** 评论者昵称 */
    private String nickname;

    /** 评论者邮箱（可选） */
    private String email;

    /** 评论者网站（可选） */
    private String website;

    /** 评论内容 */
    private String content;

    /** 状态：0-待审核，1-已通过，2-已拒绝 */
    private Integer status;

    private LocalDateTime createTime;

    private Boolean isDeleted;
}
