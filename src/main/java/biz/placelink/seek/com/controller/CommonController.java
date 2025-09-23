package biz.placelink.seek.com.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import biz.placelink.seek.system.user.service.UserService;
import biz.placelink.seek.system.user.vo.UserVO;

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

    private final UserService userService;

    public CommonController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 인증전 기본 페이지
     *
     * @return 기본 페이지 경로
     */
    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "redirect:/dashboard/file";
    }

    @RequestMapping("/api/public/login")
    @ResponseBody
    public Map<String, Object> loginApi(@RequestParam String userId, @RequestParam String password) {
        UserVO user = userService.validateUserCredentials(userId, password);
        boolean isValid = user != null;
        Map<String, Object> result = new HashMap<>();
        result.put("success", isValid);
        if (isValid) {
            result.put("userId", user.getUserId());
            result.put("userName", user.getName());
            result.put("message", "로그인에 성공했습니다.");
        } else {
            result.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        return result;
    }

}
