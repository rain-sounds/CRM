package cn.cordys.crm.submission.domain;

import cn.cordys.common.domain.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "submission")
public class Submission extends BaseModel {
    @Schema(description = "组织机构id")
    private String organizationId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "期刊id")
    private String productId;

    @Schema(description = "商机id")
    private String opportunityId;

    @Schema(description = "状态")
    private String status;
}