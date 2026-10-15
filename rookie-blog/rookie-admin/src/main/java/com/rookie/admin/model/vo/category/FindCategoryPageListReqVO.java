package com.rookie.admin.model.vo.category;

import com.rookie.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author peishunwu
 * @ClassName FindCategoryPageListReqVO
 * @description: TODO
 * @date 2024年04月24日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "查询分类分页数据入参 VO")
public class FindCategoryPageListReqVO extends BasePageQuery {
    /**
     * 分类名称
     */
    private String name;
    /**
     * 创建起始时间
     */
    private LocalDate startDate;
    /**
     * 创建结束时间
     */
    private LocalDate endDate;
}
