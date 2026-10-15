package com.rookie.admin.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class Knife4jAdminConfig {
    @Bean("adminApi")
    public GroupedOpenApi createApiDoc() {
        return GroupedOpenApi.builder()
                .group("Admin 后台接口")
                .packagesToScan("com.rookie.admin.controller")
                .addOpenApiCustomizer(api -> api.info(new Info()
                        .title("菜鸟 博客后台接口文档")
                        .description("Spring Boot 3.5 + Vue 3 前后端分离博客")
                        .version("1.0")
                        .contact(new Contact().name("shaun").email("1227117505@qq.com"))))
                .build();
    }
}
