package biz.placelink.seek.analysis.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import biz.placelink.seek.analysis.vo.AgentVO;
import biz.placelink.seek.analysis.vo.AnalysisDetailVO;
import biz.placelink.seek.analysis.vo.AnalysisVO;
import biz.placelink.seek.analysis.vo.FileOutboundHistVO;
import biz.placelink.seek.analysis.vo.SchSensitiveInformationVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationUnmaskHistVO;
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
    private final AgentService agentService;
    private final SensitiveInformationUnmaskHistService sensitiveInformationUnmaskHistService;

    public WildpathAnalysisService(ServiceWorkerService serviceWorkerService, AnalysisService analysisService, AnalysisDetailService analysisDetailService, SensitiveInformationService sensitiveInformationService, MaskHistService maskHistService, FileOutboundHistService fileOutboundHistService, FileService fileService, AgentService agentService, SensitiveInformationUnmaskHistService sensitiveInformationUnmaskHistService) {
        this.serviceWorkerService = serviceWorkerService;
        this.analysisService = analysisService;
        this.analysisDetailService = analysisDetailService;
        this.sensitiveInformationService = sensitiveInformationService;
        this.maskHistService = maskHistService;
        this.fileOutboundHistService = fileOutboundHistService;
        this.fileService = fileService;
        this.agentService = agentService;
        this.sensitiveInformationUnmaskHistService = sensitiveInformationUnmaskHistService;
    }

    @Value("${encryption.password}")
    public String encryptionPassword;

    @Transactional(readOnly = false)
    public int createProxyAnalysis(String analysisModeCcd, String requestId, String countryCcd, String url, String header, String queryString, String clientIp, String body, String contentType, InputStream fileData, String fileName) {
        List<FileDetailVO> fileList = new ArrayList<>();

        FileDetailVO fileInfo = new FileDetailVO();
        fileInfo.setFileName(fileName);
        fileInfo.setContentType(contentType);
        fileInfo.setFileData(fileData);

        fileList.add(fileInfo);
        return this.createProxyAnalysis(analysisModeCcd, requestId, countryCcd, url, header, queryString, clientIp, body, fileList);
    }

    @Transactional(readOnly = false)
    public int createProxyAnalysis(String analysisModeCcd, String requestId, String countryCcd, String url, String header, String queryString, String clientIp, String body, List<FileDetailVO> fileList) {
        int result = 0;
        String analysisId = UUID.randomUUID().toString();

        // 분석 등록 (분석 모델은 AnalyzerService 에서 분석 요청시 결정)
        AnalysisVO analysis = new AnalysisVO();
        analysis.setAnalysisId(analysisId);
        analysis.setAnalysisModeCcd(analysisModeCcd);
        analysis.setAnalysisStatusCcd(Constants.CD_ANALYSIS_STATUS_WAIT);
        analysis.setRequestId(requestId);
        analysis.setClientIp(clientIp);

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

    /**
     * 파일 외부전송 이력을 등록한다.
     *
     * @param paramVO      파일 외부전송 정보
     * @param allParamsStr 모든 매개변수 정보(원본)
     * @return
     */
    @Transactional(readOnly = false)
    public int createFileOutboundHist(FileOutboundHistVO paramVO, String allParamsStr) {
        int result = 0;

        String fileExtensionStatus = Constants.CD_FILE_EXTENSION_STATUS_NONE_NORMAL;
        String fileName = paramVO.getFileNm();

        if (fileName != null) {
            String[] fileNmArr = fileName.split(",");
            int fileNmCount = 0;
            int normalExtensionsCount = 0;

            for (String fileNm : fileNmArr) {
                if (S2Util.isNotEmpty(fileNm)) {
                    fileNmCount++;
                    if (NORMAL_EXTENSIONS.contains(S2FileUtil.getExtension(fileNm, true))) {
                        normalExtensionsCount++;
                    }
                }
            }

            if (fileNmCount > 0 && normalExtensionsCount > 0) {
                fileExtensionStatus = fileNmCount == normalExtensionsCount ? Constants.CD_FILE_EXTENSION_STATUS_ALL_NORMAL : Constants.CD_FILE_EXTENSION_STATUS_PARTIALLY_NORMAL;
            }
        }

        paramVO.setFileExtensionStatusCcd(fileExtensionStatus);

        result = fileOutboundHistService.insertFileOutboundHist(paramVO);
        if (result > 0) {
            // 파일 외부 전송 현황
            Map<String, Object> pushMap = new HashMap<>();
            pushMap.put("pushTypeCcd", Constants.CD_PUSH_TYPE_FILE_OUTBOUND);
            pushMap.put("outboundStatusCcd", paramVO.getOutboundStatusCcd());

            pushMap.put("analysisId", paramVO.getAnalysisId());
            pushMap.put("userId", paramVO.getUserId());
            pushMap.put("clientIp", paramVO.getClientIp());
            pushMap.put("orgCode", paramVO.getOrgCd());
            pushMap.put("outboundChannelCcd", paramVO.getOutboundChannelCcd());
            pushMap.put("outboundReasonCcd", paramVO.getOutboundReasonCcd());
            pushMap.put("eventDtStr", paramVO.getEventDtStr());
            pushMap.put("macAddr", paramVO.getMacAddr());
            pushMap.put("destHost", paramVO.getDestHost());
            pushMap.put("fileNm", paramVO.getFileNm());
            pushMap.put("totalFileSize", paramVO.getTotalFileSize());
            pushMap.put("totalFileCount", paramVO.getTotalFileCount());

            if (S2Util.isNotEmpty(allParamsStr)) {
                pushMap.put("allParamsStr", allParamsStr);
            }

            serviceWorkerService.sendNotificationAll(pushMap);
        }
        return result;
    }

    private static final Set<String> NORMAL_EXTENSIONS = new HashSet<>(Arrays.asList(
            "txt", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "pdf", "hwp", "jpg", "jpeg", "png", "gif", "bmp",
            "mp3", "wav", "mp4", "avi", "mov", "zip", "tar", "gz",
            "html", "css", "js", "java", "py", "sql"));

    /**
     * SEEK 에이전트 하트비트 수신
     *
     * @param agentVO SEEK 에이전트 정보
     */
    @Transactional
    public void receiveAgentHeartbeat(AgentVO agentVO) {
        Map<String, Object> pushMap = new HashMap<>();
        pushMap.put("pushTypeCcd", Constants.CD_PUSH_TYPE_AGENT_HEARTBEAT);
        pushMap.put("clientIp", agentVO.getClientIp());
        pushMap.put("userId", agentVO.getUserId());
        pushMap.put("host", agentVO.getHost());
        pushMap.put("macAddr", agentVO.getMacAddr());
        pushMap.put("orgCode", agentVO.getOrgCode());
        pushMap.put("eventTime", agentVO.getEventTime());
        pushMap.put("components", agentVO.getComponents());
        pushMap.put("minispySysYn", agentVO.getMinispySysYn());
        pushMap.put("mspyUserExeYn", agentVO.getMspyUserExeYn());
        pushMap.put("wfpBlockerExeYn", agentVO.getWfpBlockerExeYn());
        pushMap.put("clickDomainAgentExeYn", agentVO.getClickDomainAgentExeYn());

        serviceWorkerService.sendNotificationAll(pushMap);

        agentService.insertAgentHeartBeatHist(agentVO);
    }

    /**
     * 주어진 문자열 데이터에서 민감 정보를 마스킹한다.
     *
     * @param requestId       요청 ID
     * @param analysisModeCcd 분석 모드 공통코드
     * @param textData        문자열 데이터
     * @param seekMode        마스킹 모드 (mask: 마스킹된 데이터, origin: 원본 데이터, raw: 저장된 실제 데이터)
     * @param clientIp        클라이언트 IP
     * @return 마스킹한 문자열
     */
    @Transactional(readOnly = false)
    public String maskSensitiveInformation(String requestId, String analysisModeCcd, String textData, String seekMode, String clientIp) {
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

            if (sensitiveInformationList != null && !sensitiveInformationList.isEmpty()) {
                for (SensitiveInformationVO sensitiveInformation : sensitiveInformationList) {
                    resultText = resultText.replace(sensitiveInformation.getSensitiveInformationId(), Constants.CD_MASK_MODE_UNMASK.equals(maskModeCcd)
                            ? S2EncryptionUtil.decrypt(sensitiveInformation.getTargetText(), encryptionPassword)
                            : sensitiveInformation.getEscapeText());
                }

                if (Constants.CD_MASK_MODE_UNMASK.equals(maskModeCcd)) {
                    SensitiveInformationUnmaskHistVO paramVO = new SensitiveInformationUnmaskHistVO();
                    paramVO.setRequestId(requestId);
                    paramVO.setClientIp(clientIp);
                    paramVO.setSensitiveInformationCount(sensitiveInformationList.size());

                    sensitiveInformationUnmaskHistService.insertSensitiveInformationUnmaskHist(paramVO);
                    sensitiveInformationUnmaskHistService.insertSensitiveInformationUnmaskInfo(requestId, sensitiveInformationList);
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
