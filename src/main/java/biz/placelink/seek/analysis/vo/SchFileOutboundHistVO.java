package biz.placelink.seek.analysis.vo;

import biz.placelink.seek.com.vo.SearchVO;

public class SchFileOutboundHistVO extends SearchVO {

    /* 외부전송 상태 공통코드 */
    private String searchOutboundStatusCcd;

    /* 파일 확장자 상태 공통코드 */
    private String searchFileExtensionStatusCcd;

    public String getSearchOutboundStatusCcd() {
        return searchOutboundStatusCcd;
    }

    public void setSearchOutboundStatusCcd(String searchOutboundStatusCcd) {
        this.searchOutboundStatusCcd = searchOutboundStatusCcd;
    }

    public String getSearchFileExtensionStatusCcd() {
        return searchFileExtensionStatusCcd;
    }

    public void setSearchFileExtensionStatusCcd(String searchFileExtensionStatusCcd) {
        this.searchFileExtensionStatusCcd = searchFileExtensionStatusCcd;
    }

}
