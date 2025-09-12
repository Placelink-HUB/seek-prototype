package biz.placelink.seek.analysis.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import biz.placelink.seek.analysis.vo.AnalysisDetailVO;
import biz.placelink.seek.analysis.vo.AnalysisResultVO;
import biz.placelink.seek.analysis.vo.AnalysisVO;
import biz.placelink.seek.analysis.vo.SchSensitiveInformationVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.serviceworker.service.ServiceWorkerService;
import biz.placelink.seek.system.file.service.FileService;
import biz.placelink.seek.system.file.vo.FileDetailVO;
import kr.s2.ext.util.S2EncryptionUtil;
import kr.s2.ext.util.S2FileUtil;
import kr.s2.ext.util.S2Util;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 17.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class WildpathAnalysisService {

    private final ServiceWorkerService serviceWorkerService;
    private final AnalysisService analysisService;
    private final AnalysisDetailService analysisDetailService;
    private final SensitiveInformationService sensitiveInformationService;
    private final MaskHistService maskHistService;
    private final FileOutboundHistService fileOutboundHistService;
    private final FileService fileService;

    public WildpathAnalysisService(ServiceWorkerService serviceWorkerService, AnalysisService analysisService, AnalysisDetailService analysisDetailService, SensitiveInformationService sensitiveInformationService, MaskHistService maskHistService, FileOutboundHistService fileOutboundHistService, FileService fileService) {
        this.serviceWorkerService = serviceWorkerService;
        this.analysisService = analysisService;
        this.analysisDetailService = analysisDetailService;
        this.sensitiveInformationService = sensitiveInformationService;
        this.maskHistService = maskHistService;
        this.fileOutboundHistService = fileOutboundHistService;
        this.fileService = fileService;
    }

    @Value("${encryption.password}")
    public String encryptionPassword;

    @Transactional(readOnly = false)
    public int createProxyAnalysis(String analysisModeCcd, String requestId, String countryCcd, String url, String header, String queryString, String body, String contentType, InputStream fileData, String fileName) {
        List<FileDetailVO> fileList = new ArrayList<>();

        FileDetailVO fileInfo = new FileDetailVO();
        fileInfo.setFileName(fileName);
        fileInfo.setContentType(contentType);
        fileInfo.setFileData(fileData);

        fileList.add(fileInfo);
        return this.createProxyAnalysis(analysisModeCcd, requestId, countryCcd, url, header, queryString, body, fileList);
    }

    @Transactional(readOnly = false)
    public int createProxyAnalysis(String analysisModeCcd, String requestId, String countryCcd, String url, String header, String queryString, String body, List<FileDetailVO> fileList) {
        int result = 0;
        String analysisId = UUID.randomUUID().toString();

        // 분석 등록 (분석 모델은 AnalyzerService 에서 분석 요청시 결정)
        AnalysisVO analysis = new AnalysisVO();
        analysis.setAnalysisId(analysisId);
        analysis.setAnalysisModeCcd(analysisModeCcd);
        analysis.setAnalysisStatusCcd(Constants.CD_ANALYSIS_STATUS_WAIT);

        result = analysisService.insertAnalysis(analysis);

        if (result > 0) {
            AnalysisDetailVO analysisDetail = new AnalysisDetailVO();
            analysisDetail.setAnalysisId(analysisId);
            analysisDetail.setRequestId(requestId);
            analysisDetail.setCountryCcd(countryCcd);
            analysisDetail.setUrl(url);
            analysisDetail.setHeader(header);
            analysisDetail.setQueryString(queryString);
            analysisDetail.setBody(body);

            if (S2Util.isNotEmpty(fileList)) {
                List<FileDetailVO> createFileDetailList = new ArrayList<>();
                String fileId = UUID.randomUUID().toString();

                for (FileDetailVO fileInfo : fileList) {
                    String fileFullName = fileInfo.getFileFullName();
                    String contentType = fileInfo.getContentType();
                    InputStream fileData = fileInfo.getFileData();

                    if (fileData != null) {
                        FileDetailVO fileDetailVO = fileService.writeFile(fileData, analysisDetail.getAnalysisModeCcd(), Constants.CD_FILE_SE_1010);
                        if (fileDetailVO != null) {
                            String fileDetailId = UUID.randomUUID().toString();

                            // 파일 정보
                            fileDetailVO.setFileId(fileId);
                            fileDetailVO.setFileSeCcd(Constants.CD_FILE_SE_1010);

                            // 파일 상세 정보
                            fileDetailVO.setFileDetailId(fileDetailId);
                            fileDetailVO.setFileName(S2FileUtil.getBaseName(fileFullName));
                            fileDetailVO.setFileExt(S2FileUtil.getExtension(fileFullName));
                            fileDetailVO.setContentType(contentType);

                            createFileDetailList.add(fileDetailVO);
                        }
                    }
                }

                if (fileService.insertFileWithDetailList(createFileDetailList, Constants.SYSTEM_UID) > 0) {
                    analysisDetail.setFileId(fileId);
                }
            }

            analysisDetailService.insertProxyAnalysis(analysisDetail);

            Map<String, Object> pushMap = new HashMap<>();
            pushMap.put("pushTypeCcd", Constants.CD_PUSH_TYPE_ANALYSIS_REQUEST);
            pushMap.put("checkId", analysisId);
            pushMap.put("createDtStr", analysis.getCreateDtStr());
            pushMap.put("count", result);

            serviceWorkerService.sendNotificationAll(pushMap);
        }

        return result;
    }

    @Transactional(readOnly = false)
    public int createFileOutboundHist(String outboundStatusCcd, String analysisId, String orgCode, String channel, String reason, String eventTime, String macAddr, String destHost, String fileName, String fileSize, String fileCount, String allParamsStr) {
        int result = fileOutboundHistService.insertFileOutboundHist(outboundStatusCcd, macAddr, analysisId, fileName);
        if (result > 0) {
            // 파일 외부 전송 현황
            Map<String, Object> pushMap = new HashMap<>();
            pushMap.put("pushTypeCcd", Constants.CD_PUSH_TYPE_FILE_OUTBOUND);
            pushMap.put("outboundStatusCcd", outboundStatusCcd);

            pushMap.put("sigId", analysisId);
            pushMap.put("orgCode", orgCode);
            pushMap.put("channel", channel);
            pushMap.put("reason", reason);
            pushMap.put("eventTime", eventTime);
            pushMap.put("macAddr", macAddr);
            pushMap.put("destHost", destHost);
            pushMap.put("fileName", fileName);
            pushMap.put("fileSize", fileSize);
            pushMap.put("fileCount", fileCount);

            if (Constants.CD_OUTBOUND_STATUS_SENT.equals(outboundStatusCcd)) {
                AnalysisResultVO fileInfo = analysisDetailService.selectFileAnalysis(analysisId);
                if (fileInfo != null) {
                    pushMap.put("fileCount", fileInfo.getFileCount());
                    pushMap.put("totalFileSize", fileInfo.getTotalFileSize());
                }
            }

            if (StringUtils.isNotEmpty(allParamsStr)) {
                pushMap.put("allParamsStr", allParamsStr);
            }

            serviceWorkerService.sendNotificationAll(pushMap);
        }
        return result;
    }

    /**
     * SEEK 에이전트 하트비트
     *
     * @param orgCode    조직코드
     * @param eventTime  이벤트 시간
     * @param macAddr    MAC 주소
     * @param host       호스트 이름
     * @param components 컴포넌트(에이전트) 정보
     */
    public void pushAgentHeartbeat(String orgCode, String eventTime, String macAddr, String host, String components) {
        Map<String, Object> pushMap = new HashMap<>();
        pushMap.put("pushTypeCcd", Constants.CD_PUSH_TYPE_AGENT_HEARTBEAT);
        pushMap.put("orgCode", orgCode);
        pushMap.put("eventTime", eventTime);
        pushMap.put("macAddr", macAddr);
        pushMap.put("host", host);
        pushMap.put("components", components);

        serviceWorkerService.sendNotificationAll(pushMap);
    }

    /**
     * 주어진 문자열 데이터에서 민감 정보를 마스킹한다.
     *
     * @param requestId       요청 ID
     * @param analysisModeCcd 분석 모드 공통코드
     * @param textData        문자열 데이터
     * @param seekMode        마스킹 모드 (mask: 마스킹된 데이터, origin: 원본 데이터, raw: 저장된 실제 데이터)
     * @return 마스킹한 문자열
     */
    @Transactional(readOnly = false)
    public String maskSensitiveInformation(String requestId, String analysisModeCcd, String textData, String seekMode) {
        String maskModeCcd = S2Util.isEmpty(seekMode) ? Constants.CD_MASK_MODE_MASK : seekMode;
        String resultText = textData;
        List<String> patterns = new ArrayList<>();
        int maskCount = 0;

        if (!"raw".equals(seekMode)) {
            if (Pattern.compile("(\\$WT\\{[^}]+\\})").matcher(textData).find()) {
                resultText = textData.replaceAll("\\$WT\\{[^}]+\\}", "감사중인 문서 입니다.");
            } else {
                Pattern pattern = Pattern.compile("(\\$PL\\{[^}]+\\})");
                Matcher matcher = pattern.matcher(textData);

                while (matcher.find()) {
                    patterns.add(matcher.group(1));
                    maskCount++;
                }
            }
        }

        if (!patterns.isEmpty()) {
            SchSensitiveInformationVO searchVO = new SchSensitiveInformationVO();
            searchVO.setSchSensitiveInformationIdList(patterns);
            List<SensitiveInformationVO> sensitiveInformationList = sensitiveInformationService.selectSensitiveInformationList(searchVO);

            if (sensitiveInformationList != null) {
                for (SensitiveInformationVO sensitiveInformation : sensitiveInformationList) {
                    resultText = resultText.replace(sensitiveInformation.getSensitiveInformationId(), Constants.CD_MASK_MODE_UNMASK.equals(maskModeCcd)
                            ? S2EncryptionUtil.decrypt(sensitiveInformation.getTargetText(), encryptionPassword)
                            : sensitiveInformation.getEscapeText());
                }
            }
        }

        if (!Constants.CD_MASK_MODE_RAW.equals(maskModeCcd) && maskCount > 0) {
            // 마스킹 이력 등록
            maskHistService.insertMaskHist(requestId, analysisModeCcd, maskModeCcd, maskCount);

            Map<String, Object> pushMap = new HashMap<>();
            pushMap.put("pushTypeCcd", Constants.CD_PUSH_TYPE_MASKING);
            pushMap.put("checkId", requestId);
            pushMap.put("maskModeCcd", maskModeCcd);
            pushMap.put("count", maskCount);

            serviceWorkerService.sendNotificationAll(pushMap);
        }

        return resultText;
    }

    /**
     * 주어진 Content-Type 문자열로부터 문서 타입을 추론합니다.
     * 텍스트 계열, 이미지 (SVG), PDF, MS Office 문서 (docx, doc, xlsx, xls, pptx, ppt),
     * 한글 (hwp) 등의 타입을 식별하며, 그 외의 경우에는 "unknown"을 반환합니다.
     *
     * @param contentType HTTP 요청 또는 응답의 Content-Type 헤더 값. null이 허용됩니다.
     * @return 추론된 문서 타입 (예: "text", "image", "pdf", "docx", "unknown" 등).
     *         contentType 이 null 인 경우 "unknown"을 반환합니다.
     */
    public static String getDocumentTypeFromContentType(String contentType, String fileName) {
        String documentType = "unknown";
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            if (lowerContentType.startsWith("text/") ||
                    (lowerContentType.startsWith("application/") &&
                            (lowerContentType.contains("json") ||
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
                                    lowerContentType.contains("toml"))) ||
                    (lowerContentType.startsWith("image/") &&
                            (lowerContentType.contains("svg")))) {
                documentType = "text";
            } else if (lowerContentType.startsWith("image/")) {
                documentType = "image";
            } else {
                documentType = switch (lowerContentType) {
                    case "application/pdf" -> "pdf";
                    case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx";
                    case "application/msword" -> "doc";
                    case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx";
                    case "application/vnd.ms-excel" -> "xls";
                    case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "pptx";
                    case "application/vnd.ms-powerpoint" -> "ppt";
                    case "application/x-hwp", "application/vnd.hancom.hwp" -> "hwp";
                    default -> "unknown";
                };
            }

            if ("unknown".equals(documentType) && S2Util.isNotEmpty(fileName) && "application/octet-stream".equals(lowerContentType)) {
                String fileExtension = S2FileUtil.getExtension(fileName);
                if (S2Util.isNotEmpty(fileExtension)) {
                    documentType = fileExtension;
                }
            }
        }
        return documentType;
    }

}
