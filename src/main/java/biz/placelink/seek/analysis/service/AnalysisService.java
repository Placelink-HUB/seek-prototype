package biz.placelink.seek.analysis.service;

import biz.placelink.seek.analysis.vo.AnalysisResultItemVO;
import biz.placelink.seek.analysis.vo.AnalysisVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.RestApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.s2.ext.util.S2EncryptionUtil;
import kr.s2.ext.util.S2Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 파일을 비동기 분석한다.
     * 분석 시간이 오래 걸리는 관계로 트랜잭션은 사용하지 않는다.
     *
     * @param analysisRequest 분석 요청 정보
     */
    @Async("analysisTaskExecutor")
    @Transactional(readOnly = false) // 상태 변경등을 위하여 readOnly 속성을 철회한다.
    public void asyncContentAnalysis(AnalysisVO analysisRequest) {
        if (analysisRequest != null) {
            String analysisStatusCcd = Constants.CD_ANALYSIS_STATUS_ERROR;
            String analysisId = analysisRequest.getAnalysisId();
            boolean isAnalysisResultComplete = false;

            try {
                // 별도의 트랜잭션으로 상태 업데이트
                this.updateAnalysisStatusWithNewTransaction(analysisId, Constants.CD_ANALYSIS_STATUS_PROCESSING);

                String analysisSeServerUrl = S2Util.joinPaths(analyzerUrl, "/generate");

                if (S2Util.isEmpty(analysisRequest.getAnalysisContent())) {
                    return;
                }

                String analysisData = null;
                JsonNode analysisJsonData = null;

                try {
                    // 요청시간이 15~20분여 소요됨
                    analysisData = RestApiUtil.callApi(analysisSeServerUrl, HttpMethod.POST, 6000000,
                            Map.entry("user_id", analysisId),
                            Map.entry("model_name", analysisModelName),
                            Map.entry("user_input", analysisRequest.getAnalysisContent())
                    );
                } catch (HttpStatusCodeException e) {
                    if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        analysisData = e.getResponseBodyAsString();
                    }
                    logger.error("[API 호출 중 오류 발생1] url: {}, message: {}", analysisSeServerUrl, e.getMessage(), e);
                } catch (Exception e) {
                    logger.error("[API 호출 중 오류 발생2] url: {}, message: {}", analysisSeServerUrl, e.getMessage(), e);
                }

                if (S2Util.isNotEmpty(analysisData)) {
                    logger.debug("[API 호출 성공] url: {}, analysisId: {}, analysisData: {}", analysisSeServerUrl, analysisId, analysisData);

                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        analysisJsonData = objectMapper.readTree(analysisData);
                    } catch (JsonProcessingException e) {
                        logger.error("[API 결과 데이터 파싱오류] analysisId: {} analysisData: {}, message: {}", analysisId, analysisData, e.getMessage(), e);
                    }
                }

                if (analysisJsonData != null) {
                    switch (analysisJsonData.path("status").asText("").toLowerCase()) {
                        case "success" -> {
                            int totalHitCount = analysisJsonData.path("total-hit").asInt(0);
                            if (totalHitCount > 0) {
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
                                        String targetText = pattern.matcher(placeholder).group(1);
                                        String escapeText = placeholder.replaceAll("[^\\p{Punct}]", "*"); // 특수문자를 제외한 모든 일반적인 문자를 *로 치환
                                        String sensitiveInformationId = String.format("$PL{%s}", S2Util.getHashMD5(targetText));
                                        String severityCcd = item.path("severity").asText("");
                                        int hitCount = item.path("hit").asInt(0);

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

                                        // 민감 정보가 검출되어 해당 내용을 등록하였다면 더이상 분석 결과를 업데이트 하지 않는다.
                                        isAnalysisResultComplete = true;
                                    }
                                }
                            }

                            analysisStatusCcd = Constants.CD_ANALYSIS_STATUS_COMPLETE;
                        }
                        case "busy" -> {
                            // 분석 서버가 바쁜 경우 재시도할 수 있도록 상태만 변경
                            analysisStatusCcd = Constants.CD_ANALYSIS_STATUS_WAIT;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("asyncFileAnalysis Error : {}", e.getMessage(), e);
            } finally {
                if (!isAnalysisResultComplete) {
                    // 별도의 트랜잭션으로 상태 업데이트
                    this.updateAnalysisStatusWithNewTransaction(analysisId, analysisStatusCcd);
                }
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
     * 트랜잭션과 독립적으로 분석 정보 상태를 수정한다.
     *
     * @param analysisId        분석 ID
     * @param analysisStatusCcd 분석 상태 공통코드
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAnalysisStatusWithNewTransaction(String analysisId, String analysisStatusCcd) {
        this.updateAnalysisStatus(analysisId, analysisStatusCcd);
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

}