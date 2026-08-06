package com.rookie.admin.model.vo.tag;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author peishunwu
 * @ClassName DeleteTagReqVO
 * @description: TODO
 * @date 2024年05月04日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "删除标签 VO")
public class DeleteTagReqVO {
    @NotNull(message = "标签 ID 不能为空")
    private Long id;
}
