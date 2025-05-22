package biz.placelink.seek.analysis.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

    @Value("${analysis.excluded-paths}")
    public String analysisExcludedPaths;

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
                logger.debug("[WILD] Received Payload:\n" + payload);
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
        String fileName = S2ServletUtil.getFilenameFromHeader(request);
        String documentTypeFromContentType = WildpathAnalysisService.getDocumentTypeFromContentType(request.getContentType(), fileName);

        if ("text".equals(documentTypeFromContentType)) {
            String requestId = request.getHeader("X-Request-Id");
            String seekMode = request.getHeader("X-Seek-Mode");

            String payload = "";

            try {
                // POST, PUT 같은 요청일 때만 body 읽기
                if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
                    payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
                    logger.debug("[WILD] Received Payload:\n" + payload);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            if (!this.isExcludedPath(request, "/postprocess/", true)) {
                payload = wildpathAnalysisService.maskSensitiveInformation(requestId, Constants.CD_ANALYSIS_MODE_REVERSE_POST, payload, seekMode);
            }
            return ResponseEntity.ok()
                    .contentType(new MediaType(MediaType.valueOf(StringUtils.defaultIfEmpty(request.getHeader("X-Origin-Content-Type"), request.getContentType())), StandardCharsets.UTF_8))
                    .body(payload);
        } else {
            // 비 텍스트 타입의 경우, 원본 요청을 그대로 반환
            try {
                // BufferedInputStream inputStream = S2StreamUtil.getBufferedInputStream(request.getInputStream());
                // BufferedOutputStream outputStream = S2StreamUtil.getBufferedOutputStream(response.getOutputStream());
                // long length = inputStream.transferTo(outputStream);

                long length = streamWithNIO(request, response);
                response.setContentType(request.getContentType());
                response.setContentLengthLong(length);
                // response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                // response.setHeader("Transfer-Encoding", "chunked");
                response.flushBuffer();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

            logger.info("####### S201000 REQUEST END: " + request.getServletPath());
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
        if (this.isExcludedPath(request, "/response/async/", true)) {
            return;
        }

        String requestId = request.getHeader("X-Request-Id");
        String contentType = request.getContentType();
        String fileName = S2ServletUtil.getFilenameFromHeader(request);
        String documentTypeFromContentType = WildpathAnalysisService.getDocumentTypeFromContentType(contentType, fileName);
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
        case "zip":
            try (InputStream fileData = request.getInputStream()) {
                wildpathAnalysisService.createProxyAnalysis(Constants.CD_ANALYSIS_MODE_REVERSE_ASYNC_POST, requestId, countryCcd, url, header, queryString, null, contentType, fileData, fileName);
            }
            break;
        }
    }

    /**
     * 제외할 경로인지 확인한다.
     *
     * @param request                  HttpServletRequest
     * @param stdPath                  기준 경로 (기준 경로 뒤의 경로가 실제 경로)
     * @param isStaticResourceExcluded 정적 리소스 제외 여부
     * @return
     */
    private boolean isExcludedPath(HttpServletRequest request, String stdPath, boolean isStaticResourceExcluded) {
        boolean result = false;

        String realPath = request.getServletPath().split(stdPath)[1];
        if (!realPath.startsWith("/")) {
            realPath = "/" + realPath;
        }

        String[] analysisExcludedPathArray = S2Util.isNotEmpty(analysisExcludedPaths) ? analysisExcludedPaths.split(",") : null;
        if (analysisExcludedPathArray != null) {
            for (String analysisExcludedPath : analysisExcludedPathArray) {
                if (realPath.startsWith(analysisExcludedPath)) {
                    result = true;
                }

                if (result) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * NIO 채널을 사용하여 HttpServletRequest에서 HttpServletResponse로
     * 데이터를 고성능으로 스트리밍합니다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @throws IOException 입출력 예외 발생 시
     */
    public static long streamWithNIO(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long totalBytes = 0;
        // NIO 채널 생성
        ReadableByteChannel inputChannel = Channels.newChannel(request.getInputStream());
        WritableByteChannel outputChannel = Channels.newChannel(response.getOutputStream());

        // 다이렉트 버퍼 생성 (네이티브 I/O 작업에 최적화)
        ByteBuffer buffer = ByteBuffer.allocateDirect(64 * 1024); // 64KB 버퍼 (성능에 맞게 조정 가능)

        try {
            // Content-Type 전달 (필요한 경우)
            String contentType = request.getContentType();
            if (contentType != null && response.getContentType() == null) {
                response.setContentType(contentType);
            }

            // 데이터 전송 루프
            int bytesRead = 0;
            while ((bytesRead = inputChannel.read(buffer)) != -1) {
                totalBytes += bytesRead;
                // 버퍼를 읽기 모드로 전환
                buffer.flip();

                // 버퍼의 모든 데이터를 출력 채널에 쓰기
                while (buffer.hasRemaining()) {
                    outputChannel.write(buffer);
                }

                // 버퍼 비우고 쓰기 모드로 다시 전환
                buffer.clear();
            }
        } finally {
            // 채널 닫기 (스트림은 컨테이너가 관리하므로 닫지 않음)
            try {
                if (inputChannel != null) {
                    inputChannel.close();
                }
            } catch (IOException e) {
                // 로깅 처리
            }

            try {
                if (outputChannel != null) {
                    outputChannel.close();
                }
            } catch (IOException e) {
                // 로깅 처리
            }
        }

        return totalBytes;
    }

}
