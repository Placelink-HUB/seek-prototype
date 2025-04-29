package biz.placelink.seek.analysis.vo;

import biz.placelink.seek.system.file.vo.FileDetailVO;

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
    /* 파일 상세 */
    private FileDetailVO fileDetail;

    // ---------- 데이터베이스 ----------//

    /* 대상 정보 */
    private String targetInformation;
    /* 내용 */
    private String content;

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

    public FileDetailVO getFileDetail() {
        return fileDetail;
    }

    public void setFileDetail(FileDetailVO fileDetail) {
        this.fileDetail = fileDetail;
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

}
