/*
 * SEEK
 * Copyright (C) 2025 placelink
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * =========================================================================
 *
 * 상업적 이용 또는 AGPL-3.0의 공개 의무를 면제받기
 * 위해서는, placelink로부터 별도의 상업용 라이선스(Commercial License)를 구매해야 합니다.
 * For commercial use or to obtain an exemption from the AGPL-3.0 license
 * requirements, please purchase a commercial license from placelink.
 * *** 문의처: help@placelink.shop (README.md 참조)
 */
package biz.placelink.seek.dashboard.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import biz.placelink.seek.analysis.service.MaskHistService;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.GlobalSharedStore;
import biz.placelink.seek.dashboard.service.DashboardService;
import biz.placelink.seek.sample.vo.SchArticleVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.s2.ext.util.S2DateUtil;

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
     * ?console=all&consoleType=file_outbound => console=all: 모든 파라미터를 포함한 Push 메시지 로그를 남긴다. consoleType=file_outbound: 파일 외부전송 로그만 출력
     *
     * @param siteId      사이트 ID로, 렌더링할 대시보드 종류를 결정한다. 예: "integrated", "file".
     * @param console     "on": console 에서 푸시 메시지 보기, "off": console 에서 푸시 메시지 숨기기
     * @param consoleType "file_outbound": 파일 외부전송 로그만 출력, "agent_heartbeat": SEEK 에이전트 하트비트 로그만 출력
     * @param session     현재 사용자 세션 객체.
     * @param model       뷰 렌더링 시 사용되는 모델 객체.
     * @return 사용한 사이트 ID에 따라 지정된 대시보드 뷰 이름.
     */
    @GetMapping(value = "/public/dashboard/{siteId}")
    protected String detailDashboard(@PathVariable String siteId, @RequestParam(name = "schDe", defaultValue = "") String schDe, @RequestParam(name = "console", defaultValue = "") String console, @RequestParam(name = "consoleType", defaultValue = "") String consoleType, HttpSession session, Model model) {
        String vSchDe = S2DateUtil.getValidatedOrMaximumDateString(Optional.ofNullable(schDe).map(s -> s.replaceAll("-", "")).orElse(""), "yyyyMMdd", LocalDate.now());

        model.addAttribute("pl_webpush_s2_key_public", publicKey);
        model.addAttribute("schDe", vSchDe);

        /*
         * Push 메시지 console.log 작성 방법
         * on: Push 메시지 로그를 남긴다.
         * all: 모든 파라미터를 포함한 Push 메시지 로그를 남긴다.
         * off: Push 메시지 로그를 남기지 않는다.
         */
        if (Set.of("on", "all", "off").contains(console)) {
            model.addAttribute("console", console);

            if ("all".equals(console)) {
                store.put("pushConsole", console, 600_1000);
            } else if ("off".equals(console)) {
                store.remove("pushConsole");
            }
        }

        if (StringUtils.isNotEmpty(consoleType)) {
            model.addAttribute("consoleType", consoleType);
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
    @GetMapping(value = "/public/dashboard/analysis-statistics")
    public ResponseEntity<Map<String, Object>> analysisStatistics(@RequestParam(name = "schDe", defaultValue = "") String schDe, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        String vSchDe = S2DateUtil.getValidatedOrMaximumDateString(Optional.ofNullable(schDe).map(s -> s.replaceAll("-", "")).orElse(""), "yyyyMMdd", LocalDate.now());

        response.put("analysisData", dashboardService.selectAnalysisStatistics(vSchDe));
        response.put("detectionData", dashboardService.selectDetectionStatistics(vSchDe));
        response.put("realtimeData", dashboardService.selectRealtimeAnalysisCount(vSchDe));
        response.put("lastAnalysisCompleteDateTimeStr", dashboardService.selectLastAnalysisCompleteDateTimeStr(vSchDe));
        response.put("hitRankDataList", dashboardService.selectTopSensitiveInformation(vSchDe));
        response.put("maskingData", maskHistService.selectMaskStatus(vSchDe));
        response.put("fileAnalysisInfo", dashboardService.selectFileAnalysisInformation(vSchDe));
        response.put("fileOutboundHistStatusInfoList", dashboardService.selectFileOutboundHistStatusInformation(vSchDe));
        response.put("fileOutboundHistChannelInfoList", dashboardService.selectFileOutboundHistChannelInformation(vSchDe, Constants.CD_OUTBOUND_STATUS_SENT));

        response.put(Constants.RESULT_CODE, 1);
        return ResponseEntity.ok(response);
    }

    /**
     * 이상 패턴 탐지 현황
     *
     * @param model ModelMap
     * @return 이상 패턴 탐지 현황 목록
     */
    @GetMapping(value = "/public/dashboard/anomaly_detection")
    public String anomalyDetectionList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, @RequestParam(name = "schDe", defaultValue = "") String schDe, ModelMap model) {
        String pattern = "yyyyMMdd";
        String vSchDe = S2DateUtil.getValidatedOrMaximumDateString(Optional.ofNullable(schDe).map(s -> s.replaceAll("-", "")).orElse(""), pattern, LocalDate.now());

        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setSearchStartDate(vSchDe, pattern);
        searchVO.setSearchEndDate(vSchDe, pattern);
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        // 이상 패턴 탐지 현황 목록 조회
        model.addAttribute("userIntegratedActivityInformation", dashboardService.selectUserIntegratedActivityInformation(vSchDe));
        model.addAttribute("schDe", vSchDe);
        return "dashboard/anomaly_detection";
    }

}
