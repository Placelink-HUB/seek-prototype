package biz.placelink.seek.dashboard.service;

import org.apache.ibatis.annotations.Mapper;

import biz.placelink.seek.dashboard.vo.AnalysisStatisticsVO;
import biz.placelink.seek.dashboard.vo.SchAnalysisStatisticsVO;

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
@Mapper
public interface DashboardMapper {

    /**
     * 전체 데이터 분석 수를 조회한다.
     *
     * @return 전체 데이터 분석 수
     */
    AnalysisStatisticsVO selectAnalysisCount(SchAnalysisStatisticsVO searchVO);

    /**
     * 민감정보 탐지 횟수를 조회한다.
     *
     * @return 민감정보 탐지 횟수
     */
    AnalysisStatisticsVO selectAnalysisResultCount(SchAnalysisStatisticsVO searchVO);

    /**
     * 탐지 현황 정보를 조회한다.
     *
     * @return 탐지 현황
     */
    AnalysisStatisticsVO selectDetectionStatistics(SchAnalysisStatisticsVO searchVO);

    /**
     * 민감정보 상위 항목 정보를 조회한다.
     *
     * @return 민감정보 상위 항목
     */
    List<AnalysisStatisticsVO> selectTopSensitiveInformation(SchAnalysisStatisticsVO searchVO);

}
