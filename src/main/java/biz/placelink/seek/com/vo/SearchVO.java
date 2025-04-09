package biz.placelink.seek.com.vo;

import kr.s2.ext.pagination.vo.S2SearchVO;

public class SearchVO extends S2SearchVO implements SeekVO {

    /** 조회 시작 일자 */
    private String schStartDe;
    private String schBeginDe;
    /** 조회 종료 일자 */
    private String schEndDe;

    public String getSchStartDe() {
        return schStartDe;
    }

    public void setSchStartDe(String schStartDe) {
        this.schStartDe = schStartDe;
    }

    public String getSchBeginDe() {
        return schBeginDe;
    }

    public void setSchBeginDe(String schBeginDe) {
        this.schBeginDe = schBeginDe;
    }

    public String getSchEndDe() {
        return schEndDe;
    }

    public void setSchEndDe(String schEndDe) {
        this.schEndDe = schEndDe;
    }

}