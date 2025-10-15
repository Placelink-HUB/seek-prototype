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
package biz.placelink.seek.analysis.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import biz.placelink.seek.analysis.vo.AnalysisDetectionVO;
import biz.placelink.seek.analysis.vo.AnalysisResultVO;

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
public class AnalysisResultService {

    private final AnalysisResultMapper analysisResultMapper;

    public AnalysisResultService(AnalysisResultMapper analysisResultMapper) {
        this.analysisResultMapper = analysisResultMapper;
    }

    /**
     * 분석 결과 ID(해시 값)가 있는지 확인한다.
     * (동일 데이터에 대한 중복 분석 방지)
     *
     * @param analysisResultId 분석 결과 ID
     * @return 분석 데이터 해시 값의 개수
     */
    public boolean checkAnalysisHashExists(String analysisResultId) {
        return analysisResultMapper.selectAnalysisResultCountByHash(analysisResultId) > 0;
    }

    /**
     * 분석 결과를 조회한다.
     *
     * @param analysisId       분석 ID
     * @param analysisResultId 분석 결과 ID
     * @return 분석 결과
     */
    public AnalysisResultVO selectAnalysisResult(String analysisId, String analysisResultId) {
        return analysisResultMapper.selectAnalysisResult(analysisId, analysisResultId);
    }

    /**
     * 분석 결과를 등록한다.
     *
     * @param analysisResultId    분석 결과 ID
     * @param analysisRawData     분석 원본 데이터
     * @param analyzedContent     분석된 내용
     * @param maxDetectionTypeCcd 최대 검출 타입 공통코드
     * @param totalDetectionCount 총 탐지 개수
     * @return 등록 개수
     */
    public int insertAnalysisResult(String analysisResultId, String analysisRawData, String analyzedContent, String maxDetectionTypeCcd, int totalDetectionCount) {
        return analysisResultMapper.insertAnalysisResult(analysisResultId, analysisRawData, analyzedContent, maxDetectionTypeCcd, totalDetectionCount);
    }

    /**
     * 분석 검출 목록을 등록한다.
     *
     * @param analysisDetectionList 분석 결과 항목 목록
     * @return 등록 개수
     */
    public int insertAnalysisDetectionList(List<AnalysisDetectionVO> analysisDetectionList) {
        return analysisResultMapper.insertAnalysisDetectionList(analysisDetectionList);
    }

    /**
     * 분석 검출 목록을 등록한다.
     *
     * @param analysisResultId 분석 결과 ID
     * @return 분석 검출 목록
     */
    public List<AnalysisDetectionVO> selectAnalysisDetectionList(String analysisResultId) {
        return analysisResultMapper.selectAnalysisDetectionList(analysisResultId);
    }

}
