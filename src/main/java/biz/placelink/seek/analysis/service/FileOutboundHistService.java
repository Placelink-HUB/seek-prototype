package biz.placelink.seek.analysis.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import biz.placelink.seek.analysis.vo.FileOutboundHistVO;
import biz.placelink.seek.analysis.vo.SchFileOutboundHistVO;
import biz.placelink.seek.com.util.PaginationInfo;

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
public class FileOutboundHistService {

    private final FileOutboundHistMapper fileOutboundHistMapper;

    public FileOutboundHistService(FileOutboundHistMapper fileOutboundHistMapper) {
        this.fileOutboundHistMapper = fileOutboundHistMapper;
    }

    /**
     * 파일 외부전송 이력 정보를 등록한다.
     *
     * @param paramVO 파일 외부전송 정보
     * @return 등록 개수
     */
    public int insertFileOutboundHist(FileOutboundHistVO paramVO) {
        return fileOutboundHistMapper.insertFileOutboundHist(paramVO);
    }

    /**
     * 파일 외부전송 이력 목록 현황을 조회한다.
     *
     * @param searchVO 조회조건
     * @return 파일 외부전송 이력 목록 현황
     */
    public FileOutboundHistVO selectFileOutboundHistListStatus(SchFileOutboundHistVO searchVO) {
        return fileOutboundHistMapper.selectFileOutboundHistListStatus(searchVO);
    }

    /**
     * 파일 외부전송 이력 목록을 조회한다.
     *
     * @param searchVO 조회조건
     * @return 파일 외부전송 이력 목록
     */
    public PaginationInfo selectFileOutboundHistListWithPagination(SchFileOutboundHistVO searchVO) {
        List<FileOutboundHistVO> list = fileOutboundHistMapper.selectFileOutboundHistList(searchVO);
        int count = fileOutboundHistMapper.selectFileOutboundHistListCount(searchVO);
        return new PaginationInfo(searchVO, list, count);
    }

}
