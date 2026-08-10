package cn.cordys.crm.outsourcing.service;

import cn.cordys.common.constants.FormKey;
import cn.cordys.common.service.BaseResourceFieldService;
import cn.cordys.crm.outsourcing.domain.OutsourcingField;
import cn.cordys.crm.outsourcing.domain.OutsourcingFieldBlob;
import cn.cordys.mybatis.BaseMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 外包自定义字段服务
 *
 * @author ls
 * @date 2026-06-11
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OutsourcingFieldService extends BaseResourceFieldService<OutsourcingField, OutsourcingFieldBlob> {

    @Resource
    private BaseMapper<OutsourcingField> outsourcingFieldMapper;
    @Resource
    private BaseMapper<OutsourcingFieldBlob> outsourcingFieldBlobMapper;

    @Override
    protected String getFormKey() {
        return FormKey.OUTSOURCING.getKey();
    }

    @Override
    protected BaseMapper<OutsourcingField> getResourceFieldMapper() {
        return outsourcingFieldMapper;
    }

    @Override
    protected BaseMapper<OutsourcingFieldBlob> getResourceFieldBlobMapper() {
        return outsourcingFieldBlobMapper;
    }
}
