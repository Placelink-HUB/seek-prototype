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

import java.util.List;

import biz.placelink.seek.com.vo.DefaultVO;

public class SensitiveInformationVO extends DefaultVO {

    /* 민감 정보 ID (해시 값) */
    private String sensitiveInformationId;
    /* 대상 문자열 */
    private String targetText;
    /* 대체 문자열 */
    private String escapeText;
    /* 심각도 공통코드 */
    private String severityCcd;
    /* 탐지 횟수 */
    private Integer hitCount;
    /* 민감 정보 타입 목록 */
    private List<String> sensitiveInformationTypeList;

    public String getSensitiveInformationId() {
        return sensitiveInformationId;
    }

    public void setSensitiveInformationId(String sensitiveInformationId) {
        this.sensitiveInformationId = sensitiveInformationId;
    }

    public String getTargetText() {
        return targetText;
    }

    public void setTargetText(String targetText) {
        this.targetText = targetText;
    }

    public String getEscapeText() {
        return escapeText;
    }

    public void setEscapeText(String escapeText) {
        this.escapeText = escapeText;
    }

    public String getSeverityCcd() {
        return severityCcd;
    }

    public void setSeverityCcd(String severityCcd) {
        this.severityCcd = severityCcd;
    }

    public Integer getHitCount() {
        return hitCount;
    }

    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
    }

    public List<String> getSensitiveInformationTypeList() {
        return sensitiveInformationTypeList;
    }

    public void setSensitiveInformationTypeList(List<String> sensitiveInformationTypeList) {
        this.sensitiveInformationTypeList = sensitiveInformationTypeList;
    }

}
