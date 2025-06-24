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
 *  2025. 06. 24.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class EmailOutboundHistService {

    private final EmailOutboundHistMapper emailOutboundHistMapper;

    public EmailOutboundHistService(EmailOutboundHistMapper emailOutboundHistMapper) {
        this.emailOutboundHistMapper = emailOutboundHistMapper;
    }

    /**
     * 메일 외부전송 이력 정보를 등록한다.
     *
     * @param outboundStatusCcd 외부전송 상태 공통코드
     * @param senderEmail       발신자 이메일
     * @param analysisId        분석 ID
     * @return 등록 개수
     */
    public int insertEmailOutboundHist(String outboundStatusCcd, String senderEmail, String analysisId) {
        return emailOutboundHistMapper.insertEmailOutboundHist(outboundStatusCcd, senderEmail, analysisId);
    }

}
