package biz.placelink.seek.analysis.vo;

import java.time.LocalDateTime;

import biz.placelink.seek.com.vo.DefaultVO;

public class AnalysisVO extends DefaultVO {

    /* 분석 ID */
    private String analysisId;
    /* 분석 모드 공통코드 */
    private String analysisModeCcd;
    private String analysisModeCcdNm;
    /* 분석 상태 공통코드 */
    private String analysisStatusCcd;
    private String analysisStatusCcdNm;
    /* 분석 모델 */
    private String analysisModel;
    /* 분석 모델을 포함한 데이터 해시 값 */
    private String analysisDataHash;
    /* 분석 결과 ID (해시 값) */
    private String analysisResultId;
    /* 분석 시작 일시 */
    private LocalDateTime analysisStartDt;
    /* 분석 종료 일시 */
    private LocalDateTime analysisEndDt;
    /* 분석 시간(ms) */
    private Long analysisTime;
    /* 생성 일시 문자열 */
    private String createDtStr;
    /* 클라이언트 IP */
    private String clientIp;
    /* 유저 ID */
    private String userId;

    /* 사용 중 여부 */
    private boolean inUse = false;

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public String getAnalysisModeCcd() {
        return analysisModeCcd;
    }

    public void setAnalysisModeCcd(String analysisModeCcd) {
        this.analysisModeCcd = analysisModeCcd;
    }

    public String getAnalysisModeCcdNm() {
        return analysisModeCcdNm;
    }

    public void setAnalysisModeCcdNm(String analysisModeCcdNm) {
        this.analysisModeCcdNm = analysisModeCcdNm;
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

    public String getAnalysisDataHash() {
        return analysisDataHash;
    }

    public void setAnalysisDataHash(String analysisDataHash) {
        this.analysisDataHash = analysisDataHash;
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

    public String getCreateDtStr() {
        return createDtStr;
    }

    public void setCreateDtStr(String createDtStr) {
        this.createDtStr = createDtStr;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

}
