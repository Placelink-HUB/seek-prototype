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
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.analysis.vo.SchSensitiveInformationVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 09.      s2          최초생성
 * </pre>
 */
@Mapper
public interface SensitiveInformationMapper {

    /**
     * 민감 정보 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 목록
     */
    List<SensitiveInformationVO> selectSensitiveInformationList(SchSensitiveInformationVO searchVO);

    /**
     * 민감 정보 목록 개수를 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 목록 개수
     */
    Integer selectSensitiveInformationListCount(SchSensitiveInformationVO searchVO);

    /**
     * 민감 정보를 조회한다.
     *
     * @param schSensitiveInformationId 민감 정보 ID
     * @return 민감 정보
     */
    SensitiveInformationVO selectSensitiveInformation(@Param("schSensitiveInformationId") String schSensitiveInformationId);

    /**
     * 민감 정보 목록을 등록한다.
     *
     * @param sensitiveInformationList 민감 정보 목록
     * @return 등록 개수
     */
    int insertSensitiveInformationList(@Param("sensitiveInformationList") List<SensitiveInformationVO> sensitiveInformationList);

    /**
     * 민감 정보 매핑 목록을 등록한다.
     *
     * @param analysisResultId         분석 결과 ID
     * @param sensitiveInformationList 민감 정보 목록
     * @return 등록 개수
     */
    int insertSensitiveInformationMappingList(@Param("analysisResultId") String analysisResultId, @Param("sensitiveInformationList") List<SensitiveInformationVO> sensitiveInformationList);

    /**
     * 민감 정보 유형 목록을 등록한다.
     *
     * @param sensitiveInformationTypeList 민감 정보 유형 목록
     * @return 등록 개수
     */
    int insertSensitiveInformationTypeList(@Param("sensitiveInformationTypeList") List<Map<String, String>> sensitiveInformationTypeList);

}
