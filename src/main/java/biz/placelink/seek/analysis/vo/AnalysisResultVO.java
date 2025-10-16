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

public class AnalysisResultVO extends AnalysisDetailVO {

    /* 분석 결과 ID (해시 값) */
    private String analysisResultId;
    /* 분석된 내용 */
    private String analyzedContent;
    /* 최대 검출 타입 공통코드 */
    private String maxDetectionTypeCcd;
    private String maxDetectionTypeCcdNm;
    /* 전체 검출 개수 */
    private Integer totalDetectionCount;

    public String getAnalysisResultId() {
        return analysisResultId;
    }

    public void setAnalysisResultId(String analysisResultId) {
        this.analysisResultId = analysisResultId;
    }

    public String getAnalyzedContent() {
        return analyzedContent;
    }

    public void setAnalyzedContent(String analyzedContent) {
        this.analyzedContent = analyzedContent;
    }

    public String getMaxDetectionTypeCcd() {
        return maxDetectionTypeCcd;
    }

    public void setMaxDetectionTypeCcd(String maxDetectionTypeCcd) {
        this.maxDetectionTypeCcd = maxDetectionTypeCcd;
    }

    public String getMaxDetectionTypeCcdNm() {
        return maxDetectionTypeCcdNm;
    }

    public void setMaxDetectionTypeCcdNm(String maxDetectionTypeCcdNm) {
        this.maxDetectionTypeCcdNm = maxDetectionTypeCcdNm;
    }

    public Integer getTotalDetectionCount() {
        return totalDetectionCount;
    }

    public void setTotalDetectionCount(Integer totalDetectionCount) {
        this.totalDetectionCount = totalDetectionCount;
    }

}
