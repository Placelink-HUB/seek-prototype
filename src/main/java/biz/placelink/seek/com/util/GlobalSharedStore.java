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
 */
package biz.placelink.seek.com.util;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jakarta.servlet.ServletContext;

/**
 * 애플리케이션 전역에서 데이터를 공유하기 위한 키-값 저장소입니다.
 * <p>
 * {@link ServletContext}를 사용하여 애플리케이션의 모든 범위(서블릿, 컨트롤러, 서비스 등)에서
 * 접근 가능한 싱글톤과 유사한 저장 공간을 제공합니다.
 * 저장되는 각 항목은 만료 시간(TTL, Time-To-Live)을 가질 수 있으며, 만료된 데이터는 조회 시 자동으로 삭제됩니다.
 * {@link ConcurrentHashMap}을 기반으로 하여 멀티스레드 환경에서도 안전하게 사용할 수 있습니다.
 *
 * <h3>사용 방법:</h3>
 * <p>
 * 이 클래스는 Spring의 {@link Component}로 등록되어 있으므로, 필요한 곳에서 의존성 주입을 통해 사용할 수 있습니다.
 *
 * <pre>{@code
 * @Service
 * public class MyService {
 *
 *     private final GlobalSharedStore globalStore;
 *
 *     // 생성자를 통해 GlobalSharedStore 주입
 *     public MyService(GlobalSharedStore globalStore) {
 *         this.globalStore = globalStore;
 *     }
 *
 *     public void saveData() {
 *         // "myKey"라는 키로 "myValue"를 10분(600,000ms) 동안 저장
 *         globalStore.put("myKey", "myValue", 600_1000);
 *     }
 *
 *     public void readData() {
 *         // "myKey"로 데이터 조회
 *         Object value = globalStore.get("myKey");
 *         if (value != null) {
 *             System.out.println("저장된 값: " + value);
 *         } else {
 *             System.out.println("값이 없거나 만료되었습니다.");
 *         }
 *     }
 *
 *     public void deleteData() {
 *         // "myKey" 데이터 삭제
 *         globalStore.remove("myKey");
 *     }
 * }
 * }</pre>
 *
 * @see ServletContext
 * @see ConcurrentHashMap
 */
@Component
public class GlobalSharedStore {

    // ServletContext에 저장될 전역 저장소의 속성 키
    private static final String ATTR_KEY = "GLOBAL_SHARED_STORE";

    private final ServletContext servletContext;

    /**
     * GlobalSharedStore를 생성하고 ServletContext에 전역 저장소를 초기화합니다.
     * 저장소가 이미 존재하는 경우 기존 저장소를 사용하고, 없는 경우 새로 생성합니다.
     *
     * @param servletContext Spring에 의해 주입되는 서블릿 컨텍스트
     */
    public GlobalSharedStore(ServletContext servletContext) {
        this.servletContext = servletContext;
        // ServletContext에 전역 저장소(Map)가 없으면 새로 생성하여 설정
        servletContext.setAttribute(ATTR_KEY, servletContext.getAttribute(ATTR_KEY) != null
                ? servletContext.getAttribute(ATTR_KEY)
                : new ConcurrentHashMap<String, Entry>());
    }

    /**
     * ServletContext에서 전역 저장소로 사용되는 Map을 가져옵니다.
     *
     * @return {@code Map<String, Entry>} 형태의 전역 저장소
     */
    @SuppressWarnings("unchecked")
    private Map<String, Entry> map() {
        return (Map<String, Entry>) servletContext.getAttribute(ATTR_KEY);
    }

    /**
     * 저장소에 저장될 데이터 항목을 나타내는 내부 클래스.
     * 실제 값과 만료 시간을 포함합니다.
     */
    private static class Entry {
        final Object value;
        final long expireAt; // 데이터가 만료되는 시간 (Epoch milliseconds)

        Entry(Object value, long ttlMillis) {
            this.value = value;
            this.expireAt = Instant.now().toEpochMilli() + ttlMillis;
        }

        /**
         * 데이터가 만료되었는지 확인합니다.
         *
         * @return 만료되었으면 true, 그렇지 않으면 false
         */
        boolean expired() {
            return Instant.now().toEpochMilli() > expireAt;
        }
    }

    /**
     * 지정된 키와 값, 그리고 만료 시간(TTL)을 저장소에 추가합니다.
     *
     * @param key       저장할 데이터의 키
     * @param value     저장할 데이터 값
     * @param ttlMillis 데이터의 유효 시간 (밀리초 단위)
     */
    public void put(String key, Object value, long ttlMillis) {
        map().put(key, new Entry(value, ttlMillis));
    }

    /**
     * 지정된 키에 해당하는 값을 저장소에서 조회합니다.
     * 만약 데이터가 만료되었다면, 저장소에서 해당 데이터를 자동으로 삭제하고 null을 반환합니다.
     *
     * @param key 조회할 데이터의 키
     * @return 조회된 데이터 값. 키가 없거나 데이터가 만료된 경우 {@code null}
     */
    public Object get(String key) {
        Entry e = map().get(key);
        if (e == null)
            return null;

        // 데이터가 만료되었는지 확인
        if (e.expired()) {
            // 만료되었다면 저장소에서 제거하고 null 반환
            map().remove(key);
            return null;
        }
        return e.value;
    }

    /**
     * 지정된 키에 해당하는 값을 저장소에서 삭제합니다.
     *
     * @param key 삭제할 데이터의 키
     */
    public void remove(String key) {
        map().remove(key);
    }
}
