package com.aimarketplace.service;

import com.aimarketplace.dto.AssetDTO;

/**
 * Approval Service Interface
 */
public interface ApprovalService {

    /**
     * Approve an asset
     */
    AssetDTO approveAsset(Long assetId, Long approverId, String comment);

    /**
     * Reject an asset
     */
    AssetDTO rejectAsset(Long assetId, Long approverId, String comment);

    /**
     * Request changes for an asset
     */
    AssetDTO requestChanges(Long assetId, Long approverId, String comment);

    /**
     * Get pending approvals
     */
    java.util.List<com.aimarketplace.entity.ApprovalRecord> getPendingApprovals();

    /**
     * Get approval history for an asset
     */
    java.util.List<com.aimarketplace.entity.ApprovalRecord> getApprovalHistory(Long assetId);
}
