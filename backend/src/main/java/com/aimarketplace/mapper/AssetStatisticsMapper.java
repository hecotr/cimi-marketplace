package com.aimarketplace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Asset Statistics Mapper
 */
@Mapper
public interface AssetStatisticsMapper {

    @Select("SELECT COUNT(*) as count, type FROM asset WHERE status = 'published' GROUP BY type")
    List<Map<String, Object>> countByType();

    @Select("SELECT COUNT(*) as count, category_id, c.name as category_name FROM asset a LEFT JOIN category c ON a.category_id = c.id WHERE a.status = 'published' GROUP BY category_id")
    List<Map<String, Object>> countByCategory();

    @Select("SELECT DATE(created_at) as date, COUNT(*) as count FROM asset WHERE created_at >= #{startDate} GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> countByDate(String startDate);

    @Select("SELECT SUM(view_count) as views, SUM(download_count) as downloads, SUM(like_count) as likes FROM asset")
    Map<String, Object> getTotalStats();

    @Select("SELECT COUNT(*) as total FROM asset WHERE status = 'published'")
    long getTotalPublishedAssets();

    @Select("SELECT COUNT(*) as total FROM asset WHERE status = 'pending'")
    long getPendingApprovals();

    @Select("SELECT COUNT(*) as total, type FROM asset GROUP BY type")
    List<Map<String, Object>> getAssetCounts();

    @Select("SELECT COUNT(*) as total FROM \"user\"")
    long getTotalUsers();

    @Select("SELECT DATE(created_at) as date, COUNT(*) as count FROM asset WHERE created_at >= NOW() - INTERVAL '30 days' GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> getLast30DaysCount();
}
