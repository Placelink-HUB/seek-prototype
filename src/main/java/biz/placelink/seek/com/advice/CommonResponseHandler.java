package biz.placelink.seek.com.advice;

import biz.placelink.seek.com.constants.Constants;
import kr.s2.ext.util.S2Util;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

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
            Integer pResultCode =
                    S2Util.getValue(responseMap, Constants.RESULT_CODE, Integer.class);
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
