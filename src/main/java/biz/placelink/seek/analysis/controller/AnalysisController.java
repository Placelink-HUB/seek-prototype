package biz.placelink.seek.analysis.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 14.      s2          최초생성
 * </pre>
 */
@Controller
public class AnalysisController {

    /**
     * Sample 페이지
     */
    @RequestMapping(value = "analysis")
    public void tissue(HttpServletRequest request,
    HttpServletResponse response, @RequestParam(required = false) Map<String, String> formDataParams, @RequestBody(required = false) Map<String, Object> jsonParams)
            throws IOException{
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

            for (MultipartFile file : fileMap.values()) {
                if (!file.isEmpty()) {
                    // 파일 처리 로직
                }
            }
        }

        // 요청 형식 확인
        String contentType = request.getContentType();

        if (contentType != null && contentType.contains("application/json")) {
            // JSON 요청 처리
            if (jsonParams != null) {
                // jsonParams 를 이용한 처리
                System.out.println("JSON 요청: " + jsonParams);
            } else{
                // 직접 request.getInputStream() 을 이용하여 처리
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(request.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> map = objectMapper.readValue(json
                        .toString(), Map.class);
                System.out.println("JSON 요청(getInputStream): " + map);
            }
        } else if (contentType != null && (contentType
                .contains("application/x-www-form-urlencoded")
                || contentType
                        .contains("multipart/form-data"))) {
            // form-data 요청 처리
            if (formDataParams != null) {
                // formDataParams 를 이용한 처리
                System.out.println("Form-data 요청: " + formDataParams);
            } else {
                // request.getParameter() 를 이용한 처리
                System.out.println("Form-data 요청(getParameter): " + request
                        .getParameterMap());
            }
        } else {
            // 기타 요청 처리
            System.out.println("기타 요청");
        }
    }

}
