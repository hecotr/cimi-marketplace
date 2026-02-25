package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.AssetVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Asset Version Mapper
 */
@Mapper
public interface AssetVersionMapper extends BaseMapper<AssetVersion> {

    @Select("SELECT * FROM asset_version WHERE asset_id = #{assetId} ORDER BY created_at DESC")
    List<AssetVersion> findByAssetId(Long assetId);

    @Select("SELECT * FROM asset_version WHERE asset_id = #{assetId} AND version = #{version}")
    AssetVersion findByAssetIdAndVersion(Long assetId, String version);

    @Select("SELECT * FROM asset_version WHERE asset_id = #{assetId} AND is_current = true")
    AssetVersion findCurrentByAssetId(Long assetId);
}
