package com.rookie.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rookie.admin.convert.BlogSettingsConvert;
import com.rookie.admin.model.vo.blogsettings.FindBlogSettingsRspVo;
import com.rookie.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.rookie.admin.service.AdminBlogSettingsService;
import com.rookie.common.constant.CacheConstants;
import com.rookie.common.domain.dos.BlogSettingsDO;
import com.rookie.common.domain.mapper.BlogSettingsMapper;
import com.rookie.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * @author peishunwu
 * @ClassName AdminBlogSettingsServiceImpl
 * @description: TODO
 * @date 2024年05月13日
 * @version: 1.0
 */
@Service
public class AdminBlogSettingsServiceImpl extends ServiceImpl<BlogSettingsMapper, BlogSettingsDO> implements AdminBlogSettingsService{
    @Autowired
    private BlogSettingsMapper blogSettingsMapper;
    @Override
    @CacheEvict(cacheNames = CacheConstants.BLOG_SETTINGS, allEntries = true)
    public Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO) {
        //Vo转Do
//        BlogSettingsDO blogSettingsDO = BlogSettingsDO.builder()
//                .id(updateBlogSettingsReqVO.getId())
//                .logo(updateBlogSettingsReqVO.getLogo())
//                .name(updateBlogSettingsReqVO.getName())
//                .author(updateBlogSettingsReqVO.getAuthor())
//                .introduction(updateBlogSettingsReqVO.getIntroduction())
//                .avatar(updateBlogSettingsReqVO.getAvatar())
//                .githubHomepage(updateBlogSettingsReqVO.getGithubHomepage())
//                .csdnHomepage(updateBlogSettingsReqVO.getCsdnHomepage())
//                .giteeHomepage(updateBlogSettingsReqVO.getGiteeHomepage())
//                .zhihuHomepage(updateBlogSettingsReqVO.getZhihuHomepage())
//                .build();
        BlogSettingsDO blogSettingsDO = BlogSettingsConvert.INSTANCE.convertVO2DO(updateBlogSettingsReqVO);
        //更新
        blogSettingsMapper.updateById(blogSettingsDO);
        return Response.success();
    }

    @Override
    public Response findBlogSettingsDetail() {
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(1L);
        //Do转Vo
        FindBlogSettingsRspVo findBlogSettingsRspVo = BlogSettingsConvert.INSTANCE.convertDO2VO(blogSettingsDO);
        return Response.success(findBlogSettingsRspVo);
    }
}
