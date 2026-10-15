package com.rookie.web.model.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author kiro
 * @ClassName PublishCommentReqVO
 * @date 2026年08月05日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "发表评论 VO")
public class PublishCommentReqVO {

    @NotNull(message = "文章 ID 不能为空")
    private Long articleId;

    /** 父评论 ID（回复时传，一级评论不传） */
    private Long parentId;

    /** 被回复者昵称（楼中楼回复时传） */
    private String replyNickname;

    @NotBlank(message = "昵称不能为空")
    @Length(max = 30, message = "昵称最长 30 个字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String website;

    @NotBlank(message = "评论内容不能为空")
    @Length(max = 500, message = "评论内容最长 500 个字符")
    private String content;
}
