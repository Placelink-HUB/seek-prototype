package biz.placelink.seek.analysis.service;

import biz.placelink.seek.analysis.vo.AnalysisVO;
import biz.placelink.seek.analysis.vo.OperationDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
@Mapper
public interface OperationMapper {

    /**
     * 실행하려는 작업 이력 목록을 조회한다.
     *
     * @param maxCount 분석기 서버에 요청할 수 있는 최대(스레드) 수
     * @return 작업 이력 목록
     */
    public List<OperationDetailVO> selectOperationHistListToExecuted(@Param("maxCount") int maxCount);

    /**
     * 프록시 작업 정보를 등록한다.
     *
     * @param paramVO 프록시 작업 정보
     * @return 등록 개수
     */
    int insertProxyOperation(OperationDetailVO paramVO);

    /**
     * 데이터베이스 작업 정보 내용을 수정한다.
     *
     * @param operationHistId 작업 이력 ID
     * @param content         내용
     * @return 수정 개수
     */
    int updateDatabaseOperationContent(@Param("operationHistId") String operationHistId, @Param("content") String content);

}