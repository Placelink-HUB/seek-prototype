package biz.placelink.seek.com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 01. 09.      s2          최초생성
 * </pre>
 */
@Controller
public class CommonController {

    /**
     * 인증전 기본 페이지
     *
     * @return 기본 페이지 경로
     */
    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "redirect:/dashboard/file";
    }

}
