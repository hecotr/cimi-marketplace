package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Like Record Mapper
 */
@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {

    @Select("SELECT * FROM like_record WHERE user_id = #{userId} AND asset_type = #{assetType}")
    List<LikeRecord> findByUserIdAndType(Long userId, String assetType);

    @Select("SELECT * FROM like_record WHERE user_id = #{userId}")
    List<LikeRecord> findByUserId(Long userId);

    @Select("SELECT * FROM like_record WHERE user_id = #{userId} AND asset_id = #{assetId} AND asset_type = #{assetType}")
    LikeRecord findByUserAndAsset(Long userId, Long assetId, String assetType);

    @Select("SELECT COUNT(*) FROM like_record WHERE asset_id = #{assetId} AND asset_type = #{assetType}")
    long countByAsset(Long assetId, String assetType);
}
