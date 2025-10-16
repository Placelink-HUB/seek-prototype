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

import biz.placelink.seek.analysis.vo.SensitiveInformationUnmaskHistVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;
import biz.placelink.seek.com.util.PaginationInfo;
import biz.placelink.seek.com.vo.SearchVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 10. 09.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class SensitiveInformationUnmaskHistService {

    private final SensitiveInformationUnmaskHistMapper sensitiveInformationUnmaskHistMapper;

    public SensitiveInformationUnmaskHistService(SensitiveInformationUnmaskHistMapper sensitiveInformationUnmaskHistMapper) {
        this.sensitiveInformationUnmaskHistMapper = sensitiveInformationUnmaskHistMapper;
    }

    /**
     * 민감 정보 언마스크 이력을 등록한다.
     *
     * @param paramVO 민감 정보 언마스크 이력 정보
     * @return 등록 개수
     */
    public int insertSensitiveInformationUnmaskHist(SensitiveInformationUnmaskHistVO paramVO) {
        return sensitiveInformationUnmaskHistMapper.insertSensitiveInformationUnmaskHist(paramVO);
    }

    /**
     * 민감 정보 언마스크 정보를 등록한다.
     *
     * @param requestId                요청 ID
     * @param sensitiveInformationList 민감정보 목록
     * @return 등록 개수
     */
    public int insertSensitiveInformationUnmaskInfo(String requestId, List<SensitiveInformationVO> sensitiveInformationList) {
        return sensitiveInformationUnmaskHistMapper.insertSensitiveInformationUnmaskInfo(requestId, sensitiveInformationList);
    }

    /**
     * 민감 정보 언마스크 이력 목록 현황을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 언마스크 이력 목록 현황
     */
    public SensitiveInformationUnmaskHistVO selectSensitiveInformationUnmaskHistListStatus(SearchVO searchVO) {
        return sensitiveInformationUnmaskHistMapper.selectSensitiveInformationUnmaskHistListStatus(searchVO);
    }

    /**
     * 민감 정보 언마스크 이력 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 언마스크 이력 목록
     */
    public PaginationInfo selectSensitiveInformationUnmaskHistListWithPagination(SearchVO searchVO) {
        List<SensitiveInformationUnmaskHistVO> list = sensitiveInformationUnmaskHistMapper.selectSensitiveInformationUnmaskHistList(searchVO);
        int count = sensitiveInformationUnmaskHistMapper.selectSensitiveInformationUnmaskHistListCount(searchVO);
        return new PaginationInfo(searchVO, list, count);
    }

}
