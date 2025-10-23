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
package biz.placelink.seek.com.constants;

import org.springframework.stereotype.Component;

@Component("Constants")
public class Constants {

    public static final String RESULT_STATUS = "STATUS";
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAIL = "FAIL";
    public static final String RESULT_WARNING = "WARNING";

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
     * 분석_모드_공통코드 - 프록시
     */
    public static final String CD_ANALYSIS_MODE_PROXY = "PROXY";

    /**
     * 분석_모드_공통코드 - 프록시 역방향
     */
    public static final String CD_ANALYSIS_MODE_PROXY_REVERSE = "PROXY_REVERSE";
    /**
     * 분석_모드_공통코드 - 프록시 역방향 전처리
     */
    public static final String CD_ANALYSIS_MODE_PROXY_REVERSE_PRE = "PROXY_REVERSE_PRE";
    /**
     * 분석_모드_공통코드 - 프록시 역방향 전처리(비동기)
     */
    public static final String CD_ANALYSIS_MODE_PROXY_REVERSE_ASYNC_PRE = "PROXY_REVERSE_ASYNC_PRE";
    /**
     * 분석_모드_공통코드 - 프록시 역방향 후처리
     */
    public static final String CD_ANALYSIS_MODE_PROXY_REVERSE_POST = "PROXY_REVERSE_POST";
    /**
     * 분석_모드_공통코드 - 프록시 역방향 후처리(비동기)
     */
    public static final String CD_ANALYSIS_MODE_PROXY_REVERSE_ASYNC_POST = "PROXY_REVERSE_ASYNC_POST";
    /**
     * 분석_모드_공통코드 - 프록시 순방향
     */
    public static final String CD_ANALYSIS_MODE_PROXY_FORWARD = "PROXY_FORWARD";
    /**
     * 분석_모드_공통코드 - 프록시 순방향 전처리
     */
    public static final String CD_ANALYSIS_MODE_PROXY_FORWARD_PRE = "PROXY_FORWARD_PRE";
    /**
     * 분석_모드_공통코드 - 프록시 순방향 전처리(비동기)
     */
    public static final String CD_ANALYSIS_MODE_PROXY_FORWARD_ASYNC_PRE = "PROXY_FORWARD_ASYNC_PRE";
    /**
     * 분석_모드_공통코드 - 프록시 순방향 후처리
     */
    public static final String CD_ANALYSIS_MODE_PROXY_FORWARD_POST = "PROXY_FORWARD_POST";
    /**
     * 분석_모드_공통코드 - 프록시 순방향 후처리(비동기)
     */
    public static final String CD_ANALYSIS_MODE_PROXY_FORWARD_ASYNC_POST = "PROXY_FORWARD_ASYNC_POST";
    /**
     * 분석_모드_공통코드 - 데이터베이스
     */
    public static final String CD_ANALYSIS_MODE_DATABASE = "DATABASE";
    /**
     * 분석_모드_공통코드 - 파일 탐지
     */
    public static final String CD_ANALYSIS_MODE_DETECTION_FILE = "DETECTION_FILE";

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
     * 검출_타입_공통코드 - 위험
     */
    public static final String CD_DETECTION_TYPE_HIGH = "high";
    /**
     * 검출_타입_공통코드 - 보통
     */
    public static final String CD_DETECTION_TYPE_MID = "mid";
    /**
     * 검출_타입_공통코드 - 낮음
     */
    public static final String CD_DETECTION_TYPE_LOW = "low";

    /**
     * 푸시_타입_공통코드 - 알림
     */
    public static final String CD_PUSH_TYPE_NOTIFICATION = "notification";
    /**
     * 푸시_타입_공통코드 - 분석 요청
     */
    public static final String CD_PUSH_TYPE_ANALYSIS_REQUEST = "analysis_request";
    /**
     * 푸시_타입_공통코드 - 분석 완료
     */
    public static final String CD_PUSH_TYPE_ANALYSIS_COMPLETE = "analysis_complete";
    /**
     * 푸시_타입_공통코드 - 마스킹
     */
    public static final String CD_PUSH_TYPE_MASKING = "masking";
    /**
     * 푸시_타입_공통코드 - 파일 외부전송
     */
    public static final String CD_PUSH_TYPE_FILE_OUTBOUND = "file_outbound";
    /**
     * 푸시_타입_공통코드 - 파일 탐지
     */
    public static final String CD_PUSH_TYPE_FILE_DETECTION = "file_detection";
    /**
     * 푸시_타입_공통코드 - SEEK 에이전트 하트비트
     */
    public static final String CD_PUSH_TYPE_AGENT_HEARTBEAT = "agent_heartbeat";

    /**
     * 파일_구분_공통코드 - 분석 요청 파일
     */
    public static final String CD_FILE_SE_1010 = "1010";

    /**
     * 파일_구분_공통코드 - 서명 완료 파일
     */
    public static final String CD_FILE_SE_2010 = "2010";

    /**
     * 국가_공통코드 - 한국
     */
    public static final String CD_COUNTRY_KR = "KR";

    /**
     * 외부전송_상태_공통코드 - 전송
     */
    public static final String CD_OUTBOUND_STATUS_SENT = "SENT";
    /**
     * 외부전송_상태_공통코드 - 차단
     */
    public static final String CD_OUTBOUND_STATUS_BLOCKED = "BLOCKED";

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

    /**
     * 상태_수준_공통코드 - 정상
     */
    public static final String CD_CONDITION_LEVEL_ACTIVE = "ACTIVE";
    /**
     * 상태_수준_공통코드 - 점검
     */
    public static final String CD_CONDITION_LEVEL_CHECK = "CHECK";
    /**
     * 상태_수준_공통코드 - 경고
     */
    public static final String CD_CONDITION_LEVEL_ALERT = "ALERT";

    /**
     * 파일 확장자 상태 - 전체 일반 파일
     */
    public static final String CD_FILE_EXTENSION_STATUS_ALL_NORMAL = "ALL_NORMAL";
    /**
     * 파일 확장자 상태 - 일부 일반 파일
     */
    public static final String CD_FILE_EXTENSION_STATUS_PARTIALLY_NORMAL = "PARTIALLY_NORMAL";
    /**
     * 파일 확장자 상태 - 전체 비일반 파일
     */
    public static final String CD_FILE_EXTENSION_STATUS_NONE_NORMAL = "NONE_NORMAL";

    /**
     * 상태 - 정상
     */
    public static final String CD_STATUS_NORMAL = "normal";
    /**
     * 상태 - 점검
     */
    public static final String CD_STATUS_INSPECT = "inspect";
    /**
     * 상태 - 경고
     */
    public static final String CD_STATUS_WARNING = "warning";
    /**
     * 상태 - 미확인/알 수 없음
     */
    public static final String CD_STATUS_UNKNOWN = "unknown";

}
