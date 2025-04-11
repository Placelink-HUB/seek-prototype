CREATE OR REPLACE FUNCTION FN_PREPARE_TEXT_CONTENT_FOR_ANALYSIS()
    RETURNS TRIGGER AS $$
DECLARE
    analysis_uuid UUID := GEN_RANDOM_UUID();
    table_name TEXT := TG_ARGV[0];            -- 대상 테이블명
    column_name TEXT := TG_ARGV[1];           -- 대상 컬럼명
    pk_column_names TEXT := TG_ARGV[2];       -- 프라이머리 키 컬럼명들 (복합키인 경우 쉼표로 구분)
    type_column_name TEXT := TG_ARGV[3];      -- 타입 컬럼명
    pk_columns TEXT[] := STRING_TO_ARRAY(pk_column_names, ','); -- 프라이머리 키 컬럼 배열
    pk_values TEXT[] := '{}';                 -- 프라이머리 키 값 배열
    pk_combined TEXT;                         -- 합쳐진 프라이머리 키 값
    analysis_type TEXT := 'TEXT';             -- 기본값 'TEXT'
    column_value TEXT;                        -- 대상 컬럼 값
    i INT;                                    -- 루프 인덱스
BEGIN
    /**
     * table_name 테이블의 column_name 컬럼 내용을 SEEK_ANALYSIS 테이블로 이동하고,
     * 분석을 위한 준비 작업을 수행하며, pk_combined 가 SEEK_ANALYSIS 에 없을 때만 삽입한다.
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
            INTO analysis_type
            USING NEW;
    END IF;

    -- pk_combined 가 SEEK_ANALYSIS 의 TARGET_INFORMATION 에 없는 경우에만 삽입
    IF NOT EXISTS (
        SELECT 1
        FROM SEEK_ANALYSIS
        WHERE TARGET_INFORMATION = pk_combined
    ) THEN
        INSERT INTO SEEK_ANALYSIS (
            ANALYSIS_ID,
            ANALYSIS_TYPE_CCD,
            ANALYSIS_STATUS_CCD,
            TARGET_INFORMATION,
            ANALYSIS_CONTENT,
            ANALYSIS_START_DT,
            ANALYSIS_END_DT,
            ANALYSIS_TIME,
            CREATE_DT
        ) VALUES (
            analysis_uuid,
            analysis_type,
            'WAIT',
            pk_combined,
            column_value,
            NULL,
            NULL,
            NULL,
            CLOCK_TIMESTAMP()
        );

        -- NEW 의 대상 컬럼에 UUID 반영
        NEW := NEW #= hstore(column_name, '$WT{' || analysis_uuid || '}');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;