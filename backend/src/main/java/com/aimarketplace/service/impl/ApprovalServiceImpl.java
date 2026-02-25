package com.aimarketplace.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.entity.ApprovalRecord;
import com.aimarketplace.entity.Asset;
import com.aimarketplace.mapper.ApprovalRecordMapper;
import com.aimarketplace.service.ApprovalService;
import com.aimarketplace.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Approval Service Implementation
 */
@Service
public class ApprovalServiceImpl extends ServiceImpl<ApprovalRecordMapper, ApprovalRecord> implements ApprovalService {

    @Autowired
    private AssetService assetService;

    @Autowired
    private ApprovalRecordMapper approvalRecordMapper;

    @Override
    @Transactional
    public AssetDTO approveAsset(Long assetId, Long approverId, String comment) {
        Asset asset = assetService.getById(assetId);
        if (asset == null) {
            throw new RuntimeException("Asset not found");
        }
        if (!"pending".equals(asset.getStatus())) {
            throw new RuntimeException("Only pending assets can be approved");
        }

        // Update asset status
        asset.setStatus("published");
        asset.setApprovedBy(approverId);
        asset.setApprovedAt(LocalDateTime.now());
        assetService.updateById(asset);

        // Create approval record
        createApprovalRecord(assetId, asset.getVersion(), approverId, "approve", comment, "approved");

        return assetService.getAssetById(assetId);
    }

    @Override
    @Transactional
    public AssetDTO rejectAsset(Long assetId, Long approverId, String comment) {
        Asset asset = assetService.getById(assetId);
        if (asset == null) {
            throw new RuntimeException("Asset not found");
        }
        if (!"pending".equals(asset.getStatus())) {
            throw new RuntimeException("Only pending assets can be rejected");
        }

        // Update asset status
        asset.setStatus("rejected");
        assetService.updateById(asset);

        // Create approval record
        createApprovalRecord(assetId, asset.getVersion(), approverId, "reject", comment, "rejected");

        return assetService.getAssetById(assetId);
    }

    @Override
    @Transactional
    public AssetDTO requestChanges(Long assetId, Long approverId, String comment) {
        Asset asset = assetService.getById(assetId);
        if (asset == null) {
            throw new RuntimeException("Asset not found");
        }
        if (!"pending".equals(asset.getStatus())) {
            throw new RuntimeException("Only pending assets can have changes requested");
        }

        // Update asset status back to draft
        asset.setStatus("draft");
        assetService.updateById(asset);

        // Create approval record
        createApprovalRecord(assetId, asset.getVersion(), approverId, "request_changes", comment, "approved");

        return assetService.getAssetById(assetId);
    }

    @Override
    public List<ApprovalRecord> getPendingApprovals() {
        return approvalRecordMapper.findPending();
    }

    @Override
    public List<ApprovalRecord> getApprovalHistory(Long assetId) {
        return approvalRecordMapper.findByAssetId(assetId);
    }

    private void createApprovalRecord(Long assetId, String version, Long approverId, String action, String comment, String status) {
        ApprovalRecord record = new ApprovalRecord();
        record.setAssetId(assetId);
        record.setAssetVersion(version);
        record.setApproverId(approverId);
        record.setAction(action);
        record.setComment(comment);
        record.setStatus(status);
        record.setCreatedAt(LocalDateTime.now());
        save(record);
    }
}
