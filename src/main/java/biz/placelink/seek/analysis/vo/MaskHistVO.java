package biz.placelink.seek.analysis.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class MaskHistVO extends DefaultVO {

    /* 요청 ID */
    private String requestId;
    /* 분석 모드 공통코드 */
    private String analysisModeCcd;
    private String analysisModeCcdNm;
    /* 마스크 모드 공통코드 */
    private String maskModeCcd;
    private String maskModeCcdNm;
    /* 마스크 개수 */
    private Integer maskCount;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAnalysisModeCcd() {
        return analysisModeCcd;
    }

    public void setAnalysisModeCcd(String analysisModeCcd) {
        this.analysisModeCcd = analysisModeCcd;
    }

    public String getAnalysisModeCcdNm() {
        return analysisModeCcdNm;
    }

    public void setAnalysisModeCcdNm(String analysisModeCcdNm) {
        this.analysisModeCcdNm = analysisModeCcdNm;
    }

    public String getMaskModeCcd() {
        return maskModeCcd;
    }

    public void setMaskModeCcd(String maskModeCcd) {
        this.maskModeCcd = maskModeCcd;
    }

    public String getMaskModeCcdNm() {
        return maskModeCcdNm;
    }

    public void setMaskModeCcdNm(String maskModeCcdNm) {
        this.maskModeCcdNm = maskModeCcdNm;
    }

    public Integer getMaskCount() {
        return maskCount;
    }

    public void setMaskCount(Integer maskCount) {
        this.maskCount = maskCount;
    }

}
