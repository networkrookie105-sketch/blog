package com.rookie.admin.model.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * @author peishunwu
 * @ClassName PublishArticleReqVO
 * @description: TODO
 * @date 2024年05月28日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "发布文章 VO")
public class PublishArticleReqVO {
    @NotBlank(message = "文章标题不能为空")
    @Length(min = 1, max = 150, message = "文章标题字数需大于 1 小于 150")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String cover;

    private String summary;

    @NotNull(message = "文章分类不能为空")
    private Long categoryId;

    @NotEmpty(message = "文章标签不能为空")
    private List<String> tagIds;

    /**
     * 文章状态：0-草稿，1-已发布（不传默认已发布）
     */
    private Integer status;

    /**
     * 是否置顶（不传默认否）
     */
    private Boolean isTop;
}
