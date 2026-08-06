package com.rookie.admin.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author peishunwu
 * @ClassName FindArticleDetailReqVO
 * @description: TODO
 * @date 2024年06月11日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "查询文章详情请求VO")
public class FindArticleDetailReqVO {
    /**
     * 文章ID
     */
    @NotNull(message = "文章 ID 不能为空")
    private Long id;
}
