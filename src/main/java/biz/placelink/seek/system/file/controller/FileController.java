package biz.placelink.seek.system.file.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import biz.placelink.seek.system.file.service.FileService;
import biz.placelink.seek.system.file.vo.FileDetailVO;
import kr.s2.ext.file.FileManager;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 02. 09.      s2          최초생성
 * </pre>
 */
@Controller
public class FileController {

    private final FileService fileService;
    private final FileManager fileManager;

    public FileController(FileService fileService, FileManager fileManager) {
        this.fileService = fileService;
        this.fileManager = fileManager;
    }

    /**
     * 이미지 보기
     *
     * @param fileId 파일 ID
     * @param model  ModelMap
     * @return 파일 뷰
     */
    @GetMapping(value = {"/file/image/view/{fileId}", "/file/image/view/{fileId}/{sortSn}"})
    public String imageView(@PathVariable String fileId, @PathVariable(required = false) Integer sortSn, ModelMap model) {
        List<FileDetailVO> fileDetailList = sortSn != null ? fileService.selectFileDetailList(fileId, sortSn) : fileService.selectFileDetailList(fileId);
        if (fileDetailList != null && fileDetailList.size() == 1) {
            FileDetailVO fileDetail = fileDetailList.getFirst();
            if (fileDetail != null) {
                model.put("fileName", fileDetail.getFileFullName());
                model.put("fileData", fileManager.readFile(fileDetail.getSavePath(), fileDetail.getSaveName()));
            }
        }
        return "downloadView";
    }

}
