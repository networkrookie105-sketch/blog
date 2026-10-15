package com.rookie.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rookie.common.domain.dos.UserDO;
import com.rookie.common.domain.mapper.UserMapper;
import com.rookie.common.domain.mapper.ArticleMapper;
import com.rookie.common.domain.mapper.ArticleTagRelMapper;
import com.rookie.common.domain.dos.ArticleTagRelDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:boot35;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "site.owner=admin",
        "minio.endpoint=http://localhost:9000", "minio.accessKey=test", "minio.secretKey=test-secret"
})
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class RookieWebApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired UserMapper userMapper;
    @Autowired ArticleMapper articleMapper;
    @Autowired ArticleTagRelMapper articleTagRelMapper;

    @BeforeEach
    void prepareDatabase() {
        jdbc.execute("DROP ALL OBJECTS");
        jdbc.execute("CREATE TABLE t_personal_site (user_name VARCHAR(60) PRIMARY KEY, document CLOB NOT NULL, revision INT NOT NULL)");
        jdbc.execute("CREATE TABLE t_user (id BIGINT AUTO_INCREMENT PRIMARY KEY, user_name VARCHAR(100), password VARCHAR(100), create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, is_deleted BOOLEAN)");
        jdbc.execute("CREATE TABLE t_user_role (id BIGINT AUTO_INCREMENT PRIMARY KEY, user_name VARCHAR(100), role VARCHAR(100), create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, is_deleted BOOLEAN)");
        jdbc.execute("CREATE TABLE t_article (id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(150), cover VARCHAR(100), summary CLOB, legacy_note_slug VARCHAR(80), create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, is_deleted BOOLEAN DEFAULT FALSE, read_num BIGINT, status INT, is_top BOOLEAN)");
        jdbc.execute("CREATE TABLE t_article_tag_rel (id BIGINT AUTO_INCREMENT PRIMARY KEY, article_id BIGINT, tag_id BIGINT)");
        jdbc.execute("CREATE TABLE t_category (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(60), create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, is_deleted BOOLEAN)");
        jdbc.execute("CREATE TABLE t_tag (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(60), create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, is_deleted BOOLEAN)");
        jdbc.execute("CREATE TABLE t_article_content (id BIGINT AUTO_INCREMENT PRIMARY KEY, article_id BIGINT, content CLOB)");
        jdbc.execute("CREATE TABLE t_article_category_rel (id BIGINT AUTO_INCREMENT PRIMARY KEY, article_id BIGINT, category_id BIGINT)");
        jdbc.execute("CREATE TABLE t_blog_settings (id BIGINT PRIMARY KEY, name VARCHAR(80), logo VARCHAR(255), author VARCHAR(60), introduction VARCHAR(5000), avatar VARCHAR(255), github_homepage VARCHAR(255), gitee_homepage VARCHAR(255), zhihu_homepage VARCHAR(255), csdn_homepage VARCHAR(255))");
        userMapper.insert(UserDO.builder().userName("admin").password(passwordEncoder.encode("123456")).build());
        userMapper.insert(UserDO.builder().userName("reader").password(passwordEncoder.encode("123456")).build());
        jdbc.update("INSERT INTO t_user_role (user_name, role) VALUES ('admin', 'ROLE_ADMIN'), ('reader', 'ROLE_USER')");
    }

    private String login(String username) throws Exception {
        String response = mvc.perform(post("/login").contentType("application/json")
                        .content("{\"username\":\"" + username + "\",\"password\":\"123456\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("data").path("token").asText();
    }

    @Test
    void loginAuthenticationAndLogoutBlacklist() throws Exception {
        mvc.perform(post("/admin/logout")).andExpect(status().isUnauthorized());
        String token = login("admin");
        assertFalse(token.isBlank());
        mvc.perform(post("/admin/logout").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        mvc.perform(post("/admin/logout").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsWrongPasswordAndInvalidJwt() throws Exception {
        mvc.perform(post("/login").contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(jsonPath("$.success").value(false));
        mvc.perform(post("/admin/logout").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enforcesMethodSecurityForNonAdmin() throws Exception {
        String token = login("reader");
        mvc.perform(post("/admin/article/publish").header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{\"title\":\"test\",\"content\":\"body\",\"cover\":\"image\",\"categoryId\":1,\"tagIds\":[\"1\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("20004"));
    }

    @Test
    void rejectsInvalidPublicRequestAndSupportsCors() throws Exception {
        mvc.perform(post("/comment/publish").contentType("application/json").content("{}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(false));
        mvc.perform(options("/admin/logout").header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void exposesOpenApiGroupsAndKnife4j() throws Exception {
        mvc.perform(get("/v3/api-docs/Web 前台接口"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/comment/publish'].post").exists());
        mvc.perform(get("/v3/api-docs/Admin 后台接口"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/admin/article/publish'].post").exists());
        mvc.perform(get("/v3/api-docs/swagger-config"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.urls.length()").value(2));
        mvc.perform(get("/doc.html")).andExpect(status().isOk());
    }

    @Test
    void paginationAndCustomBatchInsertWork() {
        jdbc.update("INSERT INTO t_article (title, status, is_top, read_num) VALUES ('published', 1, FALSE, 0), ('draft', 0, FALSE, 0)");
        var page = articleMapper.selectPublishedPageList(1L, 1L);
        assertEquals(1, page.getTotal());
        assertEquals("published", page.getRecords().getFirst().getTitle());
        ArticleTagRelDO relation = ArticleTagRelDO.builder().articleId(1L).tagId(1L).build();
        assertEquals(1, articleTagRelMapper.insertBatchSomeColumn(List.of(relation)));
        assertEquals(1, articleTagRelMapper.selectCount(null));
    }

    @Test
    void blogProfileIsAdminOnlyAndUsesOptimisticLock() throws Exception {
        String admin = login("admin");
        String reader = login("reader");
        mvc.perform(get("/admin/site").header("Authorization", "Bearer " + reader))
                .andExpect(jsonPath("$.success").value(false));
        String data = mvc.perform(get("/admin/site").header("Authorization", "Bearer " + admin))
                .andExpect(jsonPath("$.data.username").value("admin")).andReturn().getResponse().getContentAsString();
        var document = (com.fasterxml.jackson.databind.node.ObjectNode) objectMapper.readTree(data).path("data");
        document.withObject("profile").put("name", "Blog owner");
        var request = objectMapper.createObjectNode().put("username", "reader").put("revision", 0);
        request.set("document", document.path("profile"));
        mvc.perform(put("/admin/site/profile").header("Authorization", "Bearer " + admin).contentType("application/json").content(request.toString()))
                .andExpect(jsonPath("$.success").value(true));
        mvc.perform(get("/site")).andExpect(jsonPath("$.data.profile.name").value("Blog owner"));
        mvc.perform(get("/site/reader")).andExpect(jsonPath("$.success").value(false));
        mvc.perform(put("/admin/site/profile").header("Authorization", "Bearer " + admin).contentType("application/json").content(request.toString()))
                .andExpect(jsonPath("$.errorCode").value("409"));
    }

    @Test
    void registrationAndAuthorCreationStayClosed() throws Exception {
        mvc.perform(get("/site/features")).andExpect(jsonPath("$.data.registrationEnabled").value(false));
        mvc.perform(post("/register").contentType("application/json").content("{}"))
                .andExpect(jsonPath("$.errorCode").value("403"));
        mvc.perform(post("/admin/site/accounts").header("Authorization", "Bearer " + login("admin")))
                .andExpect(jsonPath("$.errorCode").value("403"));
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM t_user", Integer.class));
    }

    @Test
    void completeArticleBackendFeedsListTaxonomiesAndArchiveAndHidesDrafts() throws Exception {
        jdbc.update("INSERT INTO t_category(name) VALUES ('工程实践')");
        String token = login("admin");
        var article = objectMapper.createObjectNode().put("title", "完整后台文章").put("content", "正文关键词")
                .put("summary", "摘要").put("categoryId", 1).put("status", 1);
        article.putArray("tagIds").add("Java");
        mvc.perform(post("/admin/article/publish").header("Authorization", "Bearer " + token).contentType("application/json").content(article.toString()))
                .andExpect(jsonPath("$.success").value(true));
        article.put("title", "未公开草稿").put("status", 0);
        mvc.perform(post("/admin/article/publish").header("Authorization", "Bearer " + token).contentType("application/json").content(article.toString()))
                .andExpect(jsonPath("$.success").value(true));
        String page = "{\"current\":1,\"size\":10}";
        mvc.perform(post("/article/list").contentType("application/json").content(page))
                .andExpect(jsonPath("$.total").value(1)).andExpect(jsonPath("$.data[0].category.name").value("工程实践"));
        mvc.perform(post("/category/list")).andExpect(jsonPath("$.data[0].name").value("工程实践"));
        mvc.perform(post("/tag/list")).andExpect(jsonPath("$.data[0].name").value("Java"));
        mvc.perform(post("/category/article/list").contentType("application/json").content("{\"current\":1,\"size\":10,\"id\":1}"))
                .andExpect(jsonPath("$.total").value(1));
        mvc.perform(post("/tag/article/list").contentType("application/json").content("{\"current\":1,\"size\":10,\"id\":1}"))
                .andExpect(jsonPath("$.total").value(1));
        mvc.perform(post("/archive/list").contentType("application/json").content(page)).andExpect(jsonPath("$.total").value(1));
        mvc.perform(post("/search").contentType("application/json").content("{\"current\":1,\"size\":10,\"keyword\":\"正文关键词\"}"))
                .andExpect(jsonPath("$.total").value(1));
        mvc.perform(post("/detail").contentType("application/json").content("{\"articleId\":2}"))
                .andExpect(jsonPath("$.success").value(false));
        mvc.perform(post("/detail").contentType("application/json").content("{\"articleId\":1}"))
                .andExpect(jsonPath("$.data.content").value("<p>正文关键词</p>\n"))
                .andExpect(jsonPath("$.data.preArticle").doesNotExist());
    }

    @Test
    void legacyLinksHideDraftsAndProfileSavePreservesArchivedNotes() throws Exception {
        String token = login("admin");
        String data = mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token)).andReturn().getResponse().getContentAsString();
        var doc = (com.fasterxml.jackson.databind.node.ObjectNode) objectMapper.readTree(data).path("data");
        var stored = doc.deepCopy();
        stored.putArray("notes").addObject().put("slug", "original").put("title", "原始备份");
        jdbc.update("UPDATE t_personal_site SET document = ? WHERE user_name = 'admin'", stored.toString());
        mvc.perform(get("/site")).andExpect(jsonPath("$.data.notes").doesNotExist());
        doc.putArray("notes").addObject().put("title", "客户端试图覆盖备份");
        var request = objectMapper.createObjectNode().put("revision", 0); request.set("document", doc.path("profile"));
        mvc.perform(put("/admin/site/profile").header("Authorization", "Bearer " + token).contentType("application/json").content(request.toString()))
                .andExpect(jsonPath("$.success").value(true));
        assertEquals("原始备份", objectMapper.readTree(jdbc.queryForObject("SELECT document FROM t_personal_site WHERE user_name = 'admin'", String.class)).path("notes").get(0).path("title").asText());
        jdbc.update("INSERT INTO t_article(title, legacy_note_slug, status) VALUES ('public', 'public-slug', 1), ('draft', 'draft-slug', 0)");
        mvc.perform(get("/site/legacy/public-slug")).andExpect(jsonPath("$.data.articleId").value(1));
        mvc.perform(get("/site/legacy/draft-slug")).andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void archivesAndSiblingLinksFollowDatesRatherThanTopFlagsOrImportIds() {
        jdbc.update("INSERT INTO t_article(title, status, is_top, create_time) VALUES ('September', 1, FALSE, '2026-09-01 00:00:00'), ('October', 1, FALSE, '2026-10-01 00:00:00'), ('August pinned', 1, TRUE, '2026-08-01 00:00:00'), ('Draft', 0, FALSE, '2026-09-15 00:00:00')");
        assertEquals("September", articleMapper.selectNextArticle(2L).getTitle());
        assertEquals("October", articleMapper.selectPreArticle(1L).getTitle());
        assertEquals("October", articleMapper.selectArchivePageList(1L, 1L).getRecords().getFirst().getTitle());
        assertEquals("August pinned", articleMapper.selectPublishedPageList(1L, 1L).getRecords().getFirst().getTitle());
    }

    @Test
    void legacySettingsMigrateWithoutOverwritingProfileAndStayMigrated() throws Exception {
        String token = login("admin");
        String first = mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token)).andReturn().getResponse().getContentAsString();
        var original = (com.fasterxml.jackson.databind.node.ObjectNode) objectMapper.readTree(first).path("data");
        original.remove("settings");
        var profile = (com.fasterxml.jackson.databind.node.ObjectNode) original.path("profile");
        profile.remove(java.util.List.of("avatar", "githubHomepage", "giteeHomepage", "zhihuHomepage", "csdnHomepage"));
        profile.put("name", "现有姓名").put("intro", "现有个人介绍");
        jdbc.update("UPDATE t_personal_site SET document = ? WHERE user_name = 'admin'", original.toString());
        jdbc.update("INSERT INTO t_blog_settings(id, name, author, introduction, avatar, github_homepage) VALUES (1, '旧博客名称', '旧作者', '网站简介', '/avatar.png', 'https://github.com/example')");
        String migrated = mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.data.profile.name").value("现有姓名"))
                .andExpect(jsonPath("$.data.profile.intro").value("现有个人介绍"))
                .andExpect(jsonPath("$.data.profile.avatar").value("/avatar.png"))
                .andExpect(jsonPath("$.data.settings.name").value("旧博客名称"))
                .andExpect(jsonPath("$.data.settings.introduction").value("网站简介"))
                .andReturn().getResponse().getContentAsString();
        var data = objectMapper.readTree(migrated).path("data");
        int revision = data.path("revision").asInt();
        mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.data.revision").value(revision));
        var request = objectMapper.createObjectNode().put("revision", revision);
        var cleared = data.path("profile").deepCopy(); ((com.fasterxml.jackson.databind.node.ObjectNode) cleared).put("avatar", ""); request.set("document", cleared);
        mvc.perform(put("/admin/site/profile").header("Authorization", "Bearer " + token).contentType("application/json").content(request.toString())).andExpect(jsonPath("$.success").value(true));
        mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token)).andExpect(jsonPath("$.data.profile.avatar").value(""));
    }

    @Test
    void sectionSavesDoNotOverwriteOtherSectionsAndProjectVisibilityIsEnforced() throws Exception {
        String token = login("admin");
        mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token));
        mvc.perform(put("/admin/site/settings").header("Authorization", "Bearer " + token).contentType("application/json")
                .content("{\"revision\":0,\"document\":{\"name\":\"我的技术博客\",\"introduction\":\"网站主题\"}}"))
                .andExpect(jsonPath("$.success").value(true));
        for (int i = 0; i < 3; i++) {
            var project = objectMapper.createObjectNode().put("slug", "project-" + i).put("name", "项目" + i)
                    .put("order", 2-i).put("published", i > 0).put("featured", i == 2); project.putArray("stack");
            var request = objectMapper.createObjectNode().put("revision", i+1).put("action", "upsert"); request.set("project", project);
            mvc.perform(put("/admin/site/projects").header("Authorization", "Bearer " + token).contentType("application/json").content(request.toString()))
                    .andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data.settings.name").value("我的技术博客"));
        }
        mvc.perform(get("/site")).andExpect(jsonPath("$.data.projects.length()").value(2))
                .andExpect(jsonPath("$.data.projects[0].slug").value("project-2"));
        mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token)).andExpect(jsonPath("$.data.projects.length()").value(3));
        mvc.perform(put("/admin/site/projects").header("Authorization", "Bearer " + token).contentType("application/json")
                .content("{\"revision\":1,\"action\":\"delete\",\"slug\":\"project-2\"}"))
                .andExpect(jsonPath("$.errorCode").value("409"));
        mvc.perform(put("/admin/site/projects").header("Authorization", "Bearer " + token).contentType("application/json")
                .content("{\"revision\":4,\"action\":\"delete\",\"slug\":\"project-2\"}"))
                .andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data.settings.name").value("我的技术博客"));
        mvc.perform(put("/admin/site").header("Authorization", "Bearer " + token).contentType("application/json").content("{}"))
                .andExpect(jsonPath("$.errorCode").value("410"));
        mvc.perform(put("/admin/site/settings").header("Authorization", "Bearer " + login("reader")).contentType("application/json").content("{}"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void profileLinksCannotUseExecutableUrls() throws Exception {
        String token = login("admin");
        String raw = mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token)).andReturn().getResponse().getContentAsString();
        var data = objectMapper.readTree(raw).path("data");
        var profile = (com.fasterxml.jackson.databind.node.ObjectNode) data.path("profile"); profile.put("githubHomepage", "javascript:alert(1)");
        var request = objectMapper.createObjectNode().put("revision", data.path("revision").asInt()); request.set("document", profile);
        mvc.perform(put("/admin/site/profile").header("Authorization", "Bearer " + token).contentType("application/json").content(request.toString()))
                .andExpect(jsonPath("$.errorCode").value("10001"));
    }

    @Test
    void passwordUpdatesCannotTargetAnotherAccount() throws Exception {
        mvc.perform(post("/admin/password/update").header("Authorization", "Bearer " + login("reader")).contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"reader-new-password\"}"))
                .andExpect(jsonPath("$.success").value(true));
        assertTrue(passwordEncoder.matches("123456", userMapper.findByUsername("admin").getPassword()));
        assertTrue(passwordEncoder.matches("reader-new-password", userMapper.findByUsername("reader").getPassword()));
    }
    @Test
    void homepageSwitchRedactsAllPublicRoutesAndPreservesAdminDataAndArticles() throws Exception {
        String token = login("admin");
        String initial = mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token)).andReturn().getResponse().getContentAsString();
        var doc = (com.fasterxml.jackson.databind.node.ObjectNode) objectMapper.readTree(initial).path("data");
        doc.putArray("projects").addObject().put("slug", "visible-project").put("name", "公开项目").put("published", true).putArray("stack");
        jdbc.update("UPDATE t_personal_site SET document = ? WHERE user_name = 'admin'", doc.toString());
        var profile = (com.fasterxml.jackson.databind.node.ObjectNode) doc.path("profile");
        profile.put("name", "Private owner").put("email", "owner@example.com").put("location", "杭州")
            .put("githubHomepage", "https://github.com/example").put("giteeHomepage", "https://gitee.com/example")
            .put("zhihuHomepage", "https://www.zhihu.com/people/example").put("csdnHomepage", "https://blog.csdn.net/example");
        profile.putArray("skills").add("Java");
        profile.putArray("experiences").addObject().put("company", "Private company");
        var flags = (com.fasterxml.jackson.databind.node.ObjectNode) profile.path("contactVisibility");
        for (String key : List.of("email", "location", "githubHomepage", "giteeHomepage", "zhihuHomepage", "csdnHomepage")) flags.put(key, false);
        var req = objectMapper.createObjectNode().put("revision", 0); req.set("document", profile);
        mvc.perform(put("/admin/site/profile").header("Authorization", "Bearer " + token).contentType("application/json").content(req.toString()))
            .andExpect(jsonPath("$.success").value(true));
        mvc.perform(get("/site")).andExpect(jsonPath("$.data.profile.name").value("Private owner"))
            .andExpect(jsonPath("$.data.profile.email").doesNotExist()).andExpect(jsonPath("$.data.profile.location").doesNotExist())
            .andExpect(jsonPath("$.data.profile.githubHomepage").doesNotExist()).andExpect(jsonPath("$.data.profile.giteeHomepage").doesNotExist())
            .andExpect(jsonPath("$.data.profile.zhihuHomepage").doesNotExist()).andExpect(jsonPath("$.data.profile.csdnHomepage").doesNotExist());
        var settings = (com.fasterxml.jackson.databind.node.ObjectNode) doc.path("settings"); settings.put("personalHomepagePublic", false);
        req.put("revision", 1); req.set("document", settings);
        mvc.perform(put("/admin/site/settings").header("Authorization", "Bearer " + token).contentType("application/json").content(req.toString()))
            .andExpect(jsonPath("$.success").value(true));
        for (String path : List.of("/site", "/site/admin")) {
            mvc.perform(get(path)).andExpect(jsonPath("$.data.profile.length()").value(0))
                .andExpect(jsonPath("$.data.projects.length()").value(0))
                .andExpect(jsonPath("$.data.settings.personalHomepagePublic").value(false))
                .andExpect(jsonPath("$.data.username").doesNotExist());
        }
        mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token))
            .andExpect(jsonPath("$.data.profile.email").value("owner@example.com"))
            .andExpect(jsonPath("$.data.profile.experiences[0].company").value("Private company"));
        jdbc.update("INSERT INTO t_article(title, status) VALUES ('Published blog', 1)");
        mvc.perform(post("/article/list").contentType("application/json").content("{\"current\":1,\"size\":10}"))
            .andExpect(jsonPath("$.data[0].title").value("Published blog"));
        // 旧客户端未携带开关字段时不能意外重新公开。
        settings.remove("personalHomepagePublic"); req.put("revision", 2); req.set("document", settings);
        mvc.perform(put("/admin/site/settings").header("Authorization", "Bearer " + token).contentType("application/json").content(req.toString()))
            .andExpect(jsonPath("$.data.settings.personalHomepagePublic").value(false));
        settings.put("personalHomepagePublic", true); req.put("revision", 3);
        mvc.perform(put("/admin/site/settings").header("Authorization", "Bearer " + token).contentType("application/json").content(req.toString()))
            .andExpect(jsonPath("$.success").value(true));
        mvc.perform(get("/site")).andExpect(jsonPath("$.data.profile.name").value("Private owner"))
            .andExpect(jsonPath("$.data.projects[0].slug").value("visible-project"))
            .andExpect(jsonPath("$.data.profile.email").doesNotExist());
    }

    @Test
    void rejectsNonBooleanPrivacySwitchesWithoutChangingData() throws Exception {
        String token = login("admin");
        String initial = mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token)).andReturn().getResponse().getContentAsString();
        var doc = objectMapper.readTree(initial).path("data");
        var settings = (com.fasterxml.jackson.databind.node.ObjectNode) doc.path("settings"); settings.put("personalHomepagePublic", "false");
        var req = objectMapper.createObjectNode().put("revision", 0); req.set("document", settings);
        mvc.perform(put("/admin/site/settings").header("Authorization", "Bearer " + token).contentType("application/json").content(req.toString()))
            .andExpect(jsonPath("$.errorCode").value("10001"));
        var profile = (com.fasterxml.jackson.databind.node.ObjectNode) doc.path("profile"); profile.put("contactVisibility", false); req.set("document", profile);
        mvc.perform(put("/admin/site/profile").header("Authorization", "Bearer " + token).contentType("application/json").content(req.toString()))
            .andExpect(jsonPath("$.errorCode").value("10001"));
        mvc.perform(get("/admin/site").header("Authorization", "Bearer " + token)).andExpect(jsonPath("$.data.revision").value(0));
    }

}
