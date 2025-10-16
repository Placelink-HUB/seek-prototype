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
package biz.placelink.seek.analysis.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.analysis.vo.AnalysisDetailVO;
import biz.placelink.seek.analysis.vo.AnalysisResultVO;
import biz.placelink.seek.com.vo.SearchVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 29.      s2          최초생성
 * </pre>
 */
@Mapper
public interface AnalysisDetailMapper {

    /**
     * 데이터베이스 분석 정보 내용을 수정한다.
     *
     * @param analysisId 분석 ID
     * @param content    내용
     * @return 수정 개수
     */
    int updateDatabaseAnalysisContent(@Param("analysisId") String analysisId, @Param("content") String content);

    /**
     * 분석 대상 컬럼을 동적으로 수정한다.
     *
     * @param tableName  테이블 명
     * @param columnName 컬럼 명
     * @param oldValue   기존 값
     * @param newValue   변경 값
     * @return 처리 개수
     */
    int updateAnalysisTargetColumnDynamically(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("oldValue") String oldValue, @Param("newValue") String newValue);

    /**
     * 프록시 분석 정보를 등록한다.
     *
     * @param paramVO 프록시 분석 정보
     * @return 등록 개수
     */
    int insertProxyAnalysis(AnalysisDetailVO paramVO);

    /**
     * 파일 분석 정보를 등록한다.
     *
     * @param paramVO 파일 분석 정보
     * @return 등록 개수
     */
    int insertFileAnalysis(AnalysisDetailVO paramVO);

    /**
     * 파일 분석 정보를 수정한다.
     *
     * @param paramVO 파일 분석 정보
     * @return 등록 개수
     */
    int updateFileAnalysis(AnalysisDetailVO paramVO);

    /**
     * 파일 분석 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 파일 분석 목록
     */
    List<AnalysisResultVO> selectFileAnalysisList(SearchVO searchVO);

    /**
     * 파일 분석 목록 개수를 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 파일 분석 목록 개수
     */
    int selectFileAnalysisListCount(SearchVO searchVO);

    /**
     * 파일 분석 목록 현황을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 파일 분석 목록 현황
     */
    AnalysisResultVO selectFileAnalysisListStatus(SearchVO searchVO);

    /**
     * 파일 분석 정보를 조회한다.
     *
     * @param analysisId 분석 ID
     * @return 파일 분석 정보
     */
    AnalysisResultVO selectFileAnalysis(String analysisId);

}
