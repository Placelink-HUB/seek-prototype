package biz.placelink.seek.sample.vo;

import biz.placelink.seek.com.vo.DefaultVO;

public class ArticleVO extends DefaultVO {

    /* 게시글 ID */
    private String articleId;
    /* 게시글 타입 공통코드 */
    private String articleTypeCcd;
    private String articleTypeCcdNm;
    /* 내용 */
    private String content;
    /* 파일 ID */
    private String fileId;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleTypeCcd() {
        return articleTypeCcd;
    }

    public void setArticleTypeCcd(String articleTypeCcd) {
        this.articleTypeCcd = articleTypeCcd;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileId() {return fileId;}

    public void setFileId(String fileId) {this.fileId = fileId;}

    public String getArticleTypeCcdNm() {return articleTypeCcdNm;}

    public void setArticleTypeCcdNm(String articleTypeCcdNm) {this.articleTypeCcdNm = articleTypeCcdNm;}
    
}