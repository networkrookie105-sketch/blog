package com.rookie.admin.site;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.rookie.common.domain.dos.PersonalSiteDO;
import com.rookie.common.domain.dos.BlogSettingsDO;
import com.rookie.common.domain.mapper.PersonalSiteMapper;
import com.rookie.common.domain.mapper.UserMapper;
import com.rookie.common.domain.mapper.BlogSettingsMapper;
import com.rookie.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PersonalSiteService {
    private final PersonalSiteMapper sites;
    private final UserMapper users;
    private final BlogSettingsMapper legacySettings;
    private final ObjectMapper json;

    @Transactional
    public PersonalSiteDO ensure(String username) {
        PersonalSiteDO site = sites.selectById(username);
        if (site == null) {
            if (users.findByUsername(username) == null) throw invalid("未找到博客账号");
            ObjectNode doc = json.createObjectNode();
            ObjectNode profile = doc.putObject("profile");
            profile.put("name", username).put("englishName", "").put("role", "").put("location", "")
                    .put("email", "").put("description", "欢迎来到我的个人主页").put("intro", "")
                    .put("interests", "").put("isExample", false);
            profile.putArray("skills"); profile.putArray("experiences");
            doc.putArray("projects"); doc.putArray("notes");
            site = new PersonalSiteDO(); site.setUserName(username); site.setRevision(0);
            site.setDocument(normalize(doc).toString()); sites.insert(site);
        } else {
            ObjectNode original = parse(site);
            ObjectNode normalized = normalize(original.deepCopy());
            if (!original.equals(normalized)) {
                persist(username, site.getRevision(), normalized);
                site = sites.selectById(username);
            }
        }
        return site;
    }

    /** 旧设置只在缺少新字段时补入，不覆盖已有姓名、介绍或用户主动清空的字段。 */
    private ObjectNode normalize(ObjectNode doc) {
        ObjectNode profile = (ObjectNode) doc.path("profile");
        BlogSettingsDO old = null;
        if (!doc.has("settings") || java.util.List.of("avatar", "githubHomepage", "giteeHomepage", "zhihuHomepage", "csdnHomepage").stream().anyMatch(key -> !profile.has(key))) old = legacySettings.selectById(1L);
        Map<String, String> personal = new LinkedHashMap<>();
        personal.put("avatar", old == null ? "" : old.getAvatar());
        personal.put("githubHomepage", old == null ? "" : old.getGithubHomepage());
        personal.put("giteeHomepage", old == null ? "" : old.getGiteeHomepage());
        personal.put("zhihuHomepage", old == null ? "" : old.getZhihuHomepage());
        personal.put("csdnHomepage", old == null ? "" : old.getCsdnHomepage());
        personal.forEach((key, value) -> { if (!profile.has(key)) profile.put(key, value == null ? "" : value); });
        if (!doc.has("settings")) {
            ObjectNode settings = doc.putObject("settings");
            settings.put("name", old != null && old.getName() != null ? old.getName() : profile.path("name").asText() + "的博客");
            settings.put("logo", old != null && old.getLogo() != null ? old.getLogo() : "");
            settings.put("introduction", old != null && old.getIntroduction() != null ? old.getIntroduction() : "");
            settings.put("favicon", "").put("footerText", "把问题想清楚，把事情做好。").put("copyright", "").put("filing", "");
        }
        ObjectNode settings = (ObjectNode) doc.path("settings");
        // 保留现有站点的公开状态；只为缺失字段补默认值，绝不重置主动关闭的开关。
        if (!settings.has("personalHomepagePublic")) settings.put("personalHomepagePublic", true);
        ObjectNode visibility;
        if (!profile.has("contactVisibility")) visibility = profile.putObject("contactVisibility");
        else if (profile.path("contactVisibility").isObject()) visibility = (ObjectNode) profile.path("contactVisibility");
        else throw invalid("联系方式展示设置不正确");
        for (String key : List.of("email", "location", "githubHomepage", "giteeHomepage", "zhihuHomepage", "csdnHomepage"))
            if (!visibility.has(key)) visibility.put(key, true);
        int position = 0;
        for (JsonNode item : doc.path("projects")) {
            ObjectNode project = (ObjectNode) item;
            if (!project.has("published")) project.put("published", true);
            if (!project.has("featured")) project.put("featured", true);
            if (!project.has("order")) project.put("order", position);
            position++;
        }
        return doc;
    }

    public ObjectNode read(String username) {
        PersonalSiteDO site = sites.selectById(username);
        if (site == null) throw new BizException("404", "未找到博客资料");
        ObjectNode result = normalize(parse(site));
        result.put("username", username).put("revision", site.getRevision());
        result.remove("notes"); // 历史文章备份永远不通过公开或编辑接口返回。
        return result;
    }

    public ObjectNode publicRead(String username) {
        ObjectNode source = read(username);
        ObjectNode result = json.createObjectNode();
        result.set("settings", source.path("settings").deepCopy());
        ArrayNode projects = result.putArray("projects");
        ObjectNode profile = result.putObject("profile");
        if (!source.path("settings").path("personalHomepagePublic").asBoolean(false)) return result;
        profile.setAll((ObjectNode) source.path("profile").deepCopy());
        JsonNode visibility = profile.path("contactVisibility");
        for (String key : List.of("email", "location", "githubHomepage", "giteeHomepage", "zhihuHomepage", "csdnHomepage"))
            if (!visibility.path(key).asBoolean(false)) profile.remove(key);
        profile.remove("contactVisibility");
        List<JsonNode> visible = new ArrayList<>();
        source.path("projects").forEach(project -> { if (project.path("published").asBoolean(true)) visible.add(project); });
        visible.sort(Comparator.comparingInt(project -> project.path("order").asInt()));
        visible.forEach(projects::add);
        return result;
    }

    @Transactional
    public ObjectNode saveSection(String username, String section, JsonNode request) {
        int revision = revision(request);
        ObjectNode doc = normalize(parse(requiredSite(username)));
        JsonNode content = request.path("document");
        if (!content.isObject()) throw invalid("表单数据不正确");
        ObjectNode copy = ((ObjectNode) content).deepCopy();
        if (section.equals("profile")) copy.retain(List.of("name", "englishName", "role", "location", "email", "description", "intro", "interests", "isExample", "skills", "experiences", "avatar", "githubHomepage", "giteeHomepage", "zhihuHomepage", "csdnHomepage", "contactVisibility"));
        else if (section.equals("settings")) copy.retain(List.of("name", "logo", "favicon", "introduction", "footerText", "copyright", "filing", "personalHomepagePublic"));
        else throw invalid("未知的内容类型");
        if (section.equals("settings") && !copy.has("personalHomepagePublic"))
            copy.set("personalHomepagePublic", doc.path("settings").path("personalHomepagePublic").deepCopy());
        if (section.equals("profile")) {
            if (!copy.has("contactVisibility")) copy.set("contactVisibility", doc.path("profile").path("contactVisibility").deepCopy());
            else if (copy.path("contactVisibility").isObject()) {
                ObjectNode flags = (ObjectNode) copy.path("contactVisibility");
                flags.retain(List.of("email", "location", "githubHomepage", "giteeHomepage", "zhihuHomepage", "csdnHomepage"));
                doc.path("profile").path("contactVisibility").fields().forEachRemaining(entry -> {
                    if (!flags.has(entry.getKey())) flags.set(entry.getKey(), entry.getValue().deepCopy());
                });
            }
        }
        doc.set(section, copy);
        normalize(doc); validate(doc); persist(username, revision, doc);
        return read(username);
    }

    @Transactional
    public ObjectNode saveProject(String username, JsonNode request) {
        int revision = revision(request);
        ObjectNode doc = normalize(parse(requiredSite(username)));
        ArrayNode projects = (ArrayNode) doc.path("projects");
        String action = request.path("action").asText();
        String slug = action.equals("delete") ? request.path("slug").asText() : request.path("project").path("slug").asText();
        int position = -1;
        for (int i = 0; i < projects.size(); i++) if (slug.equals(projects.get(i).path("slug").asText())) position = i;
        if (action.equals("delete")) {
            if (position < 0) throw new BizException("404", "项目不存在，请重新加载");
            projects.remove(position);
        } else if (action.equals("upsert") && request.path("project").isObject()) {
            ObjectNode project = ((ObjectNode) request.path("project")).deepCopy();
            project.retain(List.of("slug", "name", "subtitle", "category", "year", "description", "stack", "context", "contribution", "challenge", "result", "order", "featured", "published"));
            if (position >= 0) projects.set(position, project); else projects.add(project);
        } else throw invalid("项目操作不正确");
        validate(doc); persist(username, revision, doc);
        return read(username);
    }

    private PersonalSiteDO requiredSite(String username) {
        PersonalSiteDO site = sites.selectById(username);
        if (site == null) throw new BizException("404", "未找到博客资料");
        return site;
    }
    private ObjectNode parse(PersonalSiteDO site) {
        try { return (ObjectNode) json.readTree(site.getDocument()); }
        catch (java.io.IOException e) { throw new IllegalStateException("博客资料格式错误", e); }
    }
    private int revision(JsonNode request) {
        if (!request.isObject() || !request.path("revision").canConvertToInt()) throw invalid("缺少版本号，请重新加载");
        return request.path("revision").intValue();
    }
    private void persist(String username, int revision, ObjectNode doc) {
        if (doc.toString().length() > 400_000) throw invalid("内容超过长度限制");
        int updated = sites.update(null, new UpdateWrapper<PersonalSiteDO>().eq("user_name", username).eq("revision", revision)
                .set("document", doc.toString()).set("revision", revision + 1));
        if (updated != 1) throw new BizException("409", "内容已被另一页面修改，请重新加载后再保存");
    }
    private void validate(ObjectNode doc) {
        JsonNode profile = doc.path("profile");
        text(profile, "name", 60, true);
        for (String key : List.of("englishName", "role", "location", "email", "description", "intro", "interests")) text(profile, key, 5000, false);
        if (!profile.path("email").asText().isBlank() && !profile.path("email").asText().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw invalid("邮箱格式不正确");
        for (String key : List.of("avatar", "githubHomepage", "giteeHomepage", "zhihuHomepage", "csdnHomepage")) url(profile, key);
        if (!profile.path("skills").isArray() || profile.path("skills").size() > 30 || !profile.path("experiences").isArray() || profile.path("experiences").size() > 30) throw invalid("技能或经历最多 30 条");
        JsonNode visibility = profile.path("contactVisibility");
        if (!visibility.isObject()) throw invalid("联系方式展示设置不正确");
        for (String key : List.of("email", "location", "githubHomepage", "giteeHomepage", "zhihuHomepage", "csdnHomepage"))
            if (!visibility.path(key).isBoolean()) throw invalid("联系方式展示状态须为开关值");
        JsonNode settings = doc.path("settings"); text(settings, "name", 80, true);
        if (!settings.path("personalHomepagePublic").isBoolean()) throw invalid("个人主页公开状态须为开关值");
        for (String key : List.of("introduction", "footerText", "copyright", "filing")) text(settings, key, 5000, false);
        url(settings, "logo"); url(settings, "favicon");
        JsonNode projects = doc.path("projects");
        if (!projects.isArray() || projects.size() > 100) throw invalid("项目最多 100 条");
        Set<String> slugs = new HashSet<>();
        for (JsonNode item : projects) {
            String slug = item.path("slug").asText();
            if (!slug.matches("[a-zA-Z0-9-]{1,80}") || !slugs.add(slug)) throw invalid("项目标识不正确或重复");
            text(item, "name", 150, true);
            for (String key : List.of("subtitle", "category", "year", "description", "context", "contribution", "challenge", "result")) text(item, key, 5000, false);
            if (!item.path("stack").isArray() || item.path("stack").size() > 30) throw invalid("技术栈最多 30 项");
            if (item.has("order") && (!item.path("order").canConvertToInt() || item.path("order").asInt() < 0 || item.path("order").asInt() > 9999)) throw invalid("项目顺序须为 0 至 9999");
            for (String flag : List.of("published", "featured")) if (item.has(flag) && !item.path(flag).isBoolean()) throw invalid("项目展示状态不正确");
        }
    }
    private void text(JsonNode value, String key, int max, boolean required) {
        JsonNode field = value.path(key);
        if (required && field.asText().isBlank()) throw invalid("请填写名称或标题");
        if (!field.isMissingNode() && (!field.isTextual() || field.asText().length() > max)) throw invalid("字段 " + key + " 不正确或过长");
    }
    private void url(JsonNode value, String key) {
        text(value, key, 2000, false); String link = value.path(key).asText();
        if (!link.isBlank() && !link.matches("^(https?://[^\\s]+|/[^/\\s][^\\s]*)$")) throw invalid("链接须为 http/https 地址或站内路径");
    }
    private BizException invalid(String message) { return new BizException("10001", message); }
}
