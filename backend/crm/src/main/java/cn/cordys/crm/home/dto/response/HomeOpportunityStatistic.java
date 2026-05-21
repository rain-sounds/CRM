package cn.cordys.crm.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author jianxing
 * @date 2025-02-08 16:24:22
 */
@Data
public class HomeOpportunityStatistic {
    @Schema(description = "本年度项目")
    private HomeStatisticSearchResponse thisYearOpportunity;
    @Schema(description = "本月项目")
    private HomeStatisticSearchResponse thisMonthOpportunity;
    @Schema(description = "本周项目")
    private HomeStatisticSearchResponse thisWeekOpportunity;
    @Schema(description = "本日项目")
    private HomeStatisticSearchResponse todayOpportunity;

    @Schema(description = "本年度项目总额")
    private HomeStatisticSearchResponse thisYearOpportunityAmount;
    @Schema(description = "本月项目总额")
    private HomeStatisticSearchResponse thisMonthOpportunityAmount;
    @Schema(description = "本周项目总额")
    private HomeStatisticSearchResponse thisWeekOpportunityAmount;
    @Schema(description = "本日项目总额")
    private HomeStatisticSearchResponse todayOpportunityAmount;
}
