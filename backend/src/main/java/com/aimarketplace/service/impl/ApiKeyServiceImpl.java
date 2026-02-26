package com.aimarketplace.service.impl;

import com.aimarketplace.common.BusinessException;
import com.aimarketplace.dto.ApiKeyApplyRequest;
import com.aimarketplace.entity.ApiKey;
import com.aimarketplace.mapper.ApiKeyMapper;
import com.aimarketplace.service.ApiKeyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    @Autowired
    private ApiKeyMapper apiKeyMapper;

    @Override
    public Long applyKey(Long userId, ApiKeyApplyRequest request) {
        ApiKey apiKey = new ApiKey();
        apiKey.setUserId(userId);
        apiKey.setModelConfigId(request.getModelConfigId());
        apiKey.setApiProtocol(request.getApiProtocol());
        apiKey.setExpiryType(request.getExpiryType());
        apiKey.setStatus("pending");
        apiKey.setKeyValue(generateKeyValue());
        apiKey.setApplyTime(LocalDateTime.now());

        apiKeyMapper.insert(apiKey);
        return apiKey.getId();
    }

    @Override
    public List<ApiKey> getMyKeys(Long userId) {
        return apiKeyMapper.selectList(
            new LambdaQueryWrapper<ApiKey>()
                .eq(ApiKey::getUserId, userId)
                .orderByDesc(ApiKey::getApplyTime)
        );
    }

    @Override
    public List<ApiKey> getPendingKeys() {
        return apiKeyMapper.selectList(
            new LambdaQueryWrapper<ApiKey>()
                .eq(ApiKey::getStatus, "pending")
                .orderByAsc(ApiKey::getApplyTime)
        );
    }

    @Override
    public void approve(Long id, Long approverId) {
        ApiKey apiKey = apiKeyMapper.selectById(id);
        if (apiKey == null) {
            throw new BusinessException("API Key 不存在");
        }
        if (!"pending".equals(apiKey.getStatus())) {
            throw new BusinessException("当前状态不能审批");
        }

        apiKey.setStatus("approved");
        apiKey.setApproverId(approverId);
        apiKey.setApproveTime(LocalDateTime.now());
        apiKey.setExpiryDate(calculateExpiryDate(apiKey.getExpiryType()));

        apiKeyMapper.updateById(apiKey);
    }

    @Override
    public void reject(Long id, Long approverId, String reason) {
        ApiKey apiKey = apiKeyMapper.selectById(id);
        if (apiKey == null) {
            throw new BusinessException("API Key 不存在");
        }
        if (!"pending".equals(apiKey.getStatus())) {
            throw new BusinessException("当前状态不能审批");
        }

        apiKey.setStatus("rejected");
        apiKey.setApproverId(approverId);
        apiKey.setApproveTime(LocalDateTime.now());
        apiKey.setRejectionReason(reason);

        apiKeyMapper.updateById(apiKey);
    }

    @Override
    public void revoke(Long id, Long userId) {
        ApiKey apiKey = apiKeyMapper.selectById(id);
        if (apiKey == null) {
            throw new BusinessException("API Key 不存在");
        }
        if (!apiKey.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此 API Key");
        }

        apiKey.setStatus("revoked");
        apiKeyMapper.updateById(apiKey);
    }

    @Override
    public boolean isValid(String keyValue) {
        ApiKey apiKey = apiKeyMapper.selectOne(
            new LambdaQueryWrapper<ApiKey>()
                .eq(ApiKey::getKeyValue, keyValue)
        );

        if (apiKey == null) {
            return false;
        }

        if (!"approved".equals(apiKey.getStatus())) {
            return false;
        }

        // 检查是否过期
        if (apiKey.getExpiryDate() != null && apiKey.getExpiryDate().isBefore(LocalDateTime.now())) {
            // 更新状态为过期
            apiKey.setStatus("expired");
            apiKeyMapper.updateById(apiKey);
            return false;
        }

        return true;
    }

    private String generateKeyValue() {
        return "sk-" + UUID.randomUUID().toString().replace("-", "");
    }

    private LocalDateTime calculateExpiryDate(String expiryType) {
        LocalDateTime now = LocalDateTime.now();
        switch (expiryType) {
            case "3m":
                return now.plusMonths(3);
            case "6m":
                return now.plusMonths(6);
            case "1y":
                return now.plusYears(1);
            case "permanent":
                return null; // 永不过期
            default:
                return now.plusMonths(3);
        }
    }
}
