package biz.placelink.seek.analysis.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class OperationHistVO extends DefaultVO {

    /* 작업 ID */
    private String operationId;
    /* 작업 타입 공통코드 */
    private String operationTypeCcd;
    /* 분석 ID */
    private String analysisId;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationTypeCcd() {
        return operationTypeCcd;
    }

    public void setOperationTypeCcd(String operationTypeCcd) {
        this.operationTypeCcd = operationTypeCcd;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

}