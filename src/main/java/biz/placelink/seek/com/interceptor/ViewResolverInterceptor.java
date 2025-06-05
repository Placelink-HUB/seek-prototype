package biz.placelink.seek.com.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import biz.placelink.seek.com.constants.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2024. 06. 10.      s2          최초생성
 * </pre>
 */
@Component
public class ViewResolverInterceptor implements HandlerInterceptor {

    private static Map<String, Object> consts = null;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        this.setConsts();
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            String viewName = modelAndView.getViewName();

            if (viewName != null && !viewName.equals("jsonView") && !viewName.equals("downloadView")
                    && !viewName.startsWith("redirect:") && !viewName.startsWith("forward:")) {
                ViewResolverInterceptor.consts.put("SERVLET_PATH", request.getServletPath());
                modelAndView.addObject("Constants", ViewResolverInterceptor.getConsts());
            }
        }

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    private void setConsts() {
        if (ViewResolverInterceptor.consts == null) {
            ViewResolverInterceptor.consts = new HashMap<>();

            Constants.setConstsMap(consts);
        }
    }

    public static Map<String, Object> getConsts() {
        return ViewResolverInterceptor.consts;
    }

}
