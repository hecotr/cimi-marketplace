package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Favorite Mapper
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND asset_type = #{assetType}")
    List<Favorite> findByUserIdAndType(Long userId, String assetType);

    @Select("SELECT * FROM favorite WHERE user_id = #{userId}")
    List<Favorite> findByUserId(Long userId);

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND asset_id = #{assetId} AND asset_type = #{assetType}")
    Favorite findByUserAndAsset(Long userId, Long assetId, String assetType);

    @Select("SELECT COUNT(*) FROM favorite WHERE asset_id = #{assetId} AND asset_type = #{assetType}")
    long countByAsset(Long assetId, String assetType);
}
