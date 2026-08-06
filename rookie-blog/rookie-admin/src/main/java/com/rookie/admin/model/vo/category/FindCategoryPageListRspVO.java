package com.rookie.admin.model.vo.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author peishunwu
 * @ClassName FindCategoryPageListRspVO
 * @description: TODO
 * @date 2024年04月24日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindCategoryPageListRspVO {

    private Long id;

    private String name;

    private LocalDateTime createTime;
}
