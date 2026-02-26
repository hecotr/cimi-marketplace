package com.aimarketplace.service;

import com.aimarketplace.common.BusinessException;
import com.aimarketplace.dto.ApiKeyApplyRequest;
import com.aimarketplace.entity.ApiKey;
import com.aimarketplace.mapper.ApiKeyMapper;
import com.aimarketplace.service.impl.ApiKeyServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyMapper apiKeyMapper;

    @InjectMocks
    private ApiKeyServiceImpl apiKeyService;

    private ApiKey testApiKey;

    @BeforeEach
    void setUp() {
        testApiKey = new ApiKey();
        testApiKey.setId(1L);
        testApiKey.setUserId(1L);
        testApiKey.setModelConfigId(1L);
        testApiKey.setKeyValue("sk-test123456789");
        testApiKey.setApiProtocol("openai");
        testApiKey.setExpiryType("3m");
        testApiKey.setStatus("pending");
        testApiKey.setApplyTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("申请 API Key")
    void applyKey() {
        ApiKeyApplyRequest request = new ApiKeyApplyRequest();
        request.setModelConfigId(1L);
        request.setApiProtocol("openai");
        request.setExpiryType("3m");

        when(apiKeyMapper.insert(any(ApiKey.class))).thenAnswer(invocation -> {
            ApiKey key = invocation.getArgument(0);
            key.setId(1L);
            assertNotNull(key.getKeyValue());
            assertTrue(key.getKeyValue().startsWith("sk-"));
            return 1;
        });

        Long id = apiKeyService.applyKey(1L, request);

        assertNotNull(id);
        verify(apiKeyMapper).insert(argThat(key ->
            key.getUserId().equals(1L) &&
            "pending".equals(key.getStatus()) &&
            key.getKeyValue().startsWith("sk-")
        ));
    }

    @Test
    @DisplayName("获取我的 API Keys")
    void getMyKeys() {
        ApiKey key2 = new ApiKey();
        key2.setId(2L);
        key2.setUserId(1L);
        key2.setStatus("approved");

        when(apiKeyMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testApiKey, key2));

        List<ApiKey> result = apiKeyService.getMyKeys(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取待审批的 Keys")
    void getPendingKeys() {
        when(apiKeyMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testApiKey));

        List<ApiKey> result = apiKeyService.getPendingKeys();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("pending", result.get(0).getStatus());
    }

    @Test
    @DisplayName("审批通过 - 3个月有效期")
    void approve3Months() {
        when(apiKeyMapper.selectById(1L)).thenReturn(testApiKey);
        when(apiKeyMapper.updateById(any())).thenReturn(1);

        apiKeyService.approve(1L, 2L);

        verify(apiKeyMapper).updateById(argThat(key ->
            "approved".equals(key.getStatus()) &&
            key.getApproverId().equals(2L) &&
            key.getExpiryDate() != null
        ));
    }

    @Test
    @DisplayName("审批通过 - 永久有效")
    void approvePermanent() {
        testApiKey.setExpiryType("permanent");
        when(apiKeyMapper.selectById(1L)).thenReturn(testApiKey);
        when(apiKeyMapper.updateById(any())).thenReturn(1);

        apiKeyService.approve(1L, 2L);

        verify(apiKeyMapper).updateById(argThat(key ->
            "approved".equals(key.getStatus()) &&
            key.getExpiryDate() == null // 永不过期
        ));
    }

    @Test
    @DisplayName("审批通过 - Key 不存在")
    void approveNotFound() {
        when(apiKeyMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> apiKeyService.approve(999L, 2L));
    }

    @Test
    @DisplayName("审批通过 - 状态不是待审批")
    void approveInvalidStatus() {
        testApiKey.setStatus("approved");
        when(apiKeyMapper.selectById(1L)).thenReturn(testApiKey);

        assertThrows(BusinessException.class, () -> apiKeyService.approve(1L, 2L));
    }

    @Test
    @DisplayName("审批拒绝 - 成功")
    void rejectSuccess() {
        when(apiKeyMapper.selectById(1L)).thenReturn(testApiKey);
        when(apiKeyMapper.updateById(any())).thenReturn(1);

        apiKeyService.reject(1L, 2L, "申请理由不充分");

        verify(apiKeyMapper).updateById(argThat(key ->
            "rejected".equals(key.getStatus()) &&
            "申请理由不充分".equals(key.getRejectionReason())
        ));
    }

    @Test
    @DisplayName("审批拒绝 - Key 不存在")
    void rejectNotFound() {
        when(apiKeyMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> apiKeyService.reject(999L, 2L, "理由"));
    }

    @Test
    @DisplayName("撤销 Key - 成功")
    void revokeSuccess() {
        when(apiKeyMapper.selectById(1L)).thenReturn(testApiKey);
        when(apiKeyMapper.updateById(any())).thenReturn(1);

        apiKeyService.revoke(1L, 1L);

        verify(apiKeyMapper).updateById(argThat(key ->
            "revoked".equals(key.getStatus())
        ));
    }

    @Test
    @DisplayName("撤销 Key - 非所有者")
    void revokeNotOwner() {
        when(apiKeyMapper.selectById(1L)).thenReturn(testApiKey);

        assertThrows(BusinessException.class, () -> apiKeyService.revoke(1L, 999L));
    }

    @Test
    @DisplayName("验证 Key - 有效")
    void isValidKey() {
        testApiKey.setStatus("approved");
        testApiKey.setExpiryDate(LocalDateTime.now().plusMonths(1));

        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testApiKey);

        boolean result = apiKeyService.isValid("sk-test123456789");

        assertTrue(result);
    }

    @Test
    @DisplayName("验证 Key - 不存在")
    void isValidNotFound() {
        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        boolean result = apiKeyService.isValid("sk-nonexistent");

        assertFalse(result);
    }

    @Test
    @DisplayName("验证 Key - 状态无效")
    void isValidInvalidStatus() {
        testApiKey.setStatus("pending");
        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testApiKey);

        boolean result = apiKeyService.isValid("sk-test123456789");

        assertFalse(result);
    }

    @Test
    @DisplayName("验证 Key - 已过期")
    void isValidExpired() {
        testApiKey.setStatus("approved");
        testApiKey.setExpiryDate(LocalDateTime.now().minusDays(1));

        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testApiKey);
        when(apiKeyMapper.updateById(any())).thenReturn(1);

        boolean result = apiKeyService.isValid("sk-test123456789");

        assertFalse(result);
        verify(apiKeyMapper).updateById(argThat(key ->
            "expired".equals(key.getStatus())
        ));
    }

    @Test
    @DisplayName("验证 Key - 永久有效")
    void isValidPermanent() {
        testApiKey.setStatus("approved");
        testApiKey.setExpiryDate(null); // 永不过期

        when(apiKeyMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testApiKey);

        boolean result = apiKeyService.isValid("sk-test123456789");

        assertTrue(result);
    }
}
