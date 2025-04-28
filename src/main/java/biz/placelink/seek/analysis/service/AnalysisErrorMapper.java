package biz.placelink.seek.analysis.service;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.analysis.vo.AnalysisErrorVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 28.      s2          최초생성
 * </pre>
 */
@Mapper
public interface AnalysisErrorMapper {

    /**
     * 분석 오류 정보를 등록한다.
     *
     * @param paramVO 오류 정보
     */
    void insertAnalysisError(AnalysisErrorVO paramVO);

    /**
     * 분석 오류를 배제 처리한다.
     *
     * @param analysisResultId 분석 ID
     * @return 처리 개수
     */
    int updateAnalysisErrorExclusion(@Param("analysisResultId") String analysisResultId);

}
