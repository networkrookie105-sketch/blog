package com.rookie.web.service.impl;

import com.rookie.admin.site.PersonalSiteService;
import com.rookie.common.utils.Response;
import com.rookie.web.service.BlogSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogSettingsServiceImpl implements BlogSettingsService {
    private final PersonalSiteService sites;
    @Value("${site.owner:admin}") private String owner;
    @Override public Response findBlogSettingsDetail() {
        return Response.success(sites.publicRead(owner).path("settings"));
    }
}
