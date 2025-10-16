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

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
@Mapper
public interface AnalysisErrorMapper {

    /**
     * 분석 오류 정보를 등록한다.
     *
     * @param paramVO 오류 정보
     */
    int insertAnalysisError(AnalysisErrorVO paramVO);

    /**
     * 분석 오류를 배제 처리한다.
     *
     * @param analysisId 분석 ID
     * @return 처리 개수
     */
    int updateAnalysisErrorExclusion(@Param("analysisId") String analysisId);

    /**
     * 분석 상태를 오류 처리한다.
     *
     * @param analysisId 분석 ID
     * @return 처리 개수
     */
    int updateAnalysisStatusToError(@Param("analysisId") String analysisId);

}
