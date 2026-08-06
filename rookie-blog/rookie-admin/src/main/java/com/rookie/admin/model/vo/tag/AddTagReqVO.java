package com.rookie.admin.model.vo.tag;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author peishunwu
 * @ClassName AddTagReqVO
 * @description: TODO
 * @date 2024年05月03日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "添加标签 VO")
public class AddTagReqVO {
    @NotBlank(message = "标签名称不能为空")
    @Length(min = 1, max = 20, message = "标签名称字数限制 1 ~ 20 之间")
    private String name;
}
