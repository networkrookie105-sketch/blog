package com.rookie.admin.service.impl;

import com.rookie.admin.model.vo.file.UploadFileRspVO;
import com.rookie.admin.service.AdminFileService;
import com.rookie.admin.utils.MinioUtil;
import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.exception.BizException;
import com.rookie.common.utils.Response;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author peishunwu
 * @ClassName AdminFileServiceImpl
 * @description: TODO
 * @date 2024年05月11日
 * @version: 1.0
 */
@Service
@Slf4j
public class AdminFileServiceImpl implements AdminFileService {
    @Autowired
    private MinioUtil minioUtil;
    @Override
    public Response uploadFile(MultipartFile file) {
        try {
            String url = minioUtil.uploadFile(file);
            // 构建成功返参，将图片的访问链接返回
            return Response.success(UploadFileRspVO.builder().url(url).build());
        } catch (Exception e) {
            log.error("==> 上传文件至 Minio 错误: ", e);
            // 手动抛出业务异常，提示 “文件上传失败”
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }
}
