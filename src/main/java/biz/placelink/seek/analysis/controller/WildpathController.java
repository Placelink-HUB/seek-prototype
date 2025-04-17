package biz.placelink.seek.analysis.controller;

import biz.placelink.seek.analysis.service.WildpathAnalysisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.util.S2ServletUtil;
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

@Controller
@RequestMapping("/console")
public class WildpathController {

    private static final Logger logger = LoggerFactory.getLogger(WildpathController.class);

    private final WildpathAnalysisService wildpathAnalysisService;

    public WildpathController(WildpathAnalysisService wildpathAnalysisService) {
        this.wildpathAnalysisService = wildpathAnalysisService;
    }

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

    @RequestMapping(path = "/preprocess/**", consumes = "multipart/form-data")
    protected ResponseEntity<String> preprocess(@RequestParam("file") MultipartFile file) throws IOException {
        file.transferTo(Paths.get("/Users/e01000/Downloads/requestHandle", file.getOriginalFilename()));
        return ResponseEntity.ok().body("");
    }

    private void replaceInStream(InputStream input, OutputStream output, byte[] target, byte[] replacement)
            throws IOException {
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

    @RequestMapping(path = "/preprocess/**")
    protected ResponseEntity<String> preprocess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        byte[] target = "멋쟁이 이승수".getBytes(StandardCharsets.UTF_8);
        byte[] replacement = "이승수".getBytes(StandardCharsets.UTF_8);

        try (
                BufferedInputStream input = new BufferedInputStream(request.getInputStream());
                BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream())) {
            replaceInStream(input, output, target, replacement);
        }

        response.flushBuffer();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/postprocess/**")
    protected ResponseEntity<String> postprocess(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers) throws Exception {
        if ("text".equals(WildpathAnalysisService.getDocumentTypeFromContentType(request.getContentType()))) {
            String seekMode = request.getHeader("X-Seek-Mode");
            if (seekMode == null || seekMode.isEmpty()) {
                seekMode = request.getParameter("seek_mode");
            }

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

            payload = wildpathAnalysisService.maskSensitiveInformation(payload, seekMode);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, request.getContentType())
                    .body(payload);
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

    @PostMapping(path = "/response/async/**")
    protected void onAfterPostprocess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contentType = request.getContentType();
        String documentTypeFromContentType = WildpathAnalysisService.getDocumentTypeFromContentType(contentType);
        String requestId = request.getHeader("X-Request-ID");
        String requestType = "";
        String url = request.getRequestURL().toString();
        String header = S2ServletUtil.getHeadersAsJsonString(request);
        String queryString = S2ServletUtil.parameterToQueryString(request, true);
        String body = "";

        switch (documentTypeFromContentType) {
            case "text":
                requestType = "TEXT";
                body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
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
                break;
        }
    }

}
