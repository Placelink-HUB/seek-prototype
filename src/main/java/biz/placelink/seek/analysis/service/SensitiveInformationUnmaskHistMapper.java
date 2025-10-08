package biz.placelink.seek.analysis.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.analysis.vo.SensitiveInformationUnmaskHistVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;
import biz.placelink.seek.com.vo.SearchVO;

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
@Mapper
public interface SensitiveInformationUnmaskHistMapper {

    /**
     * 민감 정보 언마스크 이력을 등록한다.
     *
     * @param paramVO 민감 정보 언마스크 이력 정보
     * @return 등록 개수
     */
    int insertSensitiveInformationUnmaskHist(SensitiveInformationUnmaskHistVO paramVO);

    /**
     * 민감 정보 언마스크 정보를 등록한다.
     *
     * @param requestId                요청 ID
     * @param sensitiveInformationList 민감정보 목록
     * @return 등록 개수
     */
    int insertSensitiveInformationUnmaskInfo(@Param("requestId") String requestId, @Param("sensitiveInformationList") List<SensitiveInformationVO> sensitiveInformationList);

    /**
     * 민감 정보 언마스크 이력 목록 현황을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 언마스크 이력 목록
     */
    SensitiveInformationUnmaskHistVO selectSensitiveInformationUnmaskHistListStatus(SearchVO searchVO);

    /**
     * 민감 정보 언마스크 이력 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 언마스크 이력 목록
     */
    List<SensitiveInformationUnmaskHistVO> selectSensitiveInformationUnmaskHistList(SearchVO searchVO);

    /**
     * 민감 정보 언마스크 이력 목록 개수를 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 언마스크 이력 목록 개수
     */
    int selectSensitiveInformationUnmaskHistListCount(SearchVO searchVO);

}
