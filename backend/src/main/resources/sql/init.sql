-- 创建数据库
CREATE DATABASE IF NOT EXISTS accompany_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE accompany_db;

-- 病区表
DROP TABLE IF EXISTS `ward`;
CREATE TABLE `ward` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ward_code` varchar(32) NOT NULL COMMENT '病区编码',
  `ward_name` varchar(64) NOT NULL COMMENT '病区名称',
  `bed_count` int NOT NULL DEFAULT 0 COMMENT '床位数',
  `is_isolation` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否隔离病区(0-否 1-是)',
  `max_accompany_per_bed` int NOT NULL DEFAULT 1 COMMENT '每床最多陪护人数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0-停用 1-启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删 1-已删)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ward_code` (`ward_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='病区表';

-- 患者表
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `patient_no` varchar(32) NOT NULL COMMENT '住院号',
  `patient_name` varchar(32) NOT NULL COMMENT '患者姓名',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `gender` tinyint NOT NULL DEFAULT 1 COMMENT '性别(1-男 2-女)',
  `age` int DEFAULT NULL COMMENT '年龄',
  `ward_id` bigint NOT NULL COMMENT '病区ID',
  `bed_no` varchar(32) DEFAULT NULL COMMENT '床号',
  `admission_date` date NOT NULL COMMENT '入院日期',
  `expected_discharge_date` date DEFAULT NULL COMMENT '预计出院日期',
  `diagnosis` varchar(255) DEFAULT NULL COMMENT '诊断',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0-出院 1-在院)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删 1-已删)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_patient_no` (`patient_no`),
  KEY `idx_ward_id` (`ward_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者表';

-- 陪护人员表
DROP TABLE IF EXISTS `accompany_person`;
CREATE TABLE `accompany_person` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `person_name` varchar(32) NOT NULL COMMENT '陪护人员姓名',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `gender` tinyint NOT NULL DEFAULT 1 COMMENT '性别(1-男 2-女)',
  `phone` varchar(11) NOT NULL COMMENT '联系电话',
  `relation` varchar(32) NOT NULL COMMENT '与患者关系',
  `id_card_expire_date` date NOT NULL COMMENT '身份证有效期',
  `address` varchar(255) DEFAULT NULL COMMENT '住址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删 1-已删)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='陪护人员表';

-- 陪护申请表
DROP TABLE IF EXISTS `accompany_apply`;
CREATE TABLE `accompany_apply` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `apply_no` varchar(32) NOT NULL COMMENT '申请单号',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `ward_id` bigint NOT NULL COMMENT '病区ID',
  `person_id` bigint NOT NULL COMMENT '陪护人员ID',
  `apply_reason` varchar(255) DEFAULT NULL COMMENT '申请理由',
  `expected_start_date` date NOT NULL COMMENT '预计开始日期',
  `expected_end_date` date NOT NULL COMMENT '预计结束日期',
  `apply_status` tinyint NOT NULL DEFAULT 0 COMMENT '申请状态(0-待审核 1-审核通过 2-审核拒绝 3-已失效)',
  `audit_user_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `audit_user_name` varchar(32) DEFAULT NULL COMMENT '审核人姓名',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_remark` varchar(255) DEFAULT NULL COMMENT '审核意见',
  `invalid_reason` varchar(255) DEFAULT NULL COMMENT '失效原因',
  `invalid_time` datetime DEFAULT NULL COMMENT '失效时间',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删 1-已删)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_ward_id` (`ward_id`),
  KEY `idx_person_id` (`person_id`),
  KEY `idx_apply_status` (`apply_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='陪护申请表';

-- 陪护证表
DROP TABLE IF EXISTS `accompany_certificate`;
CREATE TABLE `accompany_certificate` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `cert_no` varchar(32) NOT NULL COMMENT '陪护证编号',
  `apply_id` bigint NOT NULL COMMENT '申请ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `ward_id` bigint NOT NULL COMMENT '病区ID',
  `person_id` bigint NOT NULL COMMENT '陪护人员ID',
  `start_date` date NOT NULL COMMENT '有效开始日期',
  `end_date` date NOT NULL COMMENT '有效结束日期',
  `cert_status` tinyint NOT NULL DEFAULT 1 COMMENT '证件状态(0-已失效 1-有效)',
  `issue_user_id` bigint DEFAULT NULL COMMENT '发证人员ID',
  `issue_user_name` varchar(32) DEFAULT NULL COMMENT '发证人员姓名',
  `issue_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发证时间',
  `invalid_reason` varchar(255) DEFAULT NULL COMMENT '失效原因',
  `invalid_time` datetime DEFAULT NULL COMMENT '失效时间',
  `invalid_operator_id` bigint DEFAULT NULL COMMENT '失效操作人ID',
  `invalid_operator_name` varchar(32) DEFAULT NULL COMMENT '失效操作人姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删 1-已删)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cert_no` (`cert_no`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_person_id` (`person_id`),
  KEY `idx_cert_status` (`cert_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='陪护证表';

-- 门禁出入记录表
DROP TABLE IF EXISTS `gate_record`;
CREATE TABLE `gate_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_no` varchar(32) NOT NULL COMMENT '记录编号',
  `cert_id` bigint NOT NULL COMMENT '陪护证ID',
  `cert_no` varchar(32) NOT NULL COMMENT '陪护证编号',
  `person_id` bigint NOT NULL COMMENT '陪护人员ID',
  `person_name` varchar(32) NOT NULL COMMENT '陪护人员姓名',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `ward_id` bigint NOT NULL COMMENT '病区ID',
  `ward_name` varchar(64) NOT NULL COMMENT '病区名称',
  `gate_type` tinyint NOT NULL COMMENT '闸机类型(1-入院 2-出院)',
  `check_result` tinyint NOT NULL COMMENT '查验结果(1-通过 2-拒绝)',
  `refuse_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `gate_user_id` bigint DEFAULT NULL COMMENT '门禁操作员ID',
  `gate_user_name` varchar(32) DEFAULT NULL COMMENT '门禁操作员姓名',
  `check_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '查验时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删 1-已删)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`),
  KEY `idx_cert_id` (`cert_id`),
  KEY `idx_person_id` (`person_id`),
  KEY `idx_check_time` (`check_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='门禁出入记录表';

-- 系统用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_name` varchar(32) NOT NULL COMMENT '用户名',
  `real_name` varchar(32) NOT NULL COMMENT '真实姓名',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `role_type` tinyint NOT NULL COMMENT '角色类型(1-护士站 2-门禁人员 3-管理员)',
  `ward_id` bigint DEFAULT NULL COMMENT '所属病区ID(护士站用)',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0-禁用 1-启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删 1-已删)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 初始化数据
INSERT INTO `ward` (`ward_code`, `ward_name`, `bed_count`, `is_isolation`, `max_accompany_per_bed`, `status`) VALUES
('WARD001', '内科一病区', 40, 0, 1, 1),
('WARD002', '内科二病区', 35, 0, 1, 1),
('WARD003', '外科一病区', 45, 0, 1, 1),
('WARD004', 'ICU重症监护室', 20, 0, 1, 1),
('WARD005', '隔离病区', 30, 1, 1, 1),
('WARD006', '儿科病区', 25, 0, 2, 1);

INSERT INTO `patient` (`patient_no`, `patient_name`, `id_card`, `gender`, `age`, `ward_id`, `bed_no`, `admission_date`, `expected_discharge_date`, `diagnosis`, `status`) VALUES
('P20240001', '张三', '110101199001011234', 1, 34, 1, '01-01', '2024-01-15', '2024-01-25', '高血压', 1),
('P20240002', '李四', '110101198505055678', 2, 39, 1, '01-02', '2024-01-16', '2024-01-20', '糖尿病', 1),
('P20240003', '王五', '110101197808089012', 1, 46, 3, '03-05', '2024-01-10', '2024-01-30', '阑尾炎术后', 1);

INSERT INTO `accompany_person` (`person_name`, `id_card`, `gender`, `phone`, `relation`, `id_card_expire_date`, `address`) VALUES
('张小三', '110101201001011234', 1, '13800138001', '父子', '2030-01-01', '北京市朝阳区'),
('李小花', '110101200505055678', 2, '13800138002', '母女', '2025-05-05', '北京市海淀区'),
('王大锤', '110101199508089012', 1, '13800138003', '兄弟', '2028-08-08', '北京市西城区');

INSERT INTO `sys_user` (`user_name`, `real_name`, `password`, `role_type`, `ward_id`, `phone`, `status`) VALUES
('nurse001', '护士小王', '123456', 1, 1, '13900139001', 1),
('nurse002', '护士小李', '123456', 1, 3, '13900139002', 1),
('gate001', '门禁老张', '123456', 2, NULL, '13900139003', 1),
('admin', '管理员', '123456', 3, NULL, '13900139000', 1);
