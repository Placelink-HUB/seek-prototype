package biz.placelink.seek.analysis.vo;

public class AnalysisDetectionVO extends AnalysisResultVO {

    /* 검출 타입 공통코드 */
    private String detectionTypeCcd;
    /* 검출 개수 */
    private Integer detectionCount;

    public String getDetectionTypeCcd() {
        return detectionTypeCcd;
    }

    public void setDetectionTypeCcd(String detectionTypeCcd) {
        this.detectionTypeCcd = detectionTypeCcd;
    }

    public Integer getDetectionCount() {
        return detectionCount;
    }

    public void setDetectionCount(Integer detectionCount) {
        this.detectionCount = detectionCount;
    }

}
