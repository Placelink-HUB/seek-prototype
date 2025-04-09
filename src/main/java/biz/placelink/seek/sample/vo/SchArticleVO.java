package biz.placelink.seek.sample.vo;

import biz.placelink.seek.com.vo.SearchVO;

public class SchArticleVO extends SearchVO {

    /* 게시글 ID */
    private String schArticleId;

    public String getSchArticleId() {
        return schArticleId;
    }

    public void setSchArticleId(String schArticleId) {
        this.schArticleId = schArticleId;
    }
}