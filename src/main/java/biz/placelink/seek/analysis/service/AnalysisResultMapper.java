package biz.placelink.seek.analysis.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.analysis.vo.AnalysisDetectionVO;
import biz.placelink.seek.analysis.vo.AnalysisResultVO;

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
public interface AnalysisResultMapper {

    /**
     * 해시값으로 분석 결과 ID 를 조회한다.
     *
     * @param analysisResultId 분석 결과 ID (해시 값)
     * @return 분석 결과 ID
     */
    int selectAnalysisResultCountByHash(@Param("analysisResultId") String analysisResultId);

    /**
     * 분석 결과를 조회한다.
     *
     * @param analysisId       분석 ID
     * @param analysisResultId 분석 결과 ID
     * @return 분석 결과
     */
    AnalysisResultVO selectAnalysisResult(@Param("analysisId") String analysisId, @Param("analysisResultId") String analysisResultId);

    /**
     * 분석 결과를 등록한다.
     *
     * @param analysisResultId 분석 결과 ID
     * @return 등록 개수
     */
    int insertAnalysisResult(@Param("analysisResultId") String analysisResultId);

    /**
     * 분석 결과를 수정한다.
     *
     * @param analysisResultId    분석 결과 ID
     * @param analysisRawData     분석 원본 데이터
     * @param analyzedContent     분석된 내용
     * @param totalDetectionCount 총 탐지 개수
     * @return 등록 개수
     */
    int updateAnalysisResult(@Param("analysisResultId") String analysisResultId, @Param("analysisRawData") String analysisRawData, @Param("analyzedContent") String analyzedContent, @Param("totalDetectionCount") Integer totalDetectionCount);

    /**
     * 분석 검출 목록을 등록한다.
     *
     * @param analysisDetectionList 분석 결과 항목 목록
     * @return 등록 개수
     */
    int insertAnalysisDetectionList(@Param("analysisDetectionList") List<AnalysisDetectionVO> analysisDetectionList);

    /**
     * 분석 검출 목록을 등록한다.
     *
     * @param analysisResultId 분석 결과 ID
     * @return 분석 검출 목록
     */
    List<AnalysisDetectionVO> selectAnalysisDetectionList(@Param("analysisResultId") String analysisResultId);

}
