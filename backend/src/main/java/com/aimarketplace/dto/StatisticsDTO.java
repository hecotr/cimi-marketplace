package com.aimarketplace.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Statistics DTO
 */
@Data
public class StatisticsDTO {

    private Long totalAssets;

    private Long totalPublishedAssets;

    private Long pendingApprovals;

    private Long totalUsers;

    private Map<String, Long> assetsByType;

    private List<CategoryStat> assetsByCategory;

    private List<DateCount> dailyStats;

    private Long totalViews;

    private Long totalDownloads;

    private Long totalLikes;

    @Data
    public static class CategoryStat {
        private Long categoryId;
        private String categoryName;
        private Long count;
    }

    @Data
    public static class DateCount {
        private String date;
        private Long count;
    }
}
