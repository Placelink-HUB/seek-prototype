package biz.placelink.seek.analysis.vo;

public class AnalysisResultVO extends AnalysisDetailVO {

    /* 분석 결과 ID (해시 값) */
    private String analysisResultId;
    /* 분석된 내용 */
    private String analyzedContent;
    /* 전체 검출 개수 */
    private Integer totalDetectedCount;

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

    public Integer getTotalDetectedCount() {
        return totalDetectedCount;
    }

    public void setTotalDetectedCount(Integer totalDetectedCount) {
        this.totalDetectedCount = totalDetectedCount;
    }

}
