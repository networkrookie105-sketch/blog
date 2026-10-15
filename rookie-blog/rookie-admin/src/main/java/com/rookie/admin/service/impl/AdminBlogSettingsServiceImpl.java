package com.rookie.admin.service.impl;

import com.rookie.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.rookie.admin.service.AdminBlogSettingsService;
import com.rookie.admin.site.PersonalSiteService;
import com.rookie.common.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminBlogSettingsServiceImpl implements AdminBlogSettingsService {
    private final PersonalSiteService sites;
    @Value("${site.owner:admin}") private String owner;
    @Override public Response updateBlogSettings(UpdateBlogSettingsReqVO request) {
        return Response.fail("410", "旧博客设置已合并，请使用站点设置页面保存");
    }
    @Override public Response findBlogSettingsDetail() {
        sites.ensure(owner);
        return Response.success(sites.read(owner).path("settings"));
    }
}
