package cn.cordys.crm.system.notice.sender.sms;

import cn.cordys.crm.system.domain.User;
import cn.cordys.crm.system.dto.MessageDetailDTO;
import cn.cordys.crm.system.notice.common.NoticeModel;
import cn.cordys.crm.system.notice.common.Receiver;
import cn.cordys.crm.system.notice.sender.AbstractNoticeSender;
import cn.cordys.crm.system.utils.AliyunSmsSender;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 短信通知发送器
 * <p>
 * 继承 AbstractNoticeSender，参照 MailNoticeSender 的设计模式：
 * 1. 调用 super 处理模板和接收人
 * 2. 从 User 获取手机号
 * 3. 委托 AliyunSmsSender 发送
 */
@Component
@Slf4j
public class SmsNoticeSender extends AbstractNoticeSender {

    @Resource
    private AliyunSmsSender aliyunSmsSender;

    @Value("${sms.aliyun.access-key-id:}")
    private String accessKeyId;

    @Value("${sms.aliyun.access-key-secret:}")
    private String accessKeySecret;

    @Value("${sms.aliyun.sign-name:}")
    private String signName;

    @Value("${sms.aliyun.template-code:}")
    private String templateCode;

    @Override
    public void send(MessageDetailDTO messageDetailDTO, NoticeModel noticeModel) {
        String context = super.getContext(messageDetailDTO, noticeModel);
        try {
            sendSms(context, noticeModel, messageDetailDTO.getOrganizationId());
            log.debug("发送短信结束");
        } catch (Exception e) {
            log.error("短信发送失败: {}", e.getMessage(), e);
        }
    }

    private void sendSms(String context, NoticeModel noticeModel, String organizationId) {
        // 1. 获取接收人（排除自己）
        List<Receiver> receivers = super.getReceivers(noticeModel.getReceivers(), noticeModel.isExcludeSelf(), noticeModel.getOperator());
        if (CollectionUtils.isEmpty(receivers)) {
            return;
        }

        // 2. 获取用户手机号
        List<String> userIds = receivers.stream()
                .map(Receiver::getUserId)
                .distinct()
                .collect(Collectors.toList());
        List<User> users = super.getUsers(userIds, organizationId);
        String phoneNumbers = users.stream()
                .map(User::getPhone)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.joining(","));

        if (StringUtils.isBlank(phoneNumbers)) {
            log.warn("短信接收人手机号为空，跳过发送");
            return;
        }

        // 3. 构建模板变量
        Map<String, String> templateParam = new HashMap<>();
        templateParam.put("content", context);
        String templateParamJson = JSON.toJSONString(templateParam);

        // 4. 委托工具类发送
        aliyunSmsSender.send(accessKeyId, accessKeySecret, signName, templateCode, phoneNumbers, templateParamJson);
    }
}
