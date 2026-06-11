-- 短信通知功能：sys_message_task 表增加 sms_enable 字段
ALTER TABLE sys_message_task ADD COLUMN sms_enable BIT DEFAULT FALSE COMMENT '短信启用' AFTER lark_enable;
