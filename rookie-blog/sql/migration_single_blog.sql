-- MySQL 5.7+：旧个人文章迁入完整后台。重复执行不重复导入、不覆盖后续编辑。
-- 执行前备份数据库。原 notes JSON 留作备份，不删除其他账号或资料。
USE rookieblog;
SET @blog_owner = 'admin';
ALTER TABLE t_article MODIFY title VARCHAR(150) NOT NULL, MODIFY summary TEXT;
SET @add_legacy_column = IF((SELECT COUNT(*) FROM information_schema.columns
 WHERE table_schema = DATABASE() AND table_name = 't_article' AND column_name = 'legacy_note_slug') = 0,
 'ALTER TABLE t_article ADD COLUMN legacy_note_slug VARCHAR(80) NULL, ADD UNIQUE KEY uk_legacy_note_slug (legacy_note_slug)', 'SELECT 1');
PREPARE migration_statement FROM @add_legacy_column;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;
CREATE TABLE IF NOT EXISTS t_legacy_note_migration (
 source_user_name VARCHAR(60) NOT NULL, source_slug VARCHAR(80) NOT NULL,
 article_id BIGINT NOT NULL, PRIMARY KEY (source_user_name, source_slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
DROP PROCEDURE IF EXISTS migrate_single_blog_notes;
DELIMITER $$
CREATE PROCEDURE migrate_single_blog_notes()
BEGIN
 DECLARE source_document JSON;
 DECLARE source_notes JSON;
 DECLARE source_note JSON;
 DECLARE source_paragraphs JSON;
 DECLARE note_index INT DEFAULT 0;
 DECLARE paragraph_index INT DEFAULT 0;
 DECLARE note_slug VARCHAR(80);
 DECLARE category_name VARCHAR(60);
 DECLARE article_title VARCHAR(150);
 DECLARE article_summary LONGTEXT;
 DECLARE article_body LONGTEXT;
 DECLARE article_date VARCHAR(30);
 DECLARE target_article_id BIGINT;
 DECLARE target_category_id BIGINT;
 DECLARE target_tag_id BIGINT;
 DECLARE already_migrated INT;
 DECLARE EXIT HANDLER FOR SQLEXCEPTION
 BEGIN
  ROLLBACK;
  RESIGNAL;
 END;
 START TRANSACTION;
 SELECT document INTO source_document FROM t_personal_site WHERE CAST(user_name AS BINARY) = CAST(@blog_owner AS BINARY) FOR UPDATE;
 SET source_notes = COALESCE(JSON_EXTRACT(source_document, '$.notes'), JSON_ARRAY());
 WHILE note_index < JSON_LENGTH(source_notes) DO
  SET source_note = JSON_EXTRACT(source_notes, CONCAT('$[', note_index, ']'));
  SET note_slug = JSON_UNQUOTE(JSON_EXTRACT(source_note, '$.slug'));
  SELECT COUNT(*) INTO already_migrated FROM t_legacy_note_migration
    WHERE CAST(source_user_name AS BINARY) = CAST(@blog_owner AS BINARY) AND CAST(source_slug AS BINARY) = CAST(note_slug AS BINARY);
  IF already_migrated = 0 THEN
   SET article_title = JSON_UNQUOTE(JSON_EXTRACT(source_note, '$.title'));
   SET article_summary = COALESCE(JSON_UNQUOTE(JSON_EXTRACT(source_note, '$.description')), '');
   SET category_name = COALESCE(NULLIF(JSON_UNQUOTE(JSON_EXTRACT(source_note, '$.category')), ''), '个人记录');
   SET article_date = REPLACE(JSON_UNQUOTE(JSON_EXTRACT(source_note, '$.date')), '.', '-');
   SET source_paragraphs = COALESCE(JSON_EXTRACT(source_note, '$.paragraphs'), JSON_ARRAY());
   SET article_body = '';
   SET paragraph_index = 0;
   WHILE paragraph_index < JSON_LENGTH(source_paragraphs) DO
    SET article_body = CONCAT(article_body, IF(paragraph_index = 0, '', '\n\n'), JSON_UNQUOTE(JSON_EXTRACT(source_paragraphs, CONCAT('$[', paragraph_index, ']'))));
    SET paragraph_index = paragraph_index + 1;
   END WHILE;
   SELECT MIN(id) INTO target_category_id FROM t_category WHERE CAST(name AS BINARY) = CAST(category_name AS BINARY);
   IF target_category_id IS NULL THEN
    INSERT INTO t_category(name) VALUES(category_name);
    SET target_category_id = LAST_INSERT_ID();
   END IF;
   SELECT MIN(id) INTO target_tag_id FROM t_tag WHERE name = '个人记录';
   IF target_tag_id IS NULL THEN
    INSERT INTO t_tag(name) VALUES('个人记录');
    SET target_tag_id = LAST_INSERT_ID();
   END IF;
   SELECT MIN(id) INTO target_article_id FROM t_article WHERE CAST(legacy_note_slug AS BINARY) = CAST(note_slug AS BINARY);
   IF target_article_id IS NULL THEN
    INSERT INTO t_article(title, cover, summary, status, is_top, read_num, create_time, legacy_note_slug)
      VALUES(article_title, '', article_summary, IF(JSON_UNQUOTE(JSON_EXTRACT(source_note, '$.published')) = 'true', 1, 0), 0, 0,
      CASE WHEN article_date REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}$' THEN STR_TO_DATE(article_date, '%Y-%m-%d') ELSE CURRENT_TIMESTAMP END, note_slug);
    SET target_article_id = LAST_INSERT_ID();
   END IF;
   IF NOT EXISTS (SELECT 1 FROM t_article_content WHERE article_id = target_article_id) THEN
    INSERT INTO t_article_content(article_id, content) VALUES(target_article_id, article_body);
   END IF;
   IF NOT EXISTS (SELECT 1 FROM t_article_category_rel WHERE article_id = target_article_id) THEN
    INSERT INTO t_article_category_rel(article_id, category_id) VALUES(target_article_id, target_category_id);
   END IF;
   IF NOT EXISTS (SELECT 1 FROM t_article_tag_rel WHERE article_id = target_article_id) THEN
    INSERT INTO t_article_tag_rel(article_id, tag_id) VALUES(target_article_id, target_tag_id);
   END IF;
   INSERT INTO t_legacy_note_migration(source_user_name, source_slug, article_id) VALUES(@blog_owner, note_slug, target_article_id);
  END IF;
  SET note_index = note_index + 1;
 END WHILE;
 COMMIT;
END$$
DELIMITER ;
CALL migrate_single_blog_notes();
DROP PROCEDURE migrate_single_blog_notes;
