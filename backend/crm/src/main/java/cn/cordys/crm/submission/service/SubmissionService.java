package cn.cordys.crm.submission.service;

import cn.cordys.common.pager.PagerWithOption;
import cn.cordys.crm.submission.domain.Submission;
import cn.cordys.crm.submission.dto.request.SubmissionEditRequest;
import cn.cordys.crm.submission.dto.request.SubmissionPageRequest;
import cn.cordys.crm.submission.dto.response.SubmissionGetResponse;
import cn.cordys.crm.submission.dto.response.SubmissionListResponse;
import cn.cordys.crm.system.dto.request.ResourceBatchEditRequest;

import java.util.List;

public interface SubmissionService {

    PagerWithOption<List<SubmissionListResponse>> list(SubmissionPageRequest request, String orgId);

    SubmissionGetResponse get(String id);

    Submission add(SubmissionEditRequest request, String userId, String orgId);

    Submission update(SubmissionEditRequest request, String userId, String orgId);

    void delete(String id);

    void batchUpdate(ResourceBatchEditRequest request, String userId, String organizationId);

    void batchDelete(List<String> ids, String userId);
}