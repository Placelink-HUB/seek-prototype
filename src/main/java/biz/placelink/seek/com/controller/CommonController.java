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
