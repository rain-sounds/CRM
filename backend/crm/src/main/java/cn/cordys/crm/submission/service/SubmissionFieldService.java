package cn.cordys.crm.submission.service;

import cn.cordys.common.service.BaseResourceFieldService;
import cn.cordys.crm.submission.domain.SubmissionField;
import cn.cordys.crm.submission.domain.SubmissionFieldBlob;
import cn.cordys.mybatis.BaseMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class SubmissionFieldService extends BaseResourceFieldService<SubmissionField, SubmissionFieldBlob> {
    @Resource
    private BaseMapper<SubmissionField> submissionFieldMapper;
    @Resource
    private BaseMapper<SubmissionFieldBlob> submissionFieldBlobMapper;

    @Override
    protected String getFormKey() {
        return "submission";
    }

    @Override
    protected BaseMapper<SubmissionField> getResourceFieldMapper() {
        return submissionFieldMapper;
    }

    @Override
    protected BaseMapper<SubmissionFieldBlob> getResourceFieldBlobMapper() {
        return submissionFieldBlobMapper;
    }
}