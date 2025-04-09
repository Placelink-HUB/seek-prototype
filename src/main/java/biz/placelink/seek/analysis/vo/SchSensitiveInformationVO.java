package biz.placelink.seek.analysis.vo;

import biz.placelink.seek.com.vo.SearchVO;

import java.util.List;

public class SchSensitiveInformationVO extends SearchVO {

    /* 민감 정보 ID */
    private String schSensitiveInformationId;
    /* 민감 정보 ID 목록 */
    private List<String> schSensitiveInformationIdList;

    public String getSchSensitiveInformationId() {
        return schSensitiveInformationId;
    }

    public void setSchSensitiveInformationId(String schSensitiveInformationId) {
        this.schSensitiveInformationId = schSensitiveInformationId;
    }

    public List<String> getSchSensitiveInformationIdList() {
        return schSensitiveInformationIdList;
    }

    public void setSchSensitiveInformationIdList(List<String> schSensitiveInformationIdList) {
        this.schSensitiveInformationIdList = schSensitiveInformationIdList;
    }

}