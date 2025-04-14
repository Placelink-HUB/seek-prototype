package biz.placelink.seek.analysis.vo;

import biz.placelink.seek.com.vo.DefaultVO;

import java.time.LocalDateTime;

public class AnalysisVO extends DefaultVO {

    /* 분석 id */
    private String analysisId;
    /* 분석 타입 공통코드 */
    private String analysisTypeCcd;
    private String analysisTypeCcdNm;
    /* 분석 상태 공통코드 */
    private String analysisStatusCcd;
    private String analysisStatusCcdNm;
    /* 분석 모델 */
    private String analysisModel;
    /* 대상 정보 */
    private String targetInformation;
    /* 분석 내용 */
    private String analysisContent;
    /* 분석 시작 일시 */
    private LocalDateTime analysisStartDt;
    /* 분석 종료 일시 */
    private LocalDateTime analysisEndDt;
    /* 분석 시간(ms) */
    private long analysisTime;
    /* 전체 검출 개수 */
    private Integer totalDetectedCount;

    /* 요청 상태 */
    private boolean isRequesting = false;

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public String getAnalysisTypeCcd() {
        return analysisTypeCcd;
    }

    public void setAnalysisTypeCcd(String analysisTypeCcd) {
        this.analysisTypeCcd = analysisTypeCcd;
    }

    public String getAnalysisTypeCcdNm() {
        return analysisTypeCcdNm;
    }

    public void setAnalysisTypeCcdNm(String analysisTypeCcdNm) {
        this.analysisTypeCcdNm = analysisTypeCcdNm;
    }

    public String getAnalysisStatusCcd() {
        return analysisStatusCcd;
    }

    public void setAnalysisStatusCcd(String analysisStatusCcd) {
        this.analysisStatusCcd = analysisStatusCcd;
    }

    public String getAnalysisModel() {
        return analysisModel;
    }

    public void setAnalysisModel(String analysisModel) {
        this.analysisModel = analysisModel;
    }

    public String getAnalysisStatusCcdNm() {
        return analysisStatusCcdNm;
    }

    public void setAnalysisStatusCcdNm(String analysisStatusCcdNm) {
        this.analysisStatusCcdNm = analysisStatusCcdNm;
    }

    public String getTargetInformation() {
        return targetInformation;
    }

    public void setTargetInformation(String targetInformation) {
        this.targetInformation = targetInformation;
    }

    public String getAnalysisContent() {
        return analysisContent;
    }

    public void setAnalysisContent(String analysisContent) {
        this.analysisContent = analysisContent;
    }

    public LocalDateTime getAnalysisStartDt() {
        return analysisStartDt;
    }

    public void setAnalysisStartDt(LocalDateTime analysisStartDt) {
        this.analysisStartDt = analysisStartDt;
    }

    public LocalDateTime getAnalysisEndDt() {
        return analysisEndDt;
    }

    public void setAnalysisEndDt(LocalDateTime analysisEndDt) {
        this.analysisEndDt = analysisEndDt;
    }

    public long getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(long analysisTime) {
        this.analysisTime = analysisTime;
    }

    public Integer getTotalDetectedCount() {
        return totalDetectedCount;
    }

    public void setTotalDetectedCount(Integer totalDetectedCount) {
        this.totalDetectedCount = totalDetectedCount;
    }

    public boolean isRequesting() {
        return isRequesting;
    }

    public void setRequesting(boolean requesting) {
        isRequesting = requesting;
    }
}