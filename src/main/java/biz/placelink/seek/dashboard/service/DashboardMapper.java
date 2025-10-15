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
     * @param schDe 조회 일자
     * @return 실시간 분석 현황
     */
    List<AnalysisStatisticsVO> selectRealtimeAnalysisCount(@Param("schDe") String schDe);

    /**
     * 최종 분석이 완료된 일시를 조회한다.(문자열)
     *
     * @param schDe 조회 일자
     * @return 최종 분석이 완료된 일시(문자열)
     */
    String selectLastAnalysisCompleteDateTimeStr(@Param("schDe") String schDe);

    /**
     * 민감정보 상위 항목 정보를 조회한다.
     *
     * @param schDe 조회 일자
     * @return 민감정보 상위 항목
     */
    List<AnalysisStatisticsVO> selectTopSensitiveInformation(@Param("schDe") String schDe);

    /**
     * 파일 분석 정보를 조회한다.
     *
     * @param schDe 조회 일자
     * @return 파일 분석 정보
     */
    AnalysisStatisticsVO selectFileAnalysisInformation(@Param("schDe") String schDe);

    /**
     * 파일 외부전송 이력 상태 정보를 조회한다.
     *
     * @param schDe 조회 일자
     * @return 파일 외부전송 이력 상태 정보
     */
    List<AnalysisStatisticsVO> selectFileOutboundHistStatusInformation(@Param("schDe") String schDe);

    /**
     * 파일 외부전송 이력 채널 정보를 조회한다.
     *
     * @return 파일 외부전송 채널 상태 정보
     */
    /**
     * 파일 외부전송 이력 채널 정보를 조회한다.
     *
     * @param schDe                조회 일자
     * @param schOutboundStatusCcd 외부전송 상태 공통코드
     * @return
     */
    List<AnalysisStatisticsVO> selectFileOutboundHistChannelInformation(@Param("schDe") String schDe, @Param("schOutboundStatusCcd") String schOutboundStatusCcd);

}
