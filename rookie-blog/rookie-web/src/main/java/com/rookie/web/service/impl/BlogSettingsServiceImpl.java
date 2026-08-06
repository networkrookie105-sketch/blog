package com.rookie.web.service.impl;

import com.rookie.common.domain.dos.BlogSettingsDO;
import com.rookie.common.domain.mapper.BlogSettingsMapper;
import com.rookie.common.utils.Response;
import com.rookie.web.convert.BlogSettingsConvert;
import com.rookie.web.model.vo.blogsettings.FindBlogSettingsDetailRspVO;
import com.rookie.common.constant.CacheConstants;
import com.rookie.web.service.BlogSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author peishunwu
 * @ClassName BlogSettingsServiceImpl
 * @description: TODO
 * @date 2024年09月05日
 * @version: 1.0
 */
@Service
@Slf4j
public class BlogSettingsServiceImpl implements BlogSettingsService {

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;
    @Override
    @Cacheable(cacheNames = CacheConstants.BLOG_SETTINGS)
    public Response findBlogSettingsDetail() {
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(1);
        FindBlogSettingsDetailRspVO vo = BlogSettingsConvert.INSTANCE.convertDO2VO(blogSettingsDO);
        return Response.success(vo);
    }
}
