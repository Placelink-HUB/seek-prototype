package biz.placelink.seek.analysis.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class AnalysisErrorVO extends DefaultVO {

    /* 분석 ID */
    private String analysisId;
    /* 분석 오류 순번 */
    private Integer analysisErrorSn;
    /* 분석 데이터 */
    private String analysisData;
    /* 오류 메시지 */
    private String errorMessage;

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getAnalysisErrorSn() {
        return analysisErrorSn;
    }

    public void setAnalysisErrorSn(Integer analysisErrorSn) {
        this.analysisErrorSn = analysisErrorSn;
    }

    public String getAnalysisData() {
        return analysisData;
    }

    public void setAnalysisData(String analysisData) {
        this.analysisData = analysisData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
