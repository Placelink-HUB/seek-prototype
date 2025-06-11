package biz.placelink.seek.analysis.vo;

public class AnalysisResultVO extends AnalysisDetailVO {

    /* 분석 결과 ID (해시 값) */
    private String analysisResultId;
    /* 분석된 내용 */
    private String analyzedContent;
    /* 최대 검출 타입 공통코드 */
    private String maxDetectionTypeCcd;
    private String maxDetectionTypeCcdNm;
    /* 전체 검출 개수 */
    private Integer totalDetectionCount;

    public String getAnalysisResultId() {
        return analysisResultId;
    }

    public void setAnalysisResultId(String analysisResultId) {
        this.analysisResultId = analysisResultId;
    }

    public String getAnalyzedContent() {
        return analyzedContent;
    }

    public void setAnalyzedContent(String analyzedContent) {
        this.analyzedContent = analyzedContent;
    }

    public String getMaxDetectionTypeCcd() {
        return maxDetectionTypeCcd;
    }

    public void setMaxDetectionTypeCcd(String maxDetectionTypeCcd) {
        this.maxDetectionTypeCcd = maxDetectionTypeCcd;
    }

    public String getMaxDetectionTypeCcdNm() {
        return maxDetectionTypeCcdNm;
    }

    public void setMaxDetectionTypeCcdNm(String maxDetectionTypeCcdNm) {
        this.maxDetectionTypeCcdNm = maxDetectionTypeCcdNm;
    }

    public Integer getTotalDetectionCount() {
        return totalDetectionCount;
    }

    public void setTotalDetectionCount(Integer totalDetectionCount) {
        this.totalDetectionCount = totalDetectionCount;
    }

}
