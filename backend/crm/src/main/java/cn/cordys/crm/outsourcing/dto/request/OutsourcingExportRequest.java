package cn.cordys.crm.outsourcing.dto.request;

import cn.cordys.common.dto.ExportHeadDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 外包导出请求
 *
 * @author ls
 * @date 2026-06-11
 */
@Data
public class OutsourcingExportRequest extends OutsourcingPageRequest {

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "表头信息")
    @NotEmpty(message = "{export_head_list_is_empty}")
    private List<ExportHeadDTO> headList;
}
