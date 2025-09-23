package biz.placelink.seek.system.file.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class FileVO extends DefaultVO {

    /* 파일 아이디 */
    private String fileId;
    /* 파일 타입 공통코드 */
    private String fileSeCcd;
    private String fileSeCcdNm;
    /* 파일 아이디 */
    private String fileName;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileSeCcd() {
        return fileSeCcd;
    }

    public void setFileSeCcd(String fileSeCcd) {
        this.fileSeCcd = fileSeCcd;
    }

    public String getFileSeCcdNm() {
        return fileSeCcdNm;
    }

    public void setFileSeCcdNm(String fileSeCcdNm) {
        this.fileSeCcdNm = fileSeCcdNm;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
