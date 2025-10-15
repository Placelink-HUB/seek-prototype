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

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.analysis.vo.MaskHistVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 05. 07.      s2          최초생성
 * </pre>
 */
@Mapper
public interface MaskHistMapper {

    /**
     * 마스크 이력 정보를 등록한다.
     *
     * @param requestId       요청 ID
     * @param analysisModeCcd 분석 모드 공통코드
     * @param maskModeCcd     마스크 모드 공통코드
     * @param maskCount       마스크 개수
     * @return 등록 개수
     */
    int insertMaskHist(@Param("requestId") String requestId, @Param("analysisModeCcd") String analysisModeCcd, @Param("maskModeCcd") String maskModeCcd, @Param("maskCount") int maskCount);

    /**
     * 마스크 이력 정보를 조회한다.
     *
     * @param schDe 조회일자
     * @return 마스크 이력 정보
     */
    List<MaskHistVO> selectMaskStatus(@Param("schDe") String schDe);

}
