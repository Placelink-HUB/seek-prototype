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

public class AnalysisDetailVO extends AnalysisVO {

    // ---------- 프록시 ----------//

    /* 작업 ID */
    private String requestId;
    /* 국가 공통코드 */
    private String countryCcd;
    /* URL */
    private String url;
    /* 헤더 */
    private String header;
    /* 쿼리문자열 */
    private String queryString;
    /* 바디 */
    private String body;
    /* 파일 ID */
    private String fileId;

    // ---------- 데이터베이스 ----------//

    /* 대상 정보 */
    private String targetInformation;
    /* 내용 */
    private String content;

    // ---------- 파일 ----------//

    /* 탐지 파일 ID */
    private String detectionFileId;
    /* 탐지 파일 명 */
    private String detectionFileName;
    /* 서명된 파일 ID */
    private String signedFileId;
    /* 서명된 파일 해시 */
    private String signedFileHash;
    /* 파일 개수 */
    private Integer fileCount;
    /* 전체 파일 사이즈 */
    private Long totalFileSize;
    /* 파일 다운로드 수 */
    private Long downloadCount;
    /* 요청자 식별자 */
    private String requesterUid;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCountryCcd() {
        return countryCcd;
    }

    public void setCountryCcd(String countryCcd) {
        this.countryCcd = countryCcd;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getTargetInformation() {
        return targetInformation;
    }

    public void setTargetInformation(String targetInformation) {
        this.targetInformation = targetInformation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetectionFileId() {
        return detectionFileId;
    }

    public void setDetectionFileId(String detectionFileId) {
        this.detectionFileId = detectionFileId;
    }

    public String getDetectionFileName() {
        return detectionFileName;
    }

    public void setDetectionFileName(String detectionFileName) {
        this.detectionFileName = detectionFileName;
    }

    public String getSignedFileId() {
        return signedFileId;
    }

    public void setSignedFileId(String signedFileId) {
        this.signedFileId = signedFileId;
    }

    public String getSignedFileHash() {
        return signedFileHash;
    }

    public void setSignedFileHash(String signedFileHash) {
        this.signedFileHash = signedFileHash;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public Long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(Long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public Long getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Long downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getRequesterUid() {
        return requesterUid;
    }

    public void setRequesterUid(String requesterUid) {
        this.requesterUid = requesterUid;
    }

}
