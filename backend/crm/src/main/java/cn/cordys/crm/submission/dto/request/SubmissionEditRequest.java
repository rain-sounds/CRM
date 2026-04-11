package cn.cordys.crm.submission.dto.request;

import cn.cordys.common.domain.BaseModuleFieldValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SubmissionEditRequest {

    @Schema(description = "id")
    @Size(max = 32)
    private String id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "期刊id")
    private String productId;

    @Schema(description = "商机id")
    private String opportunityId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "自定义字段")
    private List<BaseModuleFieldValue> moduleFields;
}