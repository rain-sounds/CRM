package cn.cordys.crm.outsourcing.controller;

import cn.cordys.aspectj.constants.LogModule;
import cn.cordys.common.constants.FormKey;
import cn.cordys.common.constants.PermissionConstants;
import cn.cordys.common.dto.DeptDataPermissionDTO;
import cn.cordys.common.dto.ExportDTO;
import cn.cordys.common.dto.ExportSelectRequest;
import cn.cordys.common.dto.ResourceTabEnableDTO;
import cn.cordys.common.pager.PagerWithOption;
import cn.cordys.common.service.DataScopeService;
import cn.cordys.common.utils.ConditionFilterUtils;
import cn.cordys.context.OrganizationContext;
import cn.cordys.crm.outsourcing.domain.Outsourcing;
import cn.cordys.crm.outsourcing.dto.request.OutsourcingAddRequest;
import cn.cordys.crm.outsourcing.dto.request.OutsourcingExportRequest;
import cn.cordys.crm.outsourcing.dto.request.OutsourcingPageRequest;
import cn.cordys.crm.outsourcing.dto.request.OutsourcingUpdateRequest;
import cn.cordys.crm.outsourcing.dto.response.OutsourcingGetResponse;
import cn.cordys.crm.outsourcing.dto.response.OutsourcingListResponse;
import cn.cordys.crm.outsourcing.service.OutsourcingExportService;
import cn.cordys.crm.outsourcing.service.OutsourcingService;
import cn.cordys.crm.system.constants.ExportConstants;
import cn.cordys.crm.system.dto.response.ModuleFormConfigDTO;
import cn.cordys.crm.system.service.ModuleFormCacheService;
import cn.cordys.security.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 外包控制器
 *
 * @author ls
 * @date 2026-06-11
 */
@Tag(name = "外包")
@RestController
@RequestMapping("/outsourcing")
public class OutsourcingController {

    @Resource
    private OutsourcingService outsourcingService;
    @Resource
    private OutsourcingExportService outsourcingExportService;
    @Resource
    private DataScopeService dataScopeService;
    @Resource
    private ModuleFormCacheService moduleFormCacheService;

    @GetMapping("/module/form")
    @RequiresPermissions(PermissionConstants.OUTSOURCING_READ)
    @Operation(summary = "获取表单配置")
    public ModuleFormConfigDTO getModuleFormConfig() {
        return moduleFormCacheService.getBusinessFormConfig(FormKey.OUTSOURCING.getKey(), OrganizationContext.getOrganizationId());
    }

    @PostMapping("/page")
    @RequiresPermissions(PermissionConstants.OUTSOURCING_READ)
    @Operation(summary = "外包列表")
    public PagerWithOption<List<OutsourcingListResponse>> list(@Validated @RequestBody OutsourcingPageRequest request) {
        ConditionFilterUtils.parseCondition(request, FormKey.OUTSOURCING.getKey());
        DeptDataPermissionDTO deptDataPermission = dataScopeService.getDeptDataPermission(SessionUtils.getUserId(),
                OrganizationContext.getOrganizationId(), request.getViewId(), PermissionConstants.OUTSOURCING_READ);
        return outsourcingService.list(request, SessionUtils.getUserId(), OrganizationContext.getOrganizationId(), deptDataPermission);
    }

    @GetMapping("/get/{id}")
    @RequiresPermissions(PermissionConstants.OUTSOURCING_READ)
    @Operation(summary = "外包详情")
    public OutsourcingGetResponse get(@PathVariable String id) {
        return outsourcingService.getWithDataPermissionCheck(id, SessionUtils.getUserId(), OrganizationContext.getOrganizationId());
    }

    @PostMapping("/add")
    @RequiresPermissions(PermissionConstants.OUTSOURCING_ADD)
    @Operation(summary = "添加外包")
    public Outsourcing add(@Validated @RequestBody OutsourcingAddRequest request) {
        return outsourcingService.add(request, SessionUtils.getUserId(), OrganizationContext.getOrganizationId());
    }

    @PostMapping("/update")
    @RequiresPermissions(PermissionConstants.OUTSOURCING_UPDATE)
    @Operation(summary = "更新外包")
    public Outsourcing update(@Validated @RequestBody OutsourcingUpdateRequest request) {
        return outsourcingService.update(request, SessionUtils.getUserId(), OrganizationContext.getOrganizationId());
    }

    @GetMapping("/delete/{id}")
    @RequiresPermissions(PermissionConstants.OUTSOURCING_DELETE)
    @Operation(summary = "删除外包")
    public void delete(@PathVariable String id) {
        outsourcingService.delete(id, SessionUtils.getUserId(), OrganizationContext.getOrganizationId());
    }

    @GetMapping("/tab")
    @RequiresPermissions(PermissionConstants.OUTSOURCING_READ)
    @Operation(summary = "tab是否显示")
    public ResourceTabEnableDTO getTabEnableConfig() {
        return outsourcingService.getTabEnableConfig(SessionUtils.getUserId(), OrganizationContext.getOrganizationId());
    }

    @PostMapping("/export-select")
    @Operation(summary = "导出选中外包")
    @RequiresPermissions(PermissionConstants.OUTSOURCING_READ)
    public String exportSelect(@Validated @RequestBody ExportSelectRequest request) {
        DeptDataPermissionDTO deptDataPermission = dataScopeService.getDeptDataPermission(SessionUtils.getUserId(),
                OrganizationContext.getOrganizationId(), PermissionConstants.OUTSOURCING_READ);
        ExportDTO exportDTO = ExportDTO.builder()
                .exportType(ExportConstants.ExportType.OUTSOURCING.name())
                .fileName(request.getFileName())
                .headList(request.getHeadList())
                .logModule(LogModule.OUTSOURCING)
                .locale(LocaleContextHolder.getLocale())
                .orgId(OrganizationContext.getOrganizationId())
                .userId(SessionUtils.getUserId())
                .deptDataPermission(deptDataPermission)
                .selectIds(request.getIds())
                .selectRequest(request)
                .build();
        return outsourcingExportService.exportSelect(exportDTO);
    }

    @PostMapping("/export-all")
    @Operation(summary = "导出全部外包")
    @RequiresPermissions(PermissionConstants.OUTSOURCING_READ)
    public String exportAll(@Validated @RequestBody OutsourcingExportRequest request) {
        ConditionFilterUtils.parseCondition(request, FormKey.OUTSOURCING.getKey());
        DeptDataPermissionDTO deptDataPermission = dataScopeService.getDeptDataPermission(SessionUtils.getUserId(),
                OrganizationContext.getOrganizationId(), request.getViewId(), PermissionConstants.OUTSOURCING_READ);
        ExportDTO exportDTO = ExportDTO.builder()
                .exportType(ExportConstants.ExportType.OUTSOURCING.name())
                .fileName(request.getFileName())
                .headList(request.getHeadList())
                .logModule(LogModule.OUTSOURCING)
                .locale(LocaleContextHolder.getLocale())
                .orgId(OrganizationContext.getOrganizationId())
                .userId(SessionUtils.getUserId())
                .deptDataPermission(deptDataPermission)
                .pageRequest(request)
                .build();
        return outsourcingExportService.export(exportDTO);
    }
}
