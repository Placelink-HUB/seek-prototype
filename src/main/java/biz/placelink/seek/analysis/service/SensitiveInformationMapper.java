package biz.placelink.seek.analysis.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.analysis.vo.SchSensitiveInformationVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 09.      s2          최초생성
 * </pre>
 */
@Mapper
public interface SensitiveInformationMapper {

    /**
     * 민감 정보 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 목록
     */
    List<SensitiveInformationVO> selectSensitiveInformationList(SchSensitiveInformationVO searchVO);

    /**
     * 민감 정보 목록 개수를 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 목록 개수
     */
    Integer selectSensitiveInformationListCount(SchSensitiveInformationVO searchVO);

    /**
     * 민감 정보를 조회한다.
     *
     * @param schSensitiveInformationId 민감 정보 ID
     * @return 민감 정보
     */
    SensitiveInformationVO selectSensitiveInformation(@Param("schSensitiveInformationId") String schSensitiveInformationId);

    /**
     * 민감 정보 목록을 등록한다.
     *
     * @param sensitiveInformationList 민감 정보 목록
     * @return 등록 개수
     */
    int insertSensitiveInformationList(@Param("sensitiveInformationList") List<SensitiveInformationVO> sensitiveInformationList);

    /**
     * 민감 정보 유형 목록을 등록한다.
     *
     * @param sensitiveInformationTypeList 민감 정보 유형 목록
     * @return 등록 개수
     */
    int insertSensitiveInformationTypeList(@Param("sensitiveInformationTypeList") List<Map<String, String>> sensitiveInformationTypeList);

}
