package biz.placelink.seek.analysis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
@Service
@Transactional(readOnly = true)
public class AnalysisErrorService {

    private final AnalysisErrorMapper analysisErrorMapper;

    public AnalysisErrorService(AnalysisErrorMapper analysisErrorMapper) {
        this.analysisErrorMapper = analysisErrorMapper;
    }

    /**
     * 분석 오류를 등록한다.
     *
     * @param analysisId   분석 ID
     * @param analysisData 분석 데이터
     * @param errorMessage 오류 메시지
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertAnalysisErrorWithNewTransaction(String analysisId, String analysisData, String errorMessage) {
        analysisErrorMapper.updateAnalysisErrorExclusion(analysisId);
        AnalysisErrorVO paramVO = new AnalysisErrorVO();
        paramVO.setAnalysisId(analysisId);
        paramVO.setAnalysisData(analysisData);
        paramVO.setErrorMessage(errorMessage);
        int result = analysisErrorMapper.insertAnalysisError(paramVO);
        if (result > 0) {
            analysisErrorMapper.updateAnalysisStatusToError(analysisId);
        }
    }

}
