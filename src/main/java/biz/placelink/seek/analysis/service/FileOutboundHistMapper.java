package biz.placelink.seek.analysis.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import biz.placelink.seek.analysis.vo.FileOutboundHistVO;
import biz.placelink.seek.analysis.vo.SchFileOutboundHistVO;

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
@Mapper
public interface FileOutboundHistMapper {

    /**
     * 파일 외부전송 이력 정보를 등록한다.
     *
     * @param paramVO 파일 외부전송 정보
     * @return 등록 개수
     */
    int insertFileOutboundHist(FileOutboundHistVO paramVO);

    /**
     * 파일 외부전송 차단 이력 목록 현황을 조회한다.
     *
     * @param searchVO 조회조건
     * @return 파일 외부전송 차단 이력 목록 현황
     */
    FileOutboundHistVO selectFileOutboundBlockingHistListStatus(SchFileOutboundHistVO searchVO);

    /**
     * 파일 외부전송 차단 이력 목록을 조회한다.
     *
     * @param searchVO 조회조건
     * @return 파일 외부전송 차단 이력 목록
     */
    List<FileOutboundHistVO> selectFileOutboundBlockingHistList(SchFileOutboundHistVO searchVO);

    /**
     * 파일 외부전송 차단 이력 목록 개수를 조회한다.
     *
     * @param searchVO 조회조건
     * @return 파일 외부전송 차단 이력 목록 개수
     */
    int selectFileOutboundBlockingHistListCount(SchFileOutboundHistVO searchVO);

}
