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


    /* 탐지 현황 - 위험 */
    private int highCount;
    /* 탐지 현황 - 보통 */
    private int midCount;
    /* 탐지 현황 - 낮음 */
    private int lowCount;


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


    public int getHighCount() { return highCount; }

    public void setHighCount(int highCount) { this.highCount = highCount; }

    public int getMidCount() { return midCount; }

    public void setMidCount(int midCount) { this.midCount = midCount; }

    public int getLowCount() { return lowCount; }

    public void setLowCount(int lowCount) { this.lowCount = lowCount; }
}
