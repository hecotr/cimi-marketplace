package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.ApprovalCallbackRequest;
import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.entity.ApprovalRecord;
import com.aimarketplace.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Admin Approval Controller
 */
@RestController
@RequestMapping("/api/admin/approval")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @PostMapping("/{assetId}/approve")
    public Result<AssetDTO> approveAsset(
            @PathVariable Long assetId,
            @RequestHeader("X-User-Id") Long approverId,
            @Valid @RequestBody ApprovalCallbackRequest request) {
        AssetDTO asset = approvalService.approveAsset(assetId, approverId, request.getComment());
        return Result.success(asset);
    }

    @PostMapping("/{assetId}/reject")
    public Result<AssetDTO> rejectAsset(
            @PathVariable Long assetId,
            @RequestHeader("X-User-Id") Long approverId,
            @Valid @RequestBody ApprovalCallbackRequest request) {
        AssetDTO asset = approvalService.rejectAsset(assetId, approverId, request.getComment());
        return Result.success(asset);
    }

    @PostMapping("/{assetId}/request-changes")
    public Result<AssetDTO> requestChanges(
            @PathVariable Long assetId,
            @RequestHeader("X-User-Id") Long approverId,
            @Valid @RequestBody ApprovalCallbackRequest request) {
        AssetDTO asset = approvalService.requestChanges(assetId, approverId, request.getComment());
        return Result.success(asset);
    }

    @GetMapping("/pending")
    public Result<List<ApprovalRecord>> getPendingApprovals() {
        List<ApprovalRecord> approvals = approvalService.getPendingApprovals();
        return Result.success(approvals);
    }

    @GetMapping("/history/{assetId}")
    public Result<List<ApprovalRecord>> getApprovalHistory(@PathVariable Long assetId) {
        List<ApprovalRecord> history = approvalService.getApprovalHistory(assetId);
        return Result.success(history);
    }
}
