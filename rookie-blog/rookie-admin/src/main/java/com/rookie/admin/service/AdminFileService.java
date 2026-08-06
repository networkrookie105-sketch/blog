package com.rookie.admin.service;

import com.rookie.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author peishunwu
 * @ClassName AdminFileService
 * @description: TODO
 * @date 2024年05月11日
 * @version: 1.0
 */
public interface AdminFileService {
    /**
     * 上传文件
     * @param file
     * @return
     */
    Response uploadFile(MultipartFile file);
}
