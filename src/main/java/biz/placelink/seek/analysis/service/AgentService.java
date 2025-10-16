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

import biz.placelink.seek.analysis.vo.AgentStatusVO;
import biz.placelink.seek.analysis.vo.AgentVO;
import biz.placelink.seek.com.constants.Constants;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 10. 08.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AgentService {

    private final AgentMapper agentMapper;

    public AgentService(AgentMapper agentMapper) {
        this.agentMapper = agentMapper;
    }

    /**
     * 에이전트 상태확인 이력을 등록한다.
     *
     * @param paramVO 에이전트 정보
     * @return 수정 개수
     */
    @Transactional
    public int insertAgentHeartBeatHist(AgentVO paramVO) {
        if (paramVO != null) {
            paramVO.setCreateUid(Constants.SYSTEM_UID);
        }
        int result = agentMapper.insertAgentHeartBeatHist(paramVO);
        if (result > 0) {
            agentMapper.insertAgent(paramVO);
        }
        return result;
    }

    /**
     * 에이전트 상태 목록을 조회한다.
     *
     * @return 에이전트 상태 목록
     */
    public List<AgentVO> selectAgentStatusList() {
        return agentMapper.selectAgentStatusList();
    }

    /**
     * 에이전트 상태 목록 현황을 조회한다.
     *
     * @return 에이전트 상태 목록 현황
     */
    public AgentStatusVO selectAgentStatusListStatus() {
        return agentMapper.selectAgentStatusListStatus();
    }

}
