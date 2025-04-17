package biz.placelink.seek.analysis.service;

import biz.placelink.seek.analysis.vo.SchSensitiveInformationVO;
import biz.placelink.seek.analysis.vo.SensitiveInformationVO;
import kr.s2.ext.util.S2EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 17.      s2          최초생성
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class WildpathAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(WildpathAnalysisService.class);

    private final SensitiveInformationService sensitiveInformationService;

    public WildpathAnalysisService(SensitiveInformationService sensitiveInformationService) {
        this.sensitiveInformationService = sensitiveInformationService;
    }

    @Value("${encryption.password}")
    public String encryptionPassword;

    /**
     * 주어진 문자열 데이터에서 민감 정보를 마스킹한다.
     * @param textData 문자열 데이터
     * @param seekMode 마스킹 모드 (mask: 마스킹된 데이터, origin: 원본 데이터, raw: 저장된 실제 데이터)
     * @return 마스킹한 문자열
     */
    public String maskSensitiveInformation(String textData, String seekMode) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        String resultText = textData;
        List<String> patterns = new ArrayList<>();

        if (Pattern.compile("(\\$WT\\{[^}]+\\})").matcher(textData).find()) {
            resultText = textData.replaceAll("\\$WT\\{[^}]+\\}", "감사중인 문서 입니다.");
        } else if (!"raw".equals(seekMode)) {
            Pattern pattern = Pattern.compile("(\\$PL\\{[^}]+\\})");
            Matcher matcher = pattern.matcher(textData);

            while (matcher.find()) {
                patterns.add(matcher.group(1));
            }
        }

        if (!patterns.isEmpty()) {
            SchSensitiveInformationVO searchVO = new SchSensitiveInformationVO();
            searchVO.setSchSensitiveInformationIdList(patterns);
            List<SensitiveInformationVO> sensitiveInformationList = sensitiveInformationService.selectSensitiveInformationList(searchVO);

            if (sensitiveInformationList != null) {
                for (SensitiveInformationVO sensitiveInformation : sensitiveInformationList) {
                    resultText = resultText.replace(sensitiveInformation.getSensitiveInformationId(), "origin".equals(seekMode) ? S2EncryptionUtil.decrypt(sensitiveInformation.getTargetText(), encryptionPassword) : sensitiveInformation.getEscapeText());
                }
            }
        }

        return resultText;
    }

    /**
     * 주어진 Content-Type 문자열로부터 문서 타입을 추론합니다.
     * 텍스트 계열, 이미지 (SVG), PDF, MS Office 문서 (docx, doc, xlsx, xls, pptx, ppt),
     * 한글 (hwp) 등의 타입을 식별하며, 그 외의 경우에는 "unknown"을 반환합니다.
     *
     * @param contentType HTTP 요청 또는 응답의 Content-Type 헤더 값. null이 허용됩니다.
     * @return 추론된 문서 타입 (예: "text", "image", "pdf", "docx", "unknown" 등).
     * contentType 이 null 인 경우 "unknown"을 반환합니다.
     */
    public static String getDocumentTypeFromContentType(String contentType) {
        String documentType = "unknown";
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            if (lowerContentType.startsWith("text/") ||
                    (lowerContentType.startsWith("application/") && (
                            lowerContentType.contains("json") ||
                                    lowerContentType.contains("xml") ||
                                    lowerContentType.contains("xhtml") ||
                                    lowerContentType.contains("javascript") ||
                                    lowerContentType.contains("ecmascript") ||
                                    lowerContentType.contains("graphql") ||
                                    lowerContentType.contains("markdown") ||
                                    lowerContentType.contains("yaml") ||
                                    lowerContentType.contains("yml") ||
                                    lowerContentType.contains("csv") ||
                                    lowerContentType.contains("sql") ||
                                    lowerContentType.contains("toml")
                    )) ||
                    (lowerContentType.startsWith("image/") && (
                            lowerContentType.contains("svg")
                    ))) {
                documentType = "text";
            } else if (lowerContentType.startsWith("image/")) {
                documentType = "image";
            } else {
                documentType = switch (contentType) {
                    case "application/pdf" -> "pdf";
                    case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx";
                    case "application/msword" -> "doc";
                    case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx";
                    case "application/vnd.ms-excel" -> "xls";
                    case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "pptx";
                    case "application/vnd.ms-powerpoint" -> "ppt";
                    case "application/x-hwp",
                         "application/vnd.hancom.hwp" -> "hwp";
                    default -> "unknown";
                };
            }
        }
        return documentType;
    }

}