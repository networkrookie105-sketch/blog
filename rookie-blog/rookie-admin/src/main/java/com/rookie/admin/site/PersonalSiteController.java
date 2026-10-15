package com.rookie.admin.site;

import com.fasterxml.jackson.databind.JsonNode;
import com.rookie.common.exception.BizException;
import com.rookie.common.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PersonalSiteController {
    private final PersonalSiteService service;
    private final JdbcTemplate jdbc;
    @Value("${site.owner:admin}") private String owner;

    @GetMapping("/site")
    public Response<?> publicSite() { return Response.success(service.publicRead(owner)); }
    @GetMapping("/site/{username}")
    public Response<?> previousSite(@PathVariable String username) {
        if (!owner.equals(username)) throw new BizException("404", "该个人主页已关闭");
        return publicSite();
    }
    @GetMapping("/site/legacy/{slug}")
    public Response<?> legacyArticle(@PathVariable String slug) {
        var ids = jdbc.queryForList("SELECT id FROM t_article WHERE legacy_note_slug = ? AND status = 1 AND is_deleted = 0", Long.class, slug);
        if (ids.isEmpty()) throw new BizException("404", "文章不存在或尚未发布");
        return Response.success(java.util.Map.of("articleId", ids.getFirst()));
    }
    @GetMapping("/admin/site")
    public Response<?> ownSite() {
        service.ensure(owner);
        return Response.success(service.read(owner));
    }
    @PutMapping("/admin/site/profile")
    public Response<?> saveProfile(@RequestBody JsonNode request) { return Response.success(service.saveSection(owner, "profile", request)); }
    @PutMapping("/admin/site/settings")
    public Response<?> saveSettings(@RequestBody JsonNode request) { return Response.success(service.saveSection(owner, "settings", request)); }
    @PutMapping("/admin/site/projects")
    public Response<?> saveProject(@RequestBody JsonNode request) { return Response.success(service.saveProject(owner, request)); }
    @PutMapping("/admin/site")
    public Response<?> saveSite(@RequestBody JsonNode request) {
        return Response.fail("410", "请分别保存个人资料、项目和站点设置");
    }
}
