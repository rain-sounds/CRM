package cn.cordys.crm.system.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 阿里云短信发送工具类（基于阿里云官方 SDK）
 * <p>
 * 负责底层短信发送，类似 MailSender 的定位。
 * 配置参数由调用方（SmsNoticeSender）传入。
 */
@Component
@Slf4j
public class AliyunSmsSender {

    private static final String SMS_ENDPOINT = "dysmsapi.aliyuncs.com";

    /**
     * 发送短信
     *
     * @param accessKeyId     阿里云 AccessKey ID
     * @param accessKeySecret 阿里云 AccessKey Secret
     * @param signName        短信签名
     * @param templateCode    短信模板 Code
     * @param phoneNumbers    手机号，多个用逗号分隔
     * @param templateParam   模板变量 JSON
     * @return 是否发送成功
     */
    public boolean send(String accessKeyId, String accessKeySecret,
                        String signName, String templateCode,
                        String phoneNumbers, String templateParam) {
        if (StringUtils.isAnyBlank(accessKeyId, accessKeySecret, signName, templateCode)) {
            log.warn("阿里云短信配置不完整，跳过发送");
            return false;
        }
        if (StringUtils.isBlank(phoneNumbers)) {
            log.warn("短信接收人手机号为空，跳过发送");
            return false;
        }
        try {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint(SMS_ENDPOINT);
            Client client = new Client(config);

            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumbers)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(templateParam);

            SendSmsResponse response = client.sendSms(request);
            boolean success = response.getBody() != null && "OK".equals(response.getBody().getCode());
            if (success) {
                log.info("短信发送成功，手机号: {}", phoneNumbers);
            } else {
                log.error("短信发送失败，手机号: {}，响应: {}", phoneNumbers,
                        response.getBody() != null ? response.getBody().getMessage() : "null");
            }
            return success;
        } catch (Exception e) {
            log.error("短信发送异常，手机号: {}，{}", phoneNumbers, e.getMessage(), e);
            return false;
        }
    }
}
