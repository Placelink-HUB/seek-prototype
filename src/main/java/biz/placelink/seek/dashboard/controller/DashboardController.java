package biz.placelink.seek.dashboard.controller;

import biz.placelink.seek.analysis.service.MaskHistService;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.GlobalSharedStore;
import biz.placelink.seek.dashboard.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.s2.ext.util.S2Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class DashboardController {

    @Value("${web.push.vapid.public}")
    private String publicKey;

    private final DashboardService dashboardService;
    private final MaskHistService maskHistService;
    private final GlobalSharedStore store;

    public DashboardController(DashboardService dashboardService, MaskHistService maskHistService, GlobalSharedStore store) {
        this.dashboardService = dashboardService;
        this.maskHistService = maskHistService;
        this.store = store;
    }

    /**
     * 대시보드 상세 정보를 렌더링하는 메서드.
     * 주어진 사이트 ID에 따라 적합한 대시보드 뷰를 반환하며, 콘솔 설정 값을 이용해 모델 속성을 업데이트한다.
     *
     * @param siteId  사이트 ID로, 렌더링할 대시보드 종류를 결정한다. 예: "integrated", "file".
     * @param console "on": console 에서 푸시 메시지 보기, "off": console 에서 푸시 메시지 숨기기
     * @param session 현재 사용자 세션 객체.
     * @param model   뷰 렌더링 시 사용되는 모델 객체.
     * @return 사용한 사이트 ID에 따라 지정된 대시보드 뷰 이름.
     */
    @GetMapping(value = "/dashboard/{siteId}")
    protected String detailDashboard(@PathVariable String siteId, @RequestParam(name = "console", defaultValue = "") String console, HttpSession session, Model model) {
        model.addAttribute("pl_webpush_s2_key_public", publicKey);

        /*
         * Push 메시지 console.log 작성 방법
         * on: Push 메시지 로그를 남긴다.
         * all: 모든 파라미터를 포함한 Push 메시지 로그를 남긴다.
         * off: Push 메시지 로그를 남기지 않는다.
         */
        if (Set.of("on", "all", "off").contains(console)) {
            model.addAttribute("console", console);

            if ("all".equals(console)) {
                store.put("pushConsoleType", console, 600_1000);
            } else if ("off".equals(console)) {
                store.remove("pushConsoleType");
            }
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
