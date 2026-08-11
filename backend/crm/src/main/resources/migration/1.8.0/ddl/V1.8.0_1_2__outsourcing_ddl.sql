-- ============================================================
-- 外包模块表
-- 功能：管理实验外部外包信息
-- ============================================================

-- set innodb lock wait timeout
SET SESSION innodb_lock_wait_timeout = 7200;

CREATE TABLE outsourcing
(
    `id`                    VARCHAR(32)    NOT NULL COMMENT 'id',
    `internal_project_no`   VARCHAR(255)   DEFAULT NULL COMMENT '内部项目编号',
    `project_source`        VARCHAR(255)   DEFAULT NULL COMMENT '项目来源',
    `experiment_content`    VARCHAR(2000)  DEFAULT NULL COMMENT '项目实验内容',
    `experiment_type`       VARCHAR(255)   DEFAULT NULL COMMENT '实验类型',
    `outsourcing_vendor`    VARCHAR(255)   DEFAULT NULL COMMENT '外包商',
    `deal_price`            DECIMAL(20,10) DEFAULT NULL COMMENT '成交价格',
    `outsourcing_amount`    DECIMAL(20,10) DEFAULT NULL COMMENT '外包金额',
    `outsourcing_time`      BIGINT         DEFAULT NULL COMMENT '外包时间',
    `result_return_time`    BIGINT         DEFAULT NULL COMMENT '结果返回时间',
    `settlement_time`       BIGINT         DEFAULT NULL COMMENT '结算时间',
    `settlement_amount`     DECIMAL(20,10) DEFAULT NULL COMMENT '结算金额',
    `follow_up_process`     VARCHAR(500)   DEFAULT NULL COMMENT '后续流程',
    `owner`                 VARCHAR(32)    DEFAULT NULL COMMENT '负责人',
    `organization_id`       VARCHAR(50)    NOT NULL COMMENT '组织id',
    `create_time`           BIGINT         NOT NULL COMMENT '创建时间',
    `update_time`           BIGINT         NOT NULL COMMENT '更新时间',
    `create_user`           VARCHAR(32)    NOT NULL COMMENT '创建人',
    `update_user`           VARCHAR(32)    NOT NULL COMMENT '更新人',
    PRIMARY KEY (id)
) COMMENT = '外包'
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_outsourcing_owner ON outsourcing (owner ASC);
CREATE INDEX idx_outsourcing_create_time ON outsourcing (create_time ASC);
CREATE INDEX idx_outsourcing_organization_id ON outsourcing (organization_id ASC);

CREATE TABLE outsourcing_field
(
    `id`          VARCHAR(32)  NOT NULL COMMENT 'id',
    `resource_id` VARCHAR(32)  NOT NULL COMMENT '外包id',
    `field_id`    VARCHAR(32)  NOT NULL COMMENT '自定义属性id',
    `field_value` VARCHAR(255) NOT NULL COMMENT '自定义属性值',
    PRIMARY KEY (id)
) COMMENT = '外包自定义属性'
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_outsourcing_field_resource_id ON outsourcing_field (resource_id ASC);

CREATE TABLE outsourcing_field_blob
(
    `id`          VARCHAR(32) NOT NULL COMMENT 'id',
    `resource_id` VARCHAR(32) NOT NULL COMMENT '外包id',
    `field_id`    VARCHAR(32) NOT NULL COMMENT '自定义属性id',
    `field_value` TEXT        NOT NULL COMMENT '自定义属性值',
    PRIMARY KEY (id)
) COMMENT = '外包自定义属性大文本'
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_outsourcing_field_blob_resource_id ON outsourcing_field_blob (resource_id ASC);

SET SESSION innodb_lock_wait_timeout = DEFAULT;
