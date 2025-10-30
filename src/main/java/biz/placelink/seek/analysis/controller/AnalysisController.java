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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import biz.placelink.seek.analysis.vo.AnalysisResultVO;
import biz.placelink.seek.analysis.vo.FileOutboundHistVO;
import biz.placelink.seek.analysis.vo.SchFileOutboundHistVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationUnmaskHistVO;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.FileUtils;
import biz.placelink.seek.sample.vo.SchArticleVO;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.exception.S2RuntimeException;
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
     * 검증 파일 목록 엑셀 다운로드
     *
     * @param searchStartDe 조회 시작 일자
     * @param searchEndDe   조회 종료 일자
     * @param model         모델 객체
     * @return ExcelXlsxView
     */
    @PostMapping(value = "/analysis/detection-file-list/download")
    public String detectionFileListDownload(@RequestParam(name = "searchStartDe", defaultValue = "") String searchStartDe, @RequestParam(name = "searchEndDe", defaultValue = "") String searchEndDe, ModelMap model) {
        String pattern = "yyyyMMdd";
        SearchPeriod searchPeriod = AnalysisController.setSearchPeriod(searchStartDe, searchEndDe, pattern);

        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setSearchStartDate(searchPeriod.searchStartDate());
        searchVO.setSearchEndDate(searchPeriod.searchEndDate());
        searchVO.setPagingYn("N");
        searchVO.setOrderBy("CREATE_DT DESC");

        List<AnalysisResultVO> fileAnalysisList = analysisDetailService.selectFileAnalysisList(searchVO);
        if (S2Util.isEmpty(fileAnalysisList)) {
            throw new S2RuntimeException("데이터가 존재하지 않습니다.");
        }

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("파일명");
        headers.add("등록 일시");
        headers.add("상태");
        headers.add("파일용량");
        headers.add("민감정보개수");
        headers.add("민감정보등급");
        headers.add("다운로드 수");

        ArrayList<ArrayList<Object>> bodyList = new ArrayList<ArrayList<Object>>();
        for (AnalysisResultVO fileAnalysis : fileAnalysisList) {
            ArrayList<Object> body = new ArrayList<Object>();
            body.add(fileAnalysis.getDetectionFileName());
            body.add(fileAnalysis.getCreateDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            body.add(fileAnalysis.getAnalysisStatusCcdNm());
            body.add(fileAnalysis.getFormattedTotalFileSize() + " " + fileAnalysis.getFormattedTotalFileSizeUnit());
            body.add(new DecimalFormat("###,###").format(fileAnalysis.getTotalDetectionCount()));
            body.add(fileAnalysis.getMaxDetectionTypeCcdNm());
            body.add(new DecimalFormat("###,###").format(fileAnalysis.getDownloadCount()));
            bodyList.add(body);
        }

        model.addAttribute("fileName", "detection-file-list_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        model.addAttribute("head", headers);
        model.addAttribute("body", bodyList);
        model.addAttribute(Constants.RESULT_CODE, 1);
        return "excelXlsxView";
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
     * 데이터 복호화 현황(민감정보 처리 이력) 목록 엑셀 다운로드
     *
     * @param searchStartDe 조회 시작 일자
     * @param searchEndDe   조회 종료 일자
     * @param model         모델 객체
     * @return ExcelXlsxView
     */
    @PostMapping(value = "/analysis/sensitive-access-hist/download")
    public String sensitiveAccessHistDownload(@RequestParam(name = "searchStartDe", defaultValue = "") String searchStartDe, @RequestParam(name = "searchEndDe", defaultValue = "") String searchEndDe, ModelMap model) {
        String pattern = "yyyyMMdd";
        SearchPeriod searchPeriod = AnalysisController.setSearchPeriod(searchStartDe, searchEndDe, pattern);

        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setSearchStartDate(searchPeriod.searchStartDate());
        searchVO.setSearchEndDate(searchPeriod.searchEndDate());
        searchVO.setPagingYn("N");
        searchVO.setOrderBy("LAST_REQUEST_DT DESC");

        List<SensitiveInformationUnmaskHistVO> sensitiveInformationUnmaskHistList = sensitiveInformationUnmaskHistService.selectSensitiveInformationUnmaskHistList(searchVO);
        if (S2Util.isEmpty(sensitiveInformationUnmaskHistList)) {
            throw new S2RuntimeException("데이터가 존재하지 않습니다.");
        }

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("ID");
        headers.add("복호화 요청 수");
        headers.add("유형");
        headers.add("업무시간 요청 수");
        headers.add("비업무시간 요청 수");
        headers.add("탐지 결과");

        ArrayList<ArrayList<Object>> bodyList = new ArrayList<ArrayList<Object>>();
        for (SensitiveInformationUnmaskHistVO sensitiveInformationUnmaskHist : sensitiveInformationUnmaskHistList) {
            ArrayList<Object> body = new ArrayList<Object>();
            body.add(Optional.ofNullable(sensitiveInformationUnmaskHist.getUserId()).orElse("-"));
            body.add(new DecimalFormat("###,###").format(sensitiveInformationUnmaskHist.getTotalRequestCount()));
            body.add("DB");
            body.add(new DecimalFormat("###,###").format(sensitiveInformationUnmaskHist.getNormalRequestCount()));
            body.add(new DecimalFormat("###,###").format(sensitiveInformationUnmaskHist.getAbnormalRequestCount()));
            body.add(sensitiveInformationUnmaskHist.getConditionLevel());
            bodyList.add(body);
        }

        model.addAttribute("fileName", "sensitive-access-hist_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        model.addAttribute("head", headers);
        model.addAttribute("body", bodyList);
        model.addAttribute(Constants.RESULT_CODE, 1);
        return "excelXlsxView";
    }

    /**
     * PC 에이전트 상태
     *
     * @param model ModelMap
     * @return PC 에이전트 상태 화면
     */
    @GetMapping(value = "/public/analysis/agent-status")
    public String agentStatus(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model) {
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
     * @param schDe 조회일자
     * @param model ModelMap
     * @return 파일전송 차단 현황 화면
     */
    @GetMapping(value = "/public/analysis/file-blocking")
    public String fileBlockingList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, ModelMap model,
            @RequestParam(name = "schDe", defaultValue = "") String schDe, @RequestParam(name = "searchGroupingType", defaultValue = "user") String searchGroupingType) {
        String pattern = "yyyyMMdd";
        String vSchDe = S2DateUtil.getValidatedOrMaximumDateString(Optional.ofNullable(schDe).map(s -> s.replaceAll("-", "")).orElse(""), pattern, LocalDate.now());

        SchFileOutboundHistVO searchVO = new SchFileOutboundHistVO();
        searchVO.setSearchOutboundStatusCcd(Constants.CD_OUTBOUND_STATUS_BLOCKED);
        searchVO.setSearchStartDate(vSchDe, pattern);
        searchVO.setSearchEndDate(vSchDe, pattern);
        searchVO.setSearchGroupingType(searchGroupingType);
        searchVO.setPagingYn("N");
        searchVO.setOrderBy("LAST_EVENT_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        // 파일전송 차단 현황 목록 조회
        model.addAttribute("fileOutboundHistListStatus", fileOutboundHistService.selectFileOutboundHistListStatus(searchVO));
        model.addAttribute("fileOutboundHistList", fileOutboundHistService.selectFileOutboundHistList(searchVO));
        model.addAttribute("schDe", vSchDe);
        model.addAttribute("searchGroupingType", searchGroupingType);
        return "analysis/file-blocking";
    }

    /**
     * 파일전송 차단 현황 엑셀 다운로드
     *
     * @param schDe 조회일자
     * @param model 모델 객체
     * @return ExcelXlsxView
     */
    @PostMapping(value = "/public/analysis/file-blocking/download")
    public String fileBlockingDownload(@RequestParam(name = "schDe", defaultValue = "") String schDe, @RequestParam(name = "searchGroupingType", defaultValue = "user") String searchGroupingType, ModelMap model) {
        String pattern = "yyyyMMdd";
        String vSchDe = S2DateUtil.getValidatedOrMaximumDateString(Optional.ofNullable(schDe).map(s -> s.replaceAll("-", "")).orElse(""), pattern, LocalDate.now());

        SchFileOutboundHistVO searchVO = new SchFileOutboundHistVO();
        searchVO.setSearchOutboundStatusCcd(Constants.CD_OUTBOUND_STATUS_BLOCKED);
        searchVO.setSearchStartDate(vSchDe, pattern);
        searchVO.setSearchEndDate(vSchDe, pattern);
        searchVO.setSearchGroupingType(searchGroupingType);
        searchVO.setPagingYn("N");
        searchVO.setOrderBy("LAST_EVENT_DT DESC");

        List<FileOutboundHistVO> fileOutboundHistList = fileOutboundHistService.selectFileOutboundHistList(searchVO);
        if (S2Util.isEmpty(fileOutboundHistList)) {
            throw new S2RuntimeException("데이터가 존재하지 않습니다.");
        }

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("user".equals(searchGroupingType) ? "사용자 ID" : "채널");
        headers.add("차단 건수");
        headers.add("차단 용량");
        headers.add("차단 파일 개수");
        headers.add("업무시간 차단 수");
        headers.add("비업무시간 차단 수");
        headers.add("탐지 결과");

        ArrayList<ArrayList<Object>> bodyList = new ArrayList<ArrayList<Object>>();
        for (FileOutboundHistVO fileOutboundHist : fileOutboundHistList) {
            ArrayList<Object> body = new ArrayList<Object>();
            body.add("user".equals(searchGroupingType) ? Optional.ofNullable(fileOutboundHist.getUserId()).orElse("-") : fileOutboundHist.getOutboundChannelCcd());
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getActionCount()));
            body.add(fileOutboundHist.getFormattedTotalFileSize() + " " + fileOutboundHist.getFormattedTotalFileSizeUnit());
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getTotalFileCount()));
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getWorkingHourStatusNormalCount()));
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getWorkingHourStatusAbnormalCount()));
            body.add(fileOutboundHist.getConditionLevel());
            bodyList.add(body);
        }

        model.addAttribute("fileName", "file-blocking_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        model.addAttribute("head", headers);
        model.addAttribute("body", bodyList);
        model.addAttribute(Constants.RESULT_CODE, 1);
        return "excelXlsxView";
    }

    /**
     * 서명파일 전송 현황
     *
     * @param model ModelMap
     * @return 서명파일 전송 현황
     */
    @GetMapping(value = "/public/analysis/file-transfer")
    public String fileTransferList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, ModelMap model,
            @RequestParam(name = "schDe", defaultValue = "") String schDe, @RequestParam(name = "searchGroupingType", defaultValue = "user") String searchGroupingType) {
        String pattern = "yyyyMMdd";
        String vSchDe = S2DateUtil.getValidatedOrMaximumDateString(Optional.ofNullable(schDe).map(s -> s.replaceAll("-", "")).orElse(""), pattern, LocalDate.now());

        SchFileOutboundHistVO searchVO = new SchFileOutboundHistVO();
        searchVO.setSearchOutboundStatusCcd(Constants.CD_OUTBOUND_STATUS_SENT);
        searchVO.setSearchFileExtensionStatusCcd(Constants.CD_FILE_EXTENSION_STATUS_ALL_NORMAL);
        searchVO.setSearchStartDate(vSchDe, pattern);
        searchVO.setSearchEndDate(vSchDe, pattern);
        searchVO.setSearchGroupingType(searchGroupingType);
        searchVO.setPagingYn("N");
        searchVO.setOrderBy("LAST_EVENT_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        // 서명파일 전송 현황 목록 조회
        model.addAttribute("fileOutboundHistListStatus", fileOutboundHistService.selectFileOutboundHistListStatus(searchVO));
        model.addAttribute("fileOutboundHistList", fileOutboundHistService.selectFileOutboundHistList(searchVO));
        model.addAttribute("schDe", vSchDe);
        model.addAttribute("searchGroupingType", searchGroupingType);
        return "analysis/file-transfer";
    }

    /**
     * 서명파일 전송 현황 다운로드
     *
     * @param schDe 조회일자
     * @param model 모델 객체
     * @return ExcelXlsxView
     */
    @PostMapping(value = "/public/analysis/file-transfer/download")
    public String fileTransferDownload(@RequestParam(name = "schDe", defaultValue = "") String schDe, @RequestParam(name = "searchGroupingType", defaultValue = "user") String searchGroupingType, ModelMap model) {
        String pattern = "yyyyMMdd";
        String vSchDe = S2DateUtil.getValidatedOrMaximumDateString(Optional.ofNullable(schDe).map(s -> s.replaceAll("-", "")).orElse(""), pattern, LocalDate.now());

        SchFileOutboundHistVO searchVO = new SchFileOutboundHistVO();
        searchVO.setSearchOutboundStatusCcd(Constants.CD_OUTBOUND_STATUS_SENT);
        searchVO.setSearchFileExtensionStatusCcd(Constants.CD_FILE_EXTENSION_STATUS_ALL_NORMAL);
        searchVO.setSearchStartDate(vSchDe, pattern);
        searchVO.setSearchEndDate(vSchDe, pattern);
        searchVO.setSearchGroupingType(searchGroupingType);
        searchVO.setPagingYn("N");
        searchVO.setOrderBy("LAST_EVENT_DT DESC");

        List<FileOutboundHistVO> fileOutboundHistList = fileOutboundHistService.selectFileOutboundHistList(searchVO);
        if (S2Util.isEmpty(fileOutboundHistList)) {
            throw new S2RuntimeException("데이터가 존재하지 않습니다.");
        }

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("user".equals(searchGroupingType) ? "사용자 ID" : "채널");
        headers.add("전송 건수");
        headers.add("전송 용량");
        headers.add("전송 파일 개수");
        headers.add("업무시간 전송 수");
        headers.add("비업무시간 전송 수");

        ArrayList<ArrayList<Object>> bodyList = new ArrayList<ArrayList<Object>>();
        for (FileOutboundHistVO fileOutboundHist : fileOutboundHistList) {
            ArrayList<Object> body = new ArrayList<Object>();
            body.add("user".equals(searchGroupingType) ? Optional.ofNullable(fileOutboundHist.getUserId()).orElse("-") : fileOutboundHist.getOutboundChannelCcd());
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getActionCount()));
            body.add(fileOutboundHist.getFormattedTotalFileSize() + " " + fileOutboundHist.getFormattedTotalFileSizeUnit());
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getTotalFileCount()));
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getWorkingHourStatusNormalCount()));
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getWorkingHourStatusAbnormalCount()));
            bodyList.add(body);
        }

        model.addAttribute("fileName", "file-transfer_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        model.addAttribute("head", headers);
        model.addAttribute("body", bodyList);
        model.addAttribute(Constants.RESULT_CODE, 1);
        return "excelXlsxView";
    }

    /**
     * 시스템 파일 전송 현황
     *
     * @param model ModelMap
     * @return 시스템 파일 전송 현황 화면
     */
    @GetMapping(value = "/public/analysis/system-transfer")
    public String systemTransferList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, ModelMap model,
            @RequestParam(name = "schDe", defaultValue = "") String schDe, @RequestParam(name = "searchGroupingType", defaultValue = "user") String searchGroupingType) {
        String pattern = "yyyyMMdd";
        String vSchDe = S2DateUtil.getValidatedOrMaximumDateString(Optional.ofNullable(schDe).map(s -> s.replaceAll("-", "")).orElse(""), pattern, LocalDate.now());

        SchFileOutboundHistVO searchVO = new SchFileOutboundHistVO();
        searchVO.setSearchOutboundStatusCcd(Constants.CD_OUTBOUND_STATUS_SENT);
        searchVO.setSearchFileExtensionStatusCcd(Constants.CD_FILE_EXTENSION_STATUS_NONE_NORMAL);
        searchVO.setSearchStartDate(vSchDe, pattern);
        searchVO.setSearchEndDate(vSchDe, pattern);
        searchVO.setSearchGroupingType(searchGroupingType);
        searchVO.setPagingYn("N");
        searchVO.setOrderBy("LAST_EVENT_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        // 시스템 파일 전송 현황 목록 조회
        model.addAttribute("fileOutboundHistListStatus", fileOutboundHistService.selectFileOutboundHistListStatus(searchVO));
        model.addAttribute("fileOutboundHistList", fileOutboundHistService.selectFileOutboundHistList(searchVO));
        model.addAttribute("schDe", vSchDe);
        model.addAttribute("searchGroupingType", searchGroupingType);
        return "analysis/system-transfer";
    }

    /**
     * 시스템 파일 전송 현황 다운로드
     *
     * @param schDe 조회일자
     * @param model 모델 객체
     * @return ExcelXlsxView
     */
    @PostMapping(value = "/public/analysis/system-transfer/download")
    public String systemTransferDownload(@RequestParam(name = "schDe", defaultValue = "") String schDe, @RequestParam(name = "searchGroupingType", defaultValue = "user") String searchGroupingType, ModelMap model) {
        String pattern = "yyyyMMdd";
        String vSchDe = S2DateUtil.getValidatedOrMaximumDateString(Optional.ofNullable(schDe).map(s -> s.replaceAll("-", "")).orElse(""), pattern, LocalDate.now());

        SchFileOutboundHistVO searchVO = new SchFileOutboundHistVO();
        searchVO.setSearchOutboundStatusCcd(Constants.CD_OUTBOUND_STATUS_SENT);
        searchVO.setSearchFileExtensionStatusCcd(Constants.CD_FILE_EXTENSION_STATUS_NONE_NORMAL);
        searchVO.setSearchStartDate(vSchDe, pattern);
        searchVO.setSearchEndDate(vSchDe, pattern);
        searchVO.setSearchGroupingType(searchGroupingType);
        searchVO.setPagingYn("N");
        searchVO.setOrderBy("LAST_EVENT_DT DESC");

        List<FileOutboundHistVO> fileOutboundHistList = fileOutboundHistService.selectFileOutboundHistList(searchVO);
        if (S2Util.isEmpty(fileOutboundHistList)) {
            throw new S2RuntimeException("데이터가 존재하지 않습니다.");
        }

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("user".equals(searchGroupingType) ? "사용자 ID" : "채널");
        headers.add("전송 건수");
        headers.add("전송 용량");
        headers.add("전송 파일 개수");
        headers.add("업무시간 전송 수");
        headers.add("비업무시간 전송 수");
        headers.add("탐지 결과");

        ArrayList<ArrayList<Object>> bodyList = new ArrayList<ArrayList<Object>>();
        for (FileOutboundHistVO fileOutboundHist : fileOutboundHistList) {
            ArrayList<Object> body = new ArrayList<Object>();
            body.add("user".equals(searchGroupingType) ? Optional.ofNullable(fileOutboundHist.getUserId()).orElse("-") : fileOutboundHist.getOutboundChannelCcd());
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getActionCount()));
            body.add(fileOutboundHist.getFormattedTotalFileSize() + " " + fileOutboundHist.getFormattedTotalFileSizeUnit());
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getTotalFileCount()));
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getWorkingHourStatusNormalCount()));
            body.add(new DecimalFormat("###,###").format(fileOutboundHist.getWorkingHourStatusAbnormalCount()));
            body.add(fileOutboundHist.getConditionLevel());
            bodyList.add(body);
        }

        model.addAttribute("fileName", "system-transfer_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        model.addAttribute("head", headers);
        model.addAttribute("body", bodyList);
        model.addAttribute(Constants.RESULT_CODE, 1);
        return "excelXlsxView";
    }

    public record SearchPeriod(LocalDate searchStartDate, LocalDate searchEndDate) {
        public String searchStartDe(String pattern) {
            return this.searchStartDate() != null ? this.searchStartDate().format(DateTimeFormatter.ofPattern(S2Util.isNotEmpty(pattern) ? pattern : "yyyyMMdd")) : null;
        }

        public String searchEndDe(String pattern) {
            return this.searchEndDate() != null ? this.searchEndDate().format(DateTimeFormatter.ofPattern(S2Util.isNotEmpty(pattern) ? pattern : "yyyyMMdd")) : null;
        }
    }

    /**
     * 검색 기간을 설정한다.
     * 조회 시작일이 유효 하지 않으면 종료일 보다 한달전으로 설정한다. (종료일이 2월 2일 이라면 시작일은 1월 3일로 설정한다.)
     * 조회 종료일이 유효하지 않으면 오늘로 설정한다.
     *
     * @param searchStartDe 조회 시작일 (yyyyMMdd 형식)
     * @param searchEndDe   조회 종료일 (yyyyMMdd 형식)
     * @param pattern       날짜 형식 패턴 (예: "yyyyMMdd")
     * @return 설정된 조회 시작일과 종료일을 담은 Map
     */
    public static @Nonnull SearchPeriod setSearchPeriod(String searchStartDe, String searchEndDe, String pattern) {
        LocalDate searchStartDate = S2DateUtil.isValidDate(searchStartDe, pattern, false) ? S2DateUtil.parseToLocalDate(searchStartDe, pattern, false) : null;
        LocalDate searchEndDate = S2DateUtil.isValidDate(searchEndDe, pattern, false) ? S2DateUtil.parseToLocalDate(searchEndDe, pattern, false) : LocalDate.now();

        LocalDate stdStartDate = searchEndDate.minusMonths(1).plusDays(1);
        if (searchStartDate == null || searchStartDate.isBefore(stdStartDate)) {
            searchStartDate = stdStartDate;
        }
        return new SearchPeriod(searchStartDate, searchEndDate);
    }

}
