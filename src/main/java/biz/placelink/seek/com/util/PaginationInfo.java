package biz.placelink.seek.com.util;

import kr.s2.ext.pagination.S2PaginationInfo;
import kr.s2.ext.pagination.vo.S2SearchVO;

import java.util.List;

public class PaginationInfo extends S2PaginationInfo {

    public PaginationInfo(S2SearchVO searchVO, List<?> dataList, int totalRecordCount) {
        super(searchVO, dataList, totalRecordCount);
    }

}