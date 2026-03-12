-- 用户端门户与工单扩展
-- 执行前请备份。MySQL 8.0

USE travel_db;

-- 门户用户表（用户端账号密码登录）
CREATE TABLE IF NOT EXISTS portal_user (
  id bigint NOT NULL COMMENT '用户ID',
  username varchar(64) NOT NULL UNIQUE COMMENT '登录名',
  password varchar(255) NOT NULL COMMENT '密码(加密)',
  real_name varchar(64) COMMENT '真实姓名',
  phone varchar(20) COMMENT '手机号',
  email varchar(64) COMMENT '邮箱',
  status tinyint DEFAULT 1 COMMENT '状态(0=禁用，1=启用)',
  created_at datetime(3) COMMENT '创建时间',
  updated_at datetime(3) COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_username (username),
  KEY idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '门户用户';

-- 工单表扩展：用户端提交的意向与联系方式（customer_id 改为可空，门户提交时可为空）
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

-- 工单回复表（用户与客服交互）
CREATE TABLE IF NOT EXISTS ticket_reply (
  id bigint NOT NULL COMMENT '回复ID',
  ticket_id bigint NOT NULL COMMENT '工单ID',
  from_type varchar(16) NOT NULL COMMENT '来源(PORTAL/ADMIN)',
  from_user_id bigint NULL COMMENT '用户ID(portal_user_id或sys_user_id)',
  content text NOT NULL COMMENT '回复内容',
  created_at datetime(3) COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_ticket_id (ticket_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '工单回复';
