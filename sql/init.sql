-- 基于语义匹配的政务智能问答系统 初始化脚本
-- MySQL 8+

CREATE DATABASE IF NOT EXISTS gov_qa DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gov_qa;

CREATE TABLE IF NOT EXISTS admin (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL UNIQUE,
  sort_order INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS faq (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL,
  standard_question VARCHAR(500) NOT NULL,
  standard_answer TEXT NOT NULL,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_faq_category FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE IF NOT EXISTS faq_alias (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  faq_id BIGINT NOT NULL,
  alias_question VARCHAR(500) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_alias_faq FOREIGN KEY (faq_id) REFERENCES faq(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS announcement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  publish_time DATETIME NOT NULL,
  is_top TINYINT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS qa_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_question VARCHAR(500) NOT NULL,
  matched_question VARCHAR(500) NULL,
  matched_faq_id BIGINT NULL,
  similarity DECIMAL(5,4) NULL,
  is_hit TINYINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_qalog_created_at (created_at),
  INDEX idx_qalog_hit_created (is_hit, created_at),
  CONSTRAINT fk_qalog_faq FOREIGN KEY (matched_faq_id) REFERENCES faq(id)
);

CREATE TABLE IF NOT EXISTS unmatched_question (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_question VARCHAR(500) NOT NULL,
  similarity DECIMAL(5,4) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_unmatched_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS system_setting (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  setting_key VARCHAR(100) NOT NULL UNIQUE,
  setting_value VARCHAR(500) NOT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 索引补充
CREATE INDEX idx_faq_category_status ON faq(category_id, status);
CREATE INDEX idx_faq_status ON faq(status);
CREATE INDEX idx_alias_faq_id ON faq_alias(faq_id);
CREATE INDEX idx_announcement_pub ON announcement(publish_time);
CREATE INDEX idx_announcement_top_pub ON announcement(is_top, publish_time);

-- 初始化数据
INSERT INTO admin(username, password_hash, status)
VALUES ('admin', '$2a$10$HEQw/WXshbywyFUsO9iOv..WxK0T7KiWpnNhCSjc9pUYLPzo7rdYC', 1)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;
-- 默认密码：Admin@123（BCrypt）

INSERT INTO category(name, sort_order, status)
VALUES ('社保', 1, 1), ('公积金', 2, 1), ('计划生育', 3, 1)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO system_setting(setting_key, setting_value)
VALUES ('semantic.threshold', '0.30')
ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value);
