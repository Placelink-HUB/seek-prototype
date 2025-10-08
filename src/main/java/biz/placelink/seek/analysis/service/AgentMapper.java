package biz.placelink.seek.analysis.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

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
    AgentVO selectAgentStatusListStatus();

}
