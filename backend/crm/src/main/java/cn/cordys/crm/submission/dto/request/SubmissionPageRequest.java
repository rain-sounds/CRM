package cn.cordys.crm.submission.dto.request;

import cn.cordys.common.dto.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SubmissionPageRequest extends BasePageRequest {
    @Schema(description = "状态")
    private String status;
}