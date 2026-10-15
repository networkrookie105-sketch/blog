-- =============================================================
-- rookie-blog 数据库建表脚本
-- 数据库：MySQL 5.7+ / 8.x
-- 字符集：utf8mb4
-- 说明：字段命名与实体类（DO）一致，遵循 MyBatis-Plus 默认的
--       驼峰 -> 下划线 映射规则（如 userName -> user_name）。
-- =============================================================

CREATE DATABASE IF NOT EXISTS `rookieblog`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE `rookieblog`;

-- -------------------------------------------------------------
-- 用户表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_name`   VARCHAR(60)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(120) NOT NULL COMMENT '密码（BCrypt 加密）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- -------------------------------------------------------------
-- 用户角色表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_name`   VARCHAR(60) NOT NULL COMMENT '用户名',
    `role`        VARCHAR(60) NOT NULL COMMENT '角色（如 ROLE_ADMIN）',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_user_name` (`user_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户角色表';

-- -------------------------------------------------------------
-- 文章表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `t_article`;
CREATE TABLE `t_article` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`       VARCHAR(150) NOT NULL COMMENT '标题',
    `cover`       VARCHAR(255)          DEFAULT NULL COMMENT '封面图地址',
    `summary`     TEXT                  DEFAULT NULL COMMENT '摘要',
    `read_num`    BIGINT       NOT NULL DEFAULT 0 COMMENT '阅读量',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-草稿，1-已发布',
    `is_top`      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    `legacy_note_slug` VARCHAR(80) DEFAULT NULL COMMENT '历史个人文章标识',
    UNIQUE KEY `uk_legacy_note_slug` (`legacy_note_slug`),
    KEY `idx_status_top_time` (`status`, `is_top`, `create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章表';

-- -------------------------------------------------------------
-- 文章正文表（与文章一对一，正文与列表查询解耦）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `t_article_content`;
CREATE TABLE `t_article_content` (
    `id`         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id` BIGINT   NOT NULL COMMENT '文章 ID',
    `content`    LONGTEXT NOT NULL COMMENT '正文（Markdown）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_id` (`article_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章正文表';

-- -------------------------------------------------------------
-- 分类表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(60) NOT NULL COMMENT '分类名称',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '分类表';

-- -------------------------------------------------------------
-- 标签表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(60) NOT NULL COMMENT '标签名称',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '标签表';

-- -------------------------------------------------------------
-- 文章-分类关联表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `t_article_category_rel`;
CREATE TABLE `t_article_category_rel` (
    `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id`  BIGINT NOT NULL COMMENT '文章 ID',
    `category_id` BIGINT NOT NULL COMMENT '分类 ID',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章分类关联表';

-- -------------------------------------------------------------
-- 文章-标签关联表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `t_article_tag_rel`;
CREATE TABLE `t_article_tag_rel` (
    `id`         BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id` BIGINT NOT NULL COMMENT '文章 ID',
    `tag_id`     BIGINT NOT NULL COMMENT '标签 ID',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章标签关联表';

-- -------------------------------------------------------------
-- 博客设置表（单行配置，固定 id = 1）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `t_blog_settings`;
CREATE TABLE `t_blog_settings` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `logo`            VARCHAR(255)          DEFAULT NULL COMMENT '博客 LOGO',
    `name`            VARCHAR(60)           DEFAULT NULL COMMENT '博客名称',
    `author`          VARCHAR(60)           DEFAULT NULL COMMENT '作者',
    `introduction`    VARCHAR(300)          DEFAULT NULL COMMENT '介绍语',
    `avatar`          VARCHAR(255)          DEFAULT NULL COMMENT '头像',
    `github_homepage` VARCHAR(255)          DEFAULT NULL COMMENT 'GitHub 主页',
    `csdn_homepage`   VARCHAR(255)          DEFAULT NULL COMMENT 'CSDN 主页',
    `gitee_homepage`  VARCHAR(255)          DEFAULT NULL COMMENT 'Gitee 主页',
    `zhihu_homepage`  VARCHAR(255)          DEFAULT NULL COMMENT '知乎主页',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '博客设置表';

-- -------------------------------------------------------------
-- 评论表（一级评论 + 楼中楼回复）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id`     BIGINT       NOT NULL COMMENT '所属文章 ID',
    `parent_id`      BIGINT                DEFAULT NULL COMMENT '父评论 ID：NULL 为一级评论',
    `reply_nickname` VARCHAR(30)           DEFAULT NULL COMMENT '被回复者昵称',
    `nickname`       VARCHAR(30)  NOT NULL COMMENT '评论者昵称',
    `email`          VARCHAR(60)           DEFAULT NULL COMMENT '评论者邮箱',
    `website`        VARCHAR(255)          DEFAULT NULL COMMENT '评论者网站',
    `content`        VARCHAR(500) NOT NULL COMMENT '评论内容',
    `status`         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '评论表';

-- =============================================================
-- 初始化种子数据
-- =============================================================

-- 管理员账号：admin / 123456
-- 下面的密文由项目内 BCryptPasswordEncoder 对 "123456" 加密生成，可直接使用。
-- 如需修改初始密码，用 PasswordEncoderConfig.main 生成新密文替换即可。
INSERT INTO `t_user` (`user_name`, `password`)
VALUES ('admin', '$2a$10$U/xkmvv/4D1Wc2huqHcKz.RKl56O6icc5KYXja2eogZdH8Ox02xri');

-- 管理员角色
INSERT INTO `t_user_role` (`user_name`, `role`)
VALUES ('admin', 'ROLE_ADMIN');

-- 默认博客设置（id 固定为 1，前台/后台按 id=1 读取）
INSERT INTO `t_blog_settings`
(`id`, `logo`, `name`, `author`, `introduction`, `avatar`)
VALUES
(1, NULL, 'Rookie Blog', 'rookie', '一个优雅的个人博客', NULL);

-- 无删表操作，为已有账号提供独立个人站点；内容修改由 user_name + revision 限定。
CREATE TABLE IF NOT EXISTS t_personal_site (
  user_name VARCHAR(60) NOT NULL PRIMARY KEY,
  document LONGTEXT NOT NULL,
  revision INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO t_personal_site(user_name,document,revision) VALUES ('admin', '{"profile":{"name":"林序","englishName":"LIN XU","role":"Java 后端工程师","location":"杭州，中国","email":"linxu@example.com","description":"让复杂的业务，成为可靠的系统。","intro":"我关注后端系统设计、性能优化与工程实践。喜欢把问题拆清楚，把系统做扎实，也把解决过程写下来。","skills":["Java 21","Spring Boot","MySQL","Redis","Docker"],"experiences":[{"period":"2023 — 至今","company":"云栈科技（虚构）","role":"Java 后端工程师","description":"参与订单与库存服务建设，负责接口设计、业务建模和线上问题排查。把可观测性与自动化测试纳入日常开发。"},{"period":"2021 — 2023","company":"木石软件（虚构）","role":"软件开发工程师","description":"从企业管理系统起步，积累数据库设计、权限管理及前后端协作经验。"}],"isExample":true,"interests":"喜欢步行、摄影和阅读。相信好的工程来自细致的观察，也来自愿意不断修正的耐心。"},"projects":[{"slug":"personal-platform","category":"内容平台","name":"一个持续生长的个人空间","subtitle":"个人博客与内容发布平台","description":"从内容发布到评论审核，把写作、作品和个人表达放在同一个地方。","stack":["Java 21","Spring Boot 3.5","Vue 3"],"year":"2026","context":"希望拥有一个可以长期维护的个人网站，兼顾内容发布、作品展示和管理效率。","contribution":"负责前后端实现、数据建模、JWT 认证、评论管理与部署配置。","challenge":"升级 Java 与 Spring Boot 时，认证配置、Jakarta 包名和接口文档需要一起迁移。","result":"通过自动化回归测试验证认证、权限、分页及文档接口；让技术实践与个人展示形成闭环。"},{"slug":"order-service","category":"业务系统","name":"让每一笔订单有迹可循","subtitle":"订单服务与履约流程 · 虚构案例","description":"围绕订单生命周期设计服务边界，处理重复提交、库存协作与异常恢复。","stack":["Spring Boot","Redis","MySQL"],"year":"2025","context":"示例业务需要统一管理下单、支付状态和履约流程。","contribution":"示例职责包括状态机设计、接口幂等处理和异常链路排查。","challenge":"在重复请求与跨服务失败的情况下，保持业务状态可解释、可恢复。","result":"用明确的状态迁移、请求标识和事件记录构建可追踪流程。本案例为虚构展示，没有真实业务数据。"},{"slug":"observability","category":"工程实践","name":"从“报错了”到“找到原因”","subtitle":"服务可观测性实践 · 虚构案例","description":"把分散的日志与指标组织成排查线索，缩短问题定位路径。","stack":["Docker","日志追踪","性能分析"],"year":"2024","context":"示例团队在多服务环境中难以复现和定位间歇性错误。","contribution":"示例职责包括日志结构设计、关键指标定义及排查手册整理。","challenge":"避免只堆积日志，选择真正能解释业务状态的信号。","result":"建立从请求到业务操作的追踪思路，并形成可复用的排查清单。本案例为虚构展示。"}],"notes":[{"slug":"java-migration","category":"工程实践","title":"一次 Java 21 与 Spring Boot 3.5 的迁移复盘","description":"版本升级的背后，是依赖、认证和验证方式的一次重新梳理。","date":"2026.10.12","paragraphs":["这是一篇示例文章，用来展示未来技术复盘的组织方式。","迁移先从依赖关系开始：确认框架版本、数据库驱动、注解处理器与文档工具的兼容范围。Servlet 与参数校验使用 Jakarta 包名，认证使用 SecurityFilterChain。","验证应覆盖真实业务路径：登录成功与失败、权限拒绝、登出失效、分页和接口文档。编译通过只是第一步。","正式发布时，可以补充真实的错误日志、关键修改及测试结果，让读者理解每个决策的理由。"],"published":true},{"slug":"idempotency","category":"系统设计","title":"接口幂等：从重复请求开始思考","description":"比起记住一个方案，更重要的是明确业务允许发生什么。","date":"2026.09.18","paragraphs":["这是一篇虚构的技术文章示例。","用户重复点击、网络重试和消息重复投递，都可能让同一操作执行多次。首先应明确业务中的“同一操作”怎样识别。","唯一约束、请求标识与状态校验各有适用条件。设计时要同时考虑失败后能否重试，以及用户能否得到一致的结果。"],"published":true},{"slug":"slow-query","category":"问题排查","title":"慢查询排查，先找到值得优化的那一条","description":"从执行计划、数据分布与调用频率开始，而不是先加索引。","date":"2026.08.06","paragraphs":["这是一篇虚构的排查笔记示例。","记录查询频率、耗时分布、数据量与执行计划，才能判断问题影响。一次偶发慢查询和高频调用需要不同的优先级。","每次优化都应保留修改前后的测量方式和结果，避免把测试环境中的变化当成生产收益。"],"published":true}]}', 0);

-- 旧个人文章迁移记录，避免删除后的文章在重跑迁移时复活。
CREATE TABLE IF NOT EXISTS t_legacy_note_migration (
 source_user_name VARCHAR(60) NOT NULL, source_slug VARCHAR(80) NOT NULL,
 article_id BIGINT NOT NULL, PRIMARY KEY (source_user_name, source_slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
