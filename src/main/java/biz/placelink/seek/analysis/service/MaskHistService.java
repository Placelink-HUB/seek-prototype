package biz.placelink.seek.analysis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 05. 07.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class MaskHistService {

    private final MaskHistMapper maskHistMapper;

    public MaskHistService(MaskHistMapper maskHistMapper) {
        this.maskHistMapper = maskHistMapper;
    }

    /**
     * 마스크 이력 정보를 등록한다.
     *
     * @param requestId       요청 ID
     * @param analysisModeCcd 분석 모드 공통코드
     * @param maskModeCcd     마스크 모드 공통코드
     * @param maskCount       마스크 개수
     * @return 등록 개수
     */
    @Transactional(readOnly = false)
    public int insertMaskHist(String requestId, String analysisModeCcd, String maskModeCcd, int maskCount) {
        return maskHistMapper.insertMaskHist(requestId, analysisModeCcd, maskModeCcd, maskCount);
    }

}
