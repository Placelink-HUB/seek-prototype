package biz.placelink.seek.analysis.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class AnalysisResultItemVO extends DefaultVO {

    /* 분석 ID */
    private String analysisId;
    /* 검출 타입 공통코드 */
    private String detectionTypeCcd;
    /* 검출 개수 */
    private Integer detectedCount;

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public String getDetectionTypeCcd() {
        return detectionTypeCcd;
    }

    public void setDetectionTypeCcd(String detectionTypeCcd) {
        this.detectionTypeCcd = detectionTypeCcd;
    }

    public Integer getDetectedCount() {
        return detectedCount;
    }

    public void setDetectedCount(Integer detectedCount) {
        this.detectedCount = detectedCount;
    }

}