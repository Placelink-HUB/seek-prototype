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

    /* 탐지 현황 - 전체 건수 */
    private int totalCountryCount;
    /* 탐지 현황 - 전체 국내 건수 */
    private int totalKoreaCount;
    /* 탐지 현황 - 전체 해외 건수 */
    private int totalOtherCountryCount;
    /* 탐지 현황 - 국내 건수 */
    private int koreaCount;
    /* 탐지 현황 - 해외 건수 */
    private int otherCountryCount;
    /* 탐지 현황 - 국내 위험 */
    private int koreaHighCount;
    /* 탐지 현황 - 국내 보통 */
    private int koreaMidCount;
    /* 탐지 현황 - 국내 낮음 */
    private int koreaLowCount;
    /* 탐지 현황 - 해외 위험 */
    private int otherHighCount;
    /* 탐지 현황 - 해외 보통 */
    private int otherMidCount;
    /* 탐지 현황 - 해외 낮음 */
    private int otherLowCount;

    /* 실시간 분석 현황 - 분 */
    private String minuteGroup;

    /* 민감 정보 검출 타입 공통코드 */
    private String detectionTypeCcd;
    /* 민감 정보 탐지 횟수 */
    private int hitCount;


    public int getAnalysisCount() { return analysisCount; }

    public void setAnalysisCount(int analysisCount) {
        this.analysisCount = analysisCount;
    }

    public int getDetectionCount() { return detectionCount; }

    public void setDetectionCount(int detectionCount) {
        this.detectionCount = detectionCount;
    }

    public int getMaskingCount() { return maskingCount; }

    public void setMaskingCount(int maskingCount) { this.maskingCount = maskingCount; }

    public int getUnmaskingCount() { return unmaskingCount; }

    public void setUnmaskingCount(int unmaskingCount) { this.unmaskingCount = unmaskingCount; }

    public int getTotalAnalysisCount() { return totalAnalysisCount; }

    public void setTotalAnalysisCount(int totalAnalysisCount) { this.totalAnalysisCount = totalAnalysisCount; }

    public int getTotalDetectionCount() { return totalDetectionCount; }

    public void setTotalDetectionCount(int totalDetectionCount) { this.totalDetectionCount = totalDetectionCount; }

    public int getTotalMaskingCount() { return totalMaskingCount; }

    public void setTotalMaskingCount(int totalMaskingCount) { this.totalMaskingCount = totalMaskingCount; }

    public int getTotalUnmaskingCount() { return totalUnmaskingCount; }

    public void setTotalUnmaskingCount(int totalUnmaskingCount) { this.totalUnmaskingCount = totalUnmaskingCount; }


    public int getTotalCountryCount() { return totalCountryCount; }

    public void setTotalCountryCount(int totalCountryCount) { this.totalCountryCount = totalCountryCount; }

    public int getTotalKoreaCount() { return totalKoreaCount; }

    public void setTotalKoreaCount(int totalKoreaCount) { this.totalKoreaCount = totalKoreaCount; }

    public int getTotalOtherCountryCount() { return totalOtherCountryCount; }

    public void setTotalOtherCountryCount(int totalOtherCountryCount) { this.totalOtherCountryCount = totalOtherCountryCount; }

    public int getKoreaCount() { return koreaCount; }

    public void setKoreaCount(int koreaCount) { this.koreaCount = koreaCount; }

    public int getOtherCountryCount() { return otherCountryCount; }

    public void setOtherCountryCount(int otherCountryCount) { this.otherCountryCount = otherCountryCount; }

    public int getKoreaHighCount() { return koreaHighCount; }

    public void setKoreaHighCount(int koreaHighCount) { this.koreaHighCount = koreaHighCount; }

    public int getKoreaMidCount() { return koreaMidCount; }

    public void setKoreaMidCount(int koreaMidCount) { this.koreaMidCount = koreaMidCount; }

    public int getKoreaLowCount() { return koreaLowCount; }

    public void setKoreaLowCount(int koreaLowCount) { this.koreaLowCount = koreaLowCount; }

    public int getOtherHighCount() { return otherHighCount; }

    public void setOtherHighCount(int otherHighCount) { this.otherHighCount = otherHighCount; }

    public int getOtherMidCount() { return otherMidCount; }

    public void setOtherMidCount(int otherMidCount) { this.otherMidCount = otherMidCount; }

    public int getOtherLowCount() { return otherLowCount; }

    public void setOtherLowCount(int otherLowCount) { this.otherLowCount = otherLowCount; }


    public String getMinuteGroup() { return minuteGroup; }

    public void setMinuteGroup(String minuteGroup) { this.minuteGroup = minuteGroup; }


    public String getDetectionTypeCcd() { return detectionTypeCcd; }

    public void setDetectionTypeCcd(String detectionTypeCcd) { this.detectionTypeCcd = detectionTypeCcd; }

    public int getHitCount() { return hitCount; }

    public void setHitCount(int hitCount) { this.hitCount = hitCount; }


}
