package cn.cordys.crm.submission.mapper;

import cn.cordys.common.dto.BatchUpdateDbParam;
import cn.cordys.crm.submission.domain.Submission;
import cn.cordys.crm.submission.dto.request.SubmissionPageRequest;
import cn.cordys.crm.submission.dto.response.SubmissionListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtSubmissionMapper {
    List<SubmissionListResponse> list(@Param("request") SubmissionPageRequest request, @Param("orgId") String orgId);
    void batchUpdate(@Param("request") BatchUpdateDbParam request);
}