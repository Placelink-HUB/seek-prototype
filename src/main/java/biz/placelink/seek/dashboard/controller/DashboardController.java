package biz.placelink.seek.dashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpSession;
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
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping(value = "/dashboard/{siteId}")
    protected String detailDashboard(@PathVariable String siteId, @RequestParam(name = "console", defaultValue = "") String console, HttpSession session, Model model) {
        model.addAttribute("pl_webpush_s2_key_public", publicKey);

        String vConsole = console;
        if ("all".equals(console)) {
            vConsole = "on";
            session.setAttribute("pushConsoleType", "all");
        } else if ("off".equals(console)) {
            session.removeAttribute("pushConsoleType");
        }

        if (Set.of("on", "off").contains(vConsole)) {
            model.addAttribute("console", vConsole);
        }

        String viewName = switch (siteId) {
            case "integrated" -> "dashboard/integrated-dashboard";
            case "file" -> "dashboard/file-dashboard";
            default -> "dashboard/detail-dashboard";
        };
        return viewName;
    }

    /**
     * 분석 현황 조회
     *
     * @return 분석 현황
     */
    @GetMapping(value = {"/dashboard/analysis-statistics", "/dashboard/analysis-statistics2"})
    public ResponseEntity<Map<String, Object>> analysisStatistics(@RequestParam(name = "schDe", required = false) String schDe, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (S2Util.isEmpty(schDe)) {
            schDe = new SimpleDateFormat("yyyyMMdd").format(new Date());
        }

        response.put("analysisData", dashboardService.selectAnalysisStatistics(schDe));
        response.put("detectionData", dashboardService.selectDetectionStatistics(schDe));
        response.put("realtimeData", dashboardService.selectRealtimeAnalysisCount(schDe));
        response.put("lastAnalysisCompleteDateTimeStr", dashboardService.selectLastAnalysisCompleteDateTimeStr(schDe));
        response.put("hitRankDataList", dashboardService.selectTopSensitiveInformation(schDe));

        if ("/dashboard/analysis-statistics".equals(request.getServletPath())) {
            response.put("maskingData", maskHistService.selectMaskStatus(schDe));
        } else if ("/dashboard/analysis-statistics2".equals(request.getServletPath())) {
            response.put("fileAnalysisInfo", dashboardService.selectFileAnalysisInformation(schDe));
            response.put("fileOutboundHistInfoList", dashboardService.selectFileOutboundHistInformation(schDe));
        }

        response.put(Constants.RESULT_CODE, 1);
        return ResponseEntity.ok(response);
    }

}
