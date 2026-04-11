package cn.cordys.crm.submission.controller;

import cn.cordys.common.constants.PermissionConstants;
import cn.cordys.common.dto.request.PosRequest;
import cn.cordys.common.pager.PagerWithOption;
import cn.cordys.context.OrganizationContext;
import cn.cordys.crm.submission.domain.Submission;
import cn.cordys.crm.submission.dto.request.SubmissionEditRequest;
import cn.cordys.crm.submission.dto.request.SubmissionPageRequest;
import cn.cordys.crm.submission.dto.response.SubmissionGetResponse;
import cn.cordys.crm.submission.dto.response.SubmissionListResponse;
import cn.cordys.crm.submission.service.SubmissionService;
import cn.cordys.crm.system.dto.request.ResourceBatchEditRequest;
import cn.cordys.security.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "投稿")
@RestController
@RequestMapping("/api/submission")
public class SubmissionController {

    @Resource
    private SubmissionService submissionService;

    @PostMapping("/page")
    @RequiresPermissions("submission:read")
    @Operation(summary = "投稿列表")
    public PagerWithOption<List<SubmissionListResponse>> list(@Validated @RequestBody SubmissionPageRequest request) {
        return submissionService.list(request, OrganizationContext.getOrganizationId());
    }

    @GetMapping("/get/{id}")
    @RequiresPermissions("submission:read")
    @Operation(summary = "投稿详情")
    public SubmissionGetResponse get(@PathVariable String id) {
        return submissionService.get(id);
    }

    @PostMapping("/add")
    @RequiresPermissions("submission:add")
    @Operation(summary = "添加投稿")
    public Submission add(@Validated @RequestBody SubmissionEditRequest request) {
        return submissionService.add(request, SessionUtils.getUserId(), OrganizationContext.getOrganizationId());
    }

    @PostMapping("/update")
    @RequiresPermissions("submission:update")
    @Operation(summary = "更新投稿")
    public Submission update(@Validated @RequestBody SubmissionEditRequest request) {
        return submissionService.update(request, SessionUtils.getUserId(), OrganizationContext.getOrganizationId());
    }

    @PostMapping("/batch/update")
    @RequiresPermissions("submission:update")
    @Operation(summary = "批量更新投稿")
    public void batchUpdate(@Validated @RequestBody ResourceBatchEditRequest request) {
        submissionService.batchUpdate(request, SessionUtils.getUserId(), OrganizationContext.getOrganizationId());
    }

    @GetMapping("/delete/{id}")
    @RequiresPermissions("submission:delete")
    @Operation(summary = "删除投稿")
    public void delete(@PathVariable String id) {
        submissionService.delete(id);
    }

    @PostMapping("/batch/delete")
    @RequiresPermissions("submission:delete")
    @Operation(summary = "批量删除投稿")
    public void batchDelete(@RequestBody @NotEmpty List<String> ids) {
        submissionService.batchDelete(ids, SessionUtils.getUserId());
    }
}