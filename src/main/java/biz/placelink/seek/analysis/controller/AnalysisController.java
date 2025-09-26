package biz.placelink.seek.analysis.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import biz.placelink.seek.analysis.service.AnalysisDetailService;
import biz.placelink.seek.analysis.service.AnalysisService;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.FileUtils;
import biz.placelink.seek.sample.vo.SchArticleVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.file.FileManager;
import kr.s2.ext.util.S2JsonUtil;

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
    private final FileManager fileManager;

    public AnalysisController(AnalysisService analysisService, AnalysisDetailService analysisDetailService, FileManager fileManager) {
        this.analysisService = analysisService;
        this.analysisDetailService = analysisDetailService;
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
     * 파일 검증 목록
     *
     * @param model ModelMap
     * @return 파일 검증 목록
     */
    @GetMapping(value = "/analysis/detection-file-list")
    public String file(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model) {
        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        model.addAttribute("fileAnalysisListPagination", analysisDetailService.selectFileAnalysisListWithPagination(searchVO));
        response.setHeader("X-Seek-Mode", seekMode);
        return "analysis/detection-file-list";
    }

    /**
     * 검증 파일 등록
     *
     * @param files 검증 파일 목록
     * @return 검증 파일 등록 결과
     * @throws IOException IOException
     */
    @PostMapping(value = "/analysis/create-detection-file")
    public ResponseEntity<Map<String, Object>> createDetectionFile(@RequestParam("files") List<MultipartFile> files) throws IOException {
        Map<String, Object> response = new HashMap<>();

        if (files.isEmpty()) {
            response.put("message", "파일이 비어있습니다.");
            return ResponseEntity.ok(response);
        } else if (!FileUtils.checkMultipartFileList(files, allowedFileExt.split(","))) {
            response.put("message", "등록 가능한 파일이 아닙니다.");
            return ResponseEntity.ok(response);
        }

        response.put(Constants.RESULT_CODE, analysisService.createDetectionFile(files));
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
    @GetMapping(value = "/analysis/sensitive-access-hist")
    public String sensitiveAccessHist(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model) {
        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        // 민감정보 처리 이력 목록 조회
        // model.addAttribute("fileAnalysisListPagination", analysisDetailService.selectFileAnalysisListWithPagination(searchVO));
        response.setHeader("X-Seek-Mode", seekMode);
        return "analysis/sensitive-access-hist";
    }

    /**
     * PC 에이전트 상태
     *
     * @param model ModelMap
     * @return PC 에이전트 상태 목록
     */
    @GetMapping(value = "/analysis/agent-status-list")
    public String agentStatusList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model) {
        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        // PC 에이전트 상태 목록 조회
        // model.addAttribute("fileAnalysisListPagination", analysisDetailService.selectFileAnalysisListWithPagination(searchVO));
        response.setHeader("X-Seek-Mode", seekMode);
        return "analysis/agent-status-list";
    }

    /**
     * 파일전송 차단 현황
     *
     * @param model ModelMap
     * @return 파일전송 차단 현황 목록
     */
    @GetMapping(value = "/analysis/file-blocking-list")
    public String fileBlockingList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model) {
        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        // 파일전송 차단 현황 목록 조회
        // model.addAttribute("fileAnalysisListPagination", analysisDetailService.selectFileAnalysisListWithPagination(searchVO));
        response.setHeader("X-Seek-Mode", seekMode);
        return "analysis/file-blocking-list";
    }

    /**
     * 서명파일 전송 현황
     *
     * @param model ModelMap
     * @return 서명파일 전송 현황 목록
     */
    @GetMapping(value = "/analysis/file-transfer-list")
    public String fileTransferList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model) {
        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        // 서명파일 전송 현황 목록 조회
        // model.addAttribute("fileAnalysisListPagination", analysisDetailService.selectFileAnalysisListWithPagination(searchVO));
        response.setHeader("X-Seek-Mode", seekMode);
        return "analysis/file-transfer-list";
    }

    /**
     * 시스템 파일 전송 현황
     *
     * @param model ModelMap
     * @return 시스템 파일 전송 현황 목록
     */
    @GetMapping(value = "/analysis/system-transfer-list")
    public String systemTransferList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model) {
        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        // 시스템 파일 전송 현황 목록 조회
        // model.addAttribute("fileAnalysisListPagination", analysisDetailService.selectFileAnalysisListWithPagination(searchVO));
        response.setHeader("X-Seek-Mode", seekMode);
        return "analysis/system-transfer-list";
    }

    /**
     * 이상 패턴 탐지 현황
     *
     * @param model ModelMap
     * @return 이상 패턴 탐지 현황 목록
     */
    @GetMapping(value = "/analysis/anomaly_detection-list")
    public String anomalyDetectionList(HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, ModelMap model) {
        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("CREATE_DT DESC");
        // 이상 패턴 탐지 현황 목록 조회
        // model.addAttribute("fileAnalysisListPagination", analysisDetailService.selectFileAnalysisListWithPagination(searchVO));
        response.setHeader("X-Seek-Mode", seekMode);
        return "analysis/anomaly_detection-list";
    }

}
