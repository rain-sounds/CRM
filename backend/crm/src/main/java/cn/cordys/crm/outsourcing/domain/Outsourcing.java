package cn.cordys.crm.outsourcing.domain;

import cn.cordys.common.domain.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 外包
 *
 * @author ls
 * @date 2026-06-11
 */
@Data
@Table(name = "outsourcing")
public class Outsourcing extends BaseModel {

    @Schema(description = "内部项目编号")
    private String internalProjectNo;

    @Schema(description = "项目来源")
    private String projectSource;

    @Schema(description = "项目实验内容")
    private String experimentContent;

    @Schema(description = "实验类型")
    private String experimentType;

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

    @Schema(description = "后续流程")
    private String followUpProcess;

    @Schema(description = "负责人")
    private String owner;

    @Schema(description = "组织id")
    private String organizationId;
}
