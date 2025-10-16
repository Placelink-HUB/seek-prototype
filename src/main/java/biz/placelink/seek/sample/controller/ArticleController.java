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
package biz.placelink.seek.sample.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import biz.placelink.seek.analysis.controller.AnalysisController;
import biz.placelink.seek.analysis.controller.AnalysisController.SearchPeriod;
import biz.placelink.seek.com.constants.Constants;
import biz.placelink.seek.com.util.FileUtils;
import biz.placelink.seek.sample.service.ArticleService;
import biz.placelink.seek.sample.vo.ArticleVO;
import biz.placelink.seek.sample.vo.SchArticleVO;
import biz.placelink.seek.system.file.service.FileService;
import biz.placelink.seek.system.file.vo.FileDetailVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.exception.S2RuntimeException;
import kr.s2.ext.file.FileManager;
import kr.s2.ext.util.S2Util;
import kr.s2.ext.util.vo.S2Field;

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
    private final FileService fileService;
    private final FileManager fileManager;

    public ArticleController(ArticleService articleService, FileService fileService, FileManager fileManager) {
        this.articleService = articleService;
        this.fileService = fileService;
        this.fileManager = fileManager;
    }

    @Value("${fs.file.ext}")
    private String allowedFileExt;

    /**
     * Sample 페이지
     *
     * @return Sample 페이지 경로
     */
    @GetMapping(value = "/sample/test")
    public String tissue(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam(required = false) Integer pageNo, Model model,
            @RequestParam(name = "searchStartDe", defaultValue = "") String searchStartDe, @RequestParam(name = "searchEndDe", defaultValue = "") String searchEndDe) {

        String pattern = "yyyyMMdd";
        SearchPeriod searchPeriod = AnalysisController.setSearchPeriod(searchStartDe, searchEndDe, pattern);

        SchArticleVO searchVO = new SchArticleVO();
        searchVO.setSearchStartDate(searchPeriod.searchStartDate());
        searchVO.setSearchEndDate(searchPeriod.searchEndDate());
        searchVO.setPageNo(pageNo == null ? 1 : pageNo);
        searchVO.setOrderBy("MODIFY_DT DESC");
        response.setHeader("X-Seek-Mode", seekMode);

        model.addAttribute("articleListPagination", articleService.selectArticleListWithPagination(searchVO));
        model.addAttribute("articleListStatus", articleService.selectArticleListStatus(searchVO));
        model.addAttribute("searchStartDeStr", searchPeriod.searchStartDe("yyyy년 MM월 dd일"));
        model.addAttribute("searchEndDeStr", searchPeriod.searchEndDe("yyyy년 MM월 dd일"));
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
     * @param files 파일 목록
     * @return 게시글 등록 결과
     * @throws IOException IOException
     */
    @PostMapping(value = "/sample/create-article-file")
    public ResponseEntity<Map<String, Object>> createArticleFile(@RequestParam("files") List<MultipartFile> files) throws IOException {
        Map<String, Object> response = new HashMap<>();

        if (files.isEmpty()) {
            response.put("message", "파일이 비어있습니다.");
            return ResponseEntity.ok(response);
        } else if (!FileUtils.checkMultipartFileList(files, allowedFileExt.split(","))) {
            response.put("message", "등록 가능한 파일이 아닙니다.");
            return ResponseEntity.ok(response);
        }

        response.put(Constants.RESULT_CODE, articleService.createArticleFile(files));
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회
     *
     * @param articleId 게시글 ID
     * @return 게시글 상세
     */
    @GetMapping(value = "/public/sample/article-info")
    public ResponseEntity<Map<String, Object>> selectArticle(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false, name = "seek_mode") String seekMode, @RequestParam String articleId) {
        Map<String, Object> response1 = new HashMap<>();

        response.setHeader("X-Seek-Mode", seekMode);

        response1.put("articleData", articleService.selectArticle(articleId));
        response1.put(Constants.RESULT_CODE, 1);
        return ResponseEntity.ok(response1);
    }

    /**
     * 게시글 파일 다운로드
     *
     * @param articleId 게시글 ID
     * @param model     모델 맵
     * @return 다운로드 뷰 이름
     */
    @PostMapping(value = "/sample/downloadFile")
    public String downloadFile(@RequestParam String articleId, ModelMap model) {
        ArticleVO articleVO = articleService.selectArticle(articleId);

        List<FileDetailVO> fileInfoList = fileService.selectFileDetailList(articleVO.getFileId());
        if (fileInfoList == null || fileInfoList.isEmpty()) {
            throw new S2RuntimeException("파일이 비어있습니다");
        }

        if (fileInfoList.size() == 1) {
            FileDetailVO fileInfo = fileInfoList.get(0);
            model.put("fileName", fileInfo.getFileFullName());
            model.put("fileData", fileManager.readFile(fileInfo.getSavePath(), fileInfo.getSaveName()));
        } else {
            List<Entry<String, InputStream>> inputStreamList = new ArrayList<>();
            for (FileDetailVO fileInfo : fileInfoList) {
                inputStreamList.add(Map.entry(fileInfo.getFileFullName(), fileManager.readFile(fileInfo.getSavePath(), fileInfo.getSaveName())));
            }
            model.put("fileName", "download.zip");
            model.put("fileData", inputStreamList);
        }
        model.addAttribute(Constants.RESULT_CODE, 1);
        return "downloadView";
    }

}
