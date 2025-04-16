package biz.placelink.seek.analysis.vo;

public class AnalysisDetailVO extends AnalysisVO {

    //---------- 프록시 ----------//

    /* 작업 ID */
    private String operationId;
    /* 작업 타입 공통코드 */
    private String operationTypeCcd;
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

    //---------- 데이터베이스 ----------//

    /* 대상 정보 */
    private String targetInformation;
    /* 내용 */
    private String content;


    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationTypeCcd() {
        return operationTypeCcd;
    }

    public void setOperationTypeCcd(String operationTypeCcd) {
        this.operationTypeCcd = operationTypeCcd;
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
}