package com.aimarketplace.service;

import com.aimarketplace.entity.Favorite;
import com.aimarketplace.entity.LikeRecord;
import com.aimarketplace.mapper.FavoriteMapper;
import com.aimarketplace.mapper.LikeRecordMapper;
import com.aimarketplace.service.impl.InteractionServiceImpl;
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
class InteractionServiceTest {

    @Mock
    private FavoriteMapper favoriteMapper;

    @Mock
    private LikeRecordMapper likeRecordMapper;

    @InjectMocks
    private InteractionServiceImpl interactionService;

    private Favorite testFavorite;
    private LikeRecord testLike;

    @BeforeEach
    void setUp() {
        testFavorite = new Favorite();
        testFavorite.setId(1L);
        testFavorite.setUserId(1L);
        testFavorite.setAssetId(1L);
        testFavorite.setAssetType("skill");
        testFavorite.setCreatedAt(LocalDateTime.now());

        testLike = new LikeRecord();
        testLike.setId(1L);
        testLike.setUserId(1L);
        testLike.setAssetId(1L);
        testLike.setAssetType("skill");
        testLike.setAssetVersionId(1L);
        testLike.setCreatedAt(LocalDateTime.now());
    }

    // ============ 收藏测试 ============

    @Test
    @DisplayName("添加收藏 - 成功")
    void addFavoriteSuccess() {
        when(favoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(favoriteMapper.insert(any(Favorite.class))).thenReturn(1);

        interactionService.addFavorite(1L, 1L, "skill");

        verify(favoriteMapper).insert(argThat(fav ->
            fav.getUserId().equals(1L) &&
            fav.getAssetId().equals(1L) &&
            "skill".equals(fav.getAssetType())
        ));
    }

    @Test
    @DisplayName("添加收藏 - 已收藏则跳过")
    void addFavoriteAlreadyExists() {
        when(favoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        interactionService.addFavorite(1L, 1L, "skill");

        verify(favoriteMapper, never()).insert(any());
    }

    @Test
    @DisplayName("取消收藏 - 成功")
    void removeFavoriteSuccess() {
        when(favoriteMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        interactionService.removeFavorite(1L, 1L, "skill");

        verify(favoriteMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("检查是否收藏 - 已收藏")
    void isFavoritedTrue() {
        when(favoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        boolean result = interactionService.isFavorited(1L, 1L, "skill");

        assertTrue(result);
    }

    @Test
    @DisplayName("检查是否收藏 - 未收藏")
    void isFavoritedFalse() {
        when(favoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        boolean result = interactionService.isFavorited(1L, 1L, "skill");

        assertFalse(result);
    }

    @Test
    @DisplayName("获取用户收藏列表 - 指定类型")
    void getFavoriteAssetIdsByType() {
        Favorite fav1 = new Favorite();
        fav1.setAssetId(1L);
        fav1.setAssetType("skill");

        Favorite fav2 = new Favorite();
        fav2.setAssetId(2L);
        fav2.setAssetType("skill");

        when(favoriteMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(fav1, fav2));

        List<Long> result = interactionService.getFavoriteAssetIds(1L, "skill");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
    }

    @Test
    @DisplayName("获取用户收藏列表 - 所有类型")
    void getFavoriteAssetIdsAllTypes() {
        Favorite fav1 = new Favorite();
        fav1.setAssetId(1L);
        fav1.setAssetType("skill");

        Favorite fav2 = new Favorite();
        fav2.setAssetId(2L);
        fav2.setAssetType("llm");

        when(favoriteMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(fav1, fav2));

        List<Long> result = interactionService.getFavoriteAssetIds(1L, null);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ============ 点赞测试 ============

    @Test
    @DisplayName("添加点赞 - 成功")
    void addLikeSuccess() {
        when(likeRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(likeRecordMapper.insert(any(LikeRecord.class))).thenReturn(1);

        interactionService.addLike(1L, 1L, "skill", 1L);

        verify(likeRecordMapper).insert(argThat(like ->
            like.getUserId().equals(1L) &&
            like.getAssetId().equals(1L) &&
            "skill".equals(like.getAssetType()) &&
            like.getAssetVersionId().equals(1L)
        ));
    }

    @Test
    @DisplayName("添加点赞 - 已点赞则跳过")
    void addLikeAlreadyExists() {
        when(likeRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        interactionService.addLike(1L, 1L, "skill", 1L);

        verify(likeRecordMapper, never()).insert(any());
    }

    @Test
    @DisplayName("添加点赞 - 无版本ID")
    void addLikeWithoutVersion() {
        when(likeRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(likeRecordMapper.insert(any(LikeRecord.class))).thenReturn(1);

        interactionService.addLike(1L, 1L, "llm", null);

        verify(likeRecordMapper).insert(argThat(like ->
            like.getAssetVersionId() == null
        ));
    }

    @Test
    @DisplayName("取消点赞 - 成功")
    void removeLikeSuccess() {
        when(likeRecordMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        interactionService.removeLike(1L, 1L, "skill", 1L);

        verify(likeRecordMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("取消点赞 - 无版本ID")
    void removeLikeWithoutVersion() {
        when(likeRecordMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        interactionService.removeLike(1L, 1L, "llm", null);

        verify(likeRecordMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("检查是否点赞 - 已点赞")
    void isLikedTrue() {
        when(likeRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        boolean result = interactionService.isLiked(1L, 1L, "skill", 1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("检查是否点赞 - 未点赞")
    void isLikedFalse() {
        when(likeRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        boolean result = interactionService.isLiked(1L, 1L, "skill", 1L);

        assertFalse(result);
    }

    @Test
    @DisplayName("检查是否点赞 - 无版本ID")
    void isLikedWithoutVersion() {
        when(likeRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        boolean result = interactionService.isLiked(1L, 1L, "llm", null);

        assertFalse(result);
    }

    // ============ 边界情况测试 ============

    @Test
    @DisplayName("收藏 - LLM 类型")
    void favoriteLlmType() {
        when(favoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(favoriteMapper.insert(any(Favorite.class))).thenReturn(1);

        interactionService.addFavorite(1L, 1L, "llm");

        verify(favoriteMapper).insert(argThat(fav ->
            "llm".equals(fav.getAssetType())
        ));
    }

    @Test
    @DisplayName("点赞 - LLM 类型")
    void likeLlmType() {
        when(likeRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(likeRecordMapper.insert(any(LikeRecord.class))).thenReturn(1);

        interactionService.addLike(1L, 1L, "llm", null);

        verify(likeRecordMapper).insert(argThat(like ->
            "llm".equals(like.getAssetType())
        ));
    }

    @Test
    @DisplayName("收藏列表 - 空列表")
    void getFavoritesEmpty() {
        when(favoriteMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        List<Long> result = interactionService.getFavoriteAssetIds(1L, "skill");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
