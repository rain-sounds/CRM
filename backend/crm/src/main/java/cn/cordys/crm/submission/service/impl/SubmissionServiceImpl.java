package cn.cordys.crm.submission.service.impl;

import cn.cordys.aspectj.annotation.OperationLog;
import cn.cordys.aspectj.constants.LogModule;
import cn.cordys.aspectj.constants.LogType;
import cn.cordys.aspectj.context.OperationLogContext;
import cn.cordys.aspectj.dto.LogDTO;
import cn.cordys.common.domain.BaseModuleFieldValue;
import cn.cordys.common.dto.OptionDTO;
import cn.cordys.common.exception.GenericException;
import cn.cordys.common.pager.PageUtils;
import cn.cordys.common.pager.PagerWithOption;
import cn.cordys.common.service.BaseService;
import cn.cordys.common.uid.IDGenerator;
import cn.cordys.common.util.BeanUtils;
import cn.cordys.common.util.Translator;
import cn.cordys.crm.submission.domain.Submission;
import cn.cordys.crm.submission.dto.request.SubmissionEditRequest;
import cn.cordys.crm.submission.dto.request.SubmissionPageRequest;
import cn.cordys.crm.submission.dto.response.SubmissionGetResponse;
import cn.cordys.crm.submission.dto.response.SubmissionListResponse;
import cn.cordys.crm.submission.mapper.ExtSubmissionMapper;
import cn.cordys.crm.submission.service.SubmissionFieldService;
import cn.cordys.crm.submission.service.SubmissionService;
import cn.cordys.crm.system.dto.field.base.BaseField;
import cn.cordys.crm.system.dto.request.ResourceBatchEditRequest;
import cn.cordys.crm.system.dto.response.ModuleFormConfigDTO;
import cn.cordys.crm.system.service.LogService;
import cn.cordys.crm.system.service.ModuleFormCacheService;
import cn.cordys.crm.system.service.ModuleFormService;
import cn.cordys.mybatis.BaseMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    @Resource
    private BaseMapper<Submission> submissionBaseMapper;
    @Resource
    private ExtSubmissionMapper extSubmissionMapper;
    @Resource
    private BaseService baseService;
    @Resource
    private SubmissionFieldService submissionFieldService;
    @Resource
    private LogService logService;
    @Resource
    private ModuleFormCacheService moduleFormCacheService;
    @Resource
    private ModuleFormService moduleFormService;

    @Override
    public PagerWithOption<List<SubmissionListResponse>> list(SubmissionPageRequest request, String orgId) {
        Page<Object> page = PageHelper.startPage(request.getCurrent(), request.getPageSize());
        List<SubmissionListResponse> list = extSubmissionMapper.list(request, orgId);
        List<SubmissionListResponse> buildList = buildListData(list);

        ModuleFormConfigDTO formConfig = moduleFormCacheService.getBusinessFormConfig("submission", orgId);
        List<BaseModuleFieldValue> moduleFieldValues = moduleFormService.getBaseModuleFieldValues(list, SubmissionListResponse::getModuleFields);
        
        list.forEach(item -> {
            if (StringUtils.isNotBlank(item.getProductId())) {
                moduleFieldValues.add(new BaseModuleFieldValue("productId", item.getProductId()));
            }
            if (StringUtils.isNotBlank(item.getOpportunityId())) {
                moduleFieldValues.add(new BaseModuleFieldValue("opportunityId", item.getOpportunityId()));
            }
        });

        Map<String, List<OptionDTO>> optionMap = moduleFormService.getOptionMap(formConfig, moduleFieldValues);
        
        return PageUtils.setPageInfoWithOption(page, buildList, optionMap);
    }

    private List<SubmissionListResponse> buildListData(List<SubmissionListResponse> list) {
        List<String> ids = list.stream().map(SubmissionListResponse::getId).collect(Collectors.toList());
        Map<String, List<BaseModuleFieldValue>> filedMap = submissionFieldService.getResourceFieldMap(ids, true);
        list.forEach(response -> response.setModuleFields(filedMap.get(response.getId())));
        return baseService.setCreateAndUpdateUserName(list);
    }

    @Override
    public SubmissionGetResponse get(String id) {
        Submission submission = submissionBaseMapper.selectByPrimaryKey(id);
        if (submission == null) {
            return null;
        }
        SubmissionGetResponse response = BeanUtils.copyBean(new SubmissionGetResponse(), submission);
        List<BaseModuleFieldValue> fields = submissionFieldService.getModuleFieldValuesByResourceId(id);
        response.setModuleFields(fields);
        
        // 补充标准字段的值，以便 optionMap 解析
        if (StringUtils.isNotBlank(submission.getProductId())) {
            fields.add(new BaseModuleFieldValue("productId", submission.getProductId()));
        }
        if (StringUtils.isNotBlank(submission.getOpportunityId())) {
            fields.add(new BaseModuleFieldValue("opportunityId", submission.getOpportunityId()));
        }

        ModuleFormConfigDTO formConfig = moduleFormCacheService.getBusinessFormConfig("submission", submission.getOrganizationId());
        response.setOptionMap(moduleFormService.getOptionMap(formConfig, fields));
        response.setAttachmentMap(moduleFormService.getAttachmentMap(formConfig, fields));
        return baseService.setCreateAndUpdateUserName(response);
    }

    @Override
    @OperationLog(module = "SUBMISSION_MANAGEMENT", type = LogType.ADD, resourceName = "{#request.name}", operator = "{#userId}")
    public Submission add(SubmissionEditRequest request, String userId, String orgId) {
        Submission submission = BeanUtils.copyBean(new Submission(), request);
        submission.setCreateTime(System.currentTimeMillis());
        submission.setUpdateTime(System.currentTimeMillis());
        submission.setUpdateUser(userId);
        submission.setCreateUser(userId);
        submission.setOrganizationId(orgId);
        submission.setId(IDGenerator.nextStr());

        submissionFieldService.saveModuleField(submission, orgId, userId, request.getModuleFields(), false);
        submissionBaseMapper.insert(submission);
        baseService.handleAddLog(submission, request.getModuleFields());
        return submission;
    }

    @Override
    @OperationLog(module = "SUBMISSION_MANAGEMENT", type = LogType.UPDATE, operator = "{#userId}")
    public Submission update(SubmissionEditRequest request, String userId, String orgId) {
        if (StringUtils.isBlank(request.getId())) {
            throw new GenericException(Translator.get("id.empty"));
        }
        Submission oldSubmission = submissionBaseMapper.selectByPrimaryKey(request.getId());
        Submission submission = BeanUtils.copyBean(new Submission(), request);
        submission.setUpdateTime(System.currentTimeMillis());
        submission.setUpdateUser(userId);
        submission.setOrganizationId(orgId);

        List<BaseModuleFieldValue> originFields = submissionFieldService.getModuleFieldValuesByResourceId(request.getId());
        updateModuleField(submission, request.getModuleFields(), orgId, userId);
        submissionBaseMapper.update(submission);

        baseService.handleUpdateLog(oldSubmission, submission, originFields, request.getModuleFields(), request.getId(), submission.getName());
        return submissionBaseMapper.selectByPrimaryKey(submission.getId());
    }

    private void updateModuleField(Submission submission, List<BaseModuleFieldValue> moduleFields, String orgId, String userId) {
        if (moduleFields == null) return;
        submissionFieldService.deleteByResourceId(submission.getId());
        submissionFieldService.saveModuleField(submission, orgId, userId, moduleFields, true);
    }

    @Override
    @OperationLog(module = "SUBMISSION_MANAGEMENT", type = LogType.DELETE, resourceId = "{#id}")
    public void delete(String id) {
        Submission submission = submissionBaseMapper.selectByPrimaryKey(id);
        submissionBaseMapper.deleteByPrimaryKey(id);
        submissionFieldService.deleteByResourceId(id);
        OperationLogContext.setResourceName(submission.getName());
    }

    @Override
    public void batchUpdate(ResourceBatchEditRequest request, String userId, String organizationId) {
        BaseField field = submissionFieldService.getAndCheckField(request.getFieldId(), organizationId);
        List<Submission> submissions = submissionBaseMapper.selectByIds(request.getIds());
        submissionFieldService.batchUpdate(request, field, submissions, Submission.class, "SUBMISSION_MANAGEMENT", extSubmissionMapper::batchUpdate, userId, organizationId);
    }

    @Override
    public void batchDelete(List<String> ids, String userId) {
        List<Submission> submissions = submissionBaseMapper.selectByIds(ids);
        if (submissions == null || submissions.isEmpty()) return;

        submissionBaseMapper.deleteByIds(ids);
        submissionFieldService.deleteByResourceIds(ids);

        List<LogDTO> logs = submissions.stream()
                .map(p -> {
                    LogDTO log = new LogDTO(p.getOrganizationId(), p.getId(), userId, LogType.DELETE, "SUBMISSION_MANAGEMENT", p.getName());
                    log.setOriginalValue(p.getName());
                    return log;
                }).toList();
        logService.batchAdd(logs);
    }
}