package biz.placelink.seek.analysis.schedule;

import biz.placelink.seek.analysis.service.AnalysisService;
import biz.placelink.seek.analysis.vo.AnalysisVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

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
@Component
public class AnalysisScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisScheduler.class);

    private final AnalysisService analysisService;
    private final ThreadPoolTaskExecutor analysisTaskExecutor;

    public AnalysisScheduler(AnalysisService analysisService, @Qualifier("analysisTaskExecutor") ThreadPoolTaskExecutor analysisTaskExecutor) {
        this.analysisService = analysisService;
        this.analysisTaskExecutor = analysisTaskExecutor;
    }

    @Value("${analysis.schedule.enabled:true}")
    private boolean analysisScheduleEnabled;

    @Value("${analysis.schedule.request.maxcnt}")
    private Integer analysisScheduleRequestMaxcnt;

    @Value("${analysis.server.url}")
    public String analyzerUrl;

    @Scheduled(fixedRate = 60000)
    public void analysis() {
        if (!analysisScheduleEnabled) {
            return;
        }

        analysisService.updateAnalysisTimeoutError(30);

        // 서버 상태 체크
        /*
        String resultStr = RestApiUtil.callApi(S2Util.joinPaths(analyzerUrl, "/page_check"), HttpMethod.GET);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(resultStr);
            if (jsonNode == null || !jsonNode.get("status").asText().equalsIgnoreCase("ok")) {
                return;
            }
        } catch (JsonProcessingException e) {
            return;
        }
        */

        // 스레드 풀 상태 체크
        int activeCount = analysisTaskExecutor.getActiveCount();
        int maxPoolSize = analysisTaskExecutor.getMaxPoolSize();
        int queueSize = analysisTaskExecutor.getThreadPoolExecutor().getQueue().size();
        int queueCapacity = analysisTaskExecutor.getThreadPoolExecutor().getQueue().remainingCapacity() + queueSize;

        // 활성 스레드 수와 대기열 크기 체크
        int availableSlots = maxPoolSize - activeCount + (queueCapacity - queueSize);
        if (availableSlots <= 0) {
            logger.warn("스레드 풀이 가득 찼습니다. 활성 스레드: {}, 대기열: {}/{}", activeCount, queueSize, queueCapacity);
            return;
        }

        // 처리 가능한 만큼만 요청 조회
        int processableCount = Math.min(availableSlots, analysisScheduleRequestMaxcnt);
        List<AnalysisVO> analysisList = analysisService.selectAnalysisListToExecuted(processableCount);

        if (analysisList != null) {
            for (AnalysisVO analysis : analysisList) {
                analysisService.asyncContentAnalysis(analysis);
            }
        }
    }

}
