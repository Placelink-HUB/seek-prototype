/*
 * SEEK
 * Copyright (C) 2025 placelink
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
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
    /* 파일 확장자 상태 공통코드 */
    private String fileExtensionStatusCcd;
    /* 이벤트(발생) 일시 (yyyy-MM-dd HH:mm:ss) */
    private LocalDateTime eventDt;
    private String eventDtStr;

    /* 액션(전송/차단) 건수 */
    private Integer actionCount;
    /* 업무시간 상태 정상 건수 */
    private Integer workingHourStatusNormalCount;
    /* 업무시간 상태 비정상 건수 */
    private Integer workingHourStatusAbnormalCount;

    /** 정상 개수 */
    private Integer activeCount;
    /** 점검 개수 */
    private Integer checkCount;
    /** 경고 개수 */
    private Integer alertCount;

    /** HTTPS 채널 전송 수 */
    private Integer channelHttpsCount;
    /** USB 채널 전송 수 */
    private Integer channelUsbCount;
    /** 메신저 채널 전송 수 */
    private Integer channelMessengerCount;
    /** 프린터 채널 전송 수 */
    private Integer channelPrintCount;

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

    public String getFileExtensionStatusCcd() {
        return fileExtensionStatusCcd;
    }

    public void setFileExtensionStatusCcd(String fileExtensionStatusCcd) {
        this.fileExtensionStatusCcd = fileExtensionStatusCcd;
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

    public Integer getWorkingHourStatusNormalCount() {
        return workingHourStatusNormalCount;
    }

    public void setWorkingHourStatusNormalCount(Integer workingHourStatusNormalCount) {
        this.workingHourStatusNormalCount = workingHourStatusNormalCount;
    }

    public Integer getWorkingHourStatusAbnormalCount() {
        return workingHourStatusAbnormalCount;
    }

    public void setWorkingHourStatusAbnormalCount(Integer workingHourStatusAbnormalCount) {
        this.workingHourStatusAbnormalCount = workingHourStatusAbnormalCount;
    }

    public String getConditionLevel() {
        if (this.workingHourStatusAbnormalCount == null || this.workingHourStatusAbnormalCount == 0) {
            return "정상";
        } else {
            return "점검";
        }
    }

    public Integer getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Integer activeCount) {
        this.activeCount = activeCount;
    }

    public Integer getCheckCount() {
        return checkCount;
    }

    public void setCheckCount(Integer checkCount) {
        this.checkCount = checkCount;
    }

    public Integer getAlertCount() {
        return alertCount;
    }

    public void setAlertCount(Integer alertCount) {
        this.alertCount = alertCount;
    }

    public Integer getChannelHttpsCount() {
        return channelHttpsCount;
    }

    public void setChannelHttpsCount(Integer channelHttpsCount) {
        this.channelHttpsCount = channelHttpsCount;
    }

    public Integer getChannelUsbCount() {
        return channelUsbCount;
    }

    public void setChannelUsbCount(Integer channelUsbCount) {
        this.channelUsbCount = channelUsbCount;
    }

    public Integer getChannelMessengerCount() {
        return channelMessengerCount;
    }

    public void setChannelMessengerCount(Integer channelMessengerCount) {
        this.channelMessengerCount = channelMessengerCount;
    }

    public Integer getChannelPrintCount() {
        return channelPrintCount;
    }

    public void setChannelPrintCount(Integer channelPrintCount) {
        this.channelPrintCount = channelPrintCount;
    }

}
