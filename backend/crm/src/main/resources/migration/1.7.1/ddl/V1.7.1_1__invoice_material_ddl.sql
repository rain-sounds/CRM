-- ============================================================
-- 开票资料表
-- 功能：管理开票相关资料信息
-- ============================================================

-- set innodb lock wait timeout
SET SESSION innodb_lock_wait_timeout = 7200;

CREATE TABLE invoice_material
(
    `id`                 VARCHAR(32)  NOT NULL COMMENT 'id',
    `sequence`           INT          NOT NULL AUTO_INCREMENT COMMENT '序列',
    `hospital_name`      VARCHAR(255) NOT NULL COMMENT '医院名称',
    `invoice`            VARCHAR(10)  DEFAULT NULL COMMENT '发票（是/否）',
    `verification_proof` VARCHAR(10)  DEFAULT NULL COMMENT '查验证明（是/否）',
    `sample_mailing`     VARCHAR(10)  DEFAULT NULL COMMENT '样品邮寄（是/否）',
    `sample_photo`       VARCHAR(10)  DEFAULT NULL COMMENT '样品照片（是/否）',
    `report`             VARCHAR(10)  DEFAULT NULL COMMENT '报告（是/否）',
    `outbound_order`     VARCHAR(10)  DEFAULT NULL COMMENT '出库单（是/否）',
    `contract`           VARCHAR(10)  DEFAULT NULL COMMENT '合同（是/否）',
    `platform`           VARCHAR(50)  DEFAULT NULL COMMENT '平台（喀斯码/锐竞/医院平台/无）',
    `other_materials`    VARCHAR(500) DEFAULT NULL COMMENT '其他资料',
    `organization_id`    VARCHAR(50)  NOT NULL COMMENT '组织id',
    `create_time`        BIGINT       NOT NULL COMMENT '创建时间',
    `update_time`        BIGINT       NOT NULL COMMENT '更新时间',
    `create_user`        VARCHAR(32)  NOT NULL COMMENT '创建人',
    `update_user`        VARCHAR(32)  NOT NULL COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sequence (sequence)
) COMMENT = '开票资料'
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_invoice_material_org_id ON invoice_material (organization_id ASC);
CREATE INDEX idx_invoice_material_hospital ON invoice_material (hospital_name ASC);

SET SESSION innodb_lock_wait_timeout = DEFAULT;
