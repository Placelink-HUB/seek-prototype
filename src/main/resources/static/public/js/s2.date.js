/*
 * S2Util Library
 *
 * Copyright (c) 2020 - 2025 devers2 (Daejeon, Korea)
 * All rights reserved.
 *
 * This source code is the proprietary asset of devers2.
 * Unauthorized copying, modification, or redistribution of this file is strictly prohibited.
 */

/**
 * S2Date 래퍼(Date) 객체를 생성한다.
 * @typedef {S2Day} S2Date
 *
 * @param {Date | string | number | S2Date} [dateInput]
 * - **(생략)**: 현재 시간 (new Date())
 * - **Date**: 기존 Date 객체 (복사하여 사용)
 * - **number**: 1970-01-01 UTC 기준 밀리초 (타임스탬프)
 * - **string**: ISO 8601 형식 문자열 ('YYYY-MM-DDTHH:mm:ssZ', '2025-10-30' / '2025-10-30T15:30:00+09:00')
 * @param {string} [formatStr] - dateInput이 문자열일 때, 파싱할 포맷. (예: 'YYYY-MM-DD')
 * @returns {S2Date}
 *
 * @example
 * <script type="module">
 * import { S2Date } from './js/s2.date.js'; // Thymeleaf: import { S2Date } from '[[@{/js/s2.date.js}]]'
 * S2Date().add(5, 'days').format('YYYY-MM-DD'); // 오늘 날짜로부터 5일 후의 날짜를 'YYYY-MM-DD' 형식으로 반환
 * S2Date('2025-10-30').subtract(1, 'month'); // 2025년 10월 30일에서 1개월 뺀 날짜 객체 반환
 * ...
 * </script>
 */
export const S2Date = (dateInput, formatStr) => {
    return new S2Day(dateInput, formatStr);
};

/**
 * @class S2Day
 * @description
 * Date 객체를 감싸는 래퍼 클래스
 * (S2Date() 팩토리 함수를 통해 생성된다.)
 */
class S2Day {
    /*
     * ES2015 호환성을 위해 Public field 로 선언 (ES2022를 지원하는 경우 Private field로 변경 가능, #_date, #_isValid)
     * _date → #_date
     * _isValid → #_isValid
     */
    _date;
    _isValid = true; // 유효한 날짜인지 여부

    constructor(dateInput, formatStr) {
        if (dateInput instanceof S2Day) {
            this._date = new Date(dateInput.toDate());
            this._isValid = dateInput.isValid();
            return;
        }
        if (dateInput === undefined) {
            this._date = new Date();
            return;
        }

        // 문자열 처리 로직
        if (typeof dateInput === 'string') {
            // 파싱 시 누락된 연/월/일의 기본값을 현재 시각으로 설정
            const now = new Date();
            let year = now.getFullYear(),
                month = now.getMonth() + 1,
                day = now.getDate();
            let hour = 0,
                minute = 0,
                second = 0,
                millisecond = 0;
            let parsed = false;

            // 1. 사용할 포맷 결정
            // (formatStr가 없으면, 'YYYY-MM-DD'와 'YYYYMMDD'를 기본으로 시도)
            const formatsToTry = formatStr ? [formatStr] : ['YYYY-MM-DD', 'YYYYMMDD'];

            let parser;
            let match;
            for (const fmt of formatsToTry) {
                parser = _buildParser(fmt); // 파서 동적 생성
                if (parser.regex.test(dateInput)) {
                    match = dateInput.match(parser.regex);
                    break; // 매칭 성공
                }
                parser = null; // 실패
            }

            if (parser && match) {
                // 2. 파싱 성공: 캡처 그룹과 토큰 이름을 매핑
                const values = {};
                parser.groups.forEach((tokenName, index) => {
                    values[tokenName] = match[index + 1]; // 숫자가 아닌 AM/PM도 있으므로 parseInt 제거
                });

                // 3. [개선] YY (2자리 연도) 파싱 로직 및 누락된 값 처리
                if (values.YYYY) {
                    year = parseInt(values.YYYY);
                } else if (values.YY) {
                    const twoDigitYear = parseInt(values.YY);
                    const currentYear = now.getFullYear();
                    const currentCentury = Math.floor(currentYear / 100) * 100; // 2000
                    const pivot = (currentYear + 50) % 100; // 2075 -> 75

                    if (twoDigitYear > pivot) {
                        year = twoDigitYear + currentCentury - 100; // (예: 80 -> 1980)
                    } else {
                        year = twoDigitYear + currentCentury; // (예: 15 -> 2015)
                    }
                }
                // (values.YYYY나 YY가 없으면 기본값인 'now.getFullYear()' 유지)

                if (values.MM || values.M) {
                    month = parseInt(values.MM || values.M);
                }
                if (values.DD || values.D) {
                    day = parseInt(values.DD || values.D);
                }

                hour = parseInt(values.HH || values.H || values.hh || values.h || 0);

                // 4. AM/PM 파싱 로직
                const ampm = values.A || values.a;
                if (ampm && (values.hh || values.h)) {
                    // 12시간제 토큰이 있을 때만
                    const isPM = ampm.toLowerCase() === 'pm';
                    if (isPM && hour < 12) {
                        // 1:00 PM (hour=1) -> 13
                        hour += 12;
                    } else if (!isPM && hour === 12) {
                        // 12:00 AM (hour=12) -> 0
                        hour = 0;
                    }
                }

                minute = parseInt(values.mm || values.m || 0);
                second = parseInt(values.ss || values.s || 0);
                millisecond = parseInt(values.SSS || 0);
                parsed = true;
            } else if (formatStr) {
                // formatStr가 주어졌는데 파싱 실패
                this._isValid = false;
                this._date = new Date(NaN); // Invalid Date
            } else {
                // formatStr가 없고, 기본 파싱(YYYY-MM-DD 등)도 실패
                // (표준 ISO 문자열('...T...') 또는 'abc' 등)
                this._date = new Date(dateInput);
            }

            // 5. 파싱 성공 시(parsed=true), Date 객체 생성 및 오버플로우 검증
            if (parsed) {
                const monthIndex = month - 1; // Date 객체는 0-based
                this._date = new Date(year, monthIndex, day, hour, minute, second, millisecond);

                // 날짜 오버플로우 검증 (예: 2025-02-30 -> 2025-03-02 처럼 넘어가는 경우 _isValid를 false로 설정)
                if (this._date.getFullYear() !== year || this._date.getMonth() !== monthIndex || this._date.getDate() !== day || this._date.getHours() !== hour || this._date.getMinutes() !== minute || this._date.getSeconds() !== second || this._date.getMilliseconds() !== millisecond) {
                    this._isValid = false; // 플래그를 false로 설정
                }
            }
        } else {
            // --- 문자열이 아님 (number, Date 객체) ---
            this._date = new Date(dateInput);
        }

        // 최종 검증 (예: 'abc' -> Invalid Date): getTime()은 Invalid Date에 대해 NaN을 반환한다.
        if (isNaN(this._date.getTime())) {
            this._isValid = false;
        }
    }

    /**
     * [내부용] 입력값을 S2Day 객체로 정규화합니다.
     * @param {Date | string | number | S2Date} [dateInput]
     * @returns {S2Date}
     * @private
     */
    _normalizeInput(dateInput) {
        if (dateInput instanceof S2Day) {
            return dateInput;
        }
        return new S2Day(dateInput);
    }

    /**
     * 지정된 값만큼 시간을 더한다.
     *
     * @param {number} value - 더할 시간 값 (예: 5, 10).
     * @param {'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'years' | 'months' | 'days' | 'hours' | 'minutes' | 'seconds'} unit - 시간 단위.
     * @returns {S2Date} - 새로운 S2Date 객체 (체이닝)
     *
     * @example
     * S2Date().add(5, 'days'); // 오늘 날짜로부터 5일 후의 날짜 객체 반환
     * S2Date('2025-10-30').add(1, 'month'); // 2025년 10월 30일에서 1개월 더한 날짜 객체 반환
     */
    add(value, unit) {
        const newDate = new Date(this._date); // 불변성 유지를 위해 복사

        switch (unit) {
            case 'year':
            case 'years':
                newDate.setFullYear(newDate.getFullYear() + value);
                break;
            case 'month':
            case 'months':
                newDate.setMonth(newDate.getMonth() + value);
                break;
            case 'day':
            case 'days':
                newDate.setDate(newDate.getDate() + value);
                break;
            case 'hour':
            case 'hours':
                newDate.setHours(newDate.getHours() + value);
                break;
            case 'minute':
            case 'minutes':
                newDate.setMinutes(newDate.getMinutes() + value);
                break;
            case 'second':
            case 'seconds':
                newDate.setSeconds(newDate.getSeconds() + value);
                break;
            default:
                // 지원하지 않는 단위에 대한 예외 처리
                throw new Error(`[S2Date] Unknown unit: ${unit}`);
        }

        const result = new S2Day(newDate);

        // 유효성 전파: 부모의 유효성(isValid)이 false였다면(예: 오버플로우), 자식 객체도 (설령 날짜 계산이 유효하더라도) false 상태를 유지한다.
        if (!this._isValid) {
            result._setValid(false);
        }

        return result;
    }

    /**
     * 지정된 값만큼 시간을 뺀다.
     * @param {number} value - 뺄 시간 값 (예: 5, 10).
     * @param {'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'years' | 'months' | 'days' | 'hours' | 'minutes' | 'seconds'} unit - 시간 단위.
     * @returns {S2Date} - 새로운 S2Date 객체 (체이닝)
     *
     * @example
     * S2Date().subtract(5, 'days'); // 오늘 날짜로부터 5일 전의 날짜 객체 반환
     * S2Date('2025-10-30').subtract(1, 'month'); // 2025년 10월 30일에서 1개월 이전 날짜 객체 반환
     */
    subtract(value, unit) {
        return this.add(-value, unit); // add 메소드 재활용
    }

    /**
     * 날짜를 포맷팅한다.
     * (문자열로 반환되기 때문에 체이닝의 마지막 단계에서 사용된다.)
     *
     * @param {string} [formatStr='YYYY/MM/DD HH:mm:ss'] - 포맷 문자열.
     * @param {string} [locale='ko'] - 로케일: 'ko' (기본, 한국어) | 'en' (영어)
     * @returns {string} - 포맷된 날짜 문자열
     *
     * @example
     * S2Date().format('YYYY년 MM월 DD일'); // '2025년 10월 30일' (오늘 날짜 기준)
     * S2Date('2025-10-30T15:30:00+09:00').format('YYYY-MM-DD HH:mm:ss', 'en'); // '2025-10-30 15:30:00'
     *
     * @description
     * ### 지원하는 포맷 토큰
     * (JSDoc 생략 - 기존 코드와 동일)
     */
    format(formatStr = 'YYYY/MM/DD HH:mm:ss', locale = 'ko') {
        return _formatDate(this._date, formatStr, locale);
    }

    // --- [신규] Getter 메소드 ---

    /**
     * 연도를 반환합니다.
     * @returns {number} (예: 2025)
     */
    year() {
        return this._date.getFullYear();
    }

    /**
     * 월을 반환합니다. (0-indexed)
     * @returns {number} (0=1월, 1=2월, ... 11=12월)
     */
    month() {
        return this._date.getMonth();
    }

    /**
     * 날짜(일)를 반환합니다.
     * @returns {number} (1~31)
     */
    date() {
        return this._date.getDate();
    }

    /**
     * 요일을 반환합니다. (0-indexed)
     * @returns {number} (0=일요일, 1=월요일, ... 6=토요일)
     */
    day() {
        return this._date.getDay();
    }

    /**
     * 시간(24시)을 반환합니다.
     * @returns {number} (0~23)
     */
    hour() {
        return this._date.getHours();
    }

    /**
     * 분을 반환합니다.
     * @returns {number} (0~59)
     */
    minute() {
        return this._date.getMinutes();
    }

    /**
     * 초를 반환합니다.
     * @returns {number} (0~59)
     */
    second() {
        return this._date.getSeconds();
    }

    // --- [신규] 비교 (Comparison) 메소드 ---

    /**
     * 현재 S2Date 객체가 다른 날짜보다 이전인지 확인합니다.
     * @param {Date | string | number | S2Date} otherDate - 비교할 날짜
     * @param {'year' | 'month' | 'day' | 'hour' | 'minute' | 'second'} [unit] - 비교할 단위 (해당 단위까지 동일하면 false)
     * @returns {boolean}
     *
     * @example
     * S2Date('2025-01-01').isBefore('2025-01-02') // true
     * S2Date('2025-01-01 12:00').isBefore('2025-01-01 13:00', 'day') // false
     */
    isBefore(otherDate, unit) {
        const other = this._normalizeInput(otherDate);
        if (!this.isValid() || !other.isValid()) return false;

        if (unit) {
            const thisStart = this.startOf(unit);
            const otherStart = other.startOf(unit);
            return thisStart.toDate().getTime() < otherStart.toDate().getTime();
        }

        return this.toDate().getTime() < other.toDate().getTime();
    }

    /**
     * 현재 S2Date 객체가 다른 날짜보다 이후인지 확인합니다.
     * @param {Date | string | number | S2Date} otherDate - 비교할 날짜
     * @param {'year' | 'month' | 'day' | 'hour' | 'minute' | 'second'} [unit] - 비교할 단위 (해당 단위까지 동일하면 false)
     * @returns {boolean}
     *
     * @example
     * S2Date('2025-01-02').isAfter('2025-01-01') // true
     */
    isAfter(otherDate, unit) {
        const other = this._normalizeInput(otherDate);
        if (!this.isValid() || !other.isValid()) return false;

        if (unit) {
            const thisStart = this.startOf(unit);
            const otherStart = other.startOf(unit);
            return thisStart.toDate().getTime() > otherStart.toDate().getTime();
        }

        return this.toDate().getTime() > other.toDate().getTime();
    }

    /**
     * 현재 S2Date 객체가 다른 날짜와 (지정된 단위까지) 동일한지 확인합니다.
     * @param {Date | string | number | S2Date} otherDate - 비교할 날짜
     * @param {'year' | 'month' | 'day' | 'hour' | 'minute' | 'second'} [unit] - 비교할 단위 (단위를 생략하면 ms까지 비교)
     * @returns {boolean}
     *
     * @example
     * S2Date('2025-01-01 12:00').isSame('2025-01-01 13:00', 'day') // true
     * S2Date('2025-01-01 12:00').isSame('2025-01-01 13:00') // false
     */
    isSame(otherDate, unit) {
        const other = this._normalizeInput(otherDate);
        if (!this.isValid() || !other.isValid()) return false;

        if (unit) {
            const thisStart = this.startOf(unit);
            const otherStart = other.startOf(unit);
            return thisStart.toDate().getTime() === otherStart.toDate().getTime();
        }

        return this.toDate().getTime() === other.toDate().getTime();
    }

    // --- [신규] 차이 (Difference) 메소드 ---

    /**
     * 다른 날짜와의 차이를 지정된 단위로 반환합니다. (내림 처리)
     * @param {Date | string | number | S2Date} otherDate - 비교할 날짜
     * @param {'year' | 'month' | 'day' | 'hour' | 'minute' | 'second' | 'ms'} [unit='ms'] - 반환할 단위
     * @param {boolean} [float=false] - 소수점까지 반환할지 여부 (true)
     * @returns {number}
     *
     * @example
     * S2Date('2025-10-31').diff('2025-10-30', 'day') // 1
     * S2Date('2025-10-30').diff('2025-10-31', 'day') // -1
     * S2Date('2026-01-01').diff('2025-01-01', 'month') // 12
     */
    diff(otherDate, unit = 'ms', float = false) {
        const other = this._normalizeInput(otherDate);
        if (!this.isValid() || !other.isValid()) return NaN;

        const thisMs = this.toDate().getTime();
        const otherMs = other.toDate().getTime();
        const diffMs = thisMs - otherMs;

        let result;

        switch (unit) {
            case 'year':
            case 'years':
                result = this.year() - other.year() + (this.month() - other.month()) / 12;
                break;
            case 'month':
            case 'months': {
                // (연도 차이 * 12) + (월 차이)
                result = (this.year() - other.year()) * 12 + (this.month() - other.month());
                // (일/시간 차이로 소수점 계산 - dayjs 로직 간소화)
                const thisDay = this.date();
                const otherDay = other.date();
                if (thisDay < otherDay) {
                    result -= 1; // 아직 한 달이 다 안 찼으면 1을 뺌
                }
                break;
            }
            case 'day':
            case 'days':
                result = diffMs / 86400000; // (1000 * 60 * 60 * 24)
                break;
            case 'hour':
            case 'hours':
                result = diffMs / 3600000; // (1000 * 60 * 60)
                break;
            case 'minute':
            case 'minutes':
                result = diffMs / 60000; // (1000 * 60)
                break;
            case 'second':
            case 'seconds':
                result = diffMs / 1000;
                break;
            default: // 'ms'
                result = diffMs;
                break;
        }

        return float ? result : Math.trunc(result);
    }

    // --- [신규] 조작 (Manipulation) 메소드 ---

    /**
     * 지정된 단위의 시작 시간으로 설정된 새 S2Date 객체를 반환합니다.
     * @param {'year' | 'month' | 'day' | 'hour' | 'minute' | 'second'} unit - 단위
     * @returns {S2Date}
     *
     * @example
     * S2Date('2025-10-30 15:30').startOf('day') // 2025-10-30 00:00:00.000
     * S2Date('2025-10-30 15:30').startOf('month') // 2025-10-01 00:00:00.000
     */
    startOf(unit) {
        const newDate = new Date(this._date);

        switch (unit) {
            case 'year':
            case 'years':
                newDate.setMonth(0, 1); // 1월 1일
                newDate.setHours(0, 0, 0, 0);
                break;
            case 'month':
            case 'months':
                newDate.setDate(1); // 1일
                newDate.setHours(0, 0, 0, 0);
                break;
            case 'day':
            case 'days':
                newDate.setHours(0, 0, 0, 0); // 00:00:00.000
                break;
            case 'hour':
            case 'hours':
                newDate.setMinutes(0, 0, 0); // 00:00.000
                break;
            case 'minute':
            case 'minutes':
                newDate.setSeconds(0, 0); // 00.000
                break;
            case 'second':
            case 'seconds':
                newDate.setMilliseconds(0); // .000
                break;
        }

        const result = new S2Day(newDate);
        if (!this._isValid) result._setValid(false);
        return result;
    }

    /**
     * 지정된 단위의 끝 시간으로 설정된 새 S2Date 객체를 반환합니다.
     * @param {'year' | 'month' | 'day' | 'hour' | 'minute' | 'second'} unit - 단위
     * @returns {S2Date}
     *
     * @example
     * S2Date('2025-10-30 15:30').endOf('day') // 2025-10-30 23:59:59.999
     * S2Date('2025-02-15 15:30').endOf('month') // 2025-02-28 23:59:59.999
     */
    endOf(unit) {
        const newDate = new Date(this._date);

        switch (unit) {
            case 'year':
            case 'years':
                newDate.setMonth(11, 31); // 12월 31일
                newDate.setHours(23, 59, 59, 999);
                break;
            case 'month':
            case 'months':
                // 다음 달의 0번째 날(이달의 마지막 날)로 설정
                newDate.setMonth(newDate.getMonth() + 1, 0);
                newDate.setHours(23, 59, 59, 999);
                break;
            case 'day':
            case 'days':
                newDate.setHours(23, 59, 59, 999); // 23:59:59.999
                break;
            case 'hour':
            case 'hours':
                newDate.setMinutes(59, 59, 999); // 59:59.999
                break;
            case 'minute':
            case 'minutes':
                newDate.setSeconds(59, 999); // 59.999
                break;
            case 'second':
            case 'seconds':
                newDate.setMilliseconds(999); // .999
                break;
        }

        const result = new S2Day(newDate);
        if (!this._isValid) result._setValid(false);
        return result;
    }

    // --- [신규] 유틸리티 (Utilities) ---

    /**
     * 윤년인지 확인한다.
     *
     * @returns {boolean}
     */
    isLeapYear() {
        const year = this.year();
        return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0;
    }

    /**
     * 해당 월의 총 일수(28, 29, 30, 31)를 반환한다.
     * @returns {number}
     */
    daysInMonth() {
        // month()가 0-indexed이므로, +1을 하여 다음 달의 0번째 날(이달의 마지막 날)을 가져옴
        return new Date(this.year(), this.month() + 1, 0).getDate();
    }

    // --- [기존] 유효성 및 변환 메소드 ---

    /**
     * S2Date 객체가 생성 시점부터 유효한 날짜를 참조하는지 확인한다.
     * (예: '2025-02-30' 처럼 존재하지 않는 날짜는 false)
     *
     * @returns {boolean} - 유효하면 true, 아니면 false.
     *
     * @example
     * S2Date('2025-02-28', 'YYYY-MM-DD').isValid() // true
     * S2Date('2025-02-30', 'YYYY-MM-DD').isValid() // false
     * S2Date('abc').isValid() // false
     */
    isValid() {
        return this._isValid;
    }

    /**
     * [내부용] 체이닝 시 부모의 유효성(false)을 전파하기 위해 S2Date 객체의 유효성 플래그를 강제로 설정한다.
     *
     * @param {boolean} value - 설정할 유효성 값
     * @private
     */
    _setValid(value) {
        this._isValid = value;
    }

    /**
     * 내부의 원본 Date 객체를 반환한다.
     * @returns {Date}
     */
    toDate() {
        return this._date;
    }

    /**
     * S2Date 객체를 문자열로 변환한다.
     * @returns {string}
     */
    toString() {
        return this.isValid() ? this.format() : 'Invalid Date';
    }

    /**
     * 현재 시각의 S2Date 객체를 반환한다.
     * @returns {S2Date}
     *
     * @example
     * S2Date.now().add(1, 'day').format('YYYY-MM-DD');
     */
    static now() {
        return new S2Day();
    }
}

// ===============================================
// 헬퍼 함수 (Helper Functions)
// ===============================================

/**
 * 날짜를 지정된 포맷 문자열로 변환 (헬퍼 함수)
 *
 * @param {Date} [date=new Date()] - 포맷할 Date 객체
 * @param {string} [formatStr='YYYY/MM/DD HH:mm:ss'] - Day.js 스타일 포맷 문자열
 * @param {string} [locale='ko'] - 로케일: 'ko' (기본, 한국어) | 'en' (영어)
 * @returns {string} 포맷된 날짜 문자열
 */
function _formatDate(date = new Date(), formatStr = 'YYYY/MM/DD HH:mm:ss', locale = 'ko') {
    if (!(date instanceof Date) || isNaN(date)) return '';

    const pad = (n, len = 2) => '0'.repeat(Math.max(0, len - String(n).length)) + n;
    const pad3 = (n) => '0'.repeat(Math.max(0, 3 - String(n).length)) + n;

    const locales = {
        en: {
            weekdays: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
            weekdaysShort: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
            weekdaysMin: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
            months: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
            monthsShort: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
        },
        ko: {
            weekdays: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'],
            weekdaysShort: ['일', '월', '화', '수', '목', '금', '토'],
            weekdaysMin: ['일', '월', '화', '수', '목', '금', '토'],
            months: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
            monthsShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월']
        }
    };

    const loc = locales[locale] || locales.ko;
    const {weekdays, weekdaysShort, weekdaysMin, months, monthsShort} = loc;

    const YYYY = date.getFullYear();
    const YY = String(YYYY).slice(-2);
    const MMMM = months[date.getMonth()];
    const MMM = monthsShort[date.getMonth()];
    const MM = pad(date.getMonth() + 1);
    const M = date.getMonth() + 1;
    const DD = pad(date.getDate());
    const D = date.getDate();
    const dddd = weekdays[date.getDay()];
    const ddd = weekdaysShort[date.getDay()];
    const dd = weekdaysMin[date.getDay()];
    const d = date.getDay();
    const HH = pad(date.getHours());
    const H = date.getHours();
    const hh = pad(date.getHours() % 12 || 12);
    const h = date.getHours() % 12 || 12;
    const A = date.getHours() >= 12 ? 'PM' : 'AM';
    const a = date.getHours() >= 12 ? 'pm' : 'am';
    const mm = pad(date.getMinutes());
    const m = date.getMinutes();
    const ss = pad(date.getSeconds());
    const s = date.getSeconds();
    const SSS = pad3(date.getMilliseconds());
    const tzOffset = -date.getTimezoneOffset();
    const tzSign = tzOffset >= 0 ? '+' : '-';
    const tzHours = pad(Math.floor(Math.abs(tzOffset) / 60));
    const tzMinutes = pad(Math.abs(tzOffset) % 60);
    const Z = `${tzSign}${tzHours}:${tzMinutes}`;
    const ZZ = `${tzSign}${tzHours}${tzMinutes}`;
    const X = Math.floor(date.getTime() / 1000);
    const x = date.getTime();

    const map = {YYYY, YY, MMMM, MMM, MM, M, DD, D, dddd, ddd, dd, d, A, a, HH, H, hh, h, mm, m, ss, s, SSS, Z, ZZ, X, x};

    return formatStr.replace(/YYYY|YY|MMMM|MMM|MM|M|DD|D|dddd|ddd|dd|d|A|a|HH|H|hh|h|mm|m|ss|s|SSS|Z|ZZ|X|x/g, (m) => map[m]);
}

/** 문자열을 정규식 리터럴로 이스케이프하는 헬퍼 */
function _escapeRegex(s) {
    return s.replace(/[-/\\^$*+?.()|[\]{}]/g, '\\$&');
}

/** 포맷 토큰을 정규식 문자열로 매핑 (숫자 기반 토큰) */
const _parseTokenMap = {
    YYYY: '(\\d{4})',
    YY: '(\\d{2})',
    MM: '(\\d{2})',
    M: '(\\d{1,2})',
    DD: '(\\d{2})',
    D: '(\\d{1,2})',
    A: '(AM|PM)',
    a: '(am|pm)',
    HH: '(\\d{2})',
    H: '(\\d{1,2})',
    hh: '(\\d{2})',
    h: '(\\d{1,2})',
    mm: '(\\d{2})',
    m: '(\\d{1,2})',
    ss: '(\\d{2})',
    s: '(\\d{1,2})',
    SSS: '(\\d{3})'
};

/** _parseTokenMap의 키를 정규식으로 (긴 것부터) */
const _parseTokenRegex = /YYYY|YY|MM|M|DD|D|A|a|HH|H|hh|h|mm|m|ss|s|SSS/g;

/** 생성된 파서를 캐싱하기 위한 맵 */
const _parserCache = new Map();

/**
 * 포맷 문자열(예: 'YYYY년MM월')을 기반으로 파서 객체 { regex, groups }를 동적으로 생성한다.
 */
function _buildParser(formatStr) {
    // 1. 캐시 확인
    if (_parserCache.has(formatStr)) {
        return _parserCache.get(formatStr);
    }

    let finalRegexStr = '^';
    const groups = [];
    let lastIndex = 0;

    // 2. 포맷 문자열에서 토큰(YYYY 등)을 순회
    _parseTokenRegex.lastIndex = 0; // 정규식 인덱스 초기화
    let match;
    while ((match = _parseTokenRegex.exec(formatStr)) !== null) {
        // 3. 토큰 앞의 리터럴 문자(예: '년') 처리
        if (match.index > lastIndex) {
            const literal = formatStr.substring(lastIndex, match.index);
            finalRegexStr += _escapeRegex(literal); // 정규식으로 이스케이프
        }

        // 4. 토큰(예: 'YYYY')을 정규식 그룹(예: '(\d{4})')으로 변환
        const token = match[0];
        finalRegexStr += _parseTokenMap[token];
        groups.push(token); // 캡처 그룹 순서 저장

        lastIndex = _parseTokenRegex.lastIndex;
    }

    // 5. 마지막 토큰 뒤의 리터럴 문자(예: '월') 처리
    if (lastIndex < formatStr.length) {
        const literal = formatStr.substring(lastIndex);
        finalRegexStr += _escapeRegex(literal);
    }

    finalRegexStr += '$'; // 문자열 끝 일치

    // 6. 파서 객체 생성 및 캐시 저장
    const parser = {
        regex: new RegExp(finalRegexStr),
        groups: groups
    };
    _parserCache.set(formatStr, parser);
    return parser;
}
