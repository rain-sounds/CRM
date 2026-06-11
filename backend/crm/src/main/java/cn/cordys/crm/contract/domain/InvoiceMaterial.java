package cn.cordys.crm.contract.domain;

import cn.cordys.common.domain.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "invoice_material")
public class InvoiceMaterial extends BaseModel {

    @Schema(description = "序列")
    private Integer sequence;

    @Schema(description = "医院名称")
    private String hospitalName;

    @Schema(description = "发票（是/否）")
    private String invoice;

    @Schema(description = "查验证明（是/否）")
    private String verificationProof;

    @Schema(description = "样品邮寄（是/否）")
    private String sampleMailing;

    @Schema(description = "样品照片（是/否）")
    private String samplePhoto;

    @Schema(description = "报告（是/否）")
    private String report;

    @Schema(description = "出库单（是/否）")
    private String outboundOrder;

    @Schema(description = "合同（是/否）")
    private String contract;

    @Schema(description = "平台（喀斯码/锐竞/医院平台/无）")
    private String platform;

    @Schema(description = "其他资料")
    private String otherMaterials;

    @Schema(description = "组织id")
    private String organizationId;
}
