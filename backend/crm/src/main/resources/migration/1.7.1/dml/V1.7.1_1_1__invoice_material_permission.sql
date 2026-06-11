-- set innodb lock wait timeout
SET SESSION innodb_lock_wait_timeout = 7200;

-- Init invoice material permissions for org_admin
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'org_admin', 'CONTRACT_INVOICE_MATERIAL:READ');
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'org_admin', 'CONTRACT_INVOICE_MATERIAL:ADD');
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'org_admin', 'CONTRACT_INVOICE_MATERIAL:UPDATE');
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'org_admin', 'CONTRACT_INVOICE_MATERIAL:DELETE');

-- Init invoice material permissions for sales_manager
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'sales_manager', 'CONTRACT_INVOICE_MATERIAL:READ');
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'sales_manager', 'CONTRACT_INVOICE_MATERIAL:ADD');
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'sales_manager', 'CONTRACT_INVOICE_MATERIAL:UPDATE');
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'sales_manager', 'CONTRACT_INVOICE_MATERIAL:DELETE');

-- Init invoice material permissions for sales_staff
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'sales_staff', 'CONTRACT_INVOICE_MATERIAL:READ');
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'sales_staff', 'CONTRACT_INVOICE_MATERIAL:ADD');
INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES (UUID_SHORT(), 'sales_staff', 'CONTRACT_INVOICE_MATERIAL:UPDATE');

SET SESSION innodb_lock_wait_timeout = DEFAULT;
