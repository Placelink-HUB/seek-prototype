package biz.placelink.seek.dashboard.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import biz.placelink.seek.dashboard.vo.AnalysisStatisticsVO;

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
public class DashboardService {

    private final DashboardMapper dashboardMapper;

    public DashboardService(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    /**
     * 분석 현황 정보를 조회한다.
     *
     * @return 분석 현황
     */
    public AnalysisStatisticsVO selectAnalysisStatistics(String schDe) {
        AnalysisStatisticsVO analysisStatisticsVO = new AnalysisStatisticsVO();

        // 분석
        AnalysisStatisticsVO result1 = dashboardMapper.selectAnalysisCount(schDe);
        analysisStatisticsVO.setRequestCount(result1.getRequestCount());
        analysisStatisticsVO.setCompleteCount(result1.getCompleteCount());

        // 탐지
        AnalysisStatisticsVO result2 = dashboardMapper.selectAnalysisResultCount(schDe);
        analysisStatisticsVO.setTotalDetectionCount(result2.getTotalDetectionCount());
        analysisStatisticsVO.setDetectionCount(result2.getDetectionCount());

        return analysisStatisticsVO;
    }

    /**
     * 탐지 현황 정보를 조회한다.
     *
     * @return 탐지 현황
     */
    public AnalysisStatisticsVO selectDetectionStatistics(String schDe) {
        return dashboardMapper.selectDetectionStatistics(schDe);
    }

    /**
     * 실시간 분석 정보를 조회한다.
     *
     * @return 실시간 분석 현황
     */
    public List<AnalysisStatisticsVO> selectRealtimeAnalysisCount(String schDe) {
        return dashboardMapper.selectRealtimeAnalysisCount(schDe);
    }

    /**
     * 최종 분석이 완료된 일시를 조회한다.(문자열)
     *
     * @return 최종 분석이 완료된 일시(문자열)
     */
    public String selectLastAnalysisCompleteDateTimeStr(String schDe) {
        return dashboardMapper.selectLastAnalysisCompleteDateTimeStr(schDe);
    }

    /**
     * 민감정보 상위 항목 정보를 조회한다.
     *
     * @return 민감정보 상위 항목
     */
    public List<AnalysisStatisticsVO> selectTopSensitiveInformation(String schDe) {
        return dashboardMapper.selectTopSensitiveInformation(schDe);
    }

    /**
     * 파일 분석 정보를 조회한다.
     *
     * @return 파일 분석 정보
     */
    public AnalysisStatisticsVO selectFileAnalysisInformation(String schDe) {
        return dashboardMapper.selectFileAnalysisInformation(schDe);
    }

    /**
     * 메일 외부전송 이력 정보를 조회한다.
     *
     * @return 메일 외부전송 이력 정보
     */
    public List<AnalysisStatisticsVO> selectEmailOutboundHistInformation(String schDe) {
        return dashboardMapper.selectEmailOutboundHistInformation(schDe);
    }

}
