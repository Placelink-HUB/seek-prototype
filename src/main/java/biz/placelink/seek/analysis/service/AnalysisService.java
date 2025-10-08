package biz.placelink.seek.analysis.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import biz.placelink.seek.analysis.vo.AnalysisDetailVO;
import biz.placelink.seek.analysis.vo.AnalysisResultVO;
import biz.placelink.seek.analysis.vo.AnalysisVO;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.SessionUtil;
import biz.placelink.seek.system.file.service.FileService;
import biz.placelink.seek.system.file.vo.FileDetailVO;
import kr.s2.ext.util.S2Util;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 22.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AnalysisService {

    private final AnalysisMapper analysisMapper;
    private final AnalysisDetailService analysisDetailService;
    private final FileService fileService;

    public AnalysisService(AnalysisMapper analysisMapper, AnalysisDetailService analysisDetailService, FileService fileService) {
        this.analysisMapper = analysisMapper;
        this.analysisDetailService = analysisDetailService;
        this.fileService = fileService;
    }

    @Value("${fs.file.ext}")
    private String allowedFileExt;

    /**
     * 실행하려는 작업 이력 목록을 조회한다.
     *
     * @param maxCount 분석기 서버에 요청할 수 있는 최대(스레드) 수
     * @return 작업 이력 목록
     */
    public List<AnalysisDetailVO> selectAnalysisHistListToExecuted(int maxCount) {
        return analysisMapper.selectAnalysisHistListToExecuted(maxCount);
    }

    /**
     * 실행중인 분석 정보 목록을 조회한다.
     *
     * @return 분석 정보 목록
     */
    public List<AnalysisDetailVO> selectProcessingAnalysisList() {
        return analysisMapper.selectProcessingAnalysisList();
    }

    /**
     * 분석 정보를 등록한다.
     *
     * @param paramVO 분석 정보
     * @return 등록 개수
     */
    public int insertAnalysis(AnalysisVO paramVO) {
        return analysisMapper.insertAnalysis(paramVO);
    }

    /**
     * 분석 정보를 수정한다.
     *
     * @param paramVO 수정할 분석 정보
     * @return 수정 개수
     */
    public int updateAnalysis(AnalysisVO paramVO) {
        return analysisMapper.updateAnalysis(paramVO);
    }

    /**
     * 비동기로 분석 정보 상태를 수정한다.
     *
     * @param analysisId        분석 ID
     * @param analysisStatusCcd 분석 상태 공통코드
     * @param analysisModel     분석 모델
     * @return 수정 개수
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateAnalysisStatusWithNewTransaction(String analysisId, String analysisStatusCcd, String analysisModel) {
        AnalysisVO paramVO = new AnalysisVO();
        paramVO.setAnalysisId(analysisId);
        paramVO.setAnalysisStatusCcd(analysisStatusCcd);
        paramVO.setAnalysisModel(analysisModel);

        return this.updateAnalysis(paramVO);
    }

    /**
     * 분석 모델을 포함한 데이터 해시 값을 수정한다.
     *
     * @param analysisId       분석 ID
     * @param analysisDataHash 분석 모델을 포함한 데이터 해시 값
     * @return 수정 개수
     */
    public int updateAnalysisDataHash(String analysisId, String analysisDataHash) {
        AnalysisVO paramVO = new AnalysisVO();
        paramVO.setAnalysisId(analysisId);
        paramVO.setAnalysisDataHash(analysisDataHash);

        return this.updateAnalysis(paramVO);
    }

    /**
     * 분석을 완료 처리한다.
     *
     * @param analysisId             분석 ID
     * @param analysisResultId       분석 결과 ID
     * @param analysisTime           분석 시간(ms)
     * @param analysisModeCcd        분석 모드 공통코드
     * @param existingAnalysisResult 기존 분석 완료 정보
     * @return 수정 개수
     */
    public int updateAnalysisCompleted(String analysisId, String analysisResultId, long analysisTime, String analysisModeCcd, AnalysisResultVO existingAnalysisResult) {
        int totalDetectionCount = 0;
        String dataBaseTargetInformation = null;
        String analyzedContent = null;
        String content = null;

        if (Constants.CD_ANALYSIS_MODE_DATABASE.equals(analysisModeCcd) && existingAnalysisResult != null) {
            totalDetectionCount = existingAnalysisResult.getTotalDetectionCount();
            dataBaseTargetInformation = existingAnalysisResult.getTargetInformation();
            analyzedContent = existingAnalysisResult.getAnalyzedContent();
            content = existingAnalysisResult.getContent();
        }

        return this.updateAnalysisCompleted(analysisId, analysisResultId, analysisTime, analysisModeCcd, totalDetectionCount, dataBaseTargetInformation, analyzedContent, content);
    }

    /**
     * 분석을 완료 처리한다.
     *
     * @param analysisId                분석 ID
     * @param analysisResultId          분석 결과 ID
     * @param analysisTime              분석 시간(ms)
     * @param analysisModeCcd           분석 모드 공통코드
     * @param totalDetectionCount       전체 검출 개수
     * @param dataBaseTargetInformation DB 대상 정보
     * @param analyzedContent           분석된 내용
     * @param content                   분석 내용(원본)
     * @return 수정 개수
     */
    public int updateAnalysisCompleted(String analysisId, String analysisResultId, long analysisTime, String analysisModeCcd, int totalDetectionCount, String dataBaseTargetInformation, String analyzedContent, String content) {
        AnalysisVO paramVO = new AnalysisVO();
        paramVO.setAnalysisId(analysisId);
        paramVO.setAnalysisStatusCcd(Constants.CD_ANALYSIS_STATUS_COMPLETE);
        paramVO.setAnalysisDataHash(analysisResultId);
        paramVO.setAnalysisResultId(analysisResultId);
        paramVO.setAnalysisTime(analysisTime);

        int result = this.updateAnalysis(paramVO);
        if (result > 0 && Constants.CD_ANALYSIS_MODE_DATABASE.equals(analysisModeCcd)) {
            String newContent = totalDetectionCount > 0 ? analyzedContent : content;

            if (S2Util.isNotEmpty(dataBaseTargetInformation) && S2Util.isNotEmpty(newContent)) {
                String[] targetInformationArr = dataBaseTargetInformation.split("\\.");
                if (targetInformationArr.length >= 2) {
                    // DB 대상 정보가 있다면 대상 콘텐츠 테이블의 컬럼에 마스킹 정보가 반영된 콘텐츠를 동적으로 수정한다.
                    analysisDetailService.updateAnalysisTargetColumnDynamically(targetInformationArr[0], targetInformationArr[1], String.format("$WT{%s}", analysisId), newContent);
                }
            }

            analysisDetailService.updateDatabaseAnalysisContent(analysisId, analyzedContent);
        }
        return result;
    }

    /**
     * 실행 시간이 초과된 분석을 오류 처리한다.
     *
     * @param maxMinutes 최대 허용 시간(분)
     */
    @Transactional(readOnly = false)
    public void updateAnalysisTimeoutError(int maxMinutes) {
        analysisMapper.updateAnalysisTimeoutError(maxMinutes);
    }

    /**
     * 검증 파일을 등록한다.
     *
     * @param files    파일 목록
     * @param clientIp 클라이언트 IP
     * @return 등록 개수
     */
    @Transactional
    public int createDetectionFile(List<MultipartFile> files, String clientIp) {
        int result = 0;
        int fileCount = 0;
        long fileSize = 0;

        if (files != null) {
            for (MultipartFile file : files) {
                fileCount++;
                fileSize += file.getSize();
            }
        }

        List<FileDetailVO> successFileList = fileService.writeFile(files, null, Constants.CD_ARTICLE_TYPE_FILE, Constants.CD_ARTICLE_TYPE_FILE, allowedFileExt.split(","), 50L);
        if (successFileList != null && !successFileList.isEmpty()) {
            String analysisId = UUID.randomUUID().toString();

            // 분석 등록 (분석 모델은 AnalyzerService 에서 분석 요청시 결정)
            AnalysisVO analysis = new AnalysisVO();
            analysis.setAnalysisId(analysisId);
            analysis.setAnalysisModeCcd(Constants.CD_ANALYSIS_MODE_DETECTION_FILE);
            analysis.setAnalysisStatusCcd(Constants.CD_ANALYSIS_STATUS_WAIT);
            analysis.setClientIp(clientIp);
            analysis.setUserId(SessionUtil.getSessionUserId());

            result = this.insertAnalysis(analysis);

            if (result > 0) {
                AnalysisDetailVO analysisDetail = new AnalysisDetailVO();
                analysisDetail.setAnalysisId(analysisId);
                analysisDetail.setDetectionFileId(successFileList.get(0).getFileId());
                analysisDetail.setFileCount(fileCount);
                analysisDetail.setTotalFileSize(fileSize);
                analysisDetail.setRequesterUid(SessionUtil.getSessionUserUid());

                analysisDetailService.insertFileAnalysis(analysisDetail);
            }
        }

        return result;
    }

}
