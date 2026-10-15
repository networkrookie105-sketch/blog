package com.rookie.admin.site;

import com.rookie.common.utils.Response;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/** 兼容旧客户端：个人博客永久关闭注册与作者账号创建。 */
@RestController
public class SiteAccountController {
    @GetMapping("/site/features")
    public Response<?> features() { return Response.success(Map.of("registrationEnabled", false)); }
    @PostMapping({"/register", "/admin/site/accounts"})
    public Response<?> registrationClosed() { return Response.fail("403", "这是个人博客，不提供注册或作者账号创建"); }
}
