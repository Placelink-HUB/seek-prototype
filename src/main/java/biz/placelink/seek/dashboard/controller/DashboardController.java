package biz.placelink.seek.dashboard.controller;

import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.dashboard.service.DashboardMapper;
import biz.placelink.seek.dashboard.service.DashboardService;
import biz.placelink.seek.dashboard.vo.SchAnalysisStatisticsVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping(value = "/public/dashboard/integrated")
    protected String integratedDashboard(@PathVariable String siteId) {
        return "dashboard/integrated-dashboard";
    }

    @GetMapping(value = "/public/dashboard/{siteId}")
    protected String detailDashboard(@PathVariable String siteId) {
        return "dashboard/detail-dashboard";
    }

    /**
     *  분석 현황 조회
     *
     * @return 분석 현황
     */
    @GetMapping(value = "/public/dashboard/analysis-statistics")
    public ResponseEntity<Map<String, Object>> analysisStatistics(SchAnalysisStatisticsVO searchVO) {
        Map<String, Object> response = new HashMap<>();

        response.put("statisticsData", dashboardService.selectAnalysisStatistics(searchVO));
        response.put(Constants.RESULT_CODE, 1);
        return ResponseEntity.ok(response);
    }
}
