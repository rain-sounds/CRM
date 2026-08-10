背景：
- 原来"商机"模块现在改名成"项目"；

## 项目权限逻辑

### 1. 原有权限
- 项目的 owner（负责人）、follower（跟进人）等可以直接看到

### 2. 阶段负责人权限
- **定义**：项目上指定的某部门负责人（如"临床负责人"、"生信负责人"）
- **存储**：`opportunity_field` 表（项目自定义字段值）
- **字段定义**：`sys_module_field` 表，`type=MEMBER`
- **关联方式**：字段名包含部门名称（如"生信负责人"包含"生信"）
- **权限范围**：只负责**该项目**在该阶段的工作

### 3. 阶段部门负责人权限
- **定义**：阶段关联部门的部门负责人
- **存储**：`sys_department_commander` 表
- **关联方式**：阶段配置的 `department_id` 字段
- **权限范围**：负责**所有项目**在该阶段的管理

### 相关表
- `opportunity_stage_config`：阶段配置，`department_id` 字段存储关联部门（多个用逗号分隔）
- `sys_department_commander`：部门负责人表
- `opportunity_field`：项目自定义字段，存储各部门的指定负责人
- `sys_module_field`：字段定义表，`type=MEMBER` 的字段是人员类型

### SQL 逻辑（ExtOpportunityMapper.xml）
```sql
-- 原有权限
o.owner = #{userId}

-- 阶段部门负责人
OR EXISTS (
    SELECT 1 FROM sys_department_commander dc 
    WHERE FIND_IN_SET(dc.department_id, osc.department_id) 
    AND dc.user_id = #{userId}
)

-- 阶段负责人（按字段名包含部门名关联）
OR EXISTS (
    SELECT 1 FROM opportunity_field of
    JOIN sys_module_field smf ON of.field_id = smf.id
    JOIN sys_department d ON FIND_IN_SET(d.id, osc.department_id)
    WHERE of.resource_id = o.id
    AND smf.name LIKE CONCAT('%', d.name, '%')
    AND smf.type = 'MEMBER'
    AND of.field_value = #{userId}
)
```

### 代码改动
- **文件**：`backend/crm/src/main/java/cn/cordys/crm/opportunity/mapper/ExtOpportunityMapper.xml`
- **位置**：第 83-92 行（`list` 查询的权限逻辑）
- **改动**：将原来的部门成员检查（`sys_organization_user`）改为阶段负责人检查（`opportunity_field` + `sys_module_field`）
