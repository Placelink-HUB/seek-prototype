package biz.placelink.seek.analysis.vo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import kr.s2.ext.util.S2Util;

public class FileOutboundHistVO extends AnalysisDetailVO {

    /* 외부전송 ID */
    private Long outboundId;
    /* 외부전송 상태 공통코드 */
    private String outboundStatusCcd;
    private String outboundStatusCcdNm;
    /* 외부전송 채널 공통코드 */
    private String outboundChannelCcd;
    private String outboundChannelCcdNm;
    /* 외부전송 사유 공통코드 */
    private String outboundReasonCcd;
    private String outboundReasonCcdNm;
    /* 로그인한 사용자 ID (발신자) */
    private String userId;
    /* 전송 요청한 클라이언트 IP */
    private String clientIp;
    /* 파일 이름(여러개 일때 , 로 구분) */
    private String fileNm;
    /* 기관 코드 */
    private String orgCd;
    /* MAC 주소 */
    private String macAddr;
    /* 목적지 호스트 */
    private String destHost;
    /* 전체 파일 개수 */
    private Integer totalFileCount;
    /* 전체 파일 크기(바이트) */
    private Long totalFileSize;
    /* 이벤트(발생) 일시 (yyyy-MM-dd HH:mm:ss) */
    private LocalDateTime eventDt;
    private String eventDtStr;

    /* 액션(전송/차단) 건수 */
    private Integer actionCount;
    /* 정상 건수 */
    private Integer normalCount;
    /* 비정상 건수 */
    private Integer abnormalCount;

    public Long getOutboundId() {
        return outboundId;
    }

    public void setOutboundId(Long outboundId) {
        this.outboundId = outboundId;
    }

    public String getOutboundStatusCcd() {
        return outboundStatusCcd;
    }

    public void setOutboundStatusCcd(String outboundStatusCcd) {
        this.outboundStatusCcd = outboundStatusCcd;
    }

    public String getOutboundStatusCcdNm() {
        return outboundStatusCcdNm;
    }

    public void setOutboundStatusCcdNm(String outboundStatusCcdNm) {
        this.outboundStatusCcdNm = outboundStatusCcdNm;
    }

    public String getOutboundChannelCcd() {
        return outboundChannelCcd;
    }

    public void setOutboundChannelCcd(String outboundChannelCcd) {
        this.outboundChannelCcd = outboundChannelCcd;
    }

    public String getOutboundChannelCcdNm() {
        return outboundChannelCcdNm;
    }

    public void setOutboundChannelCcdNm(String outboundChannelCcdNm) {
        this.outboundChannelCcdNm = outboundChannelCcdNm;
    }

    public String getOutboundReasonCcd() {
        return outboundReasonCcd;
    }

    public void setOutboundReasonCcd(String outboundReasonCcd) {
        this.outboundReasonCcd = outboundReasonCcd;
    }

    public String getOutboundReasonCcdNm() {
        return outboundReasonCcdNm;
    }

    public void setOutboundReasonCcdNm(String outboundReasonCcdNm) {
        this.outboundReasonCcdNm = outboundReasonCcdNm;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getFileNm() {
        return fileNm;
    }

    public void setFileNm(String fileNm) {
        this.fileNm = fileNm;
    }

    public String getOrgCd() {
        return orgCd;
    }

    public void setOrgCd(String orgCd) {
        this.orgCd = orgCd;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getDestHost() {
        return destHost;
    }

    public void setDestHost(String destHost) {
        this.destHost = destHost;
    }

    public Integer getTotalFileCount() {
        return totalFileCount;
    }

    public void setTotalFileCount(Integer totalFileCount) {
        this.totalFileCount = totalFileCount;
    }

    public Long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(Long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public LocalDateTime getEventDt() {
        if (eventDt == null && S2Util.isNotEmpty(eventDtStr)) {
            String pEventDtStr = eventDtStr.replaceAll("[^0-9]", "");
            if (pEventDtStr.length() == 14) {
                // yyyyMMddHHmmss
                eventDt = LocalDateTime.parse(pEventDtStr, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            }
        }

        return eventDt;
    }

    public void setEventDt(LocalDateTime eventDt) {
        this.eventDt = eventDt;
    }

    public String getEventDtStr() {
        return eventDtStr;
    }

    public void setEventDtStr(String eventDtStr) {
        this.eventDtStr = eventDtStr;
    }

    public Integer getActionCount() {
        return actionCount;
    }

    public void setActionCount(Integer actionCount) {
        this.actionCount = actionCount;
    }

    public Integer getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(Integer normalCount) {
        this.normalCount = normalCount;
    }

    public Integer getAbnormalCount() {
        return abnormalCount;
    }

    public void setAbnormalCount(Integer abnormalCount) {
        this.abnormalCount = abnormalCount;
    }

    public String getOutboundStatus() {
        if (this.abnormalCount == null || this.abnormalCount == 0) {
            return "정상";
        } else {
            return "점검";
        }
    }

}
