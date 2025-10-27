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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import biz.placelink.seek.analysis.vo.AnalysisDetailVO;
import biz.placelink.seek.analysis.vo.AnalysisResultVO;
import biz.placelink.seek.com.util.PaginationInfo;
import biz.placelink.seek.com.vo.SearchVO;
import kr.s2.ext.util.S2Util;

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
@Service
@Transactional(readOnly = true)
public class AnalysisDetailService {

    private final AnalysisDetailMapper analysisDetailMapper;

    public AnalysisDetailService(AnalysisDetailMapper analysisDetailMapper) {
        this.analysisDetailMapper = analysisDetailMapper;
    }

    /**
     * 데이터베이스 작업 정보 내용을 수정한다.
     *
     * @param analysisId 작업 이력 ID
     * @param content    내용
     * @return 수정 개수
     */
    public int updateDatabaseAnalysisContent(String analysisId, String content) {
        return analysisDetailMapper.updateDatabaseAnalysisContent(analysisId, content);
    }

    /**
     * 분석 대상 컬럼을 동적으로 수정한다.
     *
     * @param tableName  테이블 명
     * @param columnName 컬럼 명
     * @param oldValue   기존 값
     * @param newValue   변경 값
     * @return 처리 개수
     */
    public int updateAnalysisTargetColumnDynamically(String tableName, String columnName, String oldValue, String newValue) {
        int result = 0;
        if (S2Util.isNotEmpty(tableName) && S2Util.isNotEmpty(columnName) &&
                S2Util.isNotEmpty(oldValue) && S2Util.isNotEmpty(newValue) &&
                // 테이블명은 대소문자 구분없이 tb_로 시작하고, 컬럼명은 영문자, 숫자, 언더바(_)로만 구성되어야 한다.
                tableName.matches("(?i)^tb_[a-zA-Z0-9_]+") && columnName.matches("[a-zA-Z0-9_]+")) {
            result = analysisDetailMapper.updateAnalysisTargetColumnDynamically(tableName, columnName, oldValue, newValue);
        }
        return result;
    }

    /**
     * 프록시 분석 정보를 등록한다.
     *
     * @param paramVO 프록시 작업 정보
     * @return 등록 개수
     */
    public int insertProxyAnalysis(AnalysisDetailVO paramVO) {
        return analysisDetailMapper.insertProxyAnalysis(paramVO);
    }

    /**
     * 파일 분석 정보를 등록한다.
     *
     * @param paramVO 파일 분석 정보
     * @return 등록 개수
     */
    public int insertFileAnalysis(AnalysisDetailVO paramVO) {
        return analysisDetailMapper.insertFileAnalysis(paramVO);
    }

    /**
     * 파일 분석 정보를 수정한다.
     *
     * @param paramVO 파일 분석 정보
     * @return 등록 개수
     */
    public int updateFileAnalysis(AnalysisDetailVO paramVO) {
        return analysisDetailMapper.updateFileAnalysis(paramVO);
    }

    /**
     * 파일 분석 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 파일 분석 목록
     */
    public List<AnalysisResultVO> selectFileAnalysisList(SearchVO searchVO) {
        return analysisDetailMapper.selectFileAnalysisList(searchVO);
    }

    /**
     * 페이지 정보가 포함된 파일 분석 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 파일 분석 목록
     */
    public PaginationInfo selectFileAnalysisListWithPagination(SearchVO searchVO) {
        List<AnalysisResultVO> list = analysisDetailMapper.selectFileAnalysisList(searchVO);
        int count = analysisDetailMapper.selectFileAnalysisListCount(searchVO);
        return new PaginationInfo(searchVO, list, count);
    }

    /**
     * 파일 분석 목록 현황을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 파일 분석 목록 현황
     */
    public AnalysisResultVO selectFileAnalysisListStatus(SearchVO searchVO) {
        return analysisDetailMapper.selectFileAnalysisListStatus(searchVO);
    }

    /**
     * 파일 분석 목록을 조회한다.
     *
     * @param analysisId 분석 ID
     * @return 파일 분석 목록
     */
    public AnalysisResultVO selectFileAnalysis(String analysisId) {
        return analysisDetailMapper.selectFileAnalysis(analysisId);
    }

}
