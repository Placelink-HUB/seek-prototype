package biz.placelink.seek.analysis.service;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
@Mapper
public interface MaskHistMapper {

    /**
     * 마스크 이력 정보를 등록한다.
     *
     * @param requestId       요청 ID
     * @param analysisModeCcd 분석 모드 공통코드
     * @param maskModeCcd     마스크 모드 공통코드
     * @param maskCount       마스크 개수
     * @return 등록 개수
     */
    int insertMaskHist(@Param("requestId") String requestId, @Param("analysisModeCcd") String analysisModeCcd, @Param("maskModeCcd") String maskModeCcd, @Param("maskCount") int maskCount);

}
