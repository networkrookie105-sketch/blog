package com.rookie.admin.utils;

import com.rookie.admin.config.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author peishunwu
 * @ClassName MinioUtil
 * @description: TODO
 * @date 2024年05月11日
 * @version: 1.0
 */
@Component
@Slf4j
public class MinioUtil {
    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioClient minioClient;

    public String uploadFile(MultipartFile file) throws Exception {
        //判断文件是否为空
        if (file.isEmpty()) {
            log.error("文件为空");
            throw new RuntimeException("文件为空");
        }
        //获取文件原始名
        String originalFileName  = file.getOriginalFilename();
        //文件的ContentType
        String contentType = file.getContentType();
        //生成存储对象名称（将 UUID 字符串中的 - 替换成空字符串）
        String key = UUID.randomUUID().toString().replace("-", "");
        //获取文件后缀
        String suffix = originalFileName .substring(originalFileName .lastIndexOf("."));
        // 拼接上文件后缀，即为要存储的文件名
        String objectName = String.format("%s%s", key, suffix);

        log.info("==> 开始上传文件至 Minio, ObjectName: {}", objectName);
        //上传文件
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .stream(file.getInputStream(),file.getSize(),-1)
                .contentType(contentType)
                .build());
        // 返回文件的访问链接
        String url = String.format("%s/%s/%s", minioProperties.getEndpoint(), minioProperties.getBucketName(), objectName);
        log.info("==> 上传文件至 Minio 成功，访问路径: {}", url);
        return url;
    }
}
