package biz.placelink.seek.analysis.vo;

public class AnalysisResultItemVO extends AnalysisResultVO {

    /* 검출 타입 공통코드 */
    private String detectionTypeCcd;
    /* 검출 개수 */
    private Integer detectedCount;

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
