package biz.placelink.seek.analysis.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import biz.placelink.seek.analysis.vo.AnalysisDetailVO;
import biz.placelink.seek.analysis.vo.AnalysisResultVO;
import biz.placelink.seek.com.vo.SearchVO;

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
@Mapper
public interface AnalysisDetailMapper {

    /**
     * 데이터베이스 분석 정보 내용을 수정한다.
     *
     * @param analysisId 분석 ID
     * @param content    내용
     * @return 수정 개수
     */
    int updateDatabaseAnalysisContent(@Param("analysisId") String analysisId, @Param("content") String content);

    /**
     * 분석 대상 컬럼을 동적으로 수정한다.
     *
     * @param tableName  테이블 명
     * @param columnName 컬럼 명
     * @param oldValue   기존 값
     * @param newValue   변경 값
     * @return 처리 개수
     */
    int updateAnalysisTargetColumnDynamically(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("oldValue") String oldValue, @Param("newValue") String newValue);

    /**
     * 프록시 분석 정보를 등록한다.
     *
     * @param paramVO 프록시 분석 정보
     * @return 등록 개수
     */
    int insertProxyAnalysis(AnalysisDetailVO paramVO);

    /**
     * 파일 분석 정보를 등록한다.
     *
     * @param paramVO 파일 분석 정보
     * @return 등록 개수
     */
    int insertFileAnalysis(AnalysisDetailVO paramVO);

    /**
     * 파일 분석 정보를 수정한다.
     *
     * @param paramVO 파일 분석 정보
     * @return 등록 개수
     */
    int updateFileAnalysis(AnalysisDetailVO paramVO);

    /**
     * 파일 분석 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 파일 분석 목록
     */
    List<AnalysisResultVO> selectFileAnalysisList(SearchVO searchVO);

    /**
     * 파일 분석 목록 개수를 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 파일 분석 목록 개수
     */
    int selectFileAnalysisListCount(SearchVO searchVO);

    /**
     * 파일 분석 목록 현황을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 파일 분석 목록 현황
     */
    AnalysisResultVO selectFileAnalysisListStatus(SearchVO searchVO);

    /**
     * 파일 분석 정보를 조회한다.
     *
     * @param analysisId 분석 ID
     * @return 파일 분석 정보
     */
    AnalysisResultVO selectFileAnalysis(String analysisId);

}
