package biz.placelink.seek.dashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import biz.placelink.seek.analysis.service.MaskHistService;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.dashboard.service.DashboardService;
import biz.placelink.seek.dashboard.vo.SchAnalysisStatisticsVO;

@Controller
public class DashboardController {

    @Value("${web.push.vapid.public}")
    private String publicKey;

    private final DashboardService dashboardService;
    private final MaskHistService maskHistService;

    public DashboardController(DashboardService dashboardService, MaskHistService maskHistService) {
        this.dashboardService = dashboardService;
        this.maskHistService = maskHistService;
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

        response.put("analysisData", dashboardService.selectAnalysisStatistics(searchVO));
        response.put("detectionData", dashboardService.selectDetectionStatistics(searchVO));
        response.put("maskingData", maskHistService.selectMaskStatus(new SimpleDateFormat("yyyyMMdd").format(new Date())));
        response.put("realtimeData", dashboardService.selectRealtimeAnalysisCount(searchVO));
        response.put("hitRankDataList", dashboardService.selectTopSensitiveInformation(searchVO));

        response.put(Constants.RESULT_CODE, 1);
        return ResponseEntity.ok(response);
    }

}
