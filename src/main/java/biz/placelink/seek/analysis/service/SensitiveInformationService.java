package biz.placelink.seek.analysis.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
@Transactional(readOnly = true)
public class SensitiveInformationService {

    private final SensitiveInformationMapper sensitiveInformationMapper;

    public SensitiveInformationService(SensitiveInformationMapper sensitiveInformationMapper) {
        this.sensitiveInformationMapper = sensitiveInformationMapper;
    }

    /**
     * 민감 정보 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 목록
     */
    public List<SensitiveInformationVO> selectSensitiveInformationList(SchSensitiveInformationVO searchVO) {
        return sensitiveInformationMapper.selectSensitiveInformationList(searchVO);
    }

    /**
     * 민감 정보 목록 개수를 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 민감 정보 목록 개수
     */
    public Integer selectSensitiveInformationListCount(SchSensitiveInformationVO searchVO) {
        return sensitiveInformationMapper.selectSensitiveInformationListCount(searchVO);
    }

    /**
     * 민감 정보를 조회한다.
     *
     * @param schSensitiveInformationId 민감 정보 ID
     * @return 민감 정보
     */
    public SensitiveInformationVO selectSensitiveInformation(String schSensitiveInformationId) {
        return sensitiveInformationMapper.selectSensitiveInformation(schSensitiveInformationId);
    }

    /**
     * 민감 정보 목록을 등록한다.
     *
     * @param sensitiveInformationList 민감 정보 목록
     * @return 등록 개수
     */
    public int insertSensitiveInformationList(List<SensitiveInformationVO> sensitiveInformationList) {
        return sensitiveInformationMapper.insertSensitiveInformationList(sensitiveInformationList);
    }

    /**
     * 민감 정보 매핑 목록을 등록한다.
     *
     * @param analysisResultId         분석 결과 ID
     * @param sensitiveInformationList 민감 정보 목록
     * @return 등록 개수
     */
    public int insertSensitiveInformationMappingList(String analysisResultId, List<SensitiveInformationVO> sensitiveInformationList) {
        return sensitiveInformationMapper.insertSensitiveInformationMappingList(analysisResultId, sensitiveInformationList);
    }

    /**
     * 민감 정보 유형 목록을 등록한다.
     *
     * @param sensitiveInformationTypeList 민감 정보 유형 목록
     * @return 등록 개수
     */
    public int insertSensitiveInformationTypeList(List<Map<String, String>> sensitiveInformationTypeList) {
        return sensitiveInformationMapper.insertSensitiveInformationTypeList(sensitiveInformationTypeList);
    }

}
