package biz.placelink.seek.analysis.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class SensitiveInformationUnmaskHistVO extends DefaultVO {

    /* 요청 ID */
    private String requestId;
    /* 클라이언트 IP */
    private String clientIp;
    /* 사용자 ID */
    private String userId;
    /* 민감 정보 개수 */
    private Integer sensitiveInformationCount;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getSensitiveInformationCount() {
        return sensitiveInformationCount;
    }

    public void setSensitiveInformationCount(Integer sensitiveInformationCount) {
        this.sensitiveInformationCount = sensitiveInformationCount;
    }

}
