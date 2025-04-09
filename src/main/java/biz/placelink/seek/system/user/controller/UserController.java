package biz.placelink.seek.system.user.controller;

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
public class UserController {

    /**
     * 로그인 페이지
     *
     * @return 로그인 페이지 경로
     */
    @GetMapping(value = "/login")
    public String login() {
        return "system/user/login";
    }

}