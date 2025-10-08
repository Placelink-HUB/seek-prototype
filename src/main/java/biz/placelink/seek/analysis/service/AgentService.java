package biz.placelink.seek.analysis.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public int insertAgentHeartBeatHist(AgentVO paramVO) {
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

}
