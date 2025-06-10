package biz.placelink.seek.com.interceptor;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            String viewName = modelAndView.getViewName();

            if (viewName != null && !viewName.equals("jsonView") && !viewName.equals("downloadView")
                    && !viewName.startsWith("redirect:") && !viewName.startsWith("forward:")) {
                modelAndView.addObject("SERVLET_PATH", request.getServletPath());
            }
        }

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

}
