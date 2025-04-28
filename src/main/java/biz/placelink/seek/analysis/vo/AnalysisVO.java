package biz.placelink.seek.analysis.vo;

import java.time.LocalDateTime;

import biz.placelink.seek.com.vo.DefaultVO;

public class AnalysisVO extends DefaultVO {

    /* 분석 ID */
    private String analysisId;
    /* 분석 타입 공통코드 */
    private String analysisTypeCcd;
    private String analysisTypeCcdNm;
    /* 분석 상태 공통코드 */
    private String analysisStatusCcd;
    private String analysisStatusCcdNm;
    /* 분석 모델 */
    private String analysisModel;
    /* 분석 결과 ID (해시 값) */
    private String analysisResultId;
    /* 분석 시작 일시 */
    private LocalDateTime analysisStartDt;
    /* 분석 종료 일시 */
    private LocalDateTime analysisEndDt;
    /* 분석 시간(ms) */
    private Long analysisTime;

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

    public String getAnalysisStatusCcdNm() {
        return analysisStatusCcdNm;
    }

    public void setAnalysisStatusCcdNm(String analysisStatusCcdNm) {
        this.analysisStatusCcdNm = analysisStatusCcdNm;
    }

    public String getAnalysisModel() {
        return analysisModel;
    }

    public void setAnalysisModel(String analysisModel) {
        this.analysisModel = analysisModel;
    }

    public String getAnalysisResultId() {
        return analysisResultId;
    }

    public void setAnalysisResultId(String analysisResultId) {
        this.analysisResultId = analysisResultId;
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

    public Long getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(Long analysisTime) {
        this.analysisTime = analysisTime;
    }

    public boolean isRequesting() {
        return isRequesting;
    }

    public void setRequesting(boolean requesting) {
        isRequesting = requesting;
    }

}
