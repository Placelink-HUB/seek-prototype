package biz.placelink.seek.system.file.vo;

import kr.s2.ext.util.S2Util;

public class FileDetailVO extends FileVO {

    /* 파일 상세 ID */
    private String fileDetailId;
    /* 파일 명 */
    private String fileName;
    /* 파일 확장자 */
    private String fileExt;
    /* 파일 크기 */
    private Long fileSize;
    /* 콘텐츠 유형 */
    private String contentType;
    /* 저장 경로 */
    private String savePath;
    /* 저장 명 */
    private String saveName;
    /* 정렬 순번 */
    private Integer sortSn;

    /* 확장자를 포함한 파일 명 */
    public String getFileFullNm() {
        return this.fileName + (S2Util.isNotEmpty(this.fileExt) ? "." + this.fileExt : "");
    }

    public String getFileDetailId() {
        return fileDetailId;
    }

    public void setFileDetailId(String fileDtlId) {
        this.fileDetailId = fileDtlId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    public Integer getSortSn() {
        return sortSn;
    }

    public void setSortSn(Integer sortSn) {
        this.sortSn = sortSn;
    }

}