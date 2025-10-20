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
CREATE OR REPLACE FUNCTION FN_PREPARE_TEXT_CONTENT_FOR_ANALYSIS()
    RETURNS TRIGGER AS $$
DECLARE
    analysis_id UUID := GEN_RANDOM_UUID();
    table_name TEXT := TG_ARGV[0];            -- 대상 테이블명
    column_name TEXT := TG_ARGV[1];           -- 대상 컬럼명
    pk_column_names TEXT := TG_ARGV[2];       -- 프라이머리 키 컬럼명들 (복합키인 경우 쉼표로 구분)
    type_column_name TEXT := TG_ARGV[3];      -- 타입 컬럼명
    type_compare_value TEXT := TG_ARGV[4];    -- 타입 비교값 (타입 컬럼이 타입 비교값과 일치할때 등록한다.)
    pk_columns TEXT[] := STRING_TO_ARRAY(pk_column_names, ','); -- 프라이머리 키 컬럼 배열
    pk_values TEXT[] := '{}';                 -- 프라이머리 키 값 배열
    pk_combined TEXT;                         -- 합쳐진 프라이머리 키 값
    column_type TEXT;                         -- 기본값 'TEXT'
    column_value TEXT;                        -- 대상 컬럼 값
    i INT;                                    -- 루프 인덱스
BEGIN
    /**
     * table_name 테이블의 column_name 컬럼 내용을 SEEK_ANALYSIS_RESULT 테이블로 이동하고,
     * 분석을 위한 준비 작업을 수행하며, pk_combined 가 SEEK_ANALYSIS_RESULT 에 없을 때만 삽입한다.
     */

    -- 프라이머리 키 값 동적으로 가져오기
    FOR i IN 1..ARRAY_LENGTH(pk_columns, 1) LOOP
        DECLARE
            pk_value TEXT;
        BEGIN
            -- NEW 에서 각 프라이머리 키 컬럼 값 추출
            EXECUTE format('SELECT ($1).%I::TEXT', TRIM(pk_columns[i]))
                INTO pk_value
                USING NEW;
                -- 배열에 값 추가 (NULL 은 빈 문자열로 처리)
            pk_values := pk_values || COALESCE(pk_value, '');
        END;
    END LOOP;

    -- 프라이머리 키 값들을 쉼표로 결합
    pk_combined := table_name || '.' || column_name || '.' || ARRAY_TO_STRING(pk_values, ',');

    -- 동적으로 대상 컬럼 값 가져오기
    EXECUTE format('SELECT ($1).%I', column_name)
        INTO column_value
        USING NEW;

    -- 타입 컬럼 처리 (NULL 이 아니면 동적 값 사용)
    IF type_column_name IS NOT NULL THEN
        -- type_column_name 이 지정된 경우, NEW 에서 해당 컬럼 값 가져오기
        EXECUTE format('SELECT ($1).%I', type_column_name)
            INTO column_type
            USING NEW;
    END IF;

    -- 타입 비교 값이 NULL 이거나 대상 컬럼 값과 같고, pk_combined 가 SEEK_ANALYSIS_RESULT_DATABASE 의 TARGET_INFORMATION 에 없는 경우에만 삽입
    IF (type_compare_value IS NULL OR column_type = type_compare_value) AND NOT EXISTS (
        SELECT 1
        FROM SEEK_DATABASE_ANALYSIS
        WHERE TARGET_INFORMATION = pk_combined
    ) THEN
        INSERT INTO SEEK_ANALYSIS (
            ANALYSIS_ID,
            ANALYSIS_MODE_CCD,
            ANALYSIS_STATUS_CCD,
            ANALYSIS_RESULT_ID,
            CREATE_DT,
            MODIFY_DT
        ) VALUES (
            analysis_id,
            'DATABASE',
            'WAIT',
            NULL,
            CLOCK_TIMESTAMP(),
            CLOCK_TIMESTAMP()
        );

        INSERT INTO SEEK_DATABASE_ANALYSIS (
            ANALYSIS_ID,
            TARGET_INFORMATION,
            CONTENT
        ) VALUES (
            analysis_id,
            pk_combined,
            column_value
        );

        -- NEW 의 대상 컬럼에 UUID 반영
        NEW := NEW #= hstore(column_name, '$WT{' || analysis_id || '}');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
