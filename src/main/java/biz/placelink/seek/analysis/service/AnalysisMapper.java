package biz.placelink.seek.analysis.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.analysis.vo.AnalysisDetailVO;
import biz.placelink.seek.analysis.vo.AnalysisVO;

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
public interface AnalysisMapper {

    /**
     * 실행하려는 분석 목록을 조회한다.
     *
     * @param maxCount 분석 서버에 요청할 수 있는 최대(스레드) 수
     * @return 작업 이력 목록
     */
    public List<AnalysisDetailVO> selectAnalysisHistListToExecuted(@Param("maxCount") int maxCount);

    /**
     * 실행중인 분석 정보 목록을 조회한다.
     *
     * @return 분석 정보 목록
     */
    List<AnalysisDetailVO> selectProcessingAnalysisList();

    /**
     * 분석 정보를 등록한다.
     *
     * @param paramVO 분석 정보
     * @return 등록 개수
     */
    int insertAnalysis(AnalysisVO paramVO);

    /**
     * 분석 정보를 수정한다.
     *
     * @param paramVO 수정할 분석 정보
     * @return 수정 개수
     */
    int updateAnalysis(AnalysisVO paramVO);

    /**
     * 데이터베이스 분석 정보 내용을 수정한다.
     *
     * @param analysisId 분석 ID
     * @param content    내용
     * @return 수정 개수
     */
    int updateDatabaseAnalysisContent(@Param("analysisId") String analysisId, @Param("content") String content);

    /**
     * 분석 대상 컬럼을 동적으로 수정한다.
     *
     * @param tableName  테이블 명
     * @param columnName 컬럼 명
     * @param oldValue   기존 값
     * @param newValue   변경 값
     * @return 처리 개수
     */
    int updateAnalysisTargetColumnDynamically(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("oldValue") String oldValue, @Param("newValue") String newValue);

    /**
     * 프록시 분석 정보를 등록한다.
     *
     * @param paramVO 프록시 분석 정보
     * @return 등록 개수
     */
    int insertProxyAnalysis(AnalysisDetailVO paramVO);

    /**
     * 실행 시간이 초과된 분석을 오류 처리한다.
     *
     * @param maxMinutes 최대 허용 시간(분)
     */
    void updateAnalysisTimeoutError(@Param("maxMinutes") int maxMinutes);

}
