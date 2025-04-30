package biz.placelink.seek.dashboard.service;

import biz.placelink.seek.analysis.vo.AnalysisDetectionVO;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.PaginationInfo;
import biz.placelink.seek.dashboard.vo.AnalysisStatisticsVO;
import biz.placelink.seek.dashboard.vo.SchAnalysisStatisticsVO;
import biz.placelink.seek.sample.vo.ArticleVO;
import biz.placelink.seek.sample.vo.SchArticleVO;
import biz.placelink.seek.system.file.service.FileService;
import biz.placelink.seek.system.file.vo.FileDetailVO;
import kr.s2.ext.util.S2Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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
    public AnalysisStatisticsVO selectAnalysisStatistics(SchAnalysisStatisticsVO searchVO) {
        AnalysisStatisticsVO analysisStatisticsVO = new AnalysisStatisticsVO();

        // 분석
        AnalysisStatisticsVO result1 = dashboardMapper.selectAnalysisCount(searchVO);
        analysisStatisticsVO.setTotalAnalysisCount(result1.getTotalAnalysisCount());
        analysisStatisticsVO.setAnalysisCount(result1.getAnalysisCount());

        // 탐지
        AnalysisStatisticsVO result2 = dashboardMapper.selectAnalysisResultCount(searchVO);
        analysisStatisticsVO.setTotalDetectionCount(result2.getTotalDetectionCount());
        analysisStatisticsVO.setDetectionCount(result2.getDetectionCount());

        return analysisStatisticsVO;
    }
}