package com.rookie.admin.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author peishunwu
 * @ClassName UpdateArticleReqVO
 * @description: TODO
 * @date 2024年06月13日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "更新文章请求VO")
public class UpdateArticleReqVO {
    @NotNull(message = "文章id不能为空")
    private Long id;

    @NotNull(message = "文章标题不能为空")
    @Length(max = 50, message = "文章标题最大长度为50")
    private String title;

    @NotNull(message = "文章内容不能为空")
    private String content;
    @NotNull(message = "文章封面不能为空")
    private String cover;

    private String summary;
    @NotNull(message = "文章分类不能为空")
    private Long categoryId;
    @NotEmpty(message = "文章标签不能为空")
    private List<String> tags;

    /**
     * 文章状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 是否置顶
     */
    private Boolean isTop;
}
