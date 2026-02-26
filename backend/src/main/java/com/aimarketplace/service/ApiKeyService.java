package com.aimarketplace.service;

import com.aimarketplace.dto.ApiKeyApplyRequest;
import com.aimarketplace.entity.ApiKey;

import java.util.List;

public interface ApiKeyService {

    /**
     * 申请 API Key
     */
    Long applyKey(Long userId, ApiKeyApplyRequest request);

    /**
     * 获取用户的 API Keys
     */
    List<ApiKey> getMyKeys(Long userId);

    /**
     * 获取待审批的 API Keys（管理员）
     */
    List<ApiKey> getPendingKeys();

    /**
     * 审批通过（管理员）
     */
    void approve(Long id, Long approverId);

    /**
     * 审批拒绝（管理员）
     */
    void reject(Long id, Long approverId, String reason);

    /**
     * 撤销 API Key
     */
    void revoke(Long id, Long userId);

    /**
     * 检查 API Key 是否有效
     */
    boolean isValid(String keyValue);
}
