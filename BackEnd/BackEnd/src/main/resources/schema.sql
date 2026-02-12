CREATE DATABASE IF NOT EXISTS sqms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sqms;

DROP TABLE IF EXISTS `purchase_record`;
DROP TABLE IF EXISTS `supplier_score`;
DROP TABLE IF EXISTS `supplier`;
DROP TABLE IF EXISTS `score_rule`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role_id` (`role_id`),
    CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `score_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `rule_name` VARCHAR(50) NOT NULL COMMENT '规则名称',
    `rule_code` VARCHAR(50) NOT NULL COMMENT '规则编码',
    `weight` DECIMAL(5,2) NOT NULL COMMENT '权重（0-100）',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '规则描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rule_code` (`rule_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评分规则表';

CREATE TABLE `supplier` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
    `supplier_code` VARCHAR(50) NOT NULL COMMENT '供应商编号',
    `supplier_name` VARCHAR(100) NOT NULL COMMENT '供应商名称',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '供应商分类',
    `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `products` VARCHAR(500) DEFAULT NULL COMMENT '合作产品',
    `grade` VARCHAR(10) DEFAULT 'C' COMMENT '供应商等级：A/B/C',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_supplier_code` (`supplier_code`),
    KEY `idx_supplier_name` (`supplier_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

CREATE TABLE `purchase_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
    `quantity` INT NOT NULL COMMENT '数量',
    `unit` VARCHAR(20) DEFAULT '件' COMMENT '单位',
    `inspection_result` TINYINT NOT NULL COMMENT '检验结果：1-合格，0-不合格',
    `unqualified_reason` VARCHAR(200) DEFAULT NULL COMMENT '不合格原因',
    `arrival_date` DATE NOT NULL COMMENT '到货日期',
    `expected_date` DATE DEFAULT NULL COMMENT '预期到货日期',
    `is_delayed` TINYINT DEFAULT 0 COMMENT '是否延迟：1-是，0-否',
    `payment_status` TINYINT DEFAULT 0 COMMENT '付款状态：0-未付款，1-已付款',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_arrival_date` (`arrival_date`),
    CONSTRAINT `fk_purchase_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购与质量记录表';

CREATE TABLE `supplier_score` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评分ID',
    `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
    `quality_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '合格率（%）',
    `ontime_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '准时交货率（%）',
    `service_score` DECIMAL(5,2) DEFAULT 80.00 COMMENT '服务评分',
    `total_score` DECIMAL(5,2) DEFAULT 0.00 COMMENT '总分',
    `is_risk` TINYINT DEFAULT 0 COMMENT '是否风险供应商：1-是，0-否',
    `score_date` DATE NOT NULL COMMENT '评分日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_score_date` (`score_date`),
    KEY `idx_total_score` (`total_score`),
    CONSTRAINT `fk_score_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商评分表';

INSERT INTO `role` (`role_name`, `role_code`, `description`) VALUES
('管理员', 'ADMIN', '系统管理员，拥有所有权限'),
('采购员', 'PURCHASER', '负责采购相关操作'),
('质检员', 'INSPECTOR', '负责质量检验相关操作');

INSERT INTO `user` (`username`, `password`, `real_name`, `role_id`, `phone`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1, '13800138000', 'admin@sqms.com', 1),
('purchaser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '采购员', 2, '13800138001', 'purchaser@sqms.com', 1),
('inspector', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '质检员', 3, '13800138002', 'inspector@sqms.com', 1);

INSERT INTO `score_rule` (`rule_name`, `rule_code`, `weight`, `description`, `status`) VALUES
('合格率', 'QUALITY_RATE', 40.00, '产品质量合格率权重', 1),
('准时交货率', 'ONTIME_RATE', 30.00, '准时交货率权重', 1),
('服务评分', 'SERVICE_SCORE', 30.00, '服务评分权重', 1);

INSERT INTO `supplier` (`supplier_code`, `supplier_name`, `category`, `contact_person`, `contact_phone`, `address`, `products`, `grade`, `status`, `remark`) VALUES
('SUP001', '华兴电子有限公司', '电子元器件', '张经理', '13800138010', '深圳市南山区科技园', '电阻、电容、电感', 'A', 1, '长期合作供应商'),
('SUP002', '宏达机械制造厂', '机械配件', '李总', '13800138011', '东莞市虎门镇', '齿轮、轴承、传动件', 'A', 1, '优质供应商'),
('SUP003', '东方塑料厂', '塑料制品', '王厂长', '13800138012', '佛山市顺德区', '塑料外壳、配件', 'B', 1, '合作稳定'),
('SUP004', '南方化工原料公司', '化工原料', '赵经理', '13800138013', '广州市黄埔区', '溶剂、添加剂', 'B', 1, '价格合理'),
('SUP005', '北方金属制品厂', '金属制品', '刘总', '13800138014', '天津市滨海新区', '金属板材、管材', 'C', 1, '新供应商'),
('SUP006', '华东包装材料厂', '包装材料', '陈经理', '13800138015', '苏州市工业园区', '纸箱、泡沫', 'B', 1, '包装质量好'),
('SUP007', '西部电子元件厂', '电子元件', '杨厂长', '13800138016', '成都市高新区', '二极管、三极管', 'C', 1, '待考察'),
('SUP008', '中部橡胶制品厂', '橡胶制品', '周经理', '13800138017', '武汉市东湖新区', '密封圈、垫片', 'B', 1, '交货及时');

INSERT INTO `purchase_record` (`supplier_id`, `product_name`, `quantity`, `unit`, `inspection_result`, `unqualified_reason`, `arrival_date`, `expected_date`, `is_delayed`, `payment_status`, `remark`) VALUES
(1, '电阻R001', 1000, '个', 1, NULL, '2024-01-05', '2024-01-05', 0, 1, '质量合格'),
(1, '电容C001', 500, '个', 1, NULL, '2024-01-08', '2024-01-08', 0, 1, '质量合格'),
(1, '电感L001', 300, '个', 0, '电感值偏差', '2024-01-10', '2024-01-08', 1, 0, '部分不合格'),
(2, '齿轮G001', 200, '个', 1, NULL, '2024-01-06', '2024-01-06', 0, 1, '质量合格'),
(2, '轴承B001', 150, '个', 1, NULL, '2024-01-09', '2024-01-09', 0, 1, '质量合格'),
(2, '传动件T001', 100, '个', 1, NULL, '2024-01-12', '2024-01-12', 0, 0, '质量合格'),
(3, '塑料外壳P001', 500, '个', 1, NULL, '2024-01-07', '2024-01-07', 0, 1, '质量合格'),
(3, '塑料配件P002', 800, '个', 0, '尺寸不符', '2024-01-11', '2024-01-10', 1, 0, '需返工'),
(4, '溶剂S001', 100, '桶', 1, NULL, '2024-01-04', '2024-01-04', 0, 1, '质量合格'),
(4, '添加剂A001', 50, '桶', 1, NULL, '2024-01-08', '2024-01-08', 0, 1, '质量合格'),
(5, '金属板材M001', 20, '张', 0, '厚度不达标', '2024-01-09', '2024-01-05', 1, 0, '质量不合格'),
(5, '金属管材M002', 30, '根', 1, NULL, '2024-01-13', '2024-01-13', 0, 0, '质量合格'),
(6, '纸箱X001', 1000, '个', 1, NULL, '2024-01-03', '2024-01-03', 0, 1, '质量合格'),
(6, '泡沫F001', 500, '个', 1, NULL, '2024-01-07', '2024-01-07', 0, 1, '质量合格'),
(7, '二极管D001', 200, '个', 0, '反向击穿', '2024-01-10', '2024-01-08', 1, 0, '质量问题'),
(7, '三极管Q001', 150, '个', 1, NULL, '2024-01-14', '2024-01-14', 0, 0, '质量合格'),
(8, '密封圈R001', 300, '个', 1, NULL, '2024-01-05', '2024-01-05', 0, 1, '质量合格'),
(8, '垫片G001', 500, '个', 1, NULL, '2024-01-09', '2024-01-09', 0, 1, '质量合格');

INSERT INTO `supplier_score` (`supplier_id`, `quality_rate`, `ontime_rate`, `service_score`, `total_score`, `is_risk`, `score_date`) VALUES
(1, 66.67, 66.67, 85.00, 71.67, 0, '2024-01-15'),
(2, 100.00, 100.00, 90.00, 97.00, 0, '2024-01-15'),
(3, 50.00, 50.00, 75.00, 57.50, 1, '2024-01-15'),
(4, 100.00, 100.00, 80.00, 94.00, 0, '2024-01-15'),
(5, 50.00, 50.00, 70.00, 56.00, 1, '2024-01-15'),
(6, 100.00, 100.00, 85.00, 95.50, 0, '2024-01-15'),
(7, 50.00, 50.00, 65.00, 54.50, 1, '2024-01-15'),
(8, 100.00, 100.00, 80.00, 94.00, 0, '2024-01-15');
