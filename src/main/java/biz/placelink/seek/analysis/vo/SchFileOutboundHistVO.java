package biz.placelink.seek.analysis.vo;

import biz.placelink.seek.com.vo.SearchVO;

public class SchFileOutboundHistVO extends SearchVO {

    /* 파일 확장자 상태 공통코드 */
    private String schFileExtensionStatusCcd;

    public String getSchFileExtensionStatusCcd() {
        return schFileExtensionStatusCcd;
    }

    public void setSchFileExtensionStatusCcd(String schFileExtensionStatusCcd) {
        this.schFileExtensionStatusCcd = schFileExtensionStatusCcd;
    }

}
