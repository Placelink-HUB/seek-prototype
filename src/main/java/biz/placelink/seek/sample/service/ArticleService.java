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
package biz.placelink.seek.sample.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import biz.placelink.seek.analysis.vo.AnalysisResultVO;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.PaginationInfo;
import biz.placelink.seek.com.util.SessionUtil;
import biz.placelink.seek.sample.vo.ArticleVO;
import biz.placelink.seek.sample.vo.SchArticleVO;
import biz.placelink.seek.system.file.service.FileService;
import biz.placelink.seek.system.file.vo.FileDetailVO;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 07.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final FileService fileService;

    public ArticleService(ArticleMapper articleMapper, FileService fileService) {
        this.articleMapper = articleMapper;
        this.fileService = fileService;
    }

    @Value("${fs.file.ext}")
    private String allowedFileExt;

    /**
     * 게시글 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 게시글 목록
     */
    public List<ArticleVO> selectArticleList(SchArticleVO searchVO) {
        return articleMapper.selectArticleList(searchVO);
    }

    /**
     * 페이지 정보를 포함한 게시글 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 페이지 정보를 포함한 게시글 목록
     */
    public PaginationInfo selectArticleListWithPagination(SchArticleVO searchVO) {
        List<ArticleVO> list = articleMapper.selectArticleList(searchVO);
        int count = articleMapper.selectArticleListCount(searchVO);
        return new PaginationInfo(searchVO, list, count);
    }

    /**
     * 게시글 목록 현황을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 게시글 현황
     */
    public AnalysisResultVO selectArticleListStatus(SchArticleVO searchVO) {
        return articleMapper.selectArticleListStatus(searchVO);
    }

    /**
     * 게시글 상세 정보를 조회한다.
     *
     * @param articleId 조회 조건
     * @return 게시글
     */
    public ArticleVO selectArticle(String articleId) {
        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setSchArticleId(articleId);
        return articleMapper.selectArticle(searchVO);
    }

    /**
     * 게시글을 등록한다.
     *
     * @param articleVO 등록 정보
     * @return 등록 개수
     */
    @Transactional
    public int createArticle(ArticleVO articleVO) {
        articleVO.setArticleId(UUID.randomUUID().toString());
        articleVO.setUserId(SessionUtil.getSessionUserId());
        articleVO.setArticleTypeCcd(Constants.CD_ARTICLE_TYPE_TEXT);
        return articleMapper.createArticle(articleVO);
    }

    /**
     * 게시글 파일을 등록한다.
     *
     * @param files 파일 목록
     * @return 등록 개수
     */
    @Transactional
    public int createArticleFile(List<MultipartFile> files) {
        int result = 0;

        List<FileDetailVO> successFileList = fileService.writeFile(files, null, Constants.CD_ARTICLE_TYPE_FILE, Constants.CD_ARTICLE_TYPE_FILE, allowedFileExt.split(","), 5L);
        if (successFileList != null && !successFileList.isEmpty()) {
            ArticleVO articleVO = new ArticleVO();
            articleVO.setArticleId(UUID.randomUUID().toString());
            articleVO.setUserId(SessionUtil.getSessionUserId());
            articleVO.setFileId(successFileList.getFirst().getFileId());
            articleVO.setArticleTypeCcd(Constants.CD_ARTICLE_TYPE_FILE);
            result = articleMapper.createArticle(articleVO);
        }

        return result;
    }
}
