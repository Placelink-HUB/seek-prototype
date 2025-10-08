package biz.placelink.seek.analysis.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import biz.placelink.seek.analysis.vo.SensitiveInformationUnmaskHistVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 10. 09.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class SensitiveInformationUnmaskHistService {

    private final SensitiveInformationUnmaskHistMapper sensitiveInformationUnmaskHistMapper;

    public SensitiveInformationUnmaskHistService(SensitiveInformationUnmaskHistMapper sensitiveInformationUnmaskHistMapper) {
        this.sensitiveInformationUnmaskHistMapper = sensitiveInformationUnmaskHistMapper;
    }

    /**
     * 민감 정보 언마스크 이력을 등록한다.
     *
     * @param paramVO 민감 정보 언마스크 이력 정보
     * @return 등록 개수
     */
    public int insertSensitiveInformationUnmaskHist(SensitiveInformationUnmaskHistVO paramVO) {
        return sensitiveInformationUnmaskHistMapper.insertSensitiveInformationUnmaskHist(paramVO);
    }

    /**
     * 민감 정보 언마스크 정보를 등록한다.
     *
     * @param requestId                요청 ID
     * @param sensitiveInformationList 민감정보 목록
     * @return 등록 개수
     */
    public int insertSensitiveInformationUnmaskInfo(String requestId, List<SensitiveInformationVO> sensitiveInformationList) {
        return sensitiveInformationUnmaskHistMapper.insertSensitiveInformationUnmaskInfo(requestId, sensitiveInformationList);
    }

}
