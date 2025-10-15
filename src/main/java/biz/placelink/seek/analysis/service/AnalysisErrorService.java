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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import biz.placelink.seek.analysis.vo.AnalysisErrorVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 28.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AnalysisErrorService {

    private final AnalysisErrorMapper analysisErrorMapper;

    public AnalysisErrorService(AnalysisErrorMapper analysisErrorMapper) {
        this.analysisErrorMapper = analysisErrorMapper;
    }

    /**
     * 분석 오류를 등록한다.
     *
     * @param analysisId   분석 ID
     * @param analysisData 분석 데이터
     * @param errorMessage 오류 메시지
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertAnalysisErrorWithNewTransaction(String analysisId, String analysisData, String errorMessage) {
        analysisErrorMapper.updateAnalysisErrorExclusion(analysisId);
        AnalysisErrorVO paramVO = new AnalysisErrorVO();
        paramVO.setAnalysisId(analysisId);
        paramVO.setAnalysisData(analysisData);
        paramVO.setErrorMessage(errorMessage);
        int result = analysisErrorMapper.insertAnalysisError(paramVO);
        if (result > 0) {
            analysisErrorMapper.updateAnalysisStatusToError(analysisId);
        }
    }

}
