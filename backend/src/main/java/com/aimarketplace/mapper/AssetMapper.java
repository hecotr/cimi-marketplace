package com.aimarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aimarketplace.entity.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Asset Mapper
 */
@Mapper
public interface AssetMapper extends BaseMapper<Asset> {

    @Select("SELECT * FROM asset WHERE type = #{type} AND status = 'published' ORDER BY created_at DESC LIMIT #{limit}")
    List<Asset> findPublishedByType(String type, int limit);

    @Select("SELECT * FROM asset WHERE created_by = #{userId} ORDER BY created_at DESC")
    List<Asset> findByCreator(Long userId);

    @Select("SELECT COUNT(*) FROM asset WHERE type = #{type}")
    long countByType(String type);

    @Select("SELECT DATE(created_at) as date, COUNT(*) as count FROM asset WHERE created_at >= #{startDate} GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> countByDate(String startDate);
}
