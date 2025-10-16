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

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getArticleTypeCcdNm() {
        return articleTypeCcdNm;
    }

    public void setArticleTypeCcdNm(String articleTypeCcdNm) {
        this.articleTypeCcdNm = articleTypeCcdNm;
    }

}
