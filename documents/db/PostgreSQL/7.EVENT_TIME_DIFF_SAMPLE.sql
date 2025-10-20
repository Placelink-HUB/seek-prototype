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

/**
 * @function FN_CALCULATE_BUSINESS_HOURS
 * @brief 주어진 시작 시각과 종료 시각 사이의 총 업무 시간(Business Hours)을 계산합니다.
 *
 * 이 함수는 평일(월요일부터 금요일)의 09:00부터 18:00 사이의 시간만을 업무 시간으로 간주합니다.
 * 특히, 12:00부터 13:00까지의 점심시간은 업무 시간에서 제외됩니다.
 *
 * @param start_ts TIMESTAMPTZ: 계산을 시작할 시각 (타임존 포함)
 * @param end_ts TIMESTAMPTZ: 계산을 종료할 시각 (타임존 포함)
 * @returns INTERVAL: 계산된 총 업무 시간 간격
 *
 * @example
 * SELECT FN_CALCULATE_BUSINESS_HOURS('2025-10-17 09:57:42.000 +09'::TIMESTAMPTZ, '2025-10-20 19:16:23.234 +09'::TIMESTAMPTZ);
 * -- 결과: 15:02:18 (15시간 2분 18초)
 */
WITH BASE_EVENTS AS (
    -- 조건에 맞는 실제 이벤트 데이터
    SELECT
        USER_ID,
        EVENT_DT,
        -- 에이전트 기능 상태
        CASE
            -- 모든 기능이 활성화된 경우
            WHEN MINISPY_SYS_YN = 'Y' AND MSPY_USER_EXE_YN = 'Y' AND WFP_BLOCKER_EXE_YN = 'Y' AND CLICK_DOMAIN_AGENT_EXE_YN = 'Y' THEN 'FULL_FUNCTIONAL'
            -- 일부 기능이 활성화된 경우
            ELSE 'PARTIAL_FUNCTIONAL'
        END AS AGENT_FUNCTIONALITY_STATUS
    FROM SEEK_AGENT_HEARTBEAT_HIST sahh
    WHERE EVENT_DT >= NOW() - INTERVAL '1 month'
        AND (
            -- 에이전트 기능이 하나라도 활성화된 경우만 조회
            MINISPY_SYS_YN = 'Y'
            OR MSPY_USER_EXE_YN = 'Y'
            OR WFP_BLOCKER_EXE_YN = 'Y'
            OR CLICK_DOMAIN_AGENT_EXE_YN = 'Y'
        )
),
FULL_FUNCTIONAL_EVENTS AS (
    -- 1.1.1. 모든 기능이 활성화된 실제 이벤트 데이터
    SELECT
        USER_ID,
        'REAL' AS EVENT_TYPE,
        EVENT_DT,
        -- 에이전트 기능 상태
        AGENT_FUNCTIONALITY_STATUS
    FROM BASE_EVENTS
    WHERE AGENT_FUNCTIONALITY_STATUS = 'FULL_FUNCTIONAL'
    UNION ALL
    -- 1.1.2. BASE_EVENTS에 포함된 모든 USER_ID에 대해 '현재 시간'을 가상 이벤트로 추가
    SELECT
        DISTINCT USER_ID,
        'CURRENT' AS EVENT_TYPE,
        NOW() AS EVENT_DT,
        -- 에이전트 기능 상태: 대상아님
        'NOT_APPLICABLE' AS AGENT_FUNCTIONALITY_STATUS
    FROM BASE_EVENTS
),
PARTIAL_FUNCTIONAL_EVENTS AS (
    -- 1.2.1. 일부 기능이 활성화된 실제 이벤트 데이터
    SELECT
        USER_ID,
        'REAL' AS EVENT_TYPE,
        EVENT_DT,
        -- 에이전트 기능 상태
        AGENT_FUNCTIONALITY_STATUS
    FROM BASE_EVENTS
    WHERE AGENT_FUNCTIONALITY_STATUS = 'PARTIAL_FUNCTIONAL'
    UNION ALL
    -- 1.2.2. BASE_EVENTS에 포함된 모든 USER_ID에 대해 '현재 시간'을 가상 이벤트로 추가
    SELECT
        DISTINCT USER_ID,
        'CURRENT' AS EVENT_TYPE,
        NOW() AS EVENT_DT,
        -- 에이전트 기능 상태: 대상아님
        'NOT_APPLICABLE' AS AGENT_FUNCTIONALITY_STATUS
    FROM BASE_EVENTS
),
-- 2.1. 모든 기능이 활성화된 전체 이벤트 데이터(실제+가상)에 대해 LAG를 적용합니다.
FULL_FUNCTIONAL_EVENT_DATA_WITH_LAG AS (
    SELECT
        USER_ID,
        EVENT_TYPE,
        EVENT_DT AS CURRENT_EVENT_DT,
        LAG(EVENT_DT) OVER (
            PARTITION BY USER_ID
            ORDER BY EVENT_DT
        ) AS PREVIOUS_EVENT_DT,
        AGENT_FUNCTIONALITY_STATUS
    FROM FULL_FUNCTIONAL_EVENTS
),
-- 2.2. 일부 기능이 활성화된 전체 이벤트 데이터(실제+가상)에 대해 LAG를 적용합니다.
PARTIAL_FUNCTIONAL_EVENT_DATA_WITH_LAG AS (
    SELECT
        USER_ID,
        EVENT_TYPE,
        EVENT_DT AS CURRENT_EVENT_DT,
        LAG(EVENT_DT) OVER (
            PARTITION BY USER_ID
            ORDER BY EVENT_DT
        ) AS PREVIOUS_EVENT_DT,
        AGENT_FUNCTIONALITY_STATUS
    FROM PARTIAL_FUNCTIONAL_EVENTS
),
-- 3.1. 모든 기능이 활성화된 전체 이벤트의 최종 시간 차이를 계산합니다.
FULL_FUNCTIONAL_EVENT_INTERVALS AS (
    SELECT
        USER_ID,
        EVENT_TYPE,
        CURRENT_EVENT_DT AS EVENT_DT,
        -- 전체 시간 차이
        (CURRENT_EVENT_DT - PREVIOUS_EVENT_DT) AS TIME_DIFF,
        -- 업무 시간 차이 (UDF 사용)
        FN_CALCULATE_BUSINESS_HOURS(PREVIOUS_EVENT_DT, CURRENT_EVENT_DT) AS BUSINESS_TIME_DIFF,
        AGENT_FUNCTIONALITY_STATUS
    FROM FULL_FUNCTIONAL_EVENT_DATA_WITH_LAG
    -- 첫 번째 로우(PREVIOUS_EVENT_DT = NULL)는 제외하고 싶다면 아래 주석 해제
    -- WHERE PREVIOUS_EVENT_DT IS NOT NULL
    ORDER BY USER_ID, CURRENT_EVENT_DT
),
-- 3.2. 일부 기능이 활성화된 전체 이벤트의 최종 시간 차이를 계산합니다.
PARTIAL_FUNCTIONAL_EVENT_INTERVALS AS (
    SELECT
        USER_ID,
        EVENT_TYPE,
        CURRENT_EVENT_DT AS EVENT_DT,
        -- 전체 시간 차이
        (CURRENT_EVENT_DT - PREVIOUS_EVENT_DT) AS TIME_DIFF,
        -- 업무 시간 차이 (UDF 사용)
        FN_CALCULATE_BUSINESS_HOURS(PREVIOUS_EVENT_DT, CURRENT_EVENT_DT) AS BUSINESS_TIME_DIFF,
        AGENT_FUNCTIONALITY_STATUS
    FROM PARTIAL_FUNCTIONAL_EVENT_DATA_WITH_LAG
    -- 첫 번째 로우(PREVIOUS_EVENT_DT = NULL)는 제외하고 싶다면 아래 주석 해제
    -- WHERE PREVIOUS_EVENT_DT IS NOT NULL
    ORDER BY USER_ID, CURRENT_EVENT_DT
)
SELECT
    USER_ID,
    -- 첫 번째 이벤트 시각 (BaseEvents 기준)
    MIN(EVENT_DT) AS FIRST_EVENT_DT,
    -- 마지막 이벤트 시각 (BaseEvents 기준, NOW() 제외)
    MAX(CASE WHEN EVENT_TYPE = 'REAL' THEN EVENT_DT ELSE NULL END) AS LAST_EVENT_DT,
    -- 최대 시간 차이
    MAX(TIME_DIFF) AS MAX_TIME_DIFF,
    -- 최대 업무 시간 차이
    MAX(BUSINESS_TIME_DIFF) AS MAX_BUSINESS_TIME_DIFF,
    -- 전체 시간 차이의 총합
    SUM(TIME_DIFF) AS TOTAL_TIME_DIFF,
    -- 업무 시간 차이의 총합
    SUM(BUSINESS_TIME_DIFF) AS TOTAL_BUSINESS_TIME_DIFF,
    -- TIME_DIFF이 5분 이상인 간격의 개수 추가
    COUNT(CASE WHEN TIME_DIFF >= INTERVAL '5 minutes' THEN 1 END) AS COUNT_TIME_DIFF_OVER,
    -- BUSINESS_TIME_DIFF이 5분 이상인 간격의 개수 추가
    COUNT(CASE WHEN BUSINESS_TIME_DIFF >= INTERVAL '5 minutes' THEN 1 END) AS COUNT_BUSINESS_TIME_DIFF_OVER
FROM PARTIAL_FUNCTIONAL_EVENT_INTERVALS
GROUP BY USER_ID
ORDER BY USER_ID
;
