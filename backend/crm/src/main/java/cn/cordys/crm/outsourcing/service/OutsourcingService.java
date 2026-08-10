package cn.cordys.crm.outsourcing.service;

import cn.cordys.aspectj.annotation.OperationLog;
import cn.cordys.aspectj.constants.LogModule;
import cn.cordys.aspectj.constants.LogType;
import cn.cordys.common.constants.BusinessModuleField;
import cn.cordys.common.constants.FormKey;
import cn.cordys.common.constants.PermissionConstants;
import cn.cordys.common.domain.BaseModuleFieldValue;
import cn.cordys.common.dto.*;
import cn.cordys.common.pager.PageUtils;
import cn.cordys.common.pager.PagerWithOption;
import cn.cordys.common.permission.PermissionCache;
import cn.cordys.common.permission.PermissionUtils;
import cn.cordys.common.service.BaseService;
import cn.cordys.common.service.DataScopeService;
import cn.cordys.common.uid.IDGenerator;
import cn.cordys.common.util.BeanUtils;
import cn.cordys.crm.outsourcing.domain.Outsourcing;
import cn.cordys.crm.outsourcing.dto.request.OutsourcingAddRequest;
import cn.cordys.crm.outsourcing.dto.request.OutsourcingPageRequest;
import cn.cordys.crm.outsourcing.dto.request.OutsourcingUpdateRequest;
import cn.cordys.crm.outsourcing.dto.response.OutsourcingGetResponse;
import cn.cordys.crm.outsourcing.dto.response.OutsourcingListResponse;
import cn.cordys.crm.outsourcing.mapper.ExtOutsourcingMapper;
import cn.cordys.crm.system.dto.response.ModuleFormConfigDTO;
import cn.cordys.crm.system.service.ModuleFormCacheService;
import cn.cordys.crm.system.service.ModuleFormService;
import cn.cordys.mybatis.BaseMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 外包服务
 *
 * @author ls
 * @date 2026-06-11
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OutsourcingService {

    @Resource
    private BaseMapper<Outsourcing> outsourcingMapper;
    @Resource
    private ExtOutsourcingMapper extOutsourcingMapper;
    @Resource
    private ModuleFormCacheService moduleFormCacheService;
    @Resource
    private ModuleFormService moduleFormService;
    @Resource
    private OutsourcingFieldService outsourcingFieldService;
    @Resource
    private PermissionCache permissionCache;
    @Resource
    private DataScopeService dataScopeService;
    @Resource
    private BaseService baseService;

    public PagerWithOption<List<OutsourcingListResponse>> list(OutsourcingPageRequest request, String userId, String orgId, DeptDataPermissionDTO deptDataPermission) {
        Page<Object> page = PageHelper.startPage(request.getCurrent(), request.getPageSize());
        List<OutsourcingListResponse> list = extOutsourcingMapper.list(request, userId, orgId, deptDataPermission);
        list = buildListData(list, orgId);
        Map<String, List<OptionDTO>> optionMap = buildOptionMap(orgId, list);
        return PageUtils.setPageInfoWithOption(page, list, optionMap);
    }

    public ModuleFormConfigDTO getFormConfig(String orgId) {
        return moduleFormCacheService.getBusinessFormConfig(FormKey.OUTSOURCING.getKey(), orgId);
    }

    public Map<String, List<OptionDTO>> buildOptionMap(String orgId, List<OutsourcingListResponse> list) {
        ModuleFormConfigDTO formConfig = getFormConfig(orgId);
        List<BaseModuleFieldValue> moduleFieldValues = moduleFormService.getBaseModuleFieldValues(list, OutsourcingListResponse::getModuleFields);
        Map<String, List<OptionDTO>> optionMap = moduleFormService.getOptionMap(formConfig, moduleFieldValues);

        // 补充负责人选项
        List<OptionDTO> ownerFieldOption = moduleFormService.getBusinessFieldOption(list,
                OutsourcingListResponse::getOwner, OutsourcingListResponse::getOwnerName);
        optionMap.put(BusinessModuleField.OUTSOURCING_OWNER.getBusinessKey(), ownerFieldOption);

        return optionMap;
    }

    public List<OutsourcingListResponse> buildListData(List<OutsourcingListResponse> list, String orgId) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        List<String> outsourcingIds = list.stream().map(OutsourcingListResponse::getId)
                .collect(Collectors.toList());

        Map<String, List<BaseModuleFieldValue>> caseCustomFiledMap = outsourcingFieldService.getResourceFieldMap(outsourcingIds, true);
        Map<String, List<BaseModuleFieldValue>> resolvefieldValueMap = outsourcingFieldService.setBusinessRefFieldValue(list,
                moduleFormService.getFlattenFormFields(FormKey.OUTSOURCING.getKey(), orgId), caseCustomFiledMap);

        List<String> ownerIds = list.stream()
                .map(OutsourcingListResponse::getOwner)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<String> createUserIds = list.stream()
                .map(OutsourcingListResponse::getCreateUser)
                .distinct()
                .toList();
        List<String> updateUserIds = list.stream()
                .map(OutsourcingListResponse::getUpdateUser)
                .distinct()
                .toList();
        List<String> userIds = Stream.of(ownerIds, createUserIds, updateUserIds)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
        Map<String, String> userNameMap = baseService.getUserNameMap(userIds);

        Map<String, UserDeptDTO> userDeptMap = baseService.getUserDeptMapByUserIds(ownerIds, orgId);

        list.forEach(outsourcingListResponse -> {
            List<BaseModuleFieldValue> outsourcingFields = resolvefieldValueMap.get(outsourcingListResponse.getId());
            outsourcingListResponse.setModuleFields(outsourcingFields);

            UserDeptDTO userDeptDTO = userDeptMap.get(outsourcingListResponse.getOwner());
            if (userDeptDTO != null) {
                outsourcingListResponse.setDepartmentId(userDeptDTO.getDeptId());
                outsourcingListResponse.setDepartmentName(userDeptDTO.getDeptName());
            }

            String createUserName = baseService.getAndCheckOptionName(userNameMap.get(outsourcingListResponse.getCreateUser()));
            outsourcingListResponse.setCreateUserName(createUserName);
            String updateUserName = baseService.getAndCheckOptionName(userNameMap.get(outsourcingListResponse.getUpdateUser()));
            outsourcingListResponse.setUpdateUserName(updateUserName);
            outsourcingListResponse.setOwnerName(userNameMap.get(outsourcingListResponse.getOwner()));
        });

        return list;
    }

    public OutsourcingGetResponse getWithDataPermissionCheck(String id, String userId, String orgId) {
        OutsourcingGetResponse getResponse = get(id);
        dataScopeService.checkDataPermission(userId, orgId, getResponse.getOwner(), PermissionConstants.OUTSOURCING_READ);
        return getResponse;
    }

    public OutsourcingGetResponse get(String id) {
        Outsourcing outsourcing = outsourcingMapper.selectByPrimaryKey(id);
        OutsourcingGetResponse outsourcingGetResponse = BeanUtils.copyBean(new OutsourcingGetResponse(), outsourcing);
        outsourcingGetResponse = baseService.setCreateUpdateOwnerUserName(outsourcingGetResponse);

        List<BaseModuleFieldValue> outsourcingFields = outsourcingFieldService.getModuleFieldValuesByResourceId(id);
        outsourcingFields = outsourcingFieldService.setBusinessRefFieldValue(List.of(outsourcingGetResponse),
                moduleFormService.getFlattenFormFields(FormKey.OUTSOURCING.getKey(), outsourcing.getOrganizationId()), new HashMap<>(Map.of(id, outsourcingFields))).get(id);
        ModuleFormConfigDTO outsourcingFormConfig = getFormConfig(outsourcing.getOrganizationId());

        Map<String, List<OptionDTO>> optionMap = moduleFormService.getOptionMap(outsourcingFormConfig, outsourcingFields);

        // 补充负责人选项
        List<OptionDTO> ownerFieldOption = moduleFormService.getBusinessFieldOption(outsourcingGetResponse,
                OutsourcingGetResponse::getOwner, OutsourcingGetResponse::getOwnerName);
        optionMap.put(BusinessModuleField.OUTSOURCING_OWNER.getBusinessKey(), ownerFieldOption);

        outsourcingGetResponse.setOptionMap(optionMap);
        outsourcingGetResponse.setModuleFields(outsourcingFields);

        if (outsourcingGetResponse.getOwner() != null) {
            UserDeptDTO userDeptDTO = baseService.getUserDeptMapByUserId(outsourcingGetResponse.getOwner(), outsourcing.getOrganizationId());
            if (userDeptDTO != null) {
                outsourcingGetResponse.setDepartmentId(userDeptDTO.getDeptId());
                outsourcingGetResponse.setDepartmentName(userDeptDTO.getDeptName());
            }
        }

        outsourcingGetResponse.setAttachmentMap(moduleFormService.getAttachmentMap(outsourcingFormConfig, outsourcingFields));

        return outsourcingGetResponse;
    }

    @OperationLog(module = LogModule.OUTSOURCING, type = LogType.ADD, operator = "{#userId}")
    public Outsourcing add(OutsourcingAddRequest request, String userId, String orgId) {
        Outsourcing outsourcing = BeanUtils.copyBean(new Outsourcing(), request);
        if (StringUtils.isBlank(request.getOwner())) {
            outsourcing.setOwner(userId);
        }
        outsourcing.setCreateTime(System.currentTimeMillis());
        outsourcing.setUpdateTime(System.currentTimeMillis());
        outsourcing.setUpdateUser(userId);
        outsourcing.setCreateUser(userId);
        outsourcing.setOrganizationId(orgId);
        outsourcing.setId(IDGenerator.nextStr());
        outsourcingFieldService.saveModuleField(outsourcing, orgId, userId, request.getModuleFields(), false);
        outsourcingMapper.insert(outsourcing);
        baseService.handleAddLog(outsourcing, request.getModuleFields());
        return outsourcing;
    }

    @OperationLog(module = LogModule.OUTSOURCING, type = LogType.UPDATE, resourceId = "{#request.id}")
    public Outsourcing update(OutsourcingUpdateRequest request, String userId, String orgId) {
        Outsourcing originOutsourcing = outsourcingMapper.selectByPrimaryKey(request.getId());
        dataScopeService.checkDataPermission(userId, orgId, originOutsourcing.getOwner(), PermissionConstants.OUTSOURCING_UPDATE);

        Outsourcing outsourcing = BeanUtils.copyBean(new Outsourcing(), request);
        outsourcing.setUpdateTime(System.currentTimeMillis());
        outsourcing.setUpdateUser(userId);

        List<BaseModuleFieldValue> originOutsourcingFields = List.of();
        if (request.getModuleFields() != null) {
            originOutsourcingFields = outsourcingFieldService.getModuleFieldValuesByResourceId(request.getId());
        }

        if (BooleanUtils.isTrue(request.getAgentInvoke())) {
            outsourcingFieldService.updateModuleFieldByAgent(outsourcing, originOutsourcingFields, request.getModuleFields(), orgId, userId);
        } else {
            updateModuleField(outsourcing, request.getModuleFields(), orgId, userId);
        }

        outsourcingMapper.update(outsourcing);

        outsourcing = outsourcingMapper.selectByPrimaryKey(request.getId());

        baseService.handleUpdateLog(originOutsourcing, outsourcing, originOutsourcingFields, request.getModuleFields(), originOutsourcing.getId(), originOutsourcing.getInternalProjectNo());
        return outsourcing;
    }

    private void updateModuleField(Outsourcing outsourcing, List<BaseModuleFieldValue> moduleFields, String orgId, String userId) {
        if (moduleFields == null) {
            return;
        }
        outsourcingFieldService.deleteByResourceId(outsourcing.getId());
        outsourcingFieldService.saveModuleField(outsourcing, orgId, userId, moduleFields, true);
    }

    @OperationLog(module = LogModule.OUTSOURCING, type = LogType.DELETE, resourceId = "{#id}")
    public void delete(String id, String userId, String orgId) {
        Outsourcing originOutsourcing = outsourcingMapper.selectByPrimaryKey(id);
        dataScopeService.checkDataPermission(userId, orgId, originOutsourcing.getOwner(), PermissionConstants.OUTSOURCING_DELETE);

        outsourcingMapper.deleteByPrimaryKey(id);
    }

    public ResourceTabEnableDTO getTabEnableConfig(String userId, String orgId) {
        List<RolePermissionDTO> rolePermissions = permissionCache.getRolePermissions(userId, orgId);
        return PermissionUtils.getTabEnableConfig(userId, PermissionConstants.OUTSOURCING_READ, rolePermissions);
    }
}
