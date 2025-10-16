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
 *
 * =========================================================================
 *
 * 상업적 이용 또는 AGPL-3.0의 공개 의무를 면제받기
 * 위해서는, placelink로부터 별도의 상업용 라이선스(Commercial License)를 구매해야 합니다.
 * For commercial use or to obtain an exemption from the AGPL-3.0 license
 * requirements, please purchase a commercial license from placelink.
 * *** 문의처: help@placelink.shop (README.md 참조)
 */
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
     * @param schDe 조회 일자
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
     * @param schDe 조회 일자
     * @return 탐지 현황
     */
    public AnalysisStatisticsVO selectDetectionStatistics(String schDe) {
        return dashboardMapper.selectDetectionStatistics(schDe);
    }

    /**
     * 실시간 분석 정보를 조회한다.
     *
     * @param schDe 조회 일자
     * @return 실시간 분석 현황
     */
    public List<AnalysisStatisticsVO> selectRealtimeAnalysisCount(String schDe) {
        return dashboardMapper.selectRealtimeAnalysisCount(schDe);
    }

    /**
     * 최종 분석이 완료된 일시를 조회한다.(문자열)
     *
     * @param schDe 조회 일자
     * @return 최종 분석이 완료된 일시(문자열)
     */
    public String selectLastAnalysisCompleteDateTimeStr(String schDe) {
        return dashboardMapper.selectLastAnalysisCompleteDateTimeStr(schDe);
    }

    /**
     * 민감정보 상위 항목 정보를 조회한다.
     *
     * @param schDe 조회 일자
     * @return 민감정보 상위 항목
     */
    public List<AnalysisStatisticsVO> selectTopSensitiveInformation(String schDe) {
        return dashboardMapper.selectTopSensitiveInformation(schDe);
    }

    /**
     * 파일 분석 정보를 조회한다.
     *
     * @param schDe 조회 일자
     * @return 파일 분석 정보
     */
    public AnalysisStatisticsVO selectFileAnalysisInformation(String schDe) {
        return dashboardMapper.selectFileAnalysisInformation(schDe);
    }

    /**
     * 파일 외부전송 이력 상태 정보를 조회한다.
     *
     * @param schDe 조회 일자
     * @return 파일 외부전송 이력 상태 정보
     */
    public List<AnalysisStatisticsVO> selectFileOutboundHistStatusInformation(String schDe) {
        return dashboardMapper.selectFileOutboundHistStatusInformation(schDe);
    }

    /**
     * 파일 외부전송 이력 채널 정보를 조회한다.
     *
     * @param schDe                조회 일자
     * @param schOutboundStatusCcd 외부전송 상태 공통코드
     * @return 파일 외부전송 이력 채널 정보
     */
    public List<AnalysisStatisticsVO> selectFileOutboundHistChannelInformation(String schDe, String schOutboundStatusCcd) {
        return dashboardMapper.selectFileOutboundHistChannelInformation(schDe, schOutboundStatusCcd);
    }

}
