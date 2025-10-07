package biz.placelink.seek.analysis.service;

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

}
