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
CREATE OR REPLACE FUNCTION FN_CALCULATE_BUSINESS_HOURS(
    start_ts TIMESTAMPTZ,
    end_ts TIMESTAMPTZ
)RETURNS INTERVAL AS $$
DECLARE
    -- 총 업무 시간을 저장할 변수 (초기값: 0초)
    total_business_hours INTERVAL := '0 seconds';
    -- 루프를 돌며 날짜를 추적할 변수
    loop_day DATE;

    -- 업무 시간 정의
    morning_start_time TIME := '09:00:00'; -- 오전 업무 시작
    morning_end_time TIME   := '12:00:00'; -- 오전 업무 종료 (점심 시작)
    afternoon_start_time TIME := '13:00:00'; -- 오후 업무 시작 (점심 종료)
    afternoon_end_time TIME  := '18:00:00'; -- 오후 업무 종료

    -- 각 업무 구간의 실제 시작/종료 시각 변수 (날짜 + 시간)
    segment_start TIMESTAMPTZ;
    segment_end TIMESTAMPTZ;
    -- 이벤트 시각과 업무 구간이 겹치는 부분의 시작/종료 시각
    overlap_start TIMESTAMPTZ;
    overlap_end TIMESTAMPTZ;
BEGIN
    -- 1. 유효성 검사: 시작 시간이 종료 시간보다 크거나 같거나, NULL인 경우 0 반환
    IF start_ts IS NULL OR end_ts IS NULL OR start_ts >= end_ts THEN
        RETURN '0 seconds';
    END IF;

    -- 시작 시각의 '날짜' 부분만 추출하여 루프 시작점 설정
    loop_day := date_trunc('day', start_ts)::DATE;

    -- 2. 시작일의 자정부터 종료일의 자정까지 하루씩 루프
    WHILE loop_day <= date_trunc('day', end_ts)::DATE LOOP

        -- 3. 평일(월요일=1, 금요일=5)인지 확인 (ISO 요일 번호 사용)
        IF EXTRACT(ISODOW FROM loop_day) BETWEEN 1 AND 5 THEN

            -- 4. 오전 업무 구간 (09:00 ~ 12:00) 계산
            segment_start := loop_day::TIMESTAMPTZ + morning_start_time;
            segment_end := loop_day::TIMESTAMPTZ + morning_end_time;

            -- 이벤트 시각(start_ts, end_ts)과 오전 업무 시간(segment_start, segment_end)의 겹치는 구간 찾기
            overlap_start := GREATEST(start_ts, segment_start); -- 실제 시작점
            overlap_end := LEAST(end_ts, segment_end);         -- 실제 종료점

            -- 겹치는 구간이 유효한 경우 (시작 시각이 종료 시각보다 이른 경우) 시간에 추가
            IF overlap_end > overlap_start THEN
                total_business_hours := total_business_hours + (overlap_end - overlap_start);
            END IF;

            -- 5. 오후 업무 구간 (13:00 ~ 18:00) 계산
            segment_start := loop_day::TIMESTAMPTZ + afternoon_start_time;
            segment_end := loop_day::TIMESTAMPTZ + afternoon_end_time;

            -- 이벤트 시각과 오후 업무 시간의 겹치는 구간 찾기
            overlap_start := GREATEST(start_ts, segment_start);
            overlap_end := LEAST(end_ts, segment_end);

            -- 겹치는 구간이 유효한 경우 시간에 추가
            IF overlap_end > overlap_start THEN
                total_business_hours := total_business_hours + (overlap_end - overlap_start);
            END IF;

        END IF;

        -- 다음 날로 이동
        loop_day := loop_day + INTERVAL '1 day';
    END LOOP;

    -- 계산된 총 업무 시간 반환
    RETURN total_business_hours;
END;
$$ LANGUAGE plpgsql;
