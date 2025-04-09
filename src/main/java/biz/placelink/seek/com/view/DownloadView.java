package biz.placelink.seek.com.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.s2.ext.exception.S2RuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 02. 09.      s2          최초생성
 * </pre>
 */
@Component("downloadView")
public class DownloadView extends AbstractView {

    private static final Logger logger = LoggerFactory.getLogger(DownloadView.class);

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = (String) model.get("fileName");
        Object fileData = model.get("fileData"); // byte[] 또는 InputStream

        if (fileData == null) {
            throw new S2RuntimeException("파일 정보가 존재하지 않습니다.");
        }

        try {
            // 파일명 인코딩
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            // 응답 헤더 설정
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\";charset=\"UTF-8\"", encodedFileName));
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Transfer-Encoding", "binary");
            // jquery file download 성공 콜백받기위한 쿠키설정
            response.setHeader("Set-Cookie", "fileDownload=true; path=/");

            try (OutputStream os = response.getOutputStream()) {
                if (fileData instanceof byte[]) {
                    // byte[]인 경우
                    response.setContentLength(((byte[]) fileData).length);
                    os.write((byte[]) fileData);
                } else if (fileData instanceof InputStream) {
                    // InputStream인 경우
                    try (InputStream bufferedInput = new BufferedInputStream((InputStream) fileData)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        long totalBytes = 0;

                        while ((bytesRead = bufferedInput.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                            totalBytes += bytesRead;
                        }
                        response.setContentLengthLong(totalBytes); // InputStream 은 ContentLengthLong 사용
                    }
                } else {
                    throw new IllegalArgumentException("fileData 는 byte[] 또는 InputStream 이어야 합니다.");
                }
                os.flush();
            }
        } catch (Exception e) {
            logger.error("파일 다운로드 오류: {}", fileName, e);
            throw new S2RuntimeException("파일 다운로드에 실패했습니다.");
        }
    }

}