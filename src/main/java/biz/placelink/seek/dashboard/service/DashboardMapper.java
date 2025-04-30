package biz.placelink.seek.dashboard.service;

import biz.placelink.seek.analysis.vo.AnalysisDetectionVO;
import biz.placelink.seek.dashboard.vo.AnalysisStatisticsVO;
import biz.placelink.seek.dashboard.vo.SchAnalysisStatisticsVO;
import biz.placelink.seek.sample.vo.ArticleVO;
import biz.placelink.seek.sample.vo.SchArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     *  민감정보 탐지 횟수를 조회한다.
     *
     * @return 민감정보 탐지 횟수
     */
    AnalysisStatisticsVO selectAnalysisResultCount(SchAnalysisStatisticsVO searchVO);
}
