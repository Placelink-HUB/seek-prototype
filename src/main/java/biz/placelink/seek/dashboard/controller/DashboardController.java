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
import org.springframework.web.bind.annotation.RequestParam;

import biz.placelink.seek.analysis.service.MaskHistService;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.dashboard.service.DashboardService;
import kr.s2.ext.util.S2Util;

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
    public ResponseEntity<Map<String, Object>> analysisStatistics(@RequestParam(name = "schDe", required = false) String schDe) {
        Map<String, Object> response = new HashMap<>();

        if (S2Util.isEmpty(schDe)) {
            schDe = new SimpleDateFormat("yyyyMMdd").format(new Date());
        }

        response.put("analysisData", dashboardService.selectAnalysisStatistics(schDe));
        response.put("detectionData", dashboardService.selectDetectionStatistics(schDe));
        response.put("maskingData", maskHistService.selectMaskStatus(schDe));
        response.put("realtimeData", dashboardService.selectRealtimeAnalysisCount(schDe));
        response.put("hitRankDataList", dashboardService.selectTopSensitiveInformation(schDe));

        response.put(Constants.RESULT_CODE, 1);
        return ResponseEntity.ok(response);
    }

}
