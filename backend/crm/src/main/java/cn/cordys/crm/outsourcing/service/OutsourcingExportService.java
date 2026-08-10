package cn.cordys.crm.outsourcing.service;

import cn.cordys.common.constants.FormKey;
import cn.cordys.common.dto.ExportDTO;
import cn.cordys.common.dto.ExportHeadDTO;
import cn.cordys.common.resolver.field.AbstractModuleFieldResolver;
import cn.cordys.common.resolver.field.ModuleFieldResolverFactory;
import cn.cordys.common.service.BaseExportService;
import cn.cordys.common.util.TimeUtils;
import cn.cordys.crm.outsourcing.dto.request.OutsourcingPageRequest;
import cn.cordys.crm.outsourcing.dto.response.OutsourcingListResponse;
import cn.cordys.crm.outsourcing.mapper.ExtOutsourcingMapper;
import cn.cordys.crm.system.dto.field.base.BaseField;
import cn.cordys.registry.ExportThreadRegistry;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 外包导出服务
 *
 * @author ls
 * @date 2026-06-11
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class OutsourcingExportService extends BaseExportService {

    @Resource
    private OutsourcingService outsourcingService;
    @Resource
    private ExtOutsourcingMapper extOutsourcingMapper;

    @Override
    public List<List<Object>> getExportData(String taskId, ExportDTO exportDTO) throws InterruptedException {
        OutsourcingPageRequest pageRequest = (OutsourcingPageRequest) exportDTO.getPageRequest();
        String orgId = exportDTO.getOrgId();
        PageHelper.startPage(pageRequest.getCurrent(), pageRequest.getPageSize());
        List<OutsourcingListResponse> allList = extOutsourcingMapper.list(pageRequest, exportDTO.getUserId(), orgId, exportDTO.getDeptDataPermission());
        List<OutsourcingListResponse> dataList = outsourcingService.buildListData(allList, orgId);
        Map<String, BaseField> fieldConfigMap = getFieldConfigMap(FormKey.OUTSOURCING.getKey(), orgId);

        List<List<Object>> data = new ArrayList<>();
        for (OutsourcingListResponse response : dataList) {
            if (ExportThreadRegistry.isInterrupted(taskId)) {
                throw new InterruptedException("线程已被中断，主动退出");
            }
            List<Object> value = buildData(exportDTO.getHeadList(), response, fieldConfigMap);
            data.add(value);
        }

        return data;
    }

    private List<Object> buildData(List<ExportHeadDTO> headList, OutsourcingListResponse data, Map<String, BaseField> fieldConfigMap) {
        List<Object> dataList = new ArrayList<>();
        LinkedHashMap<String, Object> systemFieldMap = getSystemFieldMap(data, fieldConfigMap);
        Map<String, Object> moduleFieldMap = getFieldIdValueMap(data.getModuleFields());
        return transModuleFieldValue(headList, systemFieldMap, moduleFieldMap, dataList, fieldConfigMap);
    }

    public LinkedHashMap<String, Object> getSystemFieldMap(OutsourcingListResponse data, Map<String, BaseField> fieldConfigMap) {
        LinkedHashMap<String, Object> systemFieldMap = new LinkedHashMap<>();
        systemFieldMap.put("internalProjectNo", data.getInternalProjectNo());
        systemFieldMap.put("projectSource", data.getProjectSource());
        systemFieldMap.put("experimentContent", data.getExperimentContent());
        systemFieldMap.put("experimentType", data.getExperimentType());
        systemFieldMap.put("outsourcingVendor", data.getOutsourcingVendor());
        systemFieldMap.put("dealPrice", data.getDealPrice());
        systemFieldMap.put("outsourcingAmount", data.getOutsourcingAmount());

        BaseField outsourcingTimeField = fieldConfigMap.values().stream().filter(field -> Strings.CI.equals(field.getBusinessKey(), "outsourcingTime")).findFirst().orElse(null);
        if (outsourcingTimeField != null && data.getOutsourcingTime() != null) {
            AbstractModuleFieldResolver customFieldResolver = ModuleFieldResolverFactory.getResolver(outsourcingTimeField.getType());
            systemFieldMap.put("outsourcingTime", customFieldResolver.transformToValue(outsourcingTimeField, String.valueOf(data.getOutsourcingTime())));
        }

        BaseField resultReturnTimeField = fieldConfigMap.values().stream().filter(field -> Strings.CI.equals(field.getBusinessKey(), "resultReturnTime")).findFirst().orElse(null);
        if (resultReturnTimeField != null && data.getResultReturnTime() != null) {
            AbstractModuleFieldResolver customFieldResolver = ModuleFieldResolverFactory.getResolver(resultReturnTimeField.getType());
            systemFieldMap.put("resultReturnTime", customFieldResolver.transformToValue(resultReturnTimeField, String.valueOf(data.getResultReturnTime())));
        }

        BaseField settlementTimeField = fieldConfigMap.values().stream().filter(field -> Strings.CI.equals(field.getBusinessKey(), "settlementTime")).findFirst().orElse(null);
        if (settlementTimeField != null && data.getSettlementTime() != null) {
            AbstractModuleFieldResolver customFieldResolver = ModuleFieldResolverFactory.getResolver(settlementTimeField.getType());
            systemFieldMap.put("settlementTime", customFieldResolver.transformToValue(settlementTimeField, String.valueOf(data.getSettlementTime())));
        }

        systemFieldMap.put("settlementAmount", data.getSettlementAmount());
        systemFieldMap.put("followUpProcess", data.getFollowUpProcess());
        systemFieldMap.put("owner", data.getOwnerName());
        systemFieldMap.put("departmentId", data.getDepartmentName());
        systemFieldMap.put("createUser", data.getCreateUserName());
        systemFieldMap.put("createTime", TimeUtils.getDateTimeStr(data.getCreateTime()));
        systemFieldMap.put("updateUser", data.getUpdateUserName());
        systemFieldMap.put("updateTime", TimeUtils.getDateTimeStr(data.getUpdateTime()));
        return systemFieldMap;
    }

    @Override
    public List<List<Object>> getSelectExportData(List<String> ids, String taskId, ExportDTO exportDTO) throws InterruptedException {
        String orgId = exportDTO.getOrgId();
        String userId = exportDTO.getUserId();
        List<OutsourcingListResponse> allList = extOutsourcingMapper.getListByIds(ids, userId, orgId, exportDTO.getDeptDataPermission());
        List<OutsourcingListResponse> dataList = outsourcingService.buildListData(allList, orgId);
        Map<String, BaseField> fieldConfigMap = getFieldConfigMap(FormKey.OUTSOURCING.getKey(), orgId);

        List<List<Object>> data = new ArrayList<>();
        for (OutsourcingListResponse response : dataList) {
            if (ExportThreadRegistry.isInterrupted(taskId)) {
                throw new InterruptedException("线程已被中断，主动退出");
            }
            List<Object> value = buildData(exportDTO.getHeadList(), response, fieldConfigMap);
            data.add(value);
        }
        return data;
    }
}
