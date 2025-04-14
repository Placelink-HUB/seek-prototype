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

    private final AnalysisRequestStatus analysisRequestStatus;
    private final AnalysisService analysisService;
    private final ThreadPoolTaskExecutor analysisTaskExecutor;

    public AnalysisScheduler(AnalysisRequestStatus analysisRequestStatus, AnalysisService analysisService, @Qualifier("analysisTaskExecutor") ThreadPoolTaskExecutor analysisTaskExecutor) {
        this.analysisRequestStatus = analysisRequestStatus;
        this.analysisService = analysisService;
        this.analysisTaskExecutor = analysisTaskExecutor;
    }

    @Value("${analysis.schedule.enabled:true}")
    private boolean analysisScheduleEnabled;

    @Value("${analysis.schedule.request.maxcnt}")
    private Integer analysisScheduleRequestMaxcnt;

    @Scheduled(fixedRate = 60000)
    public void analysisRequest() {
        if (!analysisScheduleEnabled) {
            return;
        }

        // 실행 시간이 초과된 분석을 오류 처리
        analysisService.updateAnalysisTimeoutError(30);

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
        List<AnalysisVO> waitAnalysisList = analysisService.selectAnalysisListToExecuted(processableCount);

        if (waitAnalysisList != null) {
            for (AnalysisVO analysis : waitAnalysisList) {
                analysisService.asyncAnalysisRequest(analysis);
            }
        }

        // 처리 중인 분석 목록 등록
        analysisRequestStatus.add(analysisService.selectProcessingAnalysisList());
    }

    @Scheduled(fixedRate = 2000)
    public void analysisResult() {
        AnalysisVO analysis = null;
        do {
            analysis = analysisRequestStatus.get();
            analysisService.asyncPollAnalysisResults(analysis);
        } while (analysis != null);
    }

}
