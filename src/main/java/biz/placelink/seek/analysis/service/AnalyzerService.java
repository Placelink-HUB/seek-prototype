package biz.placelink.seek.analysis.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;

import biz.placelink.seek.analysis.schedule.AnalysisRequestStatus;
import biz.placelink.seek.analysis.vo.AnalysisDetailVO;
import biz.placelink.seek.analysis.vo.AnalysisDetectionVO;
import biz.placelink.seek.analysis.vo.AnalysisResultVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.serviceworker.service.ServiceWorkerService;
import biz.placelink.seek.com.util.RestApiUtil;
import biz.placelink.seek.system.file.service.FileService;
import biz.placelink.seek.system.file.vo.FileDetailVO;
import kr.s2.ext.exception.S2Exception;
import kr.s2.ext.exception.S2RuntimeException;
import kr.s2.ext.file.FileManager;
import kr.s2.ext.util.S2EncryptionUtil;
import kr.s2.ext.util.S2HashUtil;
import kr.s2.ext.util.S2JsonUtil;
import kr.s2.ext.util.S2StreamUtil;
import kr.s2.ext.util.S2Util;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 07.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AnalyzerService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyzerService.class);

    private final ServiceWorkerService serviceWorkerService;
    private final AnalysisRequestStatus analysisRequestStatus;
    private final AnalysisService analysisService;
    private final AnalysisResultService analysisResultService;
    private final AnalysisErrorService analysisErrorService;
    private final SensitiveInformationService sensitiveInformationService;
    private final FileManager fileManager;
    private final FileService fileService;

    public AnalyzerService(ServiceWorkerService serviceWorkerService, AnalysisRequestStatus analysisRequestStatus, AnalysisService analysisService, AnalysisResultService analysisResultService, AnalysisErrorService analysisErrorService, SensitiveInformationService sensitiveInformationService, FileManager fileManager, FileService fileService) {
        this.serviceWorkerService = serviceWorkerService;
        this.analysisRequestStatus = analysisRequestStatus;
        this.analysisService = analysisService;
        this.analysisResultService = analysisResultService;
        this.analysisErrorService = analysisErrorService;
        this.sensitiveInformationService = sensitiveInformationService;
        this.fileManager = fileManager;
        this.fileService = fileService;
    }

    @Value("${analysis.server.url}")
    public String analyzerUrl;

    @Value("${analysis.model-name}")
    public String analysisModelName;

    @Value("${encryption.password}")
    public String encryptionPassword;

    private final long hashSeed = 0;
    private final int apiTimeout = 60000; // API 요청 타임아웃 (60초)

    /**
     * 비동기 분석 요청한다.
     *
     * @param analysisDetail 작업 상세 정보
     */
    @Async("analysisTaskExecutor")
    @Transactional(readOnly = false) // 상태 변경등을 위하여 readOnly 속성을 철회한다.
    public void asyncAnalysisRequest(AnalysisDetailVO analysisDetail) {
        if (analysisDetail == null || S2Util.isEmpty(analysisDetail.getAnalysisId()) || S2Util.isNotEmpty(analysisDetail.getAnalysisResultId())) {
            // 분석 요청이 유효하지 않은 경우, 처리하지 않음
            return;
        }

        String analysisId = analysisDetail.getAnalysisId();
        String analysisModeCcd = analysisDetail.getAnalysisModeCcd();
        String analysisModel = analysisModelName;
        String analysisData = "";

        analysisService.updateAnalysisStatusWithNewTransaction(analysisId, Constants.CD_ANALYSIS_STATUS_PROCESSING, analysisModel);

        List<InputStream> hashDataStreamList = new ArrayList<>();
        List<InputStream> dataStreamList = new ArrayList<>();
        List<Map.Entry<String, Object>> analysisParamList = new ArrayList<>();

        try (ByteArrayInputStream analysisModelStream = new ByteArrayInputStream(analysisModel.getBytes(StandardCharsets.UTF_8))) {
            String analysisDataHash = ""; // 분석 모델을 포함한 데이터 해시 값
            List<FileDetailVO> fileDetailList = null;

            // analysisModelStream 사용: 동일 데이터라도 analysisModel 이 다르면 해시값이 달라지도록 한다. (동일한 데이터라도 분석 모델이 다르면 분석 결과가 다를 수 있다.)
            hashDataStreamList.addFirst(analysisModelStream);

            if (Constants.CD_ANALYSIS_MODE_DATABASE.equals(analysisModeCcd)) {
                String analyzedContent = analysisDetail.getContent();
                if (S2Util.isNotEmpty(analyzedContent)) {
                    hashDataStreamList.add(new ByteArrayInputStream(analyzedContent.getBytes(StandardCharsets.UTF_8)));
                }
                analysisDataHash = generateXXHash64(hashDataStreamList);
            } else {
                String body = analysisDetail.getBody();
                if (S2Util.isNotEmpty(body)) {
                    hashDataStreamList.add(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
                }

                fileDetailList = fileService.selectFileDetailList(analysisDetail.getFileId());
                if (fileDetailList != null && !fileDetailList.isEmpty()) {
                    for (FileDetailVO fileDetail : fileDetailList) {
                        if (fileDetail != null && S2Util.isNotEmpty(fileDetail.getSavePath()) && S2Util.isNotEmpty(fileDetail.getSaveName())) {
                            /*
                             * [원격 파일일때 검토 필요사항]
                             * 지금은 fileManager 가 로컬의 파일을 읽어와서 직접 사용한다.
                             * 만약 fileManager 가 원격의 파일을 읽어 온다면 임시 파일로 저장하여 아래의 InputStream 과 같이 사용할 것을 검토해야만 한다.
                             */
                            hashDataStreamList.add(fileManager.readFile(fileDetail.getSavePath(), fileDetail.getSaveName()));
                        }
                    }
                }

                analysisDataHash = generateXXHash64(hashDataStreamList);
            }

            if (S2Util.isEmpty(analysisDataHash)) {
                throw new S2RuntimeException("분석 데이터 해시 값이 존재하지 않습니다.");
            }

            if (analysisResultService.checkAnalysisHashExists(analysisDataHash)) {
                // 이미 분석된 데이터인 경우, 중복 분석 할 필요가 없으므로 해당 해시 값을 분석 결과 ID 로 완료 처리한다.
                // !!s2!! 분석이 완료되기 전에는 동일 데이터를 중복 요청할 수 있는데 이 부분을 어떻게 처리할지 고민 하자
                AnalysisResultVO existingAnalysisResult = analysisResultService.selectAnalysisResult(analysisId, analysisDataHash);

                if (analysisService.updateAnalysisCompleted(analysisId, analysisDataHash, 0, analysisModeCcd, existingAnalysisResult) > 0 && existingAnalysisResult != null) {
                    // 분석 완료 정보를 실시간으로 푸시한다.
                    Map<String, Object> pushMap = new HashMap<>();

                    pushMap.put("pushTypeCcd", Constants.CD_PUSH_TYPE_ANALYSIS_COMPLETE);
                    pushMap.put("checkId", analysisId);
                    pushMap.put("analysisModeCcd", existingAnalysisResult.getAnalysisModeCcd());
                    pushMap.put("countryCcd", existingAnalysisResult.getCountryCcd());
                    pushMap.put("totalDetectionCount", existingAnalysisResult.getTotalDetectionCount());
                    pushMap.put("createDtStr", this.getCreateDtStr(existingAnalysisResult.getCreateDt()));

                    List<AnalysisDetectionVO> existingDetectionList = analysisResultService.selectAnalysisDetectionList(analysisDataHash);
                    if (existingDetectionList != null) {
                        for (AnalysisDetectionVO existingDetection : existingDetectionList) {
                            pushMap.put(existingDetection.getDetectionTypeCcd(), existingDetection.getDetectionCount());
                        }

                        serviceWorkerService.sendNotificationAll(pushMap);
                    }
                }
                return;
            }

            analysisParamList.add(Map.entry("request_id", analysisId));
            analysisParamList.add(Map.entry("model_name", analysisModel));

            if (Constants.CD_ANALYSIS_MODE_DATABASE.equals(analysisDetail.getAnalysisModeCcd())) {
                analysisParamList.add(Map.entry("user_input", analysisDetail.getContent()));
                analysisData = generateAnalysisData(analysisParamList);
            } else {
                String body = analysisDetail.getBody();
                if (S2Util.isNotEmpty(body)) {
                    analysisParamList.add(Map.entry("user_input", body));
                }

                if (fileDetailList != null && !fileDetailList.isEmpty()) {
                    for (FileDetailVO fileDetail : fileDetailList) {
                        if (fileDetail != null && S2Util.isNotEmpty(fileDetail.getSavePath()) && S2Util.isNotEmpty(fileDetail.getSaveName())) {
                            InputStream fileInputStream = fileManager.readFile(fileDetail.getSavePath(), fileDetail.getSaveName());
                            dataStreamList.add(fileInputStream);

                            /*
                             * [원격 파일일때 검토 필요사항]
                             * 지금은 fileManager 가 로컬의 파일을 읽어와서 직접 사용한다.
                             * 만약 fileManager 가 원격의 파일을 읽어 온다면 임시 파일로 저장하여 위의 InputStream 과 같이 사용할 것을 검토해야만 한다.
                             */
                            analysisParamList.add(Map.entry("files", RestApiUtil.createInputStreamResource(fileInputStream, fileDetail.getFileFullName(), fileDetail.getFileSize())));
                        }
                    }
                }

                analysisData = generateAnalysisData(analysisParamList);
            }

            JsonNode analysisJsonData = S2JsonUtil.parseJson(analysisData);

            if (analysisJsonData == null || !analysisJsonData.path("request_id").asText("").equals(analysisId)) {
                // API 요청 응답의 request_id 와 분석 요청 ID 가 일치하지 않으면 하면 분석 요청이 실패한 것으로 판단한다.
                throw new S2RuntimeException("[API 요청 호출 실패]: API 요청 호출 결과 값이 틀립니다.");
            }

            // 분석 모델을 포함한 데이터 해시 값을 등록한다.
            analysisService.updateAnalysisDataHash(analysisId, analysisDataHash);
        } catch (Exception e) {
            analysisErrorService.insertAnalysisErrorWithNewTransaction(analysisId, analysisData, e.getMessage() + "\n" + S2Exception.getStackTrace(e));
            logger.error("asyncAnalysisRequest Error : {}", e.getMessage(), e);
        } finally {
            for (InputStream stream : hashDataStreamList) {
                S2StreamUtil.closeStream(stream);
            }
            for (InputStream stream : dataStreamList) {
                S2StreamUtil.closeStream(stream);
            }

            for (Map.Entry<String, Object> param : analysisParamList) {
                Object paramValue = param.getValue();
                if (paramValue instanceof InputStream) {
                    S2StreamUtil.closeStream((InputStream) paramValue);
                }
            }
        }
    }

    private String generateXXHash64(List<InputStream> hashDataStreamList) {
        if (hashDataStreamList == null || hashDataStreamList.isEmpty()) {
            throw new S2RuntimeException("분석 데이터 해시 값이 존재하지 않습니다.");
        }

        String analysisHash = S2HashUtil.generateXXHash64(hashSeed, true, hashDataStreamList.toArray(new InputStream[0]));

        if (S2Util.isEmpty(analysisHash)) {
            throw new S2RuntimeException("분석 데이터 해시 값이 존재하지 않습니다.");
        }
        return analysisHash;
    }

    @SuppressWarnings("unchecked")
    private String generateAnalysisData(List<Map.Entry<String, Object>> analysisParamList) {
        if (analysisParamList == null || analysisParamList.isEmpty()) {
            throw new S2RuntimeException("분석 데이터가 존재하지 않습니다.");
        }

        String analysisSeServerUrl = S2Util.joinPaths(analyzerUrl, "/generate");
        String analysisData = RestApiUtil.callApi(analysisSeServerUrl, HttpMethod.POST, apiTimeout, analysisParamList.toArray(new Map.Entry[0]));

        if (S2Util.isEmpty(analysisData)) {
            throw new S2RuntimeException("[API 요청 호출 실패]: 결과 값이 존재하지 않습니다.");
        }
        return analysisData;
    }

    /**
     * 분석 결과를 비동기 폴링하여 처리한다.
     *
     * @param analysisDetail 분석 상세 정보
     */
    @Async("analysisTaskExecutor")
    @Transactional(readOnly = false) // 상태 변경등을 위하여 readOnly 속성을 철회한다.
    public void asyncPollAnalysisResults(AnalysisDetailVO analysisDetail) {
        if (analysisDetail != null) {
            String analysisId = analysisDetail.getAnalysisId();
            String analysisDataHash = analysisDetail.getAnalysisDataHash(); // 분석 모델을 포함한 데이터 해시 값, 최초 분석 완료 시 analysisResultId 값으로 등록한다.
            String analysisRawData = "";

            try {
                if (S2Util.isEmpty(analysisId)) {
                    throw new S2RuntimeException("분석 ID 가 존재하지 않습니다.");
                } else if (S2Util.isEmpty(analysisDataHash)) {
                    throw new S2RuntimeException("분석 데이터 해시 값이 존재하지 않습니다.");
                }

                String analysisSeServerUrl = S2Util.joinPaths(analyzerUrl, String.format("/result?request_id=%s&model=%s", analysisId, analysisDetail.getAnalysisModel()));
                JsonNode analysisJsonData = null;

                analysisRawData = RestApiUtil.callApi(analysisSeServerUrl, HttpMethod.GET, apiTimeout);

                if (S2Util.isNotEmpty(analysisRawData)) {
                    // JSON 문자열 내에서 이스케이프되지 않은 제어 문자(예: \n, \t, \r 등, ASCII 0-31)를 허용하도록 설정
                    analysisJsonData = S2JsonUtil.parseJson(analysisRawData, JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS);
                }

                if (analysisJsonData == null) {
                    throw new S2RuntimeException(analysisRawData);
                }

                /**
                 * 분석 상태
                 * success: 성공
                 * fail: 실패 (분석 대상이 파일인 경우는 모든 파일이 분석 불가능 일때)
                 * warning: 실패 (분석 대상이 파일인 경우는 일부 파일이 분석 불가능 일때)
                 */
                String status = analysisJsonData.path("status").asText("");
                if (!"success".equalsIgnoreCase(status)) {
                    if ("fail".equalsIgnoreCase(status) || "warning".equalsIgnoreCase(status)) {
                        // 오류 상황이라면 (!!s2!! 파일 분석일때 warning 상황을 어떻게 처리할지 고민하자)
                        throw new S2RuntimeException(analysisRawData);
                    } else {
                        // 분석이 완료 될때까지 다시 시도한다.
                        return;
                    }
                }

                // int totalDetectionCount = analysisJsonData.path("total_hit").asInt(0);
                int totalDetectionCount = 0;

                String analyzedContent = analysisJsonData.path("content").asText("");
                long analysisTime = analysisJsonData.path("latency").asLong(0);
                JsonNode items = analysisJsonData.get("item");

                Pattern pattern = Pattern.compile("\\$PL\\{(.*?)\\}");

                List<SensitiveInformationVO> sensitiveInformationList = new ArrayList<>();
                List<Map<String, String>> sensitiveInformationTypeList = new ArrayList<>();
                Map<String, Integer> analysisDetectionsMap = new HashMap<>();

                if (items.isArray()) {
                    for (JsonNode item : items) {
                        String placeholder = item.path("placeholder").asText("");

                        Matcher matcher = pattern.matcher(placeholder);
                        if (!matcher.find()) {
                            throw new S2Exception("대상 문자열을 찾을 수 없습니다. [" + placeholder + "]");
                        }

                        String targetText = matcher.group(1);
                        String escapeText = targetText.replaceAll("[^\\p{Punct}]", "*"); // 특수문자를 제외한 모든 일반적인 문자를 *로 치환
                        String sensitiveInformationId = String.format("$PL{%s}", S2HashUtil.generateXXHash64(hashSeed, targetText));
                        String severityCcd = item.path("severity").asText("");
                        int hitCount = item.path("hit").asInt(0);

                        totalDetectionCount += hitCount;

                        analyzedContent = analyzedContent.replace(String.format("$PL{%s}", targetText), sensitiveInformationId);

                        JsonNode violations = item.get("violation");
                        if (violations.isArray()) {
                            for (JsonNode violation : violations) {
                                Map<String, String> sensitiveInformationType = new HashMap<>();
                                String detectionTypeCcd = violation.asText("");
                                if (S2Util.isNotEmpty(detectionTypeCcd)) {
                                    sensitiveInformationType.put("sensitiveInformationId", sensitiveInformationId);
                                    sensitiveInformationType.put("detectionTypeCcd", detectionTypeCcd);
                                    sensitiveInformationTypeList.add(sensitiveInformationType);
                                }
                            }
                        }

                        int detectionCount = S2Util.getValue(analysisDetectionsMap, severityCcd, 0);
                        analysisDetectionsMap.put(severityCcd, detectionCount + hitCount);

                        SensitiveInformationVO sensitiveInformation = new SensitiveInformationVO();
                        sensitiveInformation.setSensitiveInformationId(sensitiveInformationId);
                        sensitiveInformation.setTargetText(S2EncryptionUtil.encrypt(targetText, encryptionPassword)); // 민감 정보 문자열은 암호화한다.
                        sensitiveInformation.setEscapeText(escapeText);
                        sensitiveInformation.setHitCount(hitCount);
                        sensitiveInformation.setSeverityCcd(severityCcd);

                        sensitiveInformationList.add(sensitiveInformation);
                    }
                }

                if (sensitiveInformationList.isEmpty()) {
                    analyzedContent = null;
                }

                if (analysisResultService.insertAnalysisResult(analysisDataHash, analysisRawData, analyzedContent, totalDetectionCount) > 0) {
                    List<AnalysisDetectionVO> analysisDetectionList = new ArrayList<>();

                    Map<String, Object> pushMap = new HashMap<>();
                    pushMap.put("pushTypeCcd", Constants.CD_PUSH_TYPE_ANALYSIS_COMPLETE);
                    pushMap.put("checkId", analysisId);
                    pushMap.put("analysisModeCcd", analysisDetail.getAnalysisModeCcd());
                    pushMap.put("countryCcd", analysisDetail.getCountryCcd());
                    pushMap.put("totalDetectionCount", totalDetectionCount);
                    pushMap.put("createDtStr", this.getCreateDtStr(analysisDetail.getCreateDt()));

                    if (!sensitiveInformationList.isEmpty()) {
                        for (String detectionTypeCcd : analysisDetectionsMap.keySet()) {
                            Integer detectionCount = analysisDetectionsMap.get(detectionTypeCcd);
                            if (detectionCount != null && detectionCount > 0) {
                                AnalysisDetectionVO item = new AnalysisDetectionVO();
                                item.setAnalysisResultId(analysisDataHash);
                                item.setDetectionTypeCcd(detectionTypeCcd);
                                item.setDetectionCount(detectionCount);

                                analysisDetectionList.add(item);

                                // 푸시 데이터에 타입별 검출 개수를 누적한다.
                                pushMap.put(detectionTypeCcd, detectionCount + S2Util.getValue(pushMap, detectionTypeCcd, 0));
                            }
                        }

                        if (!analysisDetectionList.isEmpty()) {
                            analysisResultService.insertAnalysisDetectionList(analysisDetectionList);
                        }

                        int sensitiveInformationCount = sensitiveInformationService.insertSensitiveInformationList(sensitiveInformationList);
                        if (sensitiveInformationCount > 0) {
                            sensitiveInformationService.insertSensitiveInformationMappingList(analysisDataHash, sensitiveInformationList);

                            if (!sensitiveInformationTypeList.isEmpty()) {
                                sensitiveInformationService.insertSensitiveInformationTypeList(sensitiveInformationTypeList);
                            }
                        }

                        serviceWorkerService.sendNotificationAll(pushMap);
                    }

                    analysisService.updateAnalysisCompleted(analysisId, analysisDataHash, analysisTime, analysisDetail.getAnalysisModeCcd(), totalDetectionCount, analysisDetail.getTargetInformation(), analyzedContent, analysisDetail.getContent());
                    analysisRequestStatus.remove(analysisId);
                }
            } catch (Exception e) {
                analysisErrorService.insertAnalysisErrorWithNewTransaction(analysisId, analysisRawData, e.getMessage() + "\n" + S2Exception.getStackTrace(e));
                logger.error("asyncPollAnalysisResults Error : {}", e.getMessage(), e);
                analysisRequestStatus.remove(analysisId);
            } finally {
                analysisRequestStatus.setInUse(analysisId, false);
            }
        }
    }

    private String getCreateDtStr(LocalDateTime createDt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        if (createDt != null) {
            return createDt.format(formatter);
        } else {
            return LocalDateTime.now().format(formatter);
        }
    }

}
