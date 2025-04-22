package biz.placelink.seek.analysis.service;

import biz.placelink.seek.analysis.vo.OperationDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 22.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class OperationService {

    private static final Logger logger = LoggerFactory.getLogger(OperationService.class);

    private final OperationMapper operationMapper;

    public OperationService(OperationMapper operationMapper) {
        this.operationMapper = operationMapper;
    }

    /**
     * 실행하려는 작업 이력 목록을 조회한다.
     *
     * @param maxCount 분석기 서버에 요청할 수 있는 최대(스레드) 수
     * @return 작업 이력 목록
     */
    public List<OperationDetailVO> selectOperationHistListToExecuted(int maxCount) {
        return operationMapper.selectOperationHistListToExecuted(maxCount);
    }

    /**
     * 프록시 작업 정보를 등록한다.
     *
     * @param paramVO 프록시 작업 정보
     * @return 등록 개수
     */
    public int insertProxyOperation(OperationDetailVO paramVO) {
        return operationMapper.insertProxyOperation(paramVO);
    }

    /**
     * 데이터베이스 작업 정보 내용을 수정한다.
     *
     * @param operationHistId 작업 이력 ID
     * @param content         내용
     * @return 수정 개수
     */
    public int updateDatabaseOperationContent(String operationHistId, String content) {
        return operationMapper.updateDatabaseOperationContent(operationHistId, content);
    }

}