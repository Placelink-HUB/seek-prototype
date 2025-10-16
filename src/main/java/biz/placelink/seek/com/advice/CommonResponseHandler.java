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
package biz.placelink.seek.com.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import biz.placelink.seek.com.constants.Constants;
import kr.s2.ext.util.S2Util;

@ControllerAdvice(basePackages = "biz.placelink.seek")
public class CommonResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Map) {
            Map<String, Object> responseMap = (Map<String, Object>) body;

            int resultCode = -1;
            Integer pResultCode = S2Util.getValue(responseMap, Constants.RESULT_CODE, Integer.class);
            if (pResultCode != null && pResultCode > 0) {
                resultCode = 0;
            }

            String message = "";
            if (resultCode == 0) {
                message = S2Util.getValue(responseMap, Constants.RESULT_SUCC_MSG, String.class);
                if (S2Util.isEmpty(message)) {
                    message = "정상 처리되었습니다.";
                }
            } else {
                message = S2Util.getValue(responseMap, Constants.RESULT_FAIL_MSG, String.class);
                if (S2Util.isEmpty(message)) {
                    message = "요청이 처리되지 않았습니다.";
                }
            }

            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("result", resultCode == 0 ? Constants.RESULT_SUCCESS : Constants.RESULT_FAIL);
            statusMap.put("resultCode", resultCode);
            statusMap.put("message", message);

            responseMap.put("status", statusMap);
        }
        return body;
    }
}
