package biz.placelink.seek.analysis.service;

import biz.placelink.seek.analysis.vo.AnalysisResultItemVO;
import biz.placelink.seek.analysis.vo.AnalysisVO;
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
 *  2025. 04. 07.      s2          최초생성
 * </pre>
 */
@Mapper
public interface AnalysisMapper {

    /**
     * 분석 정보를 수정한다.
     *
     * @param paramVO 수정할 분석 정보
     */
    int updateAnalysis(AnalysisVO paramVO);

    /**
     * 실행 시간이 초과된 분석을 오류 처리한다.
     *
     * @param maxMinutes 최대 허용 시간(분)
     */
    void updateAnalysisTimeoutError(@Param("maxMinutes") int maxMinutes);

    /**
     * 실행하려는 분석 정보 목록을 조회한다.
     *
     * @param maxCount 분석기 서버에 요청할 수 있는 최대(스레드) 수
     * @return 분석 정보 목록
     */
    List<AnalysisVO> selectAnalysisListToExecuted(@Param("maxCount") int maxCount);

    /**
     * 실행중인 분석 정보 목록을 조회한다.
     *
     * @return 분석 정보 목록
     */
    List<AnalysisVO> selectProcessingAnalysisList();

    /**
     * 분석 정보 상태를 수정한다.
     *
     * @param analysisId        분석 ID
     * @param analysisStatusCcd 분석 상태 공통코드
     * @return 등록 개수
     */
    int updateAnalysisStatus(@Param("analysisId") String analysisId, @Param("analysisStatusCcd") String analysisStatusCcd);

    /**
     * 분석 결과 항목을 등록한다.
     *
     * @param analysisResultItemList 분석 결과 항목 목록
     * @return 등록 개수
     */
    int insertAnalysisResultItems(@Param("analysisResultItemList") List<AnalysisResultItemVO> analysisResultItemList);

}