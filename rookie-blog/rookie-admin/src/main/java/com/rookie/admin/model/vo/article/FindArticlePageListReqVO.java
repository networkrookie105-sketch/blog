package com.rookie.admin.model.vo.article;

import com.rookie.common.model.BasePageQuery;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author peishunwu
 * @ClassName FindArticlePageListReqVO
 * @description: TODO
 * @date 2024年06月07日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "查询文章分页数据入参 VO")
public class FindArticlePageListReqVO extends BasePageQuery {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章发布开始时间
     */
    private LocalDate startDate;
    /**
     * 文章发布结束时间
     */
    private LocalDate endDate;
}
