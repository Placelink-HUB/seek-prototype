package biz.placelink.seek.analysis.vo;

import java.time.LocalDateTime;

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
    /* 전체 민감 정보 개수 */
    private Long totalSensitiveInformationCount;
    /* 정상 요청 민감 정보 개수 */
    private Long normalSensitiveInformationCount;
    /* 비정상 요청 민감 정보 개수 */
    private Long abnormalSensitiveInformationCount;
    /* 전체 요청 횟수 */
    private Integer totalRequestCount;
    /* 정상 요청 횟수 */
    private Integer normalRequestCount;
    /* 비정상 요청 횟수 */
    private Integer abnormalRequestCount;
    /* 최종 요청 일시 */
    private LocalDateTime lastRequestDt;

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

    public Long getTotalSensitiveInformationCount() {
        return totalSensitiveInformationCount;
    }

    public void setTotalSensitiveInformationCount(Long totalSensitiveInformationCount) {
        this.totalSensitiveInformationCount = totalSensitiveInformationCount;
    }

    public Long getNormalSensitiveInformationCount() {
        return normalSensitiveInformationCount;
    }

    public void setNormalSensitiveInformationCount(Long normalSensitiveInformationCount) {
        this.normalSensitiveInformationCount = normalSensitiveInformationCount;
    }

    public Long getAbnormalSensitiveInformationCount() {
        return abnormalSensitiveInformationCount;
    }

    public void setAbnormalSensitiveInformationCount(Long abnormalSensitiveInformationCount) {
        this.abnormalSensitiveInformationCount = abnormalSensitiveInformationCount;
    }

    public Integer getTotalRequestCount() {
        return totalRequestCount;
    }

    public void setTotalRequestCount(Integer totalRequestCount) {
        this.totalRequestCount = totalRequestCount;
    }

    public Integer getNormalRequestCount() {
        return normalRequestCount;
    }

    public void setNormalRequestCount(Integer normalRequestCount) {
        this.normalRequestCount = normalRequestCount;
    }

    public Integer getAbnormalRequestCount() {
        return abnormalRequestCount;
    }

    public void setAbnormalRequestCount(Integer abnormalRequestCount) {
        this.abnormalRequestCount = abnormalRequestCount;
    }

    public String getConditionLevel() {
        if (this.abnormalRequestCount == null || this.abnormalRequestCount == 0) {
            return "정상";
        } else {
            return "점검";
        }
    }

    public LocalDateTime getLastRequestDt() {
        return lastRequestDt;
    }

    public void setLastRequestDt(LocalDateTime lastRequestDt) {
        this.lastRequestDt = lastRequestDt;
    }

}
