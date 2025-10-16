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
package biz.placelink.seek.com.serviceworker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import biz.placelink.seek.com.serviceworker.service.ServiceWorkerService;
import biz.placelink.seek.com.serviceworker.vo.SubscriptionVO;
import jakarta.servlet.http.HttpServletRequest;
import kr.s2.ext.util.S2ServletUtil;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 29.      s2          최초생성
 * </pre>
 */
@Controller
public class ServiceWorkerController {

    private final ServiceWorkerService serviceWorkerService;

    public ServiceWorkerController(ServiceWorkerService serviceWorkerService) {
        this.serviceWorkerService = serviceWorkerService;
    }

    @PostMapping("/public/serviceworker/subscribe.ar")
    public ResponseEntity<String> subscribe(@RequestBody SubscriptionVO subscriptionVO, HttpServletRequest request) {
        // !!s2!! 사용자 정보가 없어서 임시로 요청 IP를 사용하여 사용자 ID를 설정한다. (추후에는 삭제하고 ServiceWorkerService.subscribe 에서 사용자 정보를 사용하자)
        subscriptionVO.setUserId(S2ServletUtil.getClientIp(request));
        serviceWorkerService.subscribe(subscriptionVO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/public/serviceworker/unsubscribe.ar")
    public ResponseEntity<String> unsubscribe(@RequestBody String endpoint) {
        // serviceWorkerService.unsubscribe(SessionHelper.getUserId());
        return ResponseEntity.ok().build();
    }

}
