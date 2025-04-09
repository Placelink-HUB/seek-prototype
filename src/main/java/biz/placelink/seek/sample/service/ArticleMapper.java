package biz.placelink.seek.sample.service;

import biz.placelink.seek.com.vo.SearchVO;
import biz.placelink.seek.sample.vo.ArticleVO;
import biz.placelink.seek.sample.vo.SchArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
@Mapper
public interface ArticleMapper {

    /**
     * 게시글 목록을 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 게시글 목록
     */
    List<ArticleVO> selectArticleList(SchArticleVO searchVO);

    /**
     * 게시글 목록 개수를 조회한다.
     *
     * @param searchVO 조회 조건
     * @return 게시글 목록 개수
     */
    int selectArticleListCount(SchArticleVO searchVO);

    /**
     * 게시글 상세정보를 조회한다.
     *
     * @param schArticleId 게시글 ID
     * @return 게시글
     */
    ArticleVO selectArticle(@Param("schArticleId") String schArticleId);

    /**
     * 게시글을 등록한다.
     *
     * @param articleVO 등록 정보
     * @return 등록 개수
     */
    int createArticle(ArticleVO articleVO);

}