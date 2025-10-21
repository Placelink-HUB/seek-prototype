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
 * @function FN_IS_BUSINESS_HOUR
 * @brief 주어진 시각이 업무 시간(평일 09:00 ~ 18:00, 단 12:00 ~ 13:00 제외)에 속하는지 확인합니다.
 *
 * @param target_ts TIMESTAMPTZ: 확인할 시각 (타임존 포함)
 * @returns BOOLEAN: 업무 시간이면 TRUE, 아니면 FALSE
 *
 * @example
 * SELECT FN_IS_BUSINESS_HOUR('2025-10-17 10:30:00.000 +09'::TIMESTAMPTZ); -- 금요일 오전, 결과: TRUE
 * SELECT FN_IS_BUSINESS_HOUR('2025-10-18 10:30:00.000 +09'::TIMESTAMPTZ); -- 토요일 오전, 결과: FALSE
 * SELECT FN_IS_BUSINESS_HOUR('2025-10-17 12:30:00.000 +09'::TIMESTAMPTZ); -- 점심 시간, 결과: FALSE
 */
CREATE OR REPLACE FUNCTION FN_IS_BUSINESS_HOUR(
    target_ts TIMESTAMPTZ
)RETURNS BOOLEAN AS $$
DECLARE
    -- 타겟 시각의 요일 (1=월요일, 7=일요일)
    target_day_of_week INTEGER; -- 변수 이름 변경
    -- 타겟 시각의 시간 부분
    target_time_only TIME; -- 변수 이름 변경

    -- 업무 시간 정의
    morning_start TIME := '09:00:00';
    morning_end TIME   := '12:00:00';
    afternoon_start TIME := '13:00:00';
    afternoon_end TIME   := '18:00:00';
BEGIN
    -- 1. 유효성 검사
    IF target_ts IS NULL THEN
        RETURN FALSE;
    END IF;

    -- 2. 요일 및 시간 추출
    -- ISODOW (ISO Day Of Week): 월요일(1)부터 일요일(7)까지 반환
    target_day_of_week := EXTRACT(ISODOW FROM target_ts); -- 변수 사용
    -- 시간 부분 추출
    target_time_only := target_ts::TIME; -- 변수 사용

    -- 3. 평일(월요일=1 ~ 금요일=5)인지 확인
    IF target_day_of_week BETWEEN 1 AND 5 THEN -- 변수 사용
        -- 4. 업무 시간 범위 내에 있는지 확인

        -- 오전 업무 시간 (09:00:00 <= target_time_only < 12:00:00)
        IF target_time_only >= morning_start AND target_time_only < morning_end THEN -- 변수 사용
            RETURN TRUE;
        -- 오후 업무 시간 (13:00:00 <= target_time_only < 18:00:00)
        ELSIF target_time_only >= afternoon_start AND target_time_only < afternoon_end THEN -- 변수 사용
            RETURN TRUE;
        END IF;
    END IF;

    -- 평일이 아니거나 (주말), 업무 시간 범위 외인 경우 (점심 시간 포함)
    RETURN FALSE;
END;
$$ LANGUAGE plpgsql;
