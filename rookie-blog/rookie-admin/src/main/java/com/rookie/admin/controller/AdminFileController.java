package com.rookie.admin.controller;

import com.rookie.admin.service.AdminFileService;
import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author peishunwu
 * @ClassName AdminFileController
 * @description: TODO
 * @date 2024年05月11日
 * @version: 1.0
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 文件模块")
public class AdminFileController {
    @Autowired
    private AdminFileService adminFileService;
    @PostMapping("/file/upload")
    @Operation(summary = "文件上传")
    @ApiOperationLog(description = "文件上传")
    public Response uploadFile(@RequestParam MultipartFile file){
        return adminFileService.uploadFile(file);
    }
}
