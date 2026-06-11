package cn.cordys.crm.contract.service;

import cn.cordys.aspectj.annotation.OperationLog;
import cn.cordys.aspectj.constants.LogModule;
import cn.cordys.aspectj.constants.LogType;
import cn.cordys.aspectj.context.OperationLogContext;
import cn.cordys.aspectj.dto.LogContextInfo;
import cn.cordys.common.exception.GenericException;
import cn.cordys.common.pager.PageUtils;
import cn.cordys.common.pager.Pager;
import cn.cordys.common.service.BaseService;
import cn.cordys.common.uid.IDGenerator;
import cn.cordys.common.util.BeanUtils;
import cn.cordys.common.util.Translator;
import cn.cordys.crm.contract.domain.InvoiceMaterial;
import cn.cordys.crm.contract.dto.request.InvoiceMaterialAddRequest;
import cn.cordys.crm.contract.dto.request.InvoiceMaterialPageRequest;
import cn.cordys.crm.contract.dto.request.InvoiceMaterialUpdateRequest;
import cn.cordys.crm.contract.dto.response.InvoiceMaterialListResponse;
import cn.cordys.crm.contract.mapper.ExtInvoiceMaterialMapper;
import cn.cordys.mybatis.BaseMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class InvoiceMaterialService {

    @Resource
    private BaseMapper<InvoiceMaterial> invoiceMaterialMapper;
    @Resource
    private ExtInvoiceMaterialMapper extInvoiceMaterialMapper;
    @Resource
    private BaseService baseService;

    /**
     * 添加开票资料
     */
    @OperationLog(module = LogModule.CONTRACT_INVOICE_MATERIAL, type = LogType.ADD, resourceName = "{#request.hospitalName}")
    public InvoiceMaterial add(InvoiceMaterialAddRequest request, String userId, String orgId) {
        InvoiceMaterial invoiceMaterial = BeanUtils.copyBean(new InvoiceMaterial(), request);
        invoiceMaterial.setId(IDGenerator.nextStr());
        invoiceMaterial.setOrganizationId(orgId);
        invoiceMaterial.setCreateTime(System.currentTimeMillis());
        invoiceMaterial.setCreateUser(userId);
        invoiceMaterial.setUpdateTime(System.currentTimeMillis());
        invoiceMaterial.setUpdateUser(userId);

        // 自动生成序列号
        Integer maxSequence = extInvoiceMaterialMapper.getMaxSequence(orgId);
        invoiceMaterial.setSequence(maxSequence == null ? 1 : maxSequence + 1);

        invoiceMaterialMapper.insert(invoiceMaterial);

        OperationLogContext.setContext(
                LogContextInfo.builder()
                        .resourceId(invoiceMaterial.getId())
                        .resourceName(invoiceMaterial.getHospitalName())
                        .modifiedValue(invoiceMaterial)
                        .build()
        );
        return invoiceMaterial;
    }

    /**
     * 更新开票资料
     */
    @OperationLog(module = LogModule.CONTRACT_INVOICE_MATERIAL, type = LogType.UPDATE, resourceId = "{#request.id}")
    public InvoiceMaterial update(InvoiceMaterialUpdateRequest request, String userId, String orgId) {
        InvoiceMaterial oldMaterial = checkExist(request.getId());

        InvoiceMaterial newMaterial = BeanUtils.copyBean(new InvoiceMaterial(), request);
        newMaterial.setUpdateTime(System.currentTimeMillis());
        newMaterial.setUpdateUser(userId);
        invoiceMaterialMapper.update(newMaterial);

        OperationLogContext.setContext(
                LogContextInfo.builder()
                        .resourceName(request.getHospitalName())
                        .originalValue(oldMaterial)
                        .modifiedValue(newMaterial)
                        .build()
        );
        return newMaterial;
    }

    /**
     * 删除开票资料
     */
    @OperationLog(module = LogModule.CONTRACT_INVOICE_MATERIAL, type = LogType.DELETE, resourceId = "{#id}")
    public void delete(String id) {
        InvoiceMaterial invoiceMaterial = checkExist(id);
        invoiceMaterialMapper.deleteByPrimaryKey(id);
        OperationLogContext.setResourceName(invoiceMaterial.getHospitalName());
    }

    /**
     * 列表
     */
    public Pager<List<InvoiceMaterialListResponse>> list(InvoiceMaterialPageRequest request, String orgId) {
        Page<Object> page = PageHelper.startPage(request.getCurrent(), request.getPageSize());
        List<InvoiceMaterialListResponse> list = extInvoiceMaterialMapper.list(request, orgId);
        baseService.setCreateAndUpdateUserName(list);
        return PageUtils.setPageInfo(page, list);
    }

    /**
     * 详情
     */
    public InvoiceMaterialListResponse get(String id) {
        InvoiceMaterial invoiceMaterial = invoiceMaterialMapper.selectByPrimaryKey(id);
        if (invoiceMaterial == null) {
            throw new GenericException(Translator.get("invoice_material.not.exist"));
        }
        InvoiceMaterialListResponse response = BeanUtils.copyBean(new InvoiceMaterialListResponse(), invoiceMaterial);
        baseService.setCreateAndUpdateUserName(List.of(response));
        return response;
    }

    private InvoiceMaterial checkExist(String id) {
        InvoiceMaterial invoiceMaterial = invoiceMaterialMapper.selectByPrimaryKey(id);
        if (invoiceMaterial == null) {
            throw new GenericException(Translator.get("invoice_material.not.exist"));
        }
        return invoiceMaterial;
    }
}
