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
package biz.placelink.seek.com.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import biz.placelink.seek.com.constants.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.util.S2Util;

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
@Component
public class CommonInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            ModelMap model = modelAndView.getModelMap();

            { // 결과 코드 설정
                int resultCode = -1;

                Integer pResultCode = S2Util.getValue(model, Constants.RESULT_CODE, Integer.class);
                if (pResultCode != null && pResultCode > 0) {
                    resultCode = 0;
                }

                String message = "";
                if (resultCode == 0) {
                    message = S2Util.getValue(model, Constants.RESULT_SUCC_MSG, String.class);
                    if (S2Util.isEmpty(message)) {
                        message = "정상 처리되었습니다.";
                    }
                } else {
                    message = S2Util.getValue(model, Constants.RESULT_FAIL_MSG, String.class);
                    if (S2Util.isEmpty(message)) {
                        message = "요청이 처리되지 않았습니다.";
                    }
                }

                Map<String, Object> statusMap = new HashMap<String, Object>();
                statusMap.put("result", resultCode == 0 ? Constants.RESULT_SUCCESS : Constants.RESULT_FAIL);
                statusMap.put("resultCode", resultCode);
                statusMap.put("message", message);

                model.addAttribute("status", statusMap);
            }
        }

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

}
