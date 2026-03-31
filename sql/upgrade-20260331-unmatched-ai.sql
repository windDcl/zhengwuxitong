USE gov_qa;

ALTER TABLE unmatched_question
  ADD COLUMN status TINYINT NOT NULL DEFAULT 0 COMMENT '0待处理 1AI已建议 2已转FAQ 3已忽略' AFTER similarity,
  ADD COLUMN ai_suggested_question VARCHAR(500) NULL AFTER status,
  ADD COLUMN ai_suggested_answer TEXT NULL AFTER ai_suggested_question,
  ADD COLUMN ai_suggested_aliases TEXT NULL AFTER ai_suggested_answer,
  ADD COLUMN ai_suggested_category VARCHAR(100) NULL AFTER ai_suggested_aliases,
  ADD COLUMN resolved_faq_id BIGINT NULL AFTER ai_suggested_category,
  ADD COLUMN resolved_at DATETIME NULL AFTER resolved_faq_id;

ALTER TABLE unmatched_question
  ADD INDEX idx_unmatched_status_created (status, created_at);

ALTER TABLE unmatched_question
  ADD CONSTRAINT fk_unmatched_resolved_faq FOREIGN KEY (resolved_faq_id) REFERENCES faq(id);

INSERT INTO system_setting(setting_key, setting_value)
VALUES
('ai.enabled', 'false'),
('ai.base-url', ''),
('ai.api-key', ''),
('ai.model', ''),
('ai.system-prompt', '你是一个谨慎、可靠的政务知识助手。')
ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value);
