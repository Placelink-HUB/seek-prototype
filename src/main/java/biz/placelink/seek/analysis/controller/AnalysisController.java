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
package biz.placelink.seek.analysis.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;

import biz.placelink.seek.analysis.service.AgentService;
import biz.placelink.seek.analysis.service.AnalysisDetailService;
import biz.placelink.seek.analysis.service.AnalysisService;
import biz.placelink.seek.analysis.service.FileOutboundHistService;
import biz.placelink.seek.analysis.service.SensitiveInformationUnmaskHistService;
import biz.placelink.seek.analysis.vo.SchFileOutboundHistVO;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.FileUtils;
import biz.placelink.seek.sample.vo.SchArticleVO;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.file.FileManager;
import kr.s2.ext.util.S2DateUtil;
import kr.s2.ext.util.S2JsonUtil;
import kr.s2.ext.util.S2ServletUtil;
import kr.s2.ext.util.S2Util;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 14.      s2          최초생성
 * </pre>
 */
@Controller
public class AnalysisController {

    private final AnalysisService analysisService;
    private final AnalysisDetailService analysisDetailService;
    private final AgentService agentService;
    private final SensitiveInformationUnmaskHistService sensitiveInformationUnmaskHistService;
    private final FileOutboundHistService fileOutboundHistService;
    private final FileManager fileManager;

    public AnalysisController(AnalysisService analysisService, AnalysisDetailService analysisDetailService, AgentService agentService, SensitiveInformationUnmaskHistService sensitiveInformationUnmaskHistService, FileOutboundHistService fileOutboundHistService, FileManager fileManager) {
        this.analysisService = analysisService;
        this.analysisDetailService = analysisDetailService;
        this.agentService = agentService;
        this.sensitiveInformationUnmaskHistService = sensitiveInformationUnmaskHistService;
        this.fileOutboundHistService = fileOutboundHistService;
        this.fileManager = fileManager;
    }

    @Value("${fs.file.ext}")
    private String allowedFileExt;

    /**
     * Sample 페이지
     */
    @RequestMapping(value = "analysis")
    public void tissue(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) Map<String, String> formDataParams, @RequestBody(required = false) Map<String, Object> jsonParams)
            throws IOException {
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

            for (MultipartFile file : fileMap.values()) {
                if (!file.isEmpty()) {
                    // 파일 처리 로직
                }
            }
        }

        // 요청 형식 확인
        String contentType = request.getContentType();

        if (contentType != null && contentType.contains("application/json")) {
            // JSON 요청 처리
            if (jsonParams != null) {
                // jsonParams 를 이용한 처리
                System.out.println("JSON 요청: " + jsonParams);
            } else {
                // 직접 request.getInputStream() 을 이용하여 처리
                BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                Map<String, Object> map = S2JsonUtil.parseJsonTo(json.toString(), new TypeReference<Map<String, Object>>() {
                });
                System.out.println("JSON 요청(getInputStream): " + map);
            }
        } else if (contentType != null && (contentType.contains("application/x-www-form-urlencoded") || contentType.contains("multipart/form-data"))) {
            // form-data 요청 처리
            if (formDataParams != null) {
                // formDataParams 를 이용한 처리
                System.out.println("Form-data 요청: " + formDataParams);
            } else {
                // request.getParameter() 를 이용한 처리
                System.out.println("Form-data 요청(getParameter): " + request.getParameterMap());
            }
        } else {
            // 기타 요청 처리
            System.out.println("기타 요청");
        }
    }

    /**
     * 파일 검증
     *
     * @param model ModelMap
     * @return 파일 검증 화면
     */
    @GetMapping(value = "/public/analysis/detection-file")
    public String file(
            HttpServletResponse response, ModelMap model,
            @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo,
            @RequestParam(name = "searchStartDe", defaultValue = "") String searchStartDe, @RequestParam(name = "searchEndDe", defaultValue = "") String searchEndDe) {

        String pattern = "yyyyMMdd";
        SearchPeriod searchPeriod = setSearchPeriod(searchStartDe, searchEndDe, pattern);

        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setSearchStartDate(searchPeriod.searchStartDate());
        searchVO.setSearchEndDate(searchPeriod.searchEndDate());
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        model.addAttribute("fileAnalysisListPagination", analysisDetailService.selectFileAnalysisListWithPagination(searchVO));
        model.addAttribute("fileAnalysisListStatus", analysisDetailService.selectFileAnalysisListStatus(searchVO));
        model.addAttribute("searchStartDeStr", searchPeriod.searchStartDe("yyyy년 MM월 dd일"));
        model.addAttribute("searchEndDeStr", searchPeriod.searchEndDe("yyyy년 MM월 dd일"));
        return "analysis/detection-file";
    }

    /**
     * 검증 파일 등록
     *
     * @param files 검증 파일 목록
     * @return 검증 파일 등록 결과
     * @throws IOException IOException
     */
    @PostMapping(value = "/analysis/create-detection-file")
    public ResponseEntity<Map<String, Object>> createDetectionFile(@RequestParam("files") List<MultipartFile> files, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();

        if (files.isEmpty()) {
            response.put("message", "파일이 비어있습니다.");
            return ResponseEntity.ok(response);
        } else if (!FileUtils.checkMultipartFileList(files, allowedFileExt.split(","))) {
            response.put("message", "등록 가능한 파일이 아닙니다.");
            return ResponseEntity.ok(response);
        }

        response.put(Constants.RESULT_CODE, analysisService.createDetectionFile(files, S2ServletUtil.getClientIp(request)));
        return ResponseEntity.ok(response);
    }

    /**
     * 보고서 다운로드
     *
     * @return 보고서 다운로드
     * @throws IOException IOException
     */
    @GetMapping(value = "/analysis/download-report")
    public String downloadReport(Model model) throws IOException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String fileName = "보안점검결과보고서.pdf";

        model.addAttribute("fileName", fileName);
        model.addAttribute("fileData", fileManager.readFile("/opt/seek/var/files", fileName));
        model.addAttribute(Constants.RESULT_CODE, 1);
        return "downloadView";
    }

    /**
     * 데이터 복호화 현황(민감정보 처리 이력)
     *
     * @param model ModelMap
     * @return 민감정보 처리 이력 목록
     */
    @GetMapping(value = "/public/analysis/sensitive-access-hist")
    public String sensitiveAccessHist(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model,
            @RequestParam(name = "searchStartDe", defaultValue = "") String searchStartDe, @RequestParam(name = "searchEndDe", defaultValue = "") String searchEndDe) {

        String pattern = "yyyyMMdd";
        SearchPeriod searchPeriod = AnalysisController.setSearchPeriod(searchStartDe, searchEndDe, pattern);

        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setSearchStartDate(searchPeriod.searchStartDate());
        searchVO.setSearchEndDate(searchPeriod.searchEndDate());
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("LAST_REQUEST_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        // 민감정보 처리 이력 목록 조회
        model.addAttribute("sensitiveInformationUnmaskHistListStatus", sensitiveInformationUnmaskHistService.selectSensitiveInformationUnmaskHistListStatus(searchVO));
        model.addAttribute("sensitiveInformationUnmaskHistListPagination", sensitiveInformationUnmaskHistService.selectSensitiveInformationUnmaskHistListWithPagination(searchVO));
        model.addAttribute("searchStartDeStr", searchPeriod.searchStartDe("yyyy년 MM월 dd일"));
        model.addAttribute("searchEndDeStr", searchPeriod.searchEndDe("yyyy년 MM월 dd일"));
        return "analysis/sensitive-access-hist";
    }

    /**
     * PC 에이전트 상태
     *
     * @param model ModelMap
     * @return PC 에이전트 상태 화면
     */
    @GetMapping(value = "/public/analysis/agent-status")
    public String agentStatus(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model) {
        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        // PC 에이전트 상태 목록 조회
        // model.addAttribute("fileAnalysisListPagination", analysisDetailService.selectFileAnalysisListWithPagination(searchVO));
        return "analysis/agent-status";
    }

    /**
     * PC 에이전트 상태 목록을 조회한다.
     *
     * @return PC 에이전트 상태 목록
     */
    @GetMapping(value = "/public/analysis/agent-status-list")
    public ResponseEntity<Map<String, Object>> agentStatusList() {
        Map<String, Object> response = new HashMap<>();

        response.put("agentStatusListStatus", agentService.selectAgentStatusListStatus());
        response.put("agentStatusList", agentService.selectAgentStatusList());

        response.put(Constants.RESULT_CODE, 1);
        return ResponseEntity.ok(response);
    }

    /**
     * 파일전송 차단 현황
     *
     * @param model ModelMap
     * @return 파일전송 차단 현황 화면
     */
    @GetMapping(value = "/public/analysis/file-blocking")
    public String fileBlockingList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model,
            @RequestParam(name = "searchStartDe", defaultValue = "") String searchStartDe, @RequestParam(name = "searchEndDe", defaultValue = "") String searchEndDe, @RequestParam(name = "searchGroupingType", defaultValue = "user") String searchGroupingType) {

        String pattern = "yyyyMMdd";
        SearchPeriod searchPeriod = AnalysisController.setSearchPeriod(searchStartDe, searchEndDe, pattern);

        SchFileOutboundHistVO searchVO = new SchFileOutboundHistVO();
        searchVO.setSearchOutboundStatusCcd(Constants.CD_OUTBOUND_STATUS_BLOCKED);
        searchVO.setSearchStartDate(searchPeriod.searchStartDate());
        searchVO.setSearchEndDate(searchPeriod.searchEndDate());
        searchVO.setSearchGroupingType(searchGroupingType);
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("LAST_EVENT_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        // 파일전송 차단 현황 목록 조회
        model.addAttribute("fileOutboundHistListStatus", fileOutboundHistService.selectFileOutboundHistListStatus(searchVO));
        model.addAttribute("fileOutboundHistListPagination", fileOutboundHistService.selectFileOutboundHistListWithPagination(searchVO));
        model.addAttribute("searchStartDeStr", searchPeriod.searchStartDe("yyyy년 MM월 dd일"));
        model.addAttribute("searchEndDeStr", searchPeriod.searchEndDe("yyyy년 MM월 dd일"));
        model.addAttribute("searchGroupingType", searchGroupingType);
        return "analysis/file-blocking";
    }

    /**
     * 서명파일 전송 현황
     *
     * @param model ModelMap
     * @return 서명파일 전송 현황
     */
    @GetMapping(value = "/public/analysis/file-transfer")
    public String fileTransferList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model,
            @RequestParam(name = "searchStartDe", defaultValue = "") String searchStartDe, @RequestParam(name = "searchEndDe", defaultValue = "") String searchEndDe, @RequestParam(name = "searchGroupingType", defaultValue = "user") String searchGroupingType) {

        String pattern = "yyyyMMdd";
        SearchPeriod searchPeriod = AnalysisController.setSearchPeriod(searchStartDe, searchEndDe, pattern);

        SchFileOutboundHistVO searchVO = new SchFileOutboundHistVO();
        searchVO.setSearchOutboundStatusCcd(Constants.CD_OUTBOUND_STATUS_SENT);
        searchVO.setSearchFileExtensionStatusCcd(Constants.CD_FILE_EXTENSION_STATUS_ALL_NORMAL);
        searchVO.setSearchStartDate(searchPeriod.searchStartDate());
        searchVO.setSearchEndDate(searchPeriod.searchEndDate());
        searchVO.setSearchGroupingType(searchGroupingType);
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("LAST_EVENT_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        // 서명파일 전송 현황 목록 조회
        model.addAttribute("fileOutboundHistListStatus", fileOutboundHistService.selectFileOutboundHistListStatus(searchVO));
        model.addAttribute("fileOutboundHistListPagination", fileOutboundHistService.selectFileOutboundHistListWithPagination(searchVO));
        model.addAttribute("searchStartDeStr", searchPeriod.searchStartDe("yyyy년 MM월 dd일"));
        model.addAttribute("searchEndDeStr", searchPeriod.searchEndDe("yyyy년 MM월 dd일"));
        model.addAttribute("searchGroupingType", searchGroupingType);
        return "analysis/file-transfer";
    }

    /**
     * 시스템 파일 전송 현황
     *
     * @param model ModelMap
     * @return 시스템 파일 전송 현황 화면
     */
    @GetMapping(value = "/public/analysis/system-transfer")
    public String systemTransferList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model,
            @RequestParam(name = "searchStartDe", defaultValue = "") String searchStartDe, @RequestParam(name = "searchEndDe", defaultValue = "") String searchEndDe, @RequestParam(name = "searchGroupingType", defaultValue = "user") String searchGroupingType) {

        String pattern = "yyyyMMdd";
        SearchPeriod searchPeriod = AnalysisController.setSearchPeriod(searchStartDe, searchEndDe, pattern);

        SchFileOutboundHistVO searchVO = new SchFileOutboundHistVO();
        searchVO.setSearchOutboundStatusCcd(Constants.CD_OUTBOUND_STATUS_SENT);
        searchVO.setSearchFileExtensionStatusCcd(Constants.CD_FILE_EXTENSION_STATUS_NONE_NORMAL);
        searchVO.setSearchStartDate(searchPeriod.searchStartDate());
        searchVO.setSearchEndDate(searchPeriod.searchEndDate());
        searchVO.setSearchGroupingType(searchGroupingType);
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("LAST_EVENT_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        // 시스템 파일 전송 현황 목록 조회
        model.addAttribute("fileOutboundHistListStatus", fileOutboundHistService.selectFileOutboundHistListStatus(searchVO));
        model.addAttribute("fileOutboundHistListPagination", fileOutboundHistService.selectFileOutboundHistListWithPagination(searchVO));
        model.addAttribute("searchStartDeStr", searchPeriod.searchStartDe("yyyy년 MM월 dd일"));
        model.addAttribute("searchEndDeStr", searchPeriod.searchEndDe("yyyy년 MM월 dd일"));
        model.addAttribute("searchGroupingType", searchGroupingType);
        return "analysis/system-transfer";
    }

    /**
     * 이상 패턴 탐지 현황
     *
     * @param model ModelMap
     * @return 이상 패턴 탐지 현황 목록
     */
    @GetMapping(value = "/public/analysis/anomaly_detection")
    public String anomalyDetectionList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model,
            @RequestParam(name = "searchStartDe", defaultValue = "") String searchStartDe, @RequestParam(name = "searchEndDe", defaultValue = "") String searchEndDe) {

        String pattern = "yyyyMMdd";
        SearchPeriod searchPeriod = AnalysisController.setSearchPeriod(searchStartDe, searchEndDe, pattern);

        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setSearchStartDate(searchPeriod.searchStartDate());
        searchVO.setSearchEndDate(searchPeriod.searchEndDate());
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        // 이상 패턴 탐지 현황 목록 조회
        // model.addAttribute("fileAnalysisListPagination", analysisDetailService.selectFileAnalysisListWithPagination(searchVO));
        model.addAttribute("searchStartDeStr", searchPeriod.searchStartDe("yyyy년 MM월 dd일"));
        model.addAttribute("searchEndDeStr", searchPeriod.searchEndDe("yyyy년 MM월 dd일"));
        return "analysis/anomaly_detection";
    }

    public record SearchPeriod(LocalDate searchStartDate, LocalDate searchEndDate) {
        public SearchPeriod {
        }

        public String searchStartDe(String pattern) {
            return this.searchStartDate() != null ? this.searchStartDate().format(DateTimeFormatter.ofPattern(S2Util.isNotEmpty(pattern) ? pattern : "yyyyMMdd")) : null;
        }

        public String searchEndDe(String pattern) {
            return this.searchEndDate() != null ? this.searchEndDate().format(DateTimeFormatter.ofPattern(S2Util.isNotEmpty(pattern) ? pattern : "yyyyMMdd")) : null;
        }
    }

    /**
     * 검색 기간을 설정한다.
     * 조회 시작일과 종료일이 유효하지 않으면 최근 1개월을 기본값으로 설정한다.
     *
     * @param searchStartDe 조회 시작일 (yyyyMMdd 형식)
     * @param searchEndDe   조회 종료일 (yyyyMMdd 형식)
     * @param pattern       날짜 형식 패턴 (예: "yyyyMMdd")
     * @return 설정된 조회 시작일과 종료일을 담은 Map
     */
    public static @Nonnull SearchPeriod setSearchPeriod(String searchStartDe, String searchEndDe, String pattern) {
        LocalDate searchStartDate = null;
        LocalDate searchEndDate = null;

        if (!S2DateUtil.isValidDate(searchStartDe, pattern, false) && !S2DateUtil.isValidDate(searchStartDe, pattern, false)) {
            // 조회 기간이 없다면 최근 1달을 기본으로 한다.
            searchEndDate = LocalDate.now();
            searchStartDate = searchEndDate.minusMonths(1).plusDays(1);
        } else {
            searchStartDate = S2DateUtil.parseToLocalDate(searchStartDe, pattern, false);
            searchEndDate = S2DateUtil.parseToLocalDate(searchEndDe, pattern, false);
        }

        return new SearchPeriod(searchStartDate, searchEndDate);
    }

}
