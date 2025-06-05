package biz.placelink.seek.analysis.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import biz.placelink.seek.analysis.vo.AnalysisDetailVO;
import biz.placelink.seek.com.util.PaginationInfo;
import biz.placelink.seek.com.vo.SearchVO;
import kr.s2.ext.util.S2Util;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 29.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AnalysisDetailService {

    private final AnalysisDetailMapper analysisDetailMapper;

    public AnalysisDetailService(AnalysisDetailMapper analysisDetailMapper) {
        this.analysisDetailMapper = analysisDetailMapper;
    }

    /**
     * 데이터베이스 작업 정보 내용을 수정한다.
     *
     * @param analysisId 작업 이력 ID
     * @param content    내용
     * @return 수정 개수
     */
    public int updateDatabaseAnalysisContent(String analysisId, String content) {
        return analysisDetailMapper.updateDatabaseAnalysisContent(analysisId, content);
    }

    /**
     * 분석 대상 컬럼을 동적으로 수정한다.
     *
     * @param tableName  테이블 명
     * @param columnName 컬럼 명
     * @param oldValue   기존 값
     * @param newValue   변경 값
     * @return 처리 개수
     */
    public int updateAnalysisTargetColumnDynamically(String tableName, String columnName, String oldValue, String newValue) {
        int result = 0;
        if (S2Util.isNotEmpty(tableName) && S2Util.isNotEmpty(columnName) &&
                S2Util.isNotEmpty(oldValue) && S2Util.isNotEmpty(newValue) &&
                // 테이블명은 대소문자 구분없이 tb_로 시작하고, 컬럼명은 영문자, 숫자, 언더바(_)로만 구성되어야 한다.
                tableName.matches("(?i)^tb_[a-zA-Z0-9_]+") && columnName.matches("[a-zA-Z0-9_]+")) {
            result = analysisDetailMapper.updateAnalysisTargetColumnDynamically(tableName, columnName, oldValue, newValue);
        }
        return result;
    }

    /**
     * 프록시 분석 정보를 등록한다.
     *
     * @param paramVO 프록시 작업 정보
     * @return 등록 개수
     */
    public int insertProxyAnalysis(AnalysisDetailVO paramVO) {
        return analysisDetailMapper.insertProxyAnalysis(paramVO);
    }

    /**
     * 파일 분석 정보를 등록한다.
     *
     * @param paramVO 파일 분석 정보
     * @return 등록 개수
     */
    public int insertFileAnalysis(AnalysisDetailVO paramVO) {
        return analysisDetailMapper.insertFileAnalysis(paramVO);
    }

    /**
     * 파일 분석 정보를 수정한다.
     *
     * @param paramVO 파일 분석 정보
     * @return 등록 개수
     */
    public int updateFileAnalysis(AnalysisDetailVO paramVO) {
        return analysisDetailMapper.updateFileAnalysis(paramVO);
    }

    /**
     * 파일 분석 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 파일 분석 목록
     */
    public PaginationInfo selectFileAnalysisListWithPagination(SearchVO searchVO) {
        List<AnalysisDetailVO> list = analysisDetailMapper.selectFileAnalysisList(searchVO);
        int count = analysisDetailMapper.selectFileAnalysisListCount(searchVO);
        return new PaginationInfo(searchVO, list, count);
    }

}
