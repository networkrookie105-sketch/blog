package com.rookie.admin.model.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * @author peishunwu
 * @ClassName DeleteCategoryReqVO
 * @description: TODO
 * @date 2024年04月28日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "删除分类 VO")
public class DeleteCategoryReqVO {
    @NotNull(message = "分类 ID 不能为空")
    private Long id;
}
