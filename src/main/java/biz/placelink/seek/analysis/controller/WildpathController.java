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
 */
package biz.placelink.seek.analysis.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

import biz.placelink.seek.analysis.service.WildpathAnalysisService;
import biz.placelink.seek.analysis.vo.AgentVO;
import biz.placelink.seek.analysis.vo.FileOutboundHistVO;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.serviceworker.service.ServiceWorkerService;
import biz.placelink.seek.com.util.GlobalSharedStore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.util.S2JsonUtil;
import kr.s2.ext.util.S2ServletUtil;
import kr.s2.ext.util.S2Util;

@Controller
@RequestMapping("/console")
public class WildpathController {

    private static final Logger logger = LoggerFactory.getLogger(WildpathController.class);

    private final ServiceWorkerService serviceWorkerService;
    private final WildpathAnalysisService wildpathAnalysisService;
    private final GlobalSharedStore store;

    public WildpathController(ServiceWorkerService serviceWorkerService, WildpathAnalysisService wildpathAnalysisService, GlobalSharedStore store) {
        this.serviceWorkerService = serviceWorkerService;
        this.wildpathAnalysisService = wildpathAnalysisService;
        this.store = store;
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
                payload = wildpathAnalysisService.maskSensitiveInformation(requestId, Constants.CD_ANALYSIS_MODE_PROXY_REVERSE_POST, payload, seekMode, S2ServletUtil.getClientIp(request));
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
        String header = S2JsonUtil.toJsonString(S2ServletUtil.convertHeadersToMap(request));
        String queryString = S2ServletUtil.parameterToQueryString(request);
        String clientIp = S2ServletUtil.getClientIp(request);

        if (S2Util.isEmpty(countryCcd)) {
            // 아직 국가 코드를 보내주지 않아 임시로 한국으로 설정
            countryCcd = Constants.CD_COUNTRY_KR;
        }

        switch (documentTypeFromContentType) {
            case "text":
                String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
                wildpathAnalysisService.createProxyAnalysis(Constants.CD_ANALYSIS_MODE_PROXY_REVERSE_ASYNC_POST, requestId, countryCcd, url, header, queryString, clientIp, body, null, null, null);
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
                    wildpathAnalysisService.createProxyAnalysis(Constants.CD_ANALYSIS_MODE_PROXY_REVERSE_ASYNC_POST, requestId, countryCcd, url, header, queryString, clientIp, null, contentType, fileData, fileName);
                }
                break;
        }
    }

    /**
     * 순방향 프록시 후처리 비동기 처리(테스트용)
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException 예외 발생 시
     */
    @PostMapping(path = "/response/async2/**", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void onAfterPostprocess2(@RequestParam(value = "attachments", required = false) List<MultipartFile> attachments, @RequestParam(value = "url", required = false) String url, @RequestParam(value = "decrypted_body", required = false) String decryptedBody, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.isExcludedPath(request, "/response/async2/", true)) {
            return;
        }

        logger.info("📩 URL: " + url);
        logger.info("📩 Body: " + decryptedBody);
        logger.info("📎 첨부파일 개수: " + (attachments != null ? attachments.size() : "null"));

        // 저장 경로 설정 (파일을 계속 보내는 이유를 확인하자)
        /*
         * String uploadDir = "C:/test";
         * Path uploadPath = Paths.get(uploadDir);
         * if (!Files.exists(uploadPath)) {
         * Files.createDirectories(uploadPath);
         * }
         *
         * // attachments 저장
         * if (attachments != null) {
         * for (MultipartFile file : attachments) {
         * String fileName = file.getOriginalFilename();
         * if (fileName == null) {
         * fileName = "";
         * }
         * Long fileSize = file.getSize();
         *
         * Map<String, Object> pushMap = new HashMap<>();
         * pushMap.put("pushTypeCcd", Constants.CD_PUSH_TYPE_NOTIFICATION);
         * pushMap.put("message", "[" + fileName + "]" + (file.getName() != null ? "[" + file.getName() + "]" : "") + " 수신 사이즈: " + fileSize);
         * serviceWorkerService.sendNotificationAll(pushMap);
         *
         * if (fileName != null && !fileName.isEmpty()) {
         * try {
         * Path filePath = uploadPath.resolve(fileName);
         * Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
         * logger.info("📁 저장 완료: " + fileName + ", size: " + fileSize + " bytes");
         * } catch (Exception e) {
         * logger.error("파일 저장 오류: ", e);
         * }
         * }
         * }
         * }
         */
    }

    /**
     * 순방향 프록시 후처리 비동기 처리
     *
     * @param request HttpServletRequest
     */
    @PostMapping(path = "/forward/response/async/**")
    public void forwardOnAfterPostprocess(@RequestParam MultiValueMap<String, String> params, HttpServletRequest request) {
        if (this.isExcludedPath(request, "/forward/response/async/", true)) {
            return;
        }

        { // 파일 외부전송 이력
            String analysisId = params.getFirst("sig_user_id");
            if (S2Util.isEmpty(analysisId)) {
                analysisId = params.getFirst("sig_id");
            }

            /** 로그인한 사용자 ID (발신자) */
            String userId = Optional.ofNullable(params.getFirst("user_id")).map(s -> s.trim()).orElse("");
            /** 전송 요청한 클라이언트 IP */
            String clientIp = params.getFirst("client_ip");

            /*
             * 기관 분류 코드
             *
             * @value 조직/부서/사이트 식별용 내부 코드 뙤는 조직 없으면 "000000"
             */
            String orgCode = params.getFirst("org_code");

            /*
             * 이벤트 채널(이벤트가 발생한 경로, OUTBOUND_CHANNEL_CCD)
             *
             * @value https | smtp | messenger | usb | print
             */
            String channelCd = params.getFirst("channel");

            /*
             * 허용/차단(최종 정책 결과, OUTBOUND_STATUS_CCD => SENT: 전송, BLOCKED: 차단)
             *
             * @value ALLOW: 전송/행위 허용, BLOCK: 정책에 의해 차단
             */
            String action = params.getFirst("action");

            /*
             * 세부 사유 코드(왜 ALLOW/BLOCK 되었는지 상세 코드, OUTBOUND_REASON_CCD)
             *
             * @value
             * OK_browser3: 브라우저 1차 검사 통과
             * BLOCK_browser1: 동일 프로세스 2회 이상 읽기 → 차단
             * BLOCK_browser2: 검증 결과 파일 불허
             * BLOCK_browser_pdf: 브라우저 PDF 전송 차단
             * BLOCK_messenger_pdf: 메신저 PDF 차단
             * BLOCK_browser_image / BLOCK_kakao_image: 이미지 첨부 차단
             * OK_usb / BLOCK_usb / BLOCK_usb_nonzip: USB 정책 결과
             * BLOCK_usb_pdf_create / BLOCK_usb_pdf_rename: USB에 PDF 생성/이동 차단
             * allowed_sig: 서명 유효 → 허용
             * sig_missing: 동일이름 .sig 없음
             * sig_invalid: 서명 불일치/검증실패
             * sig_error: 검증 에러
             * path_unknown: 문서 경로 미확인
             * cancel_fail: 인쇄 취소 실패
             */
            String reasonCd = params.getFirst("reason");

            /*
             * 타임스탬프(이벤트 발생 시각)
             *
             * @value 2025-08-29 09:12:33 (YYYY-MM-DD hh:mm:ss)
             */
            String eventTimeStr = params.getFirst("event_time");

            /*
             * MAC 주소(주 네트워크 어댑터 MAC, 식별용)
             *
             * @value AA:BB:CC:DD:EE:FF 포맷 권장
             */
            String macAddr = params.getFirst("mac_addr");

            /*
             * 목적지 호스트/플랫폼(향하는 서비스 호스트/플랫폼명)
             *
             * @value
             * mail.google.com, outlook.office.com, smtp.office365.com ...
             * USB/프린트처럼 호스트 개념 없을 때: usb -> "usb", print -> "print"
             * 없으면 빈 문자열("")
             */
            String destHost = params.getFirst("dest_host");

            /*
             * 파일/문서명
             * 대상 파일명(가능하면 원본 파일명). 파일 여러개 일경우 "," 구분해서 여러개 넣기
             * 경로 전체가 아닌 파일명을 기본으로. 경로는 별도 필드가 없다면 로그에 포함하지 않는 게 안전
             *
             * @value report.pdf(바이트수), design_assets.zip(바이트수), handoff_v2.7z(바이트수)
             */
            String fileName = params.getFirst("file_name");

            /*
             * 파일 바이트 수
             * 전송/대상 파일을 모두 합한 전체 크기 (바이트)
             */
            String fileSize = params.getFirst("file_size");

            /*
             * 파일 개수
             * 전송/대상 파일을 모두 더한 파일 개수 (프린트는 항상 1)
             */
            String fileCount = params.getFirst("file_count");

            String allParamsStr = null;
            if ("all".equals(store.get("pushConsole"))) {
                allParamsStr = S2JsonUtil.toJsonString(params);
            }

            FileOutboundHistVO paramVO = new FileOutboundHistVO();
            paramVO.setOutboundStatusCcd("ALLOW".equals(action) ? Constants.CD_OUTBOUND_STATUS_SENT : Constants.CD_OUTBOUND_STATUS_BLOCKED);
            paramVO.setOutboundChannelCcd(channelCd);
            paramVO.setOutboundReasonCcd(reasonCd);
            paramVO.setUserId(userId);
            paramVO.setClientIp(clientIp);
            paramVO.setAnalysisId(analysisId);
            paramVO.setFileNm(fileName);
            paramVO.setOrgCd(orgCode);
            paramVO.setMacAddr(macAddr);
            paramVO.setDestHost(destHost);
            paramVO.setTotalFileCount(Integer.parseInt(fileCount));
            paramVO.setTotalFileSize(Long.parseLong(fileSize));
            paramVO.setEventDtStr(eventTimeStr);

            wildpathAnalysisService.createFileOutboundHist(paramVO, allParamsStr);
        }
    }

    /**
     * 순방향 프록시 후처리 비동기 처리
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException 예외 발생 시
     */
    @PostMapping(path = "/response/async3/**")
    protected void onAfterPostprocess3(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.isExcludedPath(request, "/response/async2/", true)) {
            return;
        }

        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JsonNode json = S2JsonUtil.parseJson(sb.toString());

        String url = json.path("url").asText();
        String decryptedBody = json.path("decrypted_body").asText();
        System.out.println("📩 URL: " + url);
        System.out.println("📄 본문: " + decryptedBody);

        // attachments 처리
        if (json.has("attachments") && json.get("attachments").isArray()) {
            JsonNode attachments = json.get("attachments");
            if (attachments.size() == 0) {
                System.out.println("⚠️ attachments는 비어 있음");
            } else {
                for (JsonNode file : attachments) {
                    String filename = file.path("filename").asText();
                    String base64Content = file.path("content_base64").asText();

                    try {
                        byte[] fileBytes = Base64.getDecoder().decode(base64Content);
                        FileOutputStream fos = new FileOutputStream("C:/test/" + filename);
                        fos.write(fileBytes);
                        fos.close();
                        System.out.println("✅ 저장된 파일: " + filename);
                    } catch (Exception e) {
                        System.err.println("❌ 저장 실패: " + filename + " → " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("📦 attachments 필드가 없음 또는 배열이 아님");
        }

    }

    /**
     * SEEK 에이전트 하트비트 수신
     *
     * @param params MultiValueMap<String, String>
     * @throws IOException 예외 발생 시
     */
    @PostMapping(path = "/forward/response/heartbeat")
    protected void heartbeat(@RequestParam MultiValueMap<String, String> params) throws IOException {
        /*
         * {
         * "client_ip": "<string>",
         * "user_id": "<string>",
         * "host": "<hostname>",
         * "mac_addr": "<AA:BB:CC:DD:EE:FF>",
         * "org_code": "<string>",
         * "event_time": "<YYYY-MM-DD HH:MM:SS>",
         * "components": {
         * "minispy.sys": true|false,
         * "mspyUser.exe": true|false,
         * "WfpBlocker.exe": true|false,
         * "ClickDomainAgent.exe": true|false
         * }
         */
        String clientIp = params.getFirst("client_ip");
        String userId = params.getFirst("user_id");
        String host = params.getFirst("host");
        String macAddr = params.getFirst("mac_addr");
        String orgCode = params.getFirst("org_code");
        String eventTime = params.getFirst("event_time");
        String components = params.getFirst("components");

        AgentVO agentVO = new AgentVO(clientIp, userId, host, macAddr, orgCode, eventTime, components);

        wildpathAnalysisService.receiveAgentHeartbeat(agentVO);
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
