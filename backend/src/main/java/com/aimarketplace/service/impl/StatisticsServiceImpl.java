package com.aimarketplace.service.impl;

import com.aimarketplace.dto.StatisticsDTO;
import com.aimarketplace.mapper.AssetMapper;
import com.aimarketplace.mapper.AssetStatisticsMapper;
import com.aimarketplace.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Statistics Service Implementation
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private AssetStatisticsMapper statisticsMapper;

    @Autowired
    private AssetMapper assetMapper;

    @Override
    public StatisticsDTO getOverallStatistics() {
        StatisticsDTO stats = new StatisticsDTO();

        stats.setTotalAssets((long) assetMapper.countByType(null));
        stats.setTotalPublishedAssets(statisticsMapper.getTotalPublishedAssets());
        stats.setPendingApprovals(statisticsMapper.getPendingApprovals());
        stats.setTotalUsers(statisticsMapper.getTotalUsers());

        Map<String, Long> assetsByType = new HashMap<>();
        for (Map<String, Object> item : statisticsMapper.countByType()) {
            assetsByType.put((String) item.get("type"), ((Number) item.get("count")).longValue());
        }
        stats.setAssetsByType(assetsByType);

        List<StatisticsDTO.CategoryStat> categoryStats = new ArrayList<>();
        for (Map<String, Object> item : statisticsMapper.countByCategory()) {
            StatisticsDTO.CategoryStat stat = new StatisticsDTO.CategoryStat();
            stat.setCategoryId(((Number) item.get("category_id") != null) ? ((Number) item.get("category_id")).longValue() : null);
            stat.setCategoryName((String) item.get("category_name"));
            stat.setCount(((Number) item.get("count")).longValue());
            categoryStats.add(stat);
        }
        stats.setAssetsByCategory(categoryStats);

        List<StatisticsDTO.DateCount> dailyStats = new ArrayList<>();
        for (Map<String, Object> item : statisticsMapper.getLast30DaysCount()) {
            StatisticsDTO.DateCount dc = new StatisticsDTO.DateCount();
            dc.setDate(item.get("date").toString());
            dc.setCount(((Number) item.get("count")).longValue());
            dailyStats.add(dc);
        }
        stats.setDailyStats(dailyStats);

        Map<String, Object> totalStats = statisticsMapper.getTotalStats();
        stats.setTotalViews(totalStats.get("views") != null ? ((Number) totalStats.get("views")).longValue() : 0L);
        stats.setTotalDownloads(totalStats.get("downloads") != null ? ((Number) totalStats.get("downloads")).longValue() : 0L);
        stats.setTotalLikes(totalStats.get("likes") != null ? ((Number) totalStats.get("likes")).longValue() : 0L);

        return stats;
    }

    @Override
    public StatisticsDTO getStatisticsByType(String type) {
        StatisticsDTO stats = new StatisticsDTO();

        stats.setTotalPublishedAssets(statisticsMapper.getTotalPublishedAssets());
        stats.setPendingApprovals(statisticsMapper.getPendingApprovals());

        Map<String, Long> assetsByType = new HashMap<>();
        long typeCount = assetMapper.countByType(type);
        assetsByType.put(type, typeCount);
        stats.setAssetsByType(assetsByType);

        return stats;
    }

    @Override
    public StatisticsDTO getDailyStatistics(String startDate, String endDate) {
        StatisticsDTO stats = new StatisticsDTO();

        String start = startDate != null ? startDate : LocalDate.now().minusDays(30).toString();
        List<Map<String, Object>> dateData = statisticsMapper.countByDate(start);

        List<StatisticsDTO.DateCount> dailyStats = new ArrayList<>();
        for (Map<String, Object> item : dateData) {
            StatisticsDTO.DateCount dc = new StatisticsDTO.DateCount();
            dc.setDate(item.get("date").toString());
            dc.setCount(((Number) item.get("count")).longValue());
            dailyStats.add(dc);
        }
        stats.setDailyStats(dailyStats);

        return stats;
    }

    @Override
    public StatisticsDTO getCategoryStatistics() {
        StatisticsDTO stats = new StatisticsDTO();

        List<StatisticsDTO.CategoryStat> categoryStats = new ArrayList<>();
        for (Map<String, Object> item : statisticsMapper.countByCategory()) {
            StatisticsDTO.CategoryStat stat = new StatisticsDTO.CategoryStat();
            stat.setCategoryId(((Number) item.get("category_id") != null) ? ((Number) item.get("category_id")).longValue() : null);
            stat.setCategoryName((String) item.get("category_name"));
            stat.setCount(((Number) item.get("count")).longValue());
            categoryStats.add(stat);
        }
        stats.setAssetsByCategory(categoryStats);

        return stats;
    }
}
