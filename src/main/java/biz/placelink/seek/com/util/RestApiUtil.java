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
package biz.placelink.seek.com.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import kr.s2.ext.util.S2Util;

/**
 * REST API 호출 및 관련 유틸리티 기능을 제공하는 클래스.
 * HTTP 요청을 처리하고, {@link InputStreamResource}를 생성하는 메서드를 포함한다.
 *
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

    /**
     * 지정된 URL로 REST API를 호출하여 응답을 문자열로 반환한다.
     * 기본 타임아웃(30,000ms)을 사용하며, HTTP 메서드와 매개변수를 받아 요청을 처리한다.
     *
     * @param url    호출할 REST API의 URL. (필수)
     * @param method 사용할 HTTP 메서드 (예: {@link HttpMethod#POST}, {@link HttpMethod#GET}). (필수)
     * @param params 요청에 포함할 매개변수로, 키-값 쌍의 배열. (선택)
     * @return API 호출 결과로 반환된 문자열 응답. 응답이 없거나 오류 발생 시 null을 반환.
     *
     *         <pre>{@code
     * String result = S2RestApiUtil.callApi("request.api", HttpMethod.POST,
     *     Map.entry("param1", value1),
     *     Map.entry("param2", value2),
     *     Map.entry("files", S2RestApiUtil.createInputStreamResource(fileInputStream, "file1.text", 1213)),
     *     Map.entry("files", S2RestApiUtil.createInputStreamResource(fileInputStream, "file2.pdf", 3121))
     * );
     * }</pre>
     */
    @SafeVarargs
    public static String callApi(String url, HttpMethod method, Map.Entry<String, Object>... params) {
        return RestApiUtil.callApi(url, method, null, params);
    }

    /**
     * 지정된 URL로 REST API를 호출하여 응답을 문자열로 반환한다.
     * 사용자 지정 타임아웃을 설정할 수 있으며, HTTP 메서드와 매개변수를 받아 요청을 처리한다.
     *
     * @param url     호출할 REST API의 URL. (필수)
     * @param method  사용할 HTTP 메서드 (예: {@link HttpMethod#POST}, {@link HttpMethod#GET}). (필수)
     * @param timeout 연결 및 읽기 타임아웃(밀리초 단위). null인 경우 기본값 30,000ms 사용. (선택)
     * @param params  요청에 포함할 매개변수로, 키-값 쌍의 배열. (선택)
     * @return API 호출 결과로 반환된 문자열 응답. 응답이 없거나 오류 발생 시 null을 반환.
     * @example
     *
     *          <pre>{@code
     * String result = S2RestApiUtil.callApi("request.api", HttpMethod.POST, 10000,
     *     Map.entry("param1", value1),
     *     Map.entry("param2", value2),
     *     Map.entry("files", S2RestApiUtil.createInputStreamResource(fileInputStream, "file1.text", 1213)),
     *     Map.entry("files", S2RestApiUtil.createInputStreamResource(fileInputStream, "file2.pdf", 3121))
     * );
     * }</pre>
     */
    @SafeVarargs
    public static String callApi(String url, HttpMethod method, Integer timeout, Map.Entry<String, Object>... params) {
        int vTimeout = timeout != null && timeout > 0 ? timeout : 30000;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(vTimeout);
        requestFactory.setReadTimeout(vTimeout);

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
            result = S2Util.decodeUnicode(responseEntity.getBody());

            logger.debug("Call URL: {}, Method: {}", url, method);
            logger.debug("Raw Response: {}", result);
        }

        return result;
    }

    /**
     * 주어진 {@link InputStream}, 파일명, 콘텐츠 길이를 이용하여 {@link InputStreamResource}를 생성.
     * 생성된 {@code InputStreamResource}는 {@link #getFilename()}과 {@link #contentLength()} 메서드를 오버라이드하여 파일명과 콘텐츠 길이를 명시적으로 반환.
     *
     * @param inputStream   전송할 데이터의 {@link InputStream}. (필수)
     * @param filename      다운로드될 확장자를 포함한 파일 이름. (필수)
     * @param contentLength 전송할 데이터의 총 길이 (바이트 단위). (필수, 0 이상)
     *                      !!s2!! contentLength 가 없는 경우 InputStream 을 두번읽으면서 오류나 날수 있어 반드시 넣어야 한다.
     * @return 파일명과 콘텐츠 길이가 설정된 새로운 {@link InputStreamResource} 객체.
     * @throws IllegalArgumentException {@code inputStream} 또는 {@code filename}이 null이거나 {@code contentLength}가 음수인 경우.
     * @details
     *          <dl>
     *          <dd>InputStreamResource 를 사용할 때 Content-Length를 미리 계산해 설정하면 Spring 이 스트림을 미리 읽지 않는다.</dd>
     *          <dd>!!s2!! 즉 Content-Length 명시하지 않으면 InputStreamResource 를 2번 읽으면서 java.lang.IllegalStateException 예외가 발생한다.</dd>
     *          </dl>
     */
    public static InputStreamResource createInputStreamResource(InputStream inputStream, String filename, long contentLength) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        if (filename == null) {
            throw new IllegalArgumentException("Filename must not be null");
        }
        if (contentLength < 0) {
            throw new IllegalArgumentException("Content length must not be negative");
        }
        return new InputStreamResource(inputStream) {

            @Override
            public String getFilename() {
                return filename;
            }

            @Override
            public long contentLength() {
                return contentLength;
            }
        };
    }

}
