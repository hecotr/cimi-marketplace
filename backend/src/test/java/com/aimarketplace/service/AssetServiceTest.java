package com.aimarketplace.service;

import com.aimarketplace.common.BusinessException;
import com.aimarketplace.dto.AssetDTO;
import com.aimarketplace.dto.AssetPublishRequest;
import com.aimarketplace.entity.ApprovalRecord;
import com.aimarketplace.entity.Asset;
import com.aimarketplace.entity.AssetVersion;
import com.aimarketplace.entity.Category;
import com.aimarketplace.entity.User;
import com.aimarketplace.mapper.ApprovalRecordMapper;
import com.aimarketplace.mapper.AssetMapper;
import com.aimarketplace.mapper.AssetVersionMapper;
import com.aimarketplace.mapper.CategoryMapper;
import com.aimarketplace.mapper.UserMapper;
import com.aimarketplace.service.impl.AssetServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
class AssetServiceTest {

    @Mock
    private AssetMapper assetMapper;

    @Mock
    private AssetVersionMapper assetVersionMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ApprovalRecordMapper approvalRecordMapper;

    @InjectMocks
    private AssetServiceImpl assetService;

    private Asset testAsset;
    private AssetVersion testVersion;
    private Category testCategory;
    private User testUser;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("写作助手");
        testCategory.setAssetType("skill");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testAsset = new Asset();
        testAsset.setId(1L);
        testAsset.setName("邮件写作助手");
        testAsset.setDescription("帮助撰写专业邮件");
        testAsset.setAssetType("skill");
        testAsset.setCategoryId(1L);
        testAsset.setStatus("draft");
        testAsset.setCreatedBy(1L);
        testAsset.setCurrentVersionId(1L);
        testAsset.setCreatedAt(LocalDateTime.now());
        testAsset.setUpdatedAt(LocalDateTime.now());

        testVersion = new AssetVersion();
        testVersion.setId(1L);
        testVersion.setAssetId(1L);
        testVersion.setVersionNo(1);
        testVersion.setContent("你是一个专业的邮件写作助手...");
        testVersion.setFileType("md");
        testVersion.setViewCount(0);
        testVersion.setDownloadCount(0);
        testVersion.setLikeCount(0);
        testVersion.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("发布资产 - 保存草稿")
    void publishAssetDraft() {
        AssetPublishRequest request = new AssetPublishRequest();
        request.setAssetType("skill");
        request.setName("测试技能");
        request.setCategoryId(1L);
        request.setDescription("测试描述");
        request.setContent("测试内容");
        request.setFileType("md");

        when(assetMapper.insert(any(Asset.class))).thenAnswer(invocation -> {
            Asset asset = invocation.getArgument(0);
            asset.setId(1L);
            return 1;
        });
        when(assetVersionMapper.insert(any(AssetVersion.class))).thenReturn(1);
        when(assetMapper.updateById(any())).thenReturn(1);

        Long assetId = assetService.publishAsset(1L, request);

        assertNotNull(assetId);
        verify(assetMapper).insert(any(Asset.class));
        verify(assetVersionMapper).insert(any(AssetVersion.class));
    }

    @Test
    @DisplayName("获取资产详情 - 成功")
    void getAssetByIdSuccess() {
        when(assetMapper.selectById(1L)).thenReturn(testAsset);
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(assetVersionMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testVersion));

        AssetDTO result = assetService.getAssetById(1L);

        assertNotNull(result);
        assertEquals("邮件写作助手", result.getName());
        assertEquals("写作助手", result.getCategoryName());
        assertEquals("testuser", result.getCreatedBy());
    }

    @Test
    @DisplayName("获取资产详情 - 不存在")
    void getAssetByIdNotFound() {
        when(assetMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> assetService.getAssetById(999L));
    }

    @Test
    @DisplayName("更新资产 - 成功")
    void updateAssetSuccess() {
        AssetPublishRequest request = new AssetPublishRequest();
        request.setName("更新后的名称");
        request.setDescription("更新后的描述");
        request.setCategoryId(1L);
        request.setContent("更新后的内容");

        when(assetMapper.selectById(1L)).thenReturn(testAsset);
        when(assetMapper.updateById(any())).thenReturn(1);
        when(assetVersionMapper.selectById(1L)).thenReturn(testVersion);
        when(assetVersionMapper.updateById(any())).thenReturn(1);

        assetService.updateAsset(1L, 1L, request);

        verify(assetMapper).updateById(any(Asset.class));
    }

    @Test
    @DisplayName("更新资产 - 非所有者")
    void updateAssetNotOwner() {
        AssetPublishRequest request = new AssetPublishRequest();
        request.setName("更新后的名称");

        when(assetMapper.selectById(1L)).thenReturn(testAsset);

        assertThrows(BusinessException.class, () -> assetService.updateAsset(1L, 999L, request));
    }

    @Test
    @DisplayName("更新资产 - 已审核通过不能修改")
    void updateAssetAlreadyApproved() {
        testAsset.setStatus("approved");
        AssetPublishRequest request = new AssetPublishRequest();
        request.setName("更新后的名称");

        when(assetMapper.selectById(1L)).thenReturn(testAsset);

        assertThrows(BusinessException.class, () -> assetService.updateAsset(1L, 1L, request));
    }

    @Test
    @DisplayName("提交审核 - 成功")
    void submitForReviewSuccess() {
        when(assetMapper.selectById(1L)).thenReturn(testAsset);
        when(assetMapper.updateById(any())).thenReturn(1);

        assetService.submitForReview(1L, 1L);

        verify(assetMapper).updateById(argThat(asset ->
            "pending_review".equals(asset.getStatus())
        ));
    }

    @Test
    @DisplayName("提交审核 - 非所有者")
    void submitForReviewNotOwner() {
        when(assetMapper.selectById(1L)).thenReturn(testAsset);

        assertThrows(BusinessException.class, () -> assetService.submitForReview(1L, 999L));
    }

    @Test
    @DisplayName("提交审核 - 状态不允许")
    void submitForReviewInvalidStatus() {
        testAsset.setStatus("approved");
        when(assetMapper.selectById(1L)).thenReturn(testAsset);

        assertThrows(BusinessException.class, () -> assetService.submitForReview(1L, 1L));
    }

    @Test
    @DisplayName("审核通过")
    void approveAsset() {
        testAsset.setStatus("pending_review");
        when(assetMapper.selectById(1L)).thenReturn(testAsset);
        when(assetMapper.updateById(any())).thenReturn(1);
        when(approvalRecordMapper.insert(any())).thenReturn(1);

        assetService.approve(1L, 2L, "审核通过");

        verify(assetMapper).updateById(argThat(asset ->
            "approved".equals(asset.getStatus())
        ));
        verify(approvalRecordMapper).insert(any(ApprovalRecord.class));
    }

    @Test
    @DisplayName("审核拒绝")
    void rejectAsset() {
        testAsset.setStatus("pending_review");
        when(assetMapper.selectById(1L)).thenReturn(testAsset);
        when(assetMapper.updateById(any())).thenReturn(1);
        when(approvalRecordMapper.insert(any())).thenReturn(1);

        assetService.reject(1L, 2L, "内容不符合规范");

        verify(assetMapper).updateById(argThat(asset ->
            "rejected".equals(asset.getStatus())
        ));
    }

    @Test
    @DisplayName("下架资产")
    void offlineAsset() {
        when(assetMapper.selectById(1L)).thenReturn(testAsset);
        when(assetMapper.updateById(any())).thenReturn(1);

        assetService.offline(1L, 1L);

        verify(assetMapper).updateById(argThat(asset ->
            "offline".equals(asset.getStatus())
        ));
    }

    @Test
    @DisplayName("删除资产 - 成功")
    void deleteAssetSuccess() {
        when(assetMapper.selectById(1L)).thenReturn(testAsset);
        when(assetVersionMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
        when(assetMapper.deleteById(1L)).thenReturn(1);

        assetService.deleteAsset(1L, 1L);

        verify(assetVersionMapper).delete(any(LambdaQueryWrapper.class));
        verify(assetMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除资产 - 非所有者")
    void deleteAssetNotOwner() {
        when(assetMapper.selectById(1L)).thenReturn(testAsset);

        assertThrows(BusinessException.class, () -> assetService.deleteAsset(1L, 999L));
    }

    @Test
    @DisplayName("增加浏览量")
    void incrementViewCount() {
        when(assetVersionMapper.selectById(1L)).thenReturn(testVersion);
        when(assetVersionMapper.updateById(any())).thenReturn(1);

        assetService.incrementViewCount(1L);

        verify(assetVersionMapper).updateById(argThat(version ->
            version.getViewCount() == 1
        ));
    }

    @Test
    @DisplayName("增加下载量")
    void incrementDownloadCount() {
        when(assetVersionMapper.selectById(1L)).thenReturn(testVersion);
        when(assetVersionMapper.updateById(any())).thenReturn(1);

        assetService.incrementDownloadCount(1L);

        verify(assetVersionMapper).updateById(argThat(version ->
            version.getDownloadCount() == 1
        ));
    }

    @Test
    @DisplayName("获取资产列表 - 分页")
    void getAssets() {
        Page<Asset> page = new Page<>(1, 10);
        page.setRecords(List.of(testAsset));
        page.setTotal(1);

        when(assetMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        IPage<AssetDTO> result = assetService.getAssets("skill", null, null, null, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("获取我的资产列表")
    void getMyAssets() {
        when(assetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testAsset));
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        List<AssetDTO> result = assetService.getMyAssets(1L, "skill", "draft");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("邮件写作助手", result.get(0).getName());
    }
}
