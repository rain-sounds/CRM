package cn.cordys.crm.contract.dto.response;

import cn.cordys.crm.contract.domain.InvoiceMaterial;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class InvoiceMaterialListResponse extends InvoiceMaterial {

    @Schema(description = "创建人名称")
    private String createUserName;

    @Schema(description = "修改人名称")
    private String updateUserName;
}
