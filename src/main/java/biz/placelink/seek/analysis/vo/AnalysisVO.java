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
 *
 * =========================================================================
 *
 * 상업적 이용 또는 AGPL-3.0의 공개 의무를 면제받기
 * 위해서는, placelink로부터 별도의 상업용 라이선스(Commercial License)를 구매해야 합니다.
 * For commercial use or to obtain an exemption from the AGPL-3.0 license
 * requirements, please purchase a commercial license from placelink.
 * *** 문의처: help@placelink.shop (README.md 참조)
 */
package biz.placelink.seek.analysis.vo;

import java.time.LocalDateTime;

import biz.placelink.seek.com.vo.DefaultVO;

public class AnalysisVO extends DefaultVO {

    /* 분석 ID */
    private String analysisId;
    /* 분석 모드 공통코드 */
    private String analysisModeCcd;
    private String analysisModeCcdNm;
    /* 분석 상태 공통코드 */
    private String analysisStatusCcd;
    private String analysisStatusCcdNm;
    /* 분석 모델 */
    private String analysisModel;
    /* 분석 모델을 포함한 데이터 해시 값 */
    private String analysisDataHash;
    /* 분석 결과 ID (해시 값) */
    private String analysisResultId;
    /* 분석 시작 일시 */
    private LocalDateTime analysisStartDt;
    /* 분석 종료 일시 */
    private LocalDateTime analysisEndDt;
    /* 분석 시간(ms) */
    private Long analysisTime;
    /* 생성 일시 문자열 */
    private String createDtStr;
    /* 요청 ID */
    private String requestId;
    /* 클라이언트 IP */
    private String clientIp;
    /* 유저 ID */
    private String userId;

    /* 사용 중 여부 */
    private boolean inUse = false;

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public String getAnalysisModeCcd() {
        return analysisModeCcd;
    }

    public void setAnalysisModeCcd(String analysisModeCcd) {
        this.analysisModeCcd = analysisModeCcd;
    }

    public String getAnalysisModeCcdNm() {
        return analysisModeCcdNm;
    }

    public void setAnalysisModeCcdNm(String analysisModeCcdNm) {
        this.analysisModeCcdNm = analysisModeCcdNm;
    }

    public String getAnalysisStatusCcd() {
        return analysisStatusCcd;
    }

    public void setAnalysisStatusCcd(String analysisStatusCcd) {
        this.analysisStatusCcd = analysisStatusCcd;
    }

    public String getAnalysisStatusCcdNm() {
        return analysisStatusCcdNm;
    }

    public void setAnalysisStatusCcdNm(String analysisStatusCcdNm) {
        this.analysisStatusCcdNm = analysisStatusCcdNm;
    }

    public String getAnalysisModel() {
        return analysisModel;
    }

    public void setAnalysisModel(String analysisModel) {
        this.analysisModel = analysisModel;
    }

    public String getAnalysisDataHash() {
        return analysisDataHash;
    }

    public void setAnalysisDataHash(String analysisDataHash) {
        this.analysisDataHash = analysisDataHash;
    }

    public String getAnalysisResultId() {
        return analysisResultId;
    }

    public void setAnalysisResultId(String analysisResultId) {
        this.analysisResultId = analysisResultId;
    }

    public LocalDateTime getAnalysisStartDt() {
        return analysisStartDt;
    }

    public void setAnalysisStartDt(LocalDateTime analysisStartDt) {
        this.analysisStartDt = analysisStartDt;
    }

    public LocalDateTime getAnalysisEndDt() {
        return analysisEndDt;
    }

    public void setAnalysisEndDt(LocalDateTime analysisEndDt) {
        this.analysisEndDt = analysisEndDt;
    }

    public Long getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(Long analysisTime) {
        this.analysisTime = analysisTime;
    }

    public String getCreateDtStr() {
        return createDtStr;
    }

    public void setCreateDtStr(String createDtStr) {
        this.createDtStr = createDtStr;
    }

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

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

}
