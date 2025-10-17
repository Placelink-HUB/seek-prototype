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
package biz.placelink.seek.customer.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import biz.placelink.seek.customer.service.CustomerService;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 10. 17.      s2          최초생성
 * </pre>
 */
@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * 고객 문의사항 등록 https://qseek.biz
     *
     * @param name    이름
     * @param email   이메일
     * @param subject 제목
     * @param message 내용
     * @return 등록 결과
     */
    @PostMapping(value = "/public/api/inquiries")
    public ResponseEntity<Map<String, Object>> inquiries(@RequestParam(name = "name") String name, @RequestParam(name = "email") String email, @RequestParam(name = "subject") String subject, @RequestParam(name = "message") String message) {
        Map<String, Object> response = new HashMap<>();
        customerService.insertCustomerInquiries(name, email, subject, message);
        return ResponseEntity.ok(response);
    }

}
