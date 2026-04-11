# 投稿模块 (Submission) 开发文档

## 1. 数据库层面

已在 `backend\crm\src\main\resources\migration\cust\v2000.sql` 中增加了相关 DDL。
- **submission 表**: 存储基础信息（id, name, product_id, opportunity_id, status, organization_id, create_time, update_time 等）。
- **submission_field 表**: 存储自定义属性。
- **submission_field_blob 表**: 存储自定义属性大文本。
- **权限预置**: 请在 `sys_role_permission` 中为管理员角色（如 `org_admin`）手动添加 `submission:read`, `submission:add`, `submission:update`, `submission:delete` 等权限，确保页面可以正常访问和操作。

## 2. 后端层面

基于现有 `product` 模块的包结构和命名规范开发，避免重复造轮子：
- **Entity**: `Submission`, `SubmissionField`, `SubmissionFieldBlob`。
- **Mapper**: `ExtSubmissionMapper` 及其对应 `ExtSubmissionMapper.xml`，提供了支持多条件搜索和自定义字段过滤的复杂查询 `list`。
- **Service**: 提供了标准的 CRUD 服务 `SubmissionService` 及其实现类 `SubmissionServiceImpl`。内部集成了 `ModuleFormService` 以处理动态表单字段的保存和更新，同时支持业务日志记录。
- **Controller**: `SubmissionController` 提供了 RESTful 接口，所有路径统一前缀为 `/api/submission`。

### 接口清单
- `POST /api/submission/page` : 获取分页列表。
- `GET /api/submission/get/{id}` : 获取详情。
- `POST /api/submission/add` : 新增记录。
- `POST /api/submission/update` : 更新记录。
- `POST /api/submission/batch/update` : 批量更新。
- `GET /api/submission/delete/{id}` : 删除记录。
- `POST /api/submission/batch/delete` : 批量删除。

## 3. 前端层面

- **API 封装**: `frontend/packages/web/src/api/submission.js`，包含所有接口调用。
- **视图组件**: 放置在 `frontend/packages/web/src/views/submission/` 下：
  - `index.vue` : 列表页（支持搜索、状态筛选、分页、权限控制）。
  - `form.vue` : 新增和编辑页（支持名称、产品、商机的必填校验）。
  - `detail.vue` : 详情页。
- **菜单路由**: 请在“系统管理-菜单管理”中新增“投稿”菜单（`path: /submission`, `component: 懒加载 views/submission/index` 等），并将按钮的权限标识分别设置为 `submission:read`、`submission:add` 等。

## 4. 后续扩展点

- 可通过动态表单功能，添加更多非必须字段。名称、产品和商机字段在前端代码中硬编码为表单必填项。
- 如果需要导出功能，可参照 `ProductService` 的 `EasyExcelExporter` 实现 `export` 接口。
- 提交前务必确认 `sys_menu` 表和 `sys_role_permission` 的配置已正确初始化。
