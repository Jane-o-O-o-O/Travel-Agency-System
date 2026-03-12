-- 工单表补丁：添加用户端相关列（执行一次即可）
-- 若从未执行过 V2_portal_user_and_ticket.sql，请先执行该脚本创建 portal_user、ticket_reply 并改 ticket。
-- 若已执行过 V2 则无需再执行本文件。MySQL 8.0

USE travel_db;

ALTER TABLE ticket
  MODIFY COLUMN customer_id bigint NULL COMMENT '客户ID(管理端关联，门户提交可空)',
  ADD COLUMN portal_user_id bigint NULL COMMENT '门户用户ID' AFTER customer_id,
  ADD COLUMN theme varchar(64) NULL COMMENT '意向主题(非遗/亲子/摄影等)' AFTER title,
  ADD COLUMN people_count int NULL COMMENT '出行人数' AFTER theme,
  ADD COLUMN days int NULL COMMENT '出行天数' AFTER people_count,
  ADD COLUMN expected_date date NULL COMMENT '期望日期' AFTER days,
  ADD COLUMN special_requirement text NULL COMMENT '特殊要求' AFTER expected_date,
  ADD COLUMN contact_info varchar(255) NULL COMMENT '联系方式' AFTER special_requirement,
  ADD KEY idx_portal_user_id (portal_user_id);
