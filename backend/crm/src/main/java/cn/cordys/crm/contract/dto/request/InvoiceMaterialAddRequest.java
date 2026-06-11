package cn.cordys.crm.contract.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InvoiceMaterialAddRequest {

    @NotBlank(message = "医院名称不能为空")
    @Size(max = 255)
    @Schema(description = "医院名称")
    private String hospitalName;

    @Size(max = 10)
    @Schema(description = "发票（是/否）")
    private String invoice;

    @Size(max = 10)
    @Schema(description = "查验证明（是/否）")
    private String verificationProof;

    @Size(max = 10)
    @Schema(description = "样品邮寄（是/否）")
    private String sampleMailing;

    @Size(max = 10)
    @Schema(description = "样品照片（是/否）")
    private String samplePhoto;

    @Size(max = 10)
    @Schema(description = "报告（是/否）")
    private String report;

    @Size(max = 10)
    @Schema(description = "出库单（是/否）")
    private String outboundOrder;

    @Size(max = 10)
    @Schema(description = "合同（是/否）")
    private String contract;

    @Size(max = 50)
    @Schema(description = "平台（喀斯码/锐竞/医院平台/无）")
    private String platform;

    @Size(max = 500)
    @Schema(description = "其他资料")
    private String otherMaterials;
}
