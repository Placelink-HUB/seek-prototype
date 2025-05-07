package biz.placelink.seek.analysis.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import biz.placelink.seek.analysis.service.WildpathAnalysisService;
import biz.placelink.seek.com.constants.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.util.S2ServletUtil;
import kr.s2.ext.util.S2Util;

@Controller
@RequestMapping("/console")
public class WildpathController {

    private static final Logger logger = LoggerFactory.getLogger(WildpathController.class);

    private final WildpathAnalysisService wildpathAnalysisService;

    public WildpathController(WildpathAnalysisService wildpathAnalysisService) {
        this.wildpathAnalysisService = wildpathAnalysisService;
    }

    /**
     * 역방향 프록시 전처리 비동기 처리
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return ResponseEntity<String>
     */
    @RequestMapping("/request/async/**")
    protected ResponseEntity<String> onBeforePreprocess(HttpServletRequest request, HttpServletResponse response) {
        try {
            // POST, PUT 같은 요청일 때만 body 읽기
            if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
                String payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
                System.out.println("[WILD] Received Payload:\n" + payload);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return ResponseEntity.ok("");
    }

    /**
     * 역방향 프록시 전처리 비동기 처리 - 파일 업로드
     *
     * @param file MultipartFile
     * @return ResponseEntity<String>
     * @throws IOException 예외 발생 시
     */
    @RequestMapping(path = "/preprocess/**", consumes = "multipart/form-data")
    protected ResponseEntity<String> preprocess(@RequestParam("file") MultipartFile file) throws IOException {
        file.transferTo(Paths.get("/Users/e01000/Downloads/requestHandle", file.getOriginalFilename()));
        return ResponseEntity.ok().body("");
    }

    private void replaceInStream(InputStream input, OutputStream output, byte[] target, byte[] replacement) throws IOException {
        int matchPos = 0; // 현재까지 매칭된 target 위치
        int b;

        while ((b = input.read()) != -1) {
            if (b == (target[matchPos] & 0xFF)) {
                matchPos++;
                if (matchPos == target.length) {
                    // 전체 패턴 매칭 성공: 치환 바이트 출력
                    output.write(replacement);
                    matchPos = 0;
                }
            } else {
                if (matchPos > 0) {
                    // 매칭 중간 실패: 지금까지 일치한 것 출력
                    output.write(target, 0, matchPos);
                    matchPos = 0;
                }
                output.write(b);
            }
        }

        // 스트림 끝나기 전에 남은 패턴 일부가 있을 경우 그대로 출력
        if (matchPos > 0) {
            output.write(target, 0, matchPos);
        }
    }

    /**
     * 역방향 프록시 전처리 동기 처리
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return ResponseEntity<String>
     * @throws IOException 예외 발생 시
     */
    @RequestMapping(path = "/preprocess/**")
    protected ResponseEntity<String> preprocess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        byte[] target = "Sample".getBytes(StandardCharsets.UTF_8);
        byte[] replacement = "샘플".getBytes(StandardCharsets.UTF_8);

        try (
                BufferedInputStream input = new BufferedInputStream(request.getInputStream());
                BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream())) {
            replaceInStream(input, output, target, replacement);
        }

        response.flushBuffer();
        return ResponseEntity.ok().build();
    }

    /**
     * 역방향 프록시 후처리 동기 처리
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param headers  요청 헤더
     * @return ResponseEntity<String>
     * @throws Exception 예외 발생 시
     */
    @PostMapping("/postprocess/**")
    protected ResponseEntity<String> postprocess(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers) throws Exception {
        String documentTypeFromContentType = WildpathAnalysisService.getDocumentTypeFromContentType(request.getContentType());
        String requestId = request.getHeader("X-Request-ID");
        if ("text".equals(documentTypeFromContentType) && !this.isStaticResource(request, "/postprocess/")) {
            String seekMode = request.getHeader("X-Seek-Mode");

            String payload = "";

            try {
                // POST, PUT 같은 요청일 때만 body 읽기
                if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
                    payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
                    System.out.println("[WILD] Received Payload:\n" + payload);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            // payload = wildpathAnalysisService.maskSensitiveInformation(requestId, Constants.CD_ANALYSIS_MODE_REVERSE_POST, payload, seekMode);
            payload = wildpathAnalysisService.maskSensitiveInformation(payload, seekMode);

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, request.getContentType()).body(payload);
        } else {
            // 비 텍스트 타입의 경우, 원본 요청을 그대로 반환
            try (InputStream inputStream = request.getInputStream();
                    OutputStream outputStream = response.getOutputStream()) {
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            return ResponseEntity.ok().build();
        }
    }

    /**
     * 역방향 프록시 후처리 비동기 처리
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException 예외 발생 시
     */
    @PostMapping(path = "/response/async/**")
    protected void onAfterPostprocess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.isStaticResource(request, "/response/async/")) {
            return;
        }

        String requestId = request.getHeader("X-Request-ID");
        String contentType = request.getContentType();
        String documentTypeFromContentType = WildpathAnalysisService.getDocumentTypeFromContentType(contentType);
        String countryCcd = request.getHeader("X-Country_Code");
        String url = request.getRequestURL().toString();
        String header = S2ServletUtil.getHeadersAsJsonString(request);
        String queryString = S2ServletUtil.parameterToQueryString(request, true);

        if (S2Util.isEmpty(countryCcd)) {
            // 아직 국가 코드를 보내주지 않아 임시로 한국으로 설정
            countryCcd = Constants.CD_COUNTRY_KR;
        }

        switch (documentTypeFromContentType) {
        case "text":
            String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
            wildpathAnalysisService.createProxyAnalysis(Constants.CD_ANALYSIS_MODE_REVERSE_ASYNC_POST, requestId, countryCcd, url, header, queryString, body, null, null, null);
            break;
        case "image":
        case "pdf":
        case "docx":
        case "doc":
        case "xlsx":
        case "xls":
        case "pptx":
        case "ppt":
        case "hwp":
            try (InputStream fileData = request.getInputStream()) {
                String fileName = S2ServletUtil.getFilenameFromHeader(request);
                wildpathAnalysisService.createProxyAnalysis(Constants.CD_ANALYSIS_MODE_REVERSE_ASYNC_POST, requestId, countryCcd, url, header, queryString, null, contentType, fileData, fileName);
            }
            break;
        }
    }

    /**
     * 정적 리소스인지 확인
     *
     * @param request HttpServletRequest
     * @param stdPath 정적 리소스 경로
     * @return
     */
    private boolean isStaticResource(HttpServletRequest request, String stdPath) {
        String realPath = request.getServletPath().split(stdPath)[1];
        if (!realPath.startsWith("/")) {
            realPath = "/" + realPath;
        }
        if (realPath.startsWith("/public/js") || realPath.startsWith("/public/css") || realPath.startsWith("/public/images"))

        {
            return true;
        }
        return false;
    }

}
