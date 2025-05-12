package biz.placelink.seek.dashboard.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
@Mapper
public interface DashboardMapper {

    /**
     * 전체 데이터 분석 수를 조회한다.
     *
     * @return 전체 데이터 분석 수
     */
    AnalysisStatisticsVO selectAnalysisCount(@Param("schDe") String schDe);

    /**
     * 민감정보 탐지 횟수를 조회한다.
     *
     * @return 민감정보 탐지 횟수
     */
    AnalysisStatisticsVO selectAnalysisResultCount(@Param("schDe") String schDe);

    /**
     * 탐지 현황 정보를 조회한다.
     *
     * @return 탐지 현황
     */
    AnalysisStatisticsVO selectDetectionStatistics(@Param("schDe") String schDe);

    /**
     * 실시간 분석 현황 정보를 조회한다.
     *
     * @return 실시간 분석 현황
     */
    List<AnalysisStatisticsVO> selectRealtimeAnalysisCount(@Param("schDe") String schDe);

    /**
     * 민감정보 상위 항목 정보를 조회한다.
     *
     * @return 민감정보 상위 항목
     */
    List<AnalysisStatisticsVO> selectTopSensitiveInformation(@Param("schDe") String schDe);

}
