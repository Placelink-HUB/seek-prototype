package biz.placelink.seek.com.vo;

import kr.s2.ext.pagination.vo.S2SearchVO;

public class SearchVO extends S2SearchVO implements SeekVO {

    /* 검색 분류 기준 */
    private String searchGroupingType;

    public String getSearchGroupingType() {
        return searchGroupingType;
    }

    public void setSearchGroupingType(String searchGroupingType) {
        this.searchGroupingType = searchGroupingType;
    }
}
