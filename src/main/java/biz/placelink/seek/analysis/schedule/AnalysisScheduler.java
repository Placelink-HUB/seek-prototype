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
package biz.placelink.seek.analysis.schedule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import biz.placelink.seek.analysis.service.AnalysisService;
import biz.placelink.seek.analysis.service.AnalyzerService;
import biz.placelink.seek.analysis.vo.AnalysisDetailVO;
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
@Component
public class AnalysisScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisScheduler.class);

    private final AnalysisRequestStatus analysisRequestStatus;
    private final AnalysisService analysisService;
    private final AnalyzerService AnalyzerService;
    private final ThreadPoolTaskExecutor analysisTaskExecutor;

    public AnalysisScheduler(AnalysisRequestStatus analysisRequestStatus, AnalysisService analysisService, AnalyzerService AnalyzerService, @Qualifier("analysisTaskExecutor") ThreadPoolTaskExecutor analysisTaskExecutor) {
        this.analysisRequestStatus = analysisRequestStatus;
        this.analysisService = analysisService;
        this.AnalyzerService = AnalyzerService;
        this.analysisTaskExecutor = analysisTaskExecutor;
    }

    @Value("${analysis.schedule.enabled:true}")
    private boolean analysisScheduleEnabled;

    @Value("${analysis.schedule.request.maxcnt}")
    private Integer analysisScheduleRequestMaxcnt;

    @Scheduled(fixedRate = 10000)
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
        List<AnalysisDetailVO> waitAnalysisList = analysisService.selectAnalysisHistListToExecuted(processableCount);

        if (S2Util.isNotEmpty(waitAnalysisList)) {
            for (AnalysisDetailVO analysis : waitAnalysisList) {
                AnalyzerService.asyncAnalysisRequest(analysis);
            }
        }

        // 처리 중인 분석 목록 등록
        analysisRequestStatus.add(analysisService.selectProcessingAnalysisList());
    }

    @Scheduled(fixedRate = 2000)
    public void analysisResult() {
        if (!analysisScheduleEnabled) {
            return;
        }

        AnalysisDetailVO analysisDetail = analysisRequestStatus.get();
        while (analysisDetail != null) {
            AnalyzerService.asyncPollAnalysisResults(analysisDetail);
            analysisDetail = analysisRequestStatus.get();
        }
    }

}
