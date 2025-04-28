package biz.placelink.seek.analysis.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.analysis.vo.AnalysisResultItemVO;
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
     * @param analysisResultId   분석 결과 ID
     * @param analyzedContent    분석된 내용
     * @param totalDetectedCount 총 탐지 개수
     * @return 등록 개수
     */
    int updateAnalysisResult(@Param("analysisResultId") String analysisResultId, @Param("analyzedContent") String analyzedContent, @Param("totalDetectedCount") Integer totalDetectedCount);

    /**
     * 분석 결과 항목을 등록한다.
     *
     * @param analysisResultItemList 분석 결과 항목 목록
     * @return 등록 개수
     */
    int insertAnalysisResultItems(@Param("analysisResultItemList") List<AnalysisResultItemVO> analysisResultItemList);

}
