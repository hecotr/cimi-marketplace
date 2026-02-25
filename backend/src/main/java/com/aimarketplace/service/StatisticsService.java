package com.aimarketplace.service;

import com.aimarketplace.dto.StatisticsDTO;

/**
 * Statistics Service Interface
 */
public interface StatisticsService {

    /**
     * Get overall statistics
     */
    StatisticsDTO getOverallStatistics();

    /**
     * Get statistics by asset type
     */
    StatisticsDTO getStatisticsByType(String type);

    /**
     * Get daily statistics for date range
     */
    StatisticsDTO getDailyStatistics(String startDate, String endDate);

    /**
     * Get category statistics
     */
    StatisticsDTO getCategoryStatistics();
}
