package biz.placelink.seek.com.util;

import kr.s2.ext.util.S2Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.util.Collections;
import java.util.Map;
import java.nio.charset.StandardCharsets;

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
public class RestApiUtil {

    private static final Logger logger = LoggerFactory.getLogger(RestApiUtil.class);

    @SafeVarargs
    public static String callApi(String url, HttpMethod method,
            Map.Entry<String, Object>... params) {
        return RestApiUtil.callApi(url, method, null, params);
    }

    @SafeVarargs
    public static String callApi(String url, HttpMethod method, Integer timeout,
            Map.Entry<String, Object>... params) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout != null ? timeout : 600000);
        requestFactory.setReadTimeout(timeout != null ? timeout : 600000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        // UTF-8 메시지 컨버터 설정
        restTemplate.getMessageConverters().addFirst(new StringHttpMessageConverter(StandardCharsets.UTF_8));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (params != null) {
            for (Map.Entry<String, Object> param : params) {
                if (param != null) {
                    body.add(param.getKey(), param.getValue());
                }
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));

        String result = null;
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = null;

        if (method == HttpMethod.POST) {
            responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        } else if (method == HttpMethod.GET) {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        }

        if (responseEntity != null && responseEntity.getBody() != null) {
            logger.debug("Response charset: {}", responseEntity.getHeaders().getContentType());
            logger.debug("Raw response: {}", responseEntity.getBody());

            result = S2Util.decodeUnicode(responseEntity.getBody());
        }

        return result;
    }

}