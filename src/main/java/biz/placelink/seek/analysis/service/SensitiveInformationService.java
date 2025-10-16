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
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
@Transactional(readOnly = true)
public class SensitiveInformationService {

    private final SensitiveInformationMapper sensitiveInformationMapper;

    public SensitiveInformationService(SensitiveInformationMapper sensitiveInformationMapper) {
        this.sensitiveInformationMapper = sensitiveInformationMapper;
    }

    /**
     * 민감 정보 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 목록
     */
    public List<SensitiveInformationVO> selectSensitiveInformationList(SchSensitiveInformationVO searchVO) {
        return sensitiveInformationMapper.selectSensitiveInformationList(searchVO);
    }

    /**
     * 민감 정보 목록 개수를 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 목록 개수
     */
    public Integer selectSensitiveInformationListCount(SchSensitiveInformationVO searchVO) {
        return sensitiveInformationMapper.selectSensitiveInformationListCount(searchVO);
    }

    /**
     * 민감 정보를 조회한다.
     *
     * @param schSensitiveInformationId 민감 정보 ID
     * @return 민감 정보
     */
    public SensitiveInformationVO selectSensitiveInformation(String schSensitiveInformationId) {
        return sensitiveInformationMapper.selectSensitiveInformation(schSensitiveInformationId);
    }

    /**
     * 민감 정보 목록을 등록한다.
     *
     * @param sensitiveInformationList 민감 정보 목록
     * @return 등록 개수
     */
    public int insertSensitiveInformationList(List<SensitiveInformationVO> sensitiveInformationList) {
        return sensitiveInformationMapper.insertSensitiveInformationList(sensitiveInformationList);
    }

    /**
     * 민감 정보 매핑 목록을 등록한다.
     *
     * @param analysisResultId         분석 결과 ID
     * @param sensitiveInformationList 민감 정보 목록
     * @return 등록 개수
     */
    public int insertSensitiveInformationMappingList(String analysisResultId, List<SensitiveInformationVO> sensitiveInformationList) {
        return sensitiveInformationMapper.insertSensitiveInformationMappingList(analysisResultId, sensitiveInformationList);
    }

    /**
     * 민감 정보 유형 목록을 등록한다.
     *
     * @param sensitiveInformationTypeList 민감 정보 유형 목록
     * @return 등록 개수
     */
    public int insertSensitiveInformationTypeList(List<Map<String, String>> sensitiveInformationTypeList) {
        return sensitiveInformationMapper.insertSensitiveInformationTypeList(sensitiveInformationTypeList);
    }

}
