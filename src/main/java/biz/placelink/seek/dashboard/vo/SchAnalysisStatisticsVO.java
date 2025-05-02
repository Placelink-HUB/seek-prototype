package biz.placelink.seek.dashboard.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class SchAnalysisStatisticsVO extends DefaultVO {

    /* 조회 일자 */
    private String schBeginDe;

    /* 조회 국가 */
    private String schCountryCcd;

    public String getSchBeginDe() {
        return schBeginDe;
    }

    public void setSchBeginDe(String schBeginDe) {
        this.schBeginDe = schBeginDe;
    }

    public String getSchCountryCcd() { return schCountryCcd; }

    public void setSchCountryCcd(String schCountryCcd) { this.schCountryCcd = schCountryCcd; }

}
