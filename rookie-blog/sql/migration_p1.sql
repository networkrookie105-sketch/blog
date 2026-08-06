-- =============================================================
-- rookie-blog P1 功能数据库迁移脚本
-- 适用于「已存在数据」的旧库，在原表基础上增量升级。
-- 全新环境请直接执行 schema.sql，无需本脚本。
-- 说明：MySQL 原生 ALTER 不支持 ADD COLUMN IF NOT EXISTS，
--       若列已存在会报错，可忽略对应语句或按需删除。
-- =============================================================

USE `rookieblog`;

-- -------------------------------------------------------------
-- P1-A 文章草稿/发布状态 + 置顶
-- -------------------------------------------------------------
ALTER TABLE `t_article`
    ADD COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-草稿，1-已发布' AFTER `read_num`,
    ADD COLUMN `is_top` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是' AFTER `status`;

-- 存量文章默认视为已发布
UPDATE `t_article` SET `status` = 1 WHERE `status` IS NULL;

-- 便于前台按状态+置顶+时间排序
ALTER TABLE `t_article` ADD INDEX `idx_status_top_time` (`status`, `is_top`, `create_time`);

-- -------------------------------------------------------------
-- P1-B 评论系统
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_comment` (
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
