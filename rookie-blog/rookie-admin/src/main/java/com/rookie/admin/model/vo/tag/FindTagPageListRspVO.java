package com.rookie.admin.model.vo.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author peishunwu
 * @ClassName FindTagPageListRspVO
 * @description: TODO
 * @date 2024年05月03日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindTagPageListRspVO {
    private Long id;

    private String name;

    private LocalDateTime createTime;
}
