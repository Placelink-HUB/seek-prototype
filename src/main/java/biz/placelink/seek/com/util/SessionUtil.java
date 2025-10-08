/**
 * 인증 세션관리
 *
 * @author GOONO
 * @version 1.0
 * @see Copyright (C) All right reserved.
 * @since 2021. 9. 17. 오후 5:18:11
 */
package biz.placelink.seek.com.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import biz.placelink.seek.com.security.CustomUserDetails;
import biz.placelink.seek.system.user.vo.UserVO;
import jakarta.servlet.http.HttpSession;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 02. 01.      s2          최초생성
 * </pre>
 */
@Component
public class SessionUtil {

    public static HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

    /**
     * 현재 세션에 지정된 이름으로 속성값을 저장
     *
     * @param name  세션에 저장할 속성의 이름
     * @param value 세션에 저장할 속성의 값 (Object 타입)
     */
    public static void setSessionAttribute(String name, Object value) {
        getSession().setAttribute(name, value);
    }

    /**
     * 세션에서 특정 이름의 속성을 조회하고 지정된 타입으로 캐스팅하여 반환
     *
     * @param name      조회할 세션 속성의 이름
     * @param castClass 반환값을 캐스팅할 클래스 타입
     * @param <T>       반환될 객체의 타입
     * @return 세션에서 조회한 속성값을 지정된 타입으로 캐스팅한 객체. 속성이 없거나 캐스팅에 실패하면 null 반환
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttribute(String name, Class<T> castClass) {
        T returnValue;
        try {
            returnValue = (T) getSession().getAttribute(name);
        } catch (Exception e) {
            returnValue = null;
        }
        return returnValue;
    }

    /**
     * 현재 세션에서 로그인한 사용자 정보 조회
     *
     * @return 로그인한 사용자의 UserVO 객체. 로그인 정보가 없으면 null 을 반환
     */
    public static UserVO getSessionUser() {
        UserVO result = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            result = new UserVO((CustomUserDetails) authentication.getPrincipal());
        }
        return result;
    }

    /**
     * 현재 세션에서 로그인한 사용자 식별자 조회
     *
     * @return 로그인한 사용자의 식별자. 로그인 정보가 없으면 null 을 반환
     */
    public static String getSessionUserUid() {
        UserVO userVO = getSessionUser();
        return userVO != null ? userVO.getUserUid() : null;
    }

    /**
     * 현재 세션에서 로그인한 사용자 ID 조회
     *
     * @return 로그인한 사용자의 ID. 로그인 정보가 없으면 null 을 반환
     */
    public static String getSessionUserId() {
        UserVO userVO = getSessionUser();
        return userVO != null ? userVO.getUserId() : null;
    }

}
