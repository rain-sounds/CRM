package cn.cordys.crm.outsourcing.dto.request;

import cn.cordys.common.domain.BaseModuleFieldValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 外包新增请求
 *
 * @author ls
 * @date 2026-06-11
 */
@Data
public class OutsourcingAddRequest {

    @Size(max = 255)
    @Schema(description = "内部项目编号")
    private String internalProjectNo;

    @Size(max = 255)
    @Schema(description = "项目来源")
    private String projectSource;

    @Size(max = 2000)
    @Schema(description = "项目实验内容")
    private String experimentContent;

    @Size(max = 255)
    @Schema(description = "实验类型")
    private String experimentType;

    @Size(max = 255)
    @Schema(description = "外包商")
    private String outsourcingVendor;

    @Schema(description = "成交价格")
    private BigDecimal dealPrice;

    @Schema(description = "外包金额")
    private BigDecimal outsourcingAmount;

    @Schema(description = "外包时间")
    private Long outsourcingTime;

    @Schema(description = "结果返回时间")
    private Long resultReturnTime;

    @Schema(description = "结算时间")
    private Long settlementTime;

    @Schema(description = "结算金额")
    private BigDecimal settlementAmount;

    @Size(max = 500)
    @Schema(description = "后续流程")
    private String followUpProcess;

    @Size(max = 32)
    @Schema(description = "负责人")
    private String owner;

    @Schema(description = "模块字段值")
    private List<BaseModuleFieldValue> moduleFields;
}
