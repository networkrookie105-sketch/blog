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
    `title`       VARCHAR(120) NOT NULL COMMENT '标题',
    `cover`       VARCHAR(255)          DEFAULT NULL COMMENT '封面图地址',
    `summary`     VARCHAR(300)          DEFAULT NULL COMMENT '摘要',
    `read_num`    BIGINT       NOT NULL DEFAULT 0 COMMENT '阅读量',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-草稿，1-已发布',
    `is_top`      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
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
