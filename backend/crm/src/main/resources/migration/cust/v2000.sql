DROP TABLE IF EXISTS submission;
CREATE TABLE submission
(
    `id`              VARCHAR(32)  NOT NULL COMMENT 'id',
    `name`            VARCHAR(255) NOT NULL COMMENT '名称',
    `product_id`      VARCHAR(32)  NOT NULL COMMENT '期刊id',
    `opportunity_id`  VARCHAR(32)  NOT NULL COMMENT '商机id',
    `status`          VARCHAR(32)  NOT NULL COMMENT '状态',
    `organization_id` VARCHAR(32)  NOT NULL COMMENT '组织机构id',
    `create_time`     BIGINT       NOT NULL COMMENT '创建时间',
    `update_time`     BIGINT       NOT NULL COMMENT '更新时间',
    `create_user`     VARCHAR(32)  NOT NULL COMMENT '创建人',
    `update_user`     VARCHAR(32)  NOT NULL COMMENT '更新人',
    PRIMARY KEY (id)
) COMMENT = '投稿'
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_organization_id ON submission (organization_id ASC);
CREATE INDEX idx_name ON submission (name ASC);

DROP TABLE IF EXISTS submission_field;
CREATE TABLE submission_field
(
    `id`          VARCHAR(32)  NOT NULL COMMENT 'id',
    `resource_id` VARCHAR(32)  NOT NULL COMMENT '投稿id',
    `field_id`    VARCHAR(32)  NOT NULL COMMENT '自定义属性id',
    `field_value` VARCHAR(255) NOT NULL COMMENT '自定义属性值',
    PRIMARY KEY (id)
) COMMENT = '投稿自定义属性'
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_resource_id_field_id_field_value ON submission_field (resource_id, field_id, field_value);

DROP TABLE IF EXISTS submission_field_blob;
CREATE TABLE submission_field_blob
(
    `id`          VARCHAR(32) NOT NULL COMMENT 'id',
    `resource_id` VARCHAR(32) NOT NULL COMMENT '投稿id',
    `field_id`    VARCHAR(32) NOT NULL COMMENT '自定义属性id',
    `field_value` TEXT        NOT NULL COMMENT '自定义属性值',
    PRIMARY KEY (id)
) COMMENT = '投稿自定义属性大文本'
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_resource_id ON submission_field_blob (resource_id);

INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES (UUID_SHORT(), 'org_admin', 'submission:read');
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES (UUID_SHORT(), 'org_admin', 'submission:add');
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES (UUID_SHORT(), 'org_admin', 'submission:update');
INSERT INTO sys_role_permission (id, role_id, permission_id) VALUES (UUID_SHORT(), 'org_admin', 'submission:delete');

-- 插入投稿模块记录
INSERT INTO sys_module (id, organization_id, module_key, enable, pos, create_user, create_time, update_user, update_time) 
VALUES (UUID_SHORT(), '100001', 'submission', true, 11, 'admin', UNIX_TIMESTAMP() * 1000, 'admin', UNIX_TIMESTAMP() * 1000);
