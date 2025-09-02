package biz.placelink.seek.dashboard.controller;

import biz.placelink.seek.analysis.service.MaskHistService;
import biz.placelink.seek.com.constants.Constants;
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

    public DashboardController(DashboardService dashboardService, MaskHistService maskHistService) {
        this.dashboardService = dashboardService;
        this.maskHistService = maskHistService;
    }

    /**
     * 지정된 사이트의 세부 사항 대시 보드보기를 검색하도록 요청을 처리합니다.
     * 입력 매개 변수를 기반으로 콘솔의 디스플레이 환경 설정을 관리합니다.
     *
     * @param siteId  사이트의 식별자 인 렌더링 할 특정 대시 보드보기를 결정합니다.
     * @param console "on": 푸시 메시지 보기, "off": 푸시 메시지 숨기기, "all": 푸시 메시지와 함께 전체 매개변수까지 보이기
     * @param session 현재 사용자의 상태를 관리하는 데 사용되는 HTTP 세션
     * @param model   뷰에 속성을 전달하는 데 사용되는 모델 객체
     * @return View Name
     */
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
