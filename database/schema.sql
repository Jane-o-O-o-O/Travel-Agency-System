-- 时拾纪旅行社管理平台 数据库初始化脚本
-- MySQL 8.0

-- 创建数据库
CREATE DATABASE IF NOT EXISTS travel_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE travel_db;

-- ==================== 系统管理模块 ====================

-- 系统用户表
CREATE TABLE sys_user (
  id bigint NOT NULL COMMENT '用户ID',
  username varchar(64) NOT NULL UNIQUE COMMENT '登录名',
  password varchar(255) NOT NULL COMMENT '密码(加密)',
  real_name varchar(64) COMMENT '真实名字',
  email varchar(64) COMMENT '邮箱',
  phone varchar(20) COMMENT '手机号',
  status tinyint DEFAULT 1 COMMENT '状态(0=禁用，1=启用)',
  remark text COMMENT '备注',
  created_at datetime(3) COMMENT '创建时间',
  created_by varchar(64) COMMENT '创建人',
  updated_at datetime(3) COMMENT '更新时间',
  updated_by varchar(64) COMMENT '更新人',
  deleted tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_username (username),
  KEY idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '系统用户';

-- 系统角色表
CREATE TABLE sys_role (
  id bigint NOT NULL COMMENT '角色ID',
  name varchar(64) NOT NULL COMMENT '角色名',
  code varchar(32) NOT NULL UNIQUE COMMENT '角色编码(ADMIN/OPERATOR/PLANNER/CS/FINANCE)',
  status tinyint DEFAULT 1 COMMENT '状态(0=禁用，1=启用)',
  description varchar(255) COMMENT '描述',
  created_at datetime(3) COMMENT '创建时间',
  created_by varchar(64) COMMENT '创建人',
  updated_at datetime(3) COMMENT '更新时间',
  updated_by varchar(64) COMMENT '更新人',
  deleted tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '系统角色';

-- 系统权限表
CREATE TABLE sys_permission (
  id bigint NOT NULL COMMENT '权限ID',
  name varchar(64) NOT NULL COMMENT '权限名称',
  code varchar(64) NOT NULL UNIQUE COMMENT '权限编码',
  resource varchar(32) COMMENT '资源标识',
  action varchar(32) COMMENT '操作标识',
  type varchar(16) COMMENT '权限类型(MENU/BUTTON/API)',
  description varchar(255) COMMENT '描述',
  PRIMARY KEY (id),
  KEY idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '系统权限';

-- 用户-角色关联表
CREATE TABLE sys_user_role (
  id bigint NOT NULL COMMENT 'ID',
  user_id bigint NOT NULL COMMENT '用户ID',
  role_id bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '用户-角色关联';

-- 角色-权限关联表
CREATE TABLE sys_role_permission (
  id bigint NOT NULL COMMENT 'ID',
  role_id bigint NOT NULL COMMENT '角色ID',
  permission_id bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (id),
  KEY idx_role_id (role_id),
  KEY idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '角色-权限关联';

-- ==================== 客户管理模块 ====================

-- 客户表
CREATE TABLE customer (
  id bigint NOT NULL COMMENT '客户ID',
  name varchar(64) NOT NULL COMMENT '客户名',
  phone varchar(20) NOT NULL UNIQUE COMMENT '手机号',
  email varchar(64) COMMENT '邮箱',
  source varchar(32) COMMENT '客户来源',
  budget_level varchar(16) COMMENT '预算档(LOW/MID/HIGH)',
  note text COMMENT '备注',
  last_follow_up varchar(32) COMMENT '最后跟进时间',
  created_at datetime(3) COMMENT '创建时间',
  created_by varchar(64) COMMENT '创建人',
  updated_at datetime(3) COMMENT '更新时间',
  updated_by varchar(64) COMMENT '更新人',
  deleted tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_phone (phone),
  KEY idx_name (name),
  KEY idx_source (source)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '客户信息';

-- 标签表
CREATE TABLE tag (
  id bigint NOT NULL COMMENT '标签ID',
  name varchar(64) NOT NULL COMMENT '标签名称',
  category varchar(32) COMMENT '标签分类(INTEREST/BUDGET/PREFERENCE/SEASON)',
  description varchar(255) COMMENT '描述',
  created_at datetime(3) COMMENT '创建时间',
  created_by varchar(64) COMMENT '创建人',
  updated_at datetime(3) COMMENT '更新时间',
  updated_by varchar(64) COMMENT '更新人',
  deleted tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '客户标签';

-- 客户-标签关联表
CREATE TABLE customer_tag_rel (
  id bigint NOT NULL COMMENT 'ID',
  customer_id bigint NOT NULL COMMENT '客户ID',
  tag_id bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (id),
  KEY idx_customer_id (customer_id),
  KEY idx_tag_id (tag_id),
  UNIQUE KEY uniq_customer_tag (customer_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '客户-标签关联';

-- ==================== 行程模板模块 ====================

-- 行程模板表
CREATE TABLE itinerary_template (
  id bigint NOT NULL COMMENT '模板ID',
  name varchar(128) NOT NULL COMMENT '模板名称',
  theme varchar(64) COMMENT '主题(摄影/亲子/徒步等)',
  days int NOT NULL COMMENT '行程天数',
  description text COMMENT '描述',
  applicable_crowd varchar(255) COMMENT '适用人群',
  status varchar(16) DEFAULT 'DRAFT' COMMENT '状态(DRAFT/PUBLISHED)',
  use_count int DEFAULT 0 COMMENT '使用次数',
  version int DEFAULT 1 COMMENT '版本号',
  created_at datetime(3) COMMENT '创建时间',
  created_by varchar(64) COMMENT '创建人',
  updated_at datetime(3) COMMENT '更新时间',
  updated_by varchar(64) COMMENT '更新人',
  deleted tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_theme (theme),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '行程模板';

-- 行程模板明细表
CREATE TABLE itinerary_template_item (
  id bigint NOT NULL COMMENT '明细ID',
  template_id bigint NOT NULL COMMENT '模板ID',
  day_no int NOT NULL COMMENT '第几天',
  seq int NOT NULL COMMENT '序号',
  time_slot varchar(16) COMMENT '时间段(MORNING/AFTERNOON/EVENING)',
  title varchar(128) COMMENT '标题',
  type varchar(32) COMMENT '类型(SPOT/MEAL/TRAFFIC/HOTEL/FREE)',
  detail text COMMENT '详情',
  estimated_cost decimal(10,2) DEFAULT 0 COMMENT '预估成本',
  PRIMARY KEY (id),
  KEY idx_template_day (template_id, day_no, seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '行程模板明细';

-- ==================== 旅游产品模块 ====================

-- 旅游产品表
CREATE TABLE tour_product (
  id bigint NOT NULL COMMENT '产品ID',
  name varchar(128) NOT NULL COMMENT '产品名称',
  code varchar(64) UNIQUE COMMENT '产品编码',
  template_id bigint COMMENT '模板ID(来源)',
  days int NOT NULL COMMENT '行程天数',
  base_price decimal(10,2) DEFAULT 0 COMMENT '基础价格',
  cost_description text COMMENT '成本结构说明',
  included text COMMENT '包含内容',
  excluded text COMMENT '不含内容',
  notes text COMMENT '注意事项',
  group_rule varchar(255) COMMENT '成团规则',
  highlights text COMMENT '产品亮点',
  status varchar(16) DEFAULT 'DRAFT' COMMENT '状态(DRAFT/PUBLISHED/ON_SALE/OFF_SALE)',
  view_count int DEFAULT 0 COMMENT '访问次数',
  created_at datetime(3) COMMENT '创建时间',
  created_by varchar(64) COMMENT '创建人',
  updated_at datetime(3) COMMENT '更新时间',
  updated_by varchar(64) COMMENT '更新人',
  deleted tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_template_id (template_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '旅游产品';

-- 旅游产品行程明细表
CREATE TABLE tour_product_item (
  id bigint NOT NULL COMMENT '明细ID',
  product_id bigint NOT NULL COMMENT '产品ID',
  day_no int NOT NULL COMMENT '第几天',
  seq int NOT NULL COMMENT '序号',
  time_slot varchar(16) COMMENT '时间段',
  title varchar(128) COMMENT '标题',
  type varchar(32) COMMENT '类型',
  detail text COMMENT '详情',
  cost decimal(10,2) DEFAULT 0 COMMENT '成本',
  PRIMARY KEY (id),
  KEY idx_product_day (product_id, day_no, seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '旅游产品行程明细';

-- ==================== 订单管理模块 ====================

-- 订单表
CREATE TABLE tour_order (
  id bigint NOT NULL COMMENT '订单ID',
  order_no varchar(32) NOT NULL UNIQUE COMMENT '订单号',
  customer_id bigint NOT NULL COMMENT '客户ID',
  product_id bigint COMMENT '产品ID(可为空)',
  start_date date NOT NULL COMMENT '出发日期',
  end_date date NOT NULL COMMENT '返回日期',
  people_count int DEFAULT 1 COMMENT '人数',
  amount decimal(10,2) DEFAULT 0 COMMENT '订单金额',
  pay_status varchar(16) DEFAULT 'UNPAID' COMMENT '支付状态(UNPAID/PARTIAL/PAID)',
  status varchar(16) DEFAULT 'DRAFT' COMMENT '订单状态(DRAFT/PENDING_CONFIRM/CONFIRMED/COMPLETED/CANCELLED)',
  owner_user_id bigint COMMENT '负责人ID',
  remark text COMMENT '备注',
  created_at datetime(3) COMMENT '创建时间',
  created_by varchar(64) COMMENT '创建人',
  updated_at datetime(3) COMMENT '更新时间',
  updated_by varchar(64) COMMENT '更新人',
  deleted tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_order_no (order_no),
  KEY idx_customer_id (customer_id),
  KEY idx_status (status),
  KEY idx_start_date (start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '旅游订单';

-- 订单行程明细表
CREATE TABLE tour_order_item (
  id bigint NOT NULL COMMENT '明细ID',
  order_id bigint NOT NULL COMMENT '订单ID',
  day_no int NOT NULL COMMENT '第几天',
  seq int NOT NULL COMMENT '序号',
  time_slot varchar(16) COMMENT '时间段',
  title varchar(128) COMMENT '标题',
  type varchar(32) COMMENT '类型',
  detail text COMMENT '详情',
  cost decimal(10,2) DEFAULT 0 COMMENT '成本',
  PRIMARY KEY (id),
  KEY idx_order_day (order_id, day_no, seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '订单行程明细';

-- ==================== 资源管理模块 ====================

-- 资源表
CREATE TABLE resource (
  id bigint NOT NULL COMMENT '资源ID',
  name varchar(128) NOT NULL COMMENT '资源名称',
  code varchar(64) UNIQUE COMMENT '资源编码',
  type varchar(32) NOT NULL COMMENT '资源类型(GUIDE/VEHICLE/HOTEL/OTHER)',
  description text COMMENT '详细描述',
  status varchar(16) DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE/INACTIVE)',
  contact varchar(255) COMMENT '联系信息',
  remark text COMMENT '备注',
  created_at datetime(3) COMMENT '创建时间',
  created_by varchar(64) COMMENT '创建人',
  updated_at datetime(3) COMMENT '更新时间',
  updated_by varchar(64) COMMENT '更新人',
  deleted tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_type (type),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '资源(导游/车辆/酒店等)';

-- 资源占用表（关键表，用于冲突检测）
CREATE TABLE resource_booking (
  id bigint NOT NULL COMMENT '占用ID',
  resource_id bigint NOT NULL COMMENT '资源ID',
  order_id bigint NOT NULL COMMENT '订单ID',
  start_time datetime NOT NULL COMMENT '开始时间',
  end_time datetime NOT NULL COMMENT '结束时间',
  status varchar(16) DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE/CANCELLED)',
  created_at datetime(3) COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_resource_time (resource_id, start_time, end_time, status),
  KEY idx_order_id (order_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '资源占用（冲突检测关键表）';

-- ==================== 工单系统模块 ====================

-- 工单表
CREATE TABLE ticket (
  id bigint NOT NULL COMMENT '工单ID',
  ticket_no varchar(32) NOT NULL UNIQUE COMMENT '工单编号',
  order_id bigint COMMENT '关联订单ID',
  customer_id bigint NOT NULL COMMENT '客户ID',
  problem_type varchar(32) COMMENT '问题类型(COMPLAINT/MODIFY/REFUND/CONSULT)',
  priority varchar(16) DEFAULT 'MID' COMMENT '优先级(LOW/MID/HIGH/URGENT)',
  title varchar(128) NOT NULL COMMENT '标题',
  description text COMMENT '描述',
  handler_user_id bigint COMMENT '负责人ID',
  status varchar(16) DEFAULT 'OPEN' COMMENT '状态(OPEN/IN_PROGRESS/RESOLVED/CLOSED)',
  first_response_time int COMMENT 'SLA首次响应时间(分钟)',
  resolution_time int COMMENT 'SLA解决时长(分钟)',
  created_at datetime(3) COMMENT '创建时间',
  created_by varchar(64) COMMENT '创建人',
  updated_at datetime(3) COMMENT '更新时间',
  updated_by varchar(64) COMMENT '更新人',
  deleted tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_ticket_no (ticket_no),
  KEY idx_customer_id (customer_id),
  KEY idx_status (status),
  KEY idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '工单';

-- ==================== 初始化系统数据 ====================

-- 插入默认角色
INSERT INTO sys_role (id, name, code, status, description, created_at, created_by, updated_at, updated_by)
VALUES 
  (1, '管理员', 'ADMIN', 1, '系统管理员', NOW(3), 'system', NOW(3), 'system'),
  (2, '运营', 'OPERATOR', 1, '运营人员', NOW(3), 'system', NOW(3), 'system'),
  (3, '计调', 'PLANNER', 1, '行程计调', NOW(3), 'system', NOW(3), 'system'),
  (4, '客服', 'CS', 1, '客户服务', NOW(3), 'system', NOW(3), 'system'),
  (5, '财务', 'FINANCE', 1, '财务人员', NOW(3), 'system', NOW(3), 'system');

-- 插入默认管理员账户 (密码: admin123, 已加密)
INSERT INTO sys_user (id, username, password, real_name, email, phone, status, created_at, created_by, updated_at, updated_by)
VALUES 
  (1, 'admin', '$2a$10$K0WPKyJp1QqQ7Jv1mQ1PXO5qZjB.0vf3Q0qZjB.0vf3Q0qZjB0.S6', '系统管理员', 'admin@shishiji.com', '18800000000', 1, NOW(3), 'system', NOW(3), 'system');

-- 绑定管理员角色
INSERT INTO sys_user_role (id, user_id, role_id)
VALUES (1, 1, 1);

-- 插入系统权限示例
INSERT INTO sys_permission (id, name, code, resource, action, type, description)
VALUES 
  (1, '客户管理', 'customer:view', 'customer', 'view', 'MENU', '查看客户列表'),
  (2, '客户创建', 'customer:create', 'customer', 'create', 'BUTTON', '创建客户'),
  (3, '订单管理', 'order:view', 'order', 'view', 'MENU', '查看订单列表'),
  (4, '订单确认', 'order:confirm', 'order', 'confirm', 'BUTTON', '确认订单'),
  (5, '产品管理', 'product:view', 'product', 'view', 'MENU', '查看产品列表');
