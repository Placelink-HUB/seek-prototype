package biz.placelink.seek.com.constants;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String RESULT_STATUS = "status";
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAIL = "FAIL";

    /**
     * CommonHelper.setReturnCode - 1이상: 성공, 0이하: 실패
     */
    public static final String RESULT_CODE = "s2ResultCode";
    /**
     * CommonHelper.setReturnCode - 성공 메시지
     */
    public static final String RESULT_SUCC_MSG = "s2SuccessMessage";
    /**
     * CommonHelper.setReturnCode - 실패 메시지
     */
    public static final String RESULT_FAIL_MSG = "s2FailMessage";

    /**
     * 차단 파일 확장자
     */
    public static final String[] ARR_NOT_ALLOWED_EXT = {
            "html", "htm", "php", "php2", "php3", "php4", "php5", "phtml", "pwml", "inc", "asp",
            "aspx", "ascx", "jsp", "cfm", "cfc", "pl", "bat", "exe", "com", "dll", "vbs",
            "js", "reg", "cgi", "asis", "sh", "shtml", "shtm", "phtm"
    };

    /**
     * 자료형 - String
     */
    public static final String TYPE_WRAPPER_STRING = "java.lang.String";
    /**
     * 자료형 - String 배열
     */
    public static final String TYPE_ARRAY_STRING = "[Ljava.lang.String;";
    /**
     * 자료형 - Integer
     */
    public static final String TYPE_WRAPPER_INTEGER = "java.lang.Integer";
    /**
     * 자료형 - int
     */
    public static final String TYPE_DATA_INT = "int";
    /**
     * 자료형 - Long
     */
    public static final String TYPE_WRAPPER_LONG = "java.lang.Long";
    /**
     * 자료형 - long
     */
    public static final String TYPE_DATA_LONG = "long";
    /**
     * 자료형 - Float
     */
    public static final String TYPE_WRAPPER_FLOAT = "java.lang.Float";
    /**
     * 자료형 - float
     */
    public static final String TYPE_DATA_FLOAT = "float";
    /**
     * 자료형 - Double
     */
    public static final String TYPE_WRAPPER_DOUBLE = "java.lang.Double";
    /**
     * 자료형 - double
     */
    public static final String TYPE_DATA_DOUBLE = "double";

    /**
     * 시스템 식별자
     */
    public static final String SYSTEM_UID = "00000000-0000-0000-0000-000000000000";

    /**
     * 분석_모드_공통코드 - 역방향 전처리
     */
    public static final String CD_ANALYSIS_MODE_REVERSE_PRE = "REVERSE_PRE";
    /**
     * 분석_모드_공통코드 - 역방향 전처리(비동기)
     */
    public static final String CD_ANALYSIS_MODE_REVERSE_ASYNC_PRE = "REVERSE_ASYNC_PRE";
    /**
     * 분석_모드_공통코드 - 역방향 후처리
     */
    public static final String CD_ANALYSIS_MODE_REVERSE_POST = "REVERSE_POST";
    /**
     * 분석_모드_공통코드 - 역방향 후처리(비동기)
     */
    public static final String CD_ANALYSIS_MODE_REVERSE_ASYNC_POST = "REVERSE_ASYNC_POST";
    /**
     * 분석_모드_공통코드 - 순방향 전처리
     */
    public static final String CD_ANALYSIS_MODE_FORWARD_PRE = "FORWARD_PRE";
    /**
     * 분석_모드_공통코드 - 순방향 전처리(비동기)
     */
    public static final String CD_ANALYSIS_MODE_FORWARD_ASYNC_PRE = "FORWARD_ASYNC_PRE";
    /**
     * 분석_모드_공통코드 - 순방향 후처리
     */
    public static final String CD_ANALYSIS_MODE_FORWARD_POST = "FORWARD_POST";
    /**
     * 분석_모드_공통코드 - 순방향 후처리(비동기)
     */
    public static final String CD_ANALYSIS_MODE_FORWARD_ASYNC_POST = "FORWARD_ASYNC_POST";
    /**
     * 분석_모드_공통코드 - 데이터베이스
     */
    public static final String CD_ANALYSIS_MODE_DATABASE = "DATABASE";

    /**
     * 분석_상태_공통코드 - 대기
     */
    public static final String CD_ANALYSIS_STATUS_WAIT = "WAIT";
    /**
     * 분석_상태_공통코드 - 진행중
     */
    public static final String CD_ANALYSIS_STATUS_PROCESSING = "PROCESSING";
    /**
     * 분석_상태_공통코드 - 종료
     */
    public static final String CD_ANALYSIS_STATUS_COMPLETE = "COMPLETE";
    /**
     * 분석_상태_공통코드 - 오류
     */
    public static final String CD_ANALYSIS_STATUS_ERROR = "ERROR";

    /**
     * 마스크_모드_공통코드 - 원본 유지
     */
    public static final String CD_MASK_MODE_RAW = "raw";
    /**
     * 마스크_모드_공통코드 - 데이터 마스킹
     */
    public static final String CD_MASK_MODE_MASK = "mask";
    /**
     * 마스크_모드_공통코드 - 마스킹 해제
     */
    public static final String CD_MASK_MODE_UNMASK = "unmask";

    /**
     * 파일_구분_공통코드 - 분석 요청 파일
     */
    public static final String CD_FILE_SE_1010 = "1010";

    /**
     * 국가_공통코드 - 한국
     */
    public static final String CD_COUNTRY_KR = "KR";

    /**
     * 게시글_타입_공통코드
     */
    public static final String GRPCD_ARTICLE_TYPE = "ARTICLE_TYPE_CCD";
    /**
     * 게시글_타입_공통코드 - TEXT
     */
    public static final String CD_ARTICLE_TYPE_TEXT = "TEXT";
    /**
     * 게시글_타입_공통코드 - FILE
     */
    public static final String CD_ARTICLE_TYPE_FILE = "FILE";

    public static void setConstsMap(Map<String, Object> consts) {
        if (consts == null) {
            consts = new HashMap<>();
        }

        Field[] fields = Constants.class.getFields();
        if (fields != null) {
            for (Field field : fields) {
                try {
                    switch (field.getType().getName()) {
                    case Constants.TYPE_WRAPPER_STRING:
                    case Constants.TYPE_WRAPPER_INTEGER:
                    case Constants.TYPE_DATA_INT:
                    case Constants.TYPE_WRAPPER_LONG:
                    case Constants.TYPE_DATA_LONG:
                    case Constants.TYPE_WRAPPER_FLOAT:
                    case Constants.TYPE_DATA_FLOAT:
                    case Constants.TYPE_WRAPPER_DOUBLE:
                    case Constants.TYPE_DATA_DOUBLE:
                        consts.put(field.getName(), field.get(Constants.class));
                        break;
                    case Constants.TYPE_ARRAY_STRING:
                        consts.put(field.getName(), Arrays.toString((String[]) field.get(Constants.class)).replaceAll("[]\\[\\s]", ""));
                        break;
                    default:
                        break;
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    continue;
                }
            }
        }
    }

}
