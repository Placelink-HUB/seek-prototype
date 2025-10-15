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
package biz.placelink.seek.dashboard.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class AnalysisStatisticsVO extends DefaultVO {

    /* (분석) 요청 개수 */
    private Integer requestCount;
    /* (분석) 완료 개수 */
    private Integer completeCount;
    /* 탐지 개수 */
    private Integer detectionCount;
    /* 전체 탐지 개수 */
    private Integer totalDetectionCount;
    /* 마스킹 개수 */
    private Integer maskingCount;
    /* 전체 마스킹 개수 */
    private Integer totalMaskingCount;
    /* 언마스킹 개수 */
    private Integer unmaskingCount;
    /* 전체 언마스킹 개수 */
    private Integer totalUnmaskingCount;

    /* 탐지 현황 - 전체 건수 */
    private Integer totalCountryCount;
    /* 탐지 현황 - 전체 국내 건수 */
    private Integer totalKoreaCount;
    /* 탐지 현황 - 전체 해외 건수 */
    private Integer totalOtherCountryCount;
    /* 탐지 현황 - 국내 건수 */
    private Integer koreaCount;
    /* 탐지 현황 - 해외 건수 */
    private Integer otherCountryCount;
    /* 탐지 현황 - 국내 위험 */
    private Integer koreaHighCount;
    /* 탐지 현황 - 국내 보통 */
    private Integer koreaMidCount;
    /* 탐지 현황 - 국내 낮음 */
    private Integer koreaLowCount;
    /* 탐지 현황 - 해외 위험 */
    private Integer otherHighCount;
    /* 탐지 현황 - 해외 보통 */
    private Integer otherMidCount;
    /* 탐지 현황 - 해외 낮음 */
    private Integer otherLowCount;

    /* 실시간 분석 현황 - 분 (YYYYMMDDHH24MI) */
    private String minuteGroup;

    /* 민감 정보 검출 타입 공통코드 */
    private String detectionTypeCcd;
    /* 민감 정보 탐지 횟수 */
    private Integer hitCount;

    /* 분석 파일 개수 */
    private Integer analysisFileCount;
    /* 분석 파일 크기 */
    private Long analysisFileSize;

    /* 외부전송 상태 공통코드 */
    private String outboundStatusCcd;
    /* 외부전송 채널 공통코드 */
    private String outboundChannelCcd;
    /* 외부전송 이력 개수 */
    private Integer outboundHistCount;
    /* 외부전송 파일 개수 */
    private Integer outboundFileCount;
    /* 외부전송 파일 크기 */
    private Long outboundFileSize;

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(Integer completeCount) {
        this.completeCount = completeCount;
    }

    public Integer getDetectionCount() {
        return detectionCount;
    }

    public void setDetectionCount(Integer detectionCount) {
        this.detectionCount = detectionCount;
    }

    public Integer getTotalDetectionCount() {
        return totalDetectionCount;
    }

    public void setTotalDetectionCount(Integer totalDetectionCount) {
        this.totalDetectionCount = totalDetectionCount;
    }

    public Integer getMaskingCount() {
        return maskingCount;
    }

    public void setMaskingCount(Integer maskingCount) {
        this.maskingCount = maskingCount;
    }

    public Integer getTotalMaskingCount() {
        return totalMaskingCount;
    }

    public void setTotalMaskingCount(Integer totalMaskingCount) {
        this.totalMaskingCount = totalMaskingCount;
    }

    public Integer getUnmaskingCount() {
        return unmaskingCount;
    }

    public void setUnmaskingCount(Integer unmaskingCount) {
        this.unmaskingCount = unmaskingCount;
    }

    public Integer getTotalUnmaskingCount() {
        return totalUnmaskingCount;
    }

    public void setTotalUnmaskingCount(Integer totalUnmaskingCount) {
        this.totalUnmaskingCount = totalUnmaskingCount;
    }

    public Integer getTotalCountryCount() {
        return totalCountryCount;
    }

    public void setTotalCountryCount(Integer totalCountryCount) {
        this.totalCountryCount = totalCountryCount;
    }

    public Integer getTotalKoreaCount() {
        return totalKoreaCount;
    }

    public void setTotalKoreaCount(Integer totalKoreaCount) {
        this.totalKoreaCount = totalKoreaCount;
    }

    public Integer getTotalOtherCountryCount() {
        return totalOtherCountryCount;
    }

    public void setTotalOtherCountryCount(Integer totalOtherCountryCount) {
        this.totalOtherCountryCount = totalOtherCountryCount;
    }

    public Integer getKoreaCount() {
        return koreaCount;
    }

    public void setKoreaCount(Integer koreaCount) {
        this.koreaCount = koreaCount;
    }

    public Integer getOtherCountryCount() {
        return otherCountryCount;
    }

    public void setOtherCountryCount(Integer otherCountryCount) {
        this.otherCountryCount = otherCountryCount;
    }

    public Integer getKoreaHighCount() {
        return koreaHighCount;
    }

    public void setKoreaHighCount(Integer koreaHighCount) {
        this.koreaHighCount = koreaHighCount;
    }

    public Integer getKoreaMidCount() {
        return koreaMidCount;
    }

    public void setKoreaMidCount(Integer koreaMidCount) {
        this.koreaMidCount = koreaMidCount;
    }

    public Integer getKoreaLowCount() {
        return koreaLowCount;
    }

    public void setKoreaLowCount(Integer koreaLowCount) {
        this.koreaLowCount = koreaLowCount;
    }

    public Integer getOtherHighCount() {
        return otherHighCount;
    }

    public void setOtherHighCount(Integer otherHighCount) {
        this.otherHighCount = otherHighCount;
    }

    public Integer getOtherMidCount() {
        return otherMidCount;
    }

    public void setOtherMidCount(Integer otherMidCount) {
        this.otherMidCount = otherMidCount;
    }

    public Integer getOtherLowCount() {
        return otherLowCount;
    }

    public void setOtherLowCount(Integer otherLowCount) {
        this.otherLowCount = otherLowCount;
    }

    public String getMinuteGroup() {
        return minuteGroup;
    }

    public void setMinuteGroup(String minuteGroup) {
        this.minuteGroup = minuteGroup;
    }

    public String getDetectionTypeCcd() {
        return detectionTypeCcd;
    }

    public void setDetectionTypeCcd(String detectionTypeCcd) {
        this.detectionTypeCcd = detectionTypeCcd;
    }

    public Integer getHitCount() {
        return hitCount;
    }

    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
    }

    public Integer getAnalysisFileCount() {
        return analysisFileCount;
    }

    public void setAnalysisFileCount(Integer analysisFileCount) {
        this.analysisFileCount = analysisFileCount;
    }

    public Long getAnalysisFileSize() {
        return analysisFileSize;
    }

    public void setAnalysisFileSize(Long analysisFileSize) {
        this.analysisFileSize = analysisFileSize;
    }

    public String getOutboundStatusCcd() {
        return outboundStatusCcd;
    }

    public void setOutboundStatusCcd(String outboundStatusCcd) {
        this.outboundStatusCcd = outboundStatusCcd;
    }

    public String getOutboundChannelCcd() {
        return outboundChannelCcd;
    }

    public void setOutboundChannelCcd(String outboundChannelCcd) {
        this.outboundChannelCcd = outboundChannelCcd;
    }

    public Integer getOutboundHistCount() {
        return outboundHistCount;
    }

    public void setOutboundHistCount(Integer outboundHistCount) {
        this.outboundHistCount = outboundHistCount;
    }

    public Integer getOutboundFileCount() {
        return outboundFileCount;
    }

    public void setOutboundFileCount(Integer outboundFileCount) {
        this.outboundFileCount = outboundFileCount;
    }

    public Long getOutboundFileSize() {
        return outboundFileSize;
    }

    public void setOutboundFileSize(Long outboundFileSize) {
        this.outboundFileSize = outboundFileSize;
    }

}
