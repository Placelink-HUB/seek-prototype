package biz.placelink.seek.analysis.service;

import biz.placelink.seek.analysis.vo.AnalysisErrorVO;
import biz.placelink.seek.analysis.vo.AnalysisResultItemVO;
import biz.placelink.seek.analysis.vo.AnalysisVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.RestApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import kr.s2.ext.exception.S2Exception;
import kr.s2.ext.util.S2EncryptionUtil;
import kr.s2.ext.util.S2HashUtil;
import kr.s2.ext.util.S2Util;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    private final AnalysisMapper analysisMapper;

    private final SensitiveInformationService sensitiveInformationService;

    public AnalysisService(AnalysisMapper analysisMapper, SensitiveInformationService sensitiveInformationService) {
        this.analysisMapper = analysisMapper;
        this.sensitiveInformationService = sensitiveInformationService;
    }

    @Value("${analysis.server.url}")
    public String analyzerUrl;

    @Value("${analysis.model-name}")
    public String analysisModelName;

    @Value("${encryption.password}")
    public String encryptionPassword;

    /**
     * 비동기 분석 요청한다.
     *
     * @param analysisRequest 분석 요청 정보
     */
    @Async("analysisTaskExecutor")
    @Transactional(readOnly = false) // 상태 변경등을 위하여 readOnly 속성을 철회한다.
    public void asyncAnalysisRequest(AnalysisVO analysisRequest) {
        if (analysisRequest != null) {
            String analysisId = analysisRequest.getAnalysisId();

            try {
                String analysisSeServerUrl = S2Util.joinPaths(analyzerUrl, "/generate");

                if (S2Util.isEmpty(analysisRequest.getAnalysisContent())) {
                    this.updateAnalysisStatus(analysisId, Constants.CD_ANALYSIS_STATUS_ERROR);
                    return;
                }

                JsonNode analysisJsonData = null;

                String analysisData = RestApiUtil.callApi(analysisSeServerUrl, HttpMethod.POST, 6000000,
                        Map.entry("request_id", analysisId),
                        Map.entry("model_name", analysisModelName),
                        Map.entry("user_input", analysisRequest.getAnalysisContent())
                );

                if (S2Util.isNotEmpty(analysisData)) {
                    logger.debug("[API 요청 호출 성공] url: {}, analysisId: {}, analysisData: {}", analysisSeServerUrl, analysisId, analysisData);

                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        analysisJsonData = objectMapper.readTree(analysisData);
                    } catch (JsonProcessingException e) {
                        logger.error("[API 요청 호출 데이터 파싱오류] analysisId: {} analysisData: {}, message: {}", analysisId, analysisData, e.getMessage(), e);
                    }
                }

                if (analysisJsonData != null && analysisJsonData.path("request_id").asText("").equals(analysisId)) {
                    this.updateAnalysisStatus(analysisId, Constants.CD_ANALYSIS_STATUS_PROCESSING);
                }
            } catch (Exception e) {
                logger.error("asyncAnalysisRequest Error : {}", e.getMessage(), e);
            }

        }
    }

    /**
     * 분석결과를 비동기 폴링하여 처리한다.
     *
     * @param analysisRequest 분석      */
    @Async("analysisTaskExecutor")
    @Transactional(readOnly = false) // 상태 변경등을 위하여 readOnly 속성을 철회한다.
    public void asyncPollAnalysisResults(AnalysisVO analysisRequest) {
        if (analysisRequest != null) {
            String analysisId = analysisRequest.getAnalysisId();
            String targetInformation = analysisRequest.getTargetInformation();
            String analysisData = "";

            try {
                String analysisSeServerUrl = S2Util.joinPaths(analyzerUrl, String.format("/result?request_id=%s&model=%s", analysisId, analysisModelName));

                JsonNode analysisJsonData = null;

                analysisData = RestApiUtil.callApi(analysisSeServerUrl, HttpMethod.GET, 6000000);

                if (S2Util.isNotEmpty(analysisData)) {
                    logger.debug("[API 결과 호출 성공] url: {}, analysisId: {}, analysisData: {}", analysisSeServerUrl, analysisId, analysisData);

                    try {
                        ObjectMapper objectMapper = JsonMapper.builder().enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS).build();
                        analysisJsonData = objectMapper.readTree(analysisData);
                    } catch (JsonProcessingException e) {
                        logger.error("[API 결과 호출 데이터 파싱오류] analysisId: {} analysisData: {}, message: {}", analysisId, analysisData, e.getMessage(), e);
                    }
                }

                if (analysisJsonData != null && analysisJsonData.path("status").asText("").equalsIgnoreCase("success")) {
                    int totalHitCount = analysisJsonData.path("total_hit").asInt(0);
                    if (totalHitCount == 0) {
                        // 완료처리
                        return;
                    }
                    
                    String analysisContent = analysisJsonData.path("content").asText("");
                    long latency = analysisJsonData.path("latency").asLong(0);
                    JsonNode items = analysisJsonData.get("item");

                    Pattern pattern = Pattern.compile("\\$PL\\{(.*?)\\}");

                    List<SensitiveInformationVO> sensitiveInformationList = new ArrayList<>();
                    List<Map<String, String>> sensitiveInformationTypeList = new ArrayList<>();
                    Map<String, Integer> analysisResultItemsMap = new HashMap<>();

                    if (items.isArray()) {
                        for (JsonNode item : items) {
                            String placeholder = item.path("placeholder").asText("");

                            Matcher matcher = pattern.matcher(placeholder);
                            if (!matcher.find()) {
                                throw new S2Exception("대상 문자열을 찾을 수 없습니다. [" + placeholder + "]");
                            }

                            String targetText = matcher.group(1);
                            String escapeText = targetText.replaceAll("[^\\p{Punct}]", "*"); // 특수문자를 제외한 모든 일반적인 문자를 *로 치환
                            String sensitiveInformationId = String.format("$PL{%s}", S2HashUtil.generateMD5(targetText));
                            String severityCcd = item.path("severity").asText("");
                            int hitCount = item.path("hit").asInt(0);

                            analysisContent = analysisContent.replace(String.format("$PL{%s}", targetText), sensitiveInformationId);

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

                            int detectedCount = S2Util.getValue(analysisResultItemsMap, severityCcd, 0);
                            analysisResultItemsMap.put(severityCcd, detectedCount + hitCount);

                            SensitiveInformationVO sensitiveInformation = new SensitiveInformationVO();
                            sensitiveInformation.setSensitiveInformationId(sensitiveInformationId);
                            sensitiveInformation.setTargetText(S2EncryptionUtil.encrypt(targetText, encryptionPassword)); // 민감 정보 문자열은 암호화한다.
                            sensitiveInformation.setEscapeText(escapeText);
                            sensitiveInformation.setHitCount(hitCount);
                            sensitiveInformation.setSeverityCcd(severityCcd);

                            sensitiveInformationList.add(sensitiveInformation);
                        }
                    }

                    if (!sensitiveInformationList.isEmpty()) {
                        AnalysisVO analysisResult = new AnalysisVO();
                        analysisResult.setAnalysisId(analysisId);
                        analysisResult.setAnalysisStatusCcd(Constants.CD_ANALYSIS_STATUS_COMPLETE);
                        analysisResult.setAnalysisModel(analysisModelName);
                        analysisResult.setAnalysisContent(analysisContent);
                        analysisResult.setAnalysisTime(latency);
                        analysisResult.setTotalDetectedCount(totalHitCount);

                        if (analysisMapper.updateAnalysis(analysisResult) > 0) {
                            List<AnalysisResultItemVO> analysisResultItemList = new ArrayList<>();

                            for (String key : analysisResultItemsMap.keySet()) {
                                Integer detectedCount = analysisResultItemsMap.get(key);
                                if (detectedCount != null && detectedCount > 0) {
                                    AnalysisResultItemVO item = new AnalysisResultItemVO();
                                    item.setAnalysisId(analysisId);
                                    item.setDetectionTypeCcd(key);
                                    item.setDetectedCount(detectedCount);

                                    analysisResultItemList.add(item);
                                }
                            }

                            if (!analysisResultItemList.isEmpty()) {
                                analysisMapper.insertAnalysisResultItems(analysisResultItemList);
                            }

                            sensitiveInformationService.insertSensitiveInformationList(sensitiveInformationList);

                            if (!sensitiveInformationTypeList.isEmpty()) {
                                sensitiveInformationService.insertSensitiveInformationTypes(sensitiveInformationTypeList);
                            }

                            if (S2Util.isNotEmpty(targetInformation)) {
                                String[] targetInformationArr = targetInformation.split("\\.");
                                if (targetInformationArr.length >= 2) {
                                    // 대상 콘텐츠 테이블의 컬럼에 마스킹 정보가 반영된 콘텐츠를 동적으로 수정한다.
                                    this.updateAnalysisTargetColumnDynamically(targetInformationArr[0], targetInformationArr[1], String.format("$WT{%s}", analysisId), analysisContent);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                this.insertAnalysisErrorWithNewTransaction(analysisId, analysisData, e.getMessage() + "\n" + S2Exception.getStackTrace(e));
                logger.error("asyncPollAnalysisResults Error : {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 실행 시간이 초과된 분석을 오류 처리한다.
     *
     * @param maxMinutes 최대 허용 시간(분)
     */
    @Transactional(readOnly = false)
    public void updateAnalysisTimeoutError(int maxMinutes) {
        analysisMapper.updateAnalysisTimeoutError(maxMinutes);
    }

    /**
     * 실행하려는 분석 정보 목록을 조회한다.
     *
     * @param maxCount 분석기 서버에 요청할 수 있는 최대(스레드) 수
     * @return 분석 정보 목록
     */
    public List<AnalysisVO> selectAnalysisListToExecuted(int maxCount) {
        return analysisMapper.selectAnalysisListToExecuted(maxCount);
    }

    /**
     * 실행중인 분석 정보 목록을 조회한다.
     *
     * @return 분석 정보 목록
     */
    public List<AnalysisVO> selectProcessingAnalysisList() {
        return analysisMapper.selectProcessingAnalysisList();
    }

    /**
     * 분석 요청 정보 상태를 수정한다.
     *
     * @param analysisId        분석 ID
     * @param analysisStatusCcd 분석 상태 공통코드
     * @return 등록 개수
     */
    public int updateAnalysisStatus(String analysisId, String analysisStatusCcd) {
        return analysisMapper.updateAnalysisStatus(analysisId, analysisStatusCcd);
    }

    /**
     * 분석 오류를 등록한다.
     *
     * @param analysisId   분석 ID
     * @param analysisData 분석 데이터
     * @param errorMessage 오류 메시지
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertAnalysisErrorWithNewTransaction(String analysisId, String analysisData, String errorMessage) {
        analysisMapper.updateAnalysisErrorExclusion(analysisId);
        AnalysisErrorVO paramVO = new AnalysisErrorVO();
        paramVO.setAnalysisId(analysisId);
        paramVO.setAnalysisData(analysisData);
        paramVO.setErrorMessage(errorMessage);
        analysisMapper.insertAnalysisError(paramVO);
    }

    /**
     * 분석 대상 컬럼을 동적으로 수정한다.
     *
     * @param tableName  테이블 명
     * @param columnName 컬럼 명
     * @param oldValue   기존 값
     * @param newValue   변경 값
     * @return 처리 개수
     */
    public int updateAnalysisTargetColumnDynamically(String tableName, String columnName, String oldValue, String newValue) {
        int result = 0;
        if (S2Util.isNotEmpty(tableName) && S2Util.isNotEmpty(columnName) &&
                S2Util.isNotEmpty(oldValue) && S2Util.isNotEmpty(newValue) &&
                // 테이블명은 대소문자 구분없이 tb_로 시작하고, 컬럼명은 영문자, 숫자, 언더바(_)로만 구성되어야 한다.
                tableName.matches("(?i)^tb_[a-zA-Z0-9_]+") && columnName.matches("[a-zA-Z0-9_]+")) {
            result = analysisMapper.updateAnalysisTargetColumnDynamically(tableName, columnName, oldValue, newValue);
        }
        return result;
    }

}