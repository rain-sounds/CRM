package cn.cordys.crm.contract.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceMaterialUpdateRequest extends InvoiceMaterialAddRequest {

    @NotBlank(message = "id不能为空")
    @Schema(description = "id")
    private String id;
}
