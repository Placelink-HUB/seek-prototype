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

import biz.placelink.seek.analysis.vo.AgentStatusVO;
import biz.placelink.seek.analysis.vo.AgentVO;

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
@Mapper
public interface AgentMapper {

    /**
     * 에이전트 정보를 등록한다.
     *
     * @param paramVO 에이전트 정보
     * @return 등록 개수
     */
    int insertAgent(AgentVO paramVO);

    /**
     * 에이전트 상태확인 이력을 등록한다.
     *
     * @param paramVO 에이전트 정보
     * @return 등록 개수
     */
    int insertAgentHeartBeatHist(AgentVO paramVO);

    /**
     * 에이전트 상태 목록을 조회한다.
     *
     * @return 에이전트 상태 목록
     */
    List<AgentVO> selectAgentStatusList();

    /**
     * 에이전트 상태 목록 현황을 조회한다.
     *
     * @return 에이전트 상태 목록 현황
     */
    AgentStatusVO selectAgentStatusListStatus();

}
