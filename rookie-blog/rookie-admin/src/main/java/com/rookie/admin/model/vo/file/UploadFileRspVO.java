package com.rookie.admin.model.vo.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author peishunwu
 * @ClassName UploadFileRspVO
 * @description: TODO
 * @date 2024年05月11日
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadFileRspVO {
    /**
     * 文件的访问链接
     */
    private String url;
}
