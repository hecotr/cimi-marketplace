package com.aimarketplace.controller;

import com.aimarketplace.common.Result;
import com.aimarketplace.dto.StatisticsDTO;
import com.aimarketplace.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Statistics Controller
 */
@RestController
@RequestMapping("/api/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/overall")
    public Result<StatisticsDTO> getOverallStatistics() {
        StatisticsDTO stats = statisticsService.getOverallStatistics();
        return Result.success(stats);
    }

    @GetMapping("/type/{type}")
    public Result<StatisticsDTO> getStatisticsByType(@PathVariable String type) {
        StatisticsDTO stats = statisticsService.getStatisticsByType(type);
        return Result.success(stats);
    }

    @GetMapping("/daily")
    public Result<StatisticsDTO> getDailyStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        StatisticsDTO stats = statisticsService.getDailyStatistics(startDate, endDate);
        return Result.success(stats);
    }

    @GetMapping("/category")
    public Result<StatisticsDTO> getCategoryStatistics() {
        StatisticsDTO stats = statisticsService.getCategoryStatistics();
        return Result.success(stats);
    }
}
