-- ============================================================
-- 项目阶段关联部门权限（支持多部门）
-- 功能：项目到达某阶段后，关联部门的部门负责人和部门成员可以看到该项目
-- department_id 存储逗号分隔的多个部门ID，如 "dept1,dept2"
-- ============================================================

-- 1. 阶段配置表增加"关联部门"字段（如果不存在则新增，已存在则修改为支持多部门的长度）
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'opportunity_stage_config'
    AND COLUMN_NAME = 'department_id'
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE opportunity_stage_config ADD COLUMN department_id VARCHAR(1000) DEFAULT NULL COMMENT ''关联部门ID（多值逗号分隔）'' AFTER pos',
    'ALTER TABLE opportunity_stage_config MODIFY COLUMN department_id VARCHAR(1000) DEFAULT NULL COMMENT ''关联部门ID（多值逗号分隔）'''
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
