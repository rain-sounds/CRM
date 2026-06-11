package cn.cordys.crm.contract.controller;

import cn.cordys.common.constants.PermissionConstants;
import cn.cordys.common.pager.Pager;
import cn.cordys.common.utils.ConditionFilterUtils;
import cn.cordys.context.OrganizationContext;
import cn.cordys.crm.contract.domain.InvoiceMaterial;
import cn.cordys.crm.contract.dto.request.InvoiceMaterialAddRequest;
import cn.cordys.crm.contract.dto.request.InvoiceMaterialPageRequest;
import cn.cordys.crm.contract.dto.request.InvoiceMaterialUpdateRequest;
import cn.cordys.crm.contract.dto.response.InvoiceMaterialListResponse;
import cn.cordys.crm.contract.service.InvoiceMaterialService;
import cn.cordys.security.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "开票资料")
@RestController
@RequestMapping("/contract/invoice-material")
public class InvoiceMaterialController {

    @Resource
    private InvoiceMaterialService invoiceMaterialService;

    @PostMapping("/add")
    @RequiresPermissions(PermissionConstants.CONTRACT_INVOICE_MATERIAL_ADD)
    @Operation(summary = "创建")
    public InvoiceMaterial add(@Validated @RequestBody InvoiceMaterialAddRequest request) {
        return invoiceMaterialService.add(request, SessionUtils.getUserId(), OrganizationContext.getOrganizationId());
    }

    @PostMapping("/update")
    @RequiresPermissions(PermissionConstants.CONTRACT_INVOICE_MATERIAL_UPDATE)
    @Operation(summary = "更新")
    public InvoiceMaterial update(@Validated @RequestBody InvoiceMaterialUpdateRequest request) {
        return invoiceMaterialService.update(request, SessionUtils.getUserId(), OrganizationContext.getOrganizationId());
    }

    @GetMapping("/delete/{id}")
    @RequiresPermissions(PermissionConstants.CONTRACT_INVOICE_MATERIAL_DELETE)
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        invoiceMaterialService.delete(id);
    }

    @PostMapping("/page")
    @RequiresPermissions(PermissionConstants.CONTRACT_INVOICE_MATERIAL_READ)
    @Operation(summary = "列表")
    public Pager<List<InvoiceMaterialListResponse>> list(@Validated @RequestBody InvoiceMaterialPageRequest request) {
        ConditionFilterUtils.parseCondition(request);
        return invoiceMaterialService.list(request, OrganizationContext.getOrganizationId());
    }

    @GetMapping("/get/{id}")
    @RequiresPermissions(PermissionConstants.CONTRACT_INVOICE_MATERIAL_READ)
    @Operation(summary = "详情")
    public InvoiceMaterialListResponse get(@PathVariable("id") String id) {
        return invoiceMaterialService.get(id);
    }
}
