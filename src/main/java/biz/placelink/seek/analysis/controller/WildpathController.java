package biz.placelink.seek.analysis.controller;

import biz.placelink.seek.analysis.service.SensitiveInformationService;
import biz.placelink.seek.analysis.vo.SchSensitiveInformationVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.util.S2EncryptionUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/console")
public class WildpathController {

    private final SensitiveInformationService sensitiveInformationService;

    public WildpathController(SensitiveInformationService sensitiveInformationService) {
        this.sensitiveInformationService = sensitiveInformationService;
    }

    @Value("${encryption.password}")
    public String encryptionPassword;

    @RequestMapping("/request/async/**")
    protected ResponseEntity<String> onBeforePreprocess(HttpServletRequest request, HttpServletResponse response) {

        try {
            // POST, PUT 같은 요청일 때만 body 읽기
            if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
                String payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
                System.out.println("[WILD] Received Payload:\n" + payload);
            }
        } catch (Exception e) {
            // e.printStackTrace();
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
        if ("text".equals(this.getDocumentTypeFromContentType(request.getContentType()))) {
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
                e.printStackTrace();
            }

            List<String> patterns = new ArrayList<>();

            if (Pattern.compile("(\\$WT\\{[^}]+\\})").matcher(payload).find()) {
                payload = payload.replaceAll("\\$WT\\{[^}]+\\}", "감사중인 문서 입니다.");
            } else if (!"raw".equals(seekMode)) {
                Pattern pattern = Pattern.compile("(\\$PL\\{[^}]+\\})");
                Matcher matcher = pattern.matcher(payload);

                while (matcher.find()) {
                    patterns.add(matcher.group(1));
                }
            }

            if (!patterns.isEmpty()) {
                SchSensitiveInformationVO searchVO = new SchSensitiveInformationVO();
                searchVO.setSchSensitiveInformationIdList(patterns);
                List<SensitiveInformationVO> sensitiveInformationList = sensitiveInformationService.selectSensitiveInformationList(searchVO);

                if (sensitiveInformationList != null) {
                    for (SensitiveInformationVO sensitiveInformation : sensitiveInformationList) {
                        payload = payload.replace(sensitiveInformation.getSensitiveInformationId(), "origin".equals(seekMode) ? S2EncryptionUtil.decrypt(sensitiveInformation.getTargetText(), encryptionPassword) : sensitiveInformation.getEscapeText());
                    }
                }
            }

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
                e.printStackTrace();
            }
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping(path = "/response/async/**")
    protected void onAfterPostprocess(HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getContentType();

        switch (contentType) {
            case "application/json":
            case "text/html":
                break;
        }
    }

    private String getDocumentTypeFromContentType(String contentType) {
        String documentType = "unknown";
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            if (lowerContentType.startsWith("text/") ||
                    (lowerContentType.startsWith("application/") && (
                            lowerContentType.contains("json") ||
                                    lowerContentType.contains("xml") ||
                                    lowerContentType.contains("xhtml") ||
                                    lowerContentType.contains("javascript") ||
                                    lowerContentType.contains("ecmascript") ||
                                    lowerContentType.contains("graphql") ||
                                    lowerContentType.contains("markdown") ||
                                    lowerContentType.contains("yaml") ||
                                    lowerContentType.contains("yml") ||
                                    lowerContentType.contains("csv") ||
                                    lowerContentType.contains("sql") ||
                                    lowerContentType.contains("toml")
                    )) ||
                    (lowerContentType.startsWith("image/") && (
                            lowerContentType.contains("svg")
                    ))) {
                documentType = "text";
            } else if (lowerContentType.startsWith("image/")) {
                documentType = "image";
            } else {
                documentType = switch (contentType) {
                    case "application/pdf" -> "pdf";
                    case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx";
                    case "application/msword" -> "doc";
                    case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx";
                    case "application/vnd.ms-excel" -> "xls";
                    case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "pptx";
                    case "application/vnd.ms-powerpoint" -> "ppt";
                    case "application/x-hwp",
                         "application/vnd.hancom.hwp" -> "hwp";
                    default -> "unknown";
                };
            }
        }
        return documentType;
    }

}
