package biz.placelink.seek.sample.controller;

import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.FileUtils;
import biz.placelink.seek.com.vo.SearchVO;
import biz.placelink.seek.sample.service.ArticleService;
import biz.placelink.seek.sample.vo.ArticleVO;
import biz.placelink.seek.sample.vo.SchArticleVO;
import kr.s2.ext.util.S2Util;
import kr.s2.ext.util.vo.S2Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
@Controller
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Value("${fs.file.ext}")
    private String allowedFileExt;

    /**
     * Sample 페이지
     *
     * @return Sample 페이지 경로
     */
    @GetMapping(value = "/public/sample/test")
    public String tissue(@RequestParam(required = false) Integer pageNo, Model model) {
        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("MODIFY_DT DESC");
        model.addAttribute("articleListPagination", articleService.selectArticleListWithPagination(searchVO));
        return "sample/test";
    }

    /**
     * 게시글 등록
     *
     * @param articleVO 등록 정보
     * @return 게시글 등록 결과
     */
    @PostMapping(value = "/public/sample/create-article")
    public ResponseEntity<Map<String, Object>> createArticle(ArticleVO articleVO) {
        Map<String, Object> response = new HashMap<>();

        S2Util.validate(articleVO, new S2Field("content", "내용"));
        response.put(Constants.RESULT_CODE, articleService.createArticle(articleVO));
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 파일 등록
     *
     * @param file 파일 데이터
     * @return 게시글 등록 결과
     * @throws IOException IOException
     */
    @PostMapping(value = "/public/sample/create-article-file")
    public ResponseEntity<Map<String, Object>> createArticleFile(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "파일이 비어있습니다.");
            return ResponseEntity.ok(response);
        } else if (!FileUtils.checkMultipartFile(file, allowedFileExt.split(","))) {
            response.put("message", "등록 가능한 파일이 아닙니다.");
            return ResponseEntity.ok(response);
        }

        response.put(Constants.RESULT_CODE, articleService.createArticleFile(file));
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회
     *
     * @param articleId 게시글 ID
     * @return 게시글 상세
     */
    @GetMapping(value = "/public/sample/article-info")
    public ResponseEntity<Map<String, Object>> selectArticle(@RequestParam String articleId) {
        Map<String, Object> response = new HashMap<>();

        response.put("articleData", articleService.selectArticle(articleId));
        response.put(Constants.RESULT_CODE, 1);
        return ResponseEntity.ok(response);
    }
}