package biz.placelink.seek.analysis.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
@Transactional(readOnly = true)
public class AnalysisResultService {

    private final AnalysisResultMapper analysisResultMapper;

    public AnalysisResultService(AnalysisResultMapper analysisResultMapper) {
        this.analysisResultMapper = analysisResultMapper;
    }

    /**
     * 분석 결과 ID(해시 값)가 있는지 확인한다.
     * (동일 데이터에 대한 중복 분석 방지)
     *
     * @param analysisResultId 분석 결과 ID
     * @return 분석 해시값의 개수
     */
    public boolean checkAnalysisHashExists(String analysisResultId) {
        return analysisResultMapper.selectAnalysisResultCountByHash(analysisResultId) > 0;
    }

    /**
     * 분석 결과를 조회한다.
     *
     * @param analysisId       분석 ID
     * @param analysisResultId 분석 결과 ID
     * @return 분석 결과
     */
    public AnalysisResultVO selectAnalysisResult(String analysisId, String analysisResultId) {
        return analysisResultMapper.selectAnalysisResult(analysisId, analysisResultId);
    }

    /**
     * 분석 결과를 등록한다.
     *
     * @param analysisResultId 분석 결과 ID
     * @return 등록 개수
     */
    public int insertAnalysisResult(String analysisResultId) {
        return analysisResultMapper.insertAnalysisResult(analysisResultId);
    }

    /**
     * 분석 결과를 수정한다.
     *
     * @param analysisResultId   분석 결과 ID
     * @param analyzedContent    분석된 내용
     * @param totalDetectedCount 총 탐지 개수
     * @return 등록 개수
     */
    public int updateAnalysisResult(String analysisResultId, String analyzedContent, Integer totalDetectedCount) {
        return analysisResultMapper.updateAnalysisResult(analysisResultId, analyzedContent, totalDetectedCount);
    }

    /**
     * 분석 결과 항목을 등록한다.
     *
     * @param analysisResultItemList 분석 결과 항목 목록
     * @return 등록 개수
     */
    public int insertAnalysisResultItems(List<AnalysisResultItemVO> analysisResultItemList) {
        return analysisResultMapper.insertAnalysisResultItems(analysisResultItemList);
    }

}
