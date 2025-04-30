package biz.placelink.seek.dashboard.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.dashboard.service.DashboardService;
import biz.placelink.seek.dashboard.vo.SchAnalysisStatisticsVO;

@Controller
public class DashboardController {

    @Value("${web.push.vapid.public}")
    private String publicKey;

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping(value = "/public/dashboard/integrated")
    protected String integratedDashboard(@PathVariable String siteId, Model model) {
        model.addAttribute("pl_webpush_s2_key_public", publicKey);
        return "dashboard/integrated-dashboard";
    }

    @GetMapping(value = "/public/dashboard/{siteId}")
    protected String detailDashboard(@PathVariable String siteId, Model model) {
        model.addAttribute("pl_webpush_s2_key_public", publicKey);
        return "dashboard/detail-dashboard";
    }

    /**
     * 분석 현황 조회
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

    /**
     * 탐지 현황 조회
     *
     * @return 탐지 현황
     */
    @GetMapping(value = "/public/dashboard/detection-statistics")
    public ResponseEntity<Map<String, Object>> detectionStatistics(SchAnalysisStatisticsVO searchVO) {
        Map<String, Object> response = new HashMap<>();

        response.put("detectionData", dashboardService.selectDetectionStatistics(searchVO));
        response.put(Constants.RESULT_CODE, 1);
        return ResponseEntity.ok(response);
    }
}
