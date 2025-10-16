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

import biz.placelink.seek.com.vo.DefaultVO;

public class MaskHistVO extends DefaultVO {

    /* 요청 ID */
    private String requestId;
    /* 분석 모드 공통코드 */
    private String analysisModeCcd;
    private String analysisModeCcdNm;
    /* 마스크 모드 공통코드 */
    private String maskModeCcd;
    private String maskModeCcdNm;
    /* 마스크 개수 */
    private Integer maskCount;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public String getMaskModeCcd() {
        return maskModeCcd;
    }

    public void setMaskModeCcd(String maskModeCcd) {
        this.maskModeCcd = maskModeCcd;
    }

    public String getMaskModeCcdNm() {
        return maskModeCcdNm;
    }

    public void setMaskModeCcdNm(String maskModeCcdNm) {
        this.maskModeCcdNm = maskModeCcdNm;
    }

    public Integer getMaskCount() {
        return maskCount;
    }

    public void setMaskCount(Integer maskCount) {
        this.maskCount = maskCount;
    }

}
