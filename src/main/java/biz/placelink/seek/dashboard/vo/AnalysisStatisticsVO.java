package biz.placelink.seek.dashboard.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class AnalysisStatisticsVO extends DefaultVO {

    /* 분석 개수 */
    private int analysisCount;
    /* 탐지 개수 */
    private int detectionCount;
    /* 마스킹 개수 */
    private int maskingCount;
    /* 언마스킹 개수 */
    private int unmaskingCount;

    /* 전체 분석 개수 */
    private int totalAnalysisCount;
    /* 전체 탐지 개수 */
    private int totalDetectionCount;
    /* 전체 마스킹 개수 */
    private int totalMaskingCount;
    /* 전체 언마스킹 개수 */
    private int totalUnmaskingCount;


    public Integer getAnalysisCount() { return analysisCount; }

    public void setAnalysisCount(int analysisCount) {
        this.analysisCount = analysisCount;
    }

    public Integer getDetectionCount() { return detectionCount; }

    public void setDetectionCount(int detectionCount) {
        this.detectionCount = detectionCount;
    }

    public Integer getMaskingCount() { return maskingCount; }

    public void setMaskingCount(int maskingCount) { this.maskingCount = maskingCount; }

    public Integer getUnmaskingCount() { return unmaskingCount; }

    public void setUnmaskingCount(int unmaskingCount) { this.unmaskingCount = unmaskingCount; }

    public Integer getTotalAnalysisCount() { return totalAnalysisCount; }

    public void setTotalAnalysisCount(int totalAnalysisCount) { this.totalAnalysisCount = totalAnalysisCount; }

    public Integer getTotalDetectionCount() { return totalDetectionCount; }

    public void setTotalDetectionCount(int totalDetectionCount) { this.totalDetectionCount = totalDetectionCount; }

    public Integer getTotalMaskingCount() { return totalMaskingCount; }

    public void setTotalMaskingCount(int totalMaskingCount) { this.totalMaskingCount = totalMaskingCount; }

    public Integer getTotalUnmaskingCount() { return totalUnmaskingCount; }

    public void setTotalUnmaskingCount(int totalUnmaskingCount) { this.totalUnmaskingCount = totalUnmaskingCount; }


}
