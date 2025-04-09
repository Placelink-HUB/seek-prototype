package biz.placelink.seek.com.advice;

import jakarta.servlet.http.HttpServletRequest;
import kr.s2.ext.exception.S2Exception;
import kr.s2.ext.exception.S2RuntimeException;
import kr.s2.ext.util.S2ServletUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class S2ExceptionHandler {

    @ExceptionHandler(S2Exception.class)
    public Object s2ExceptionHandler(HttpServletRequest request, S2Exception e) {
        String errorMessage = e.getMessage().replace("S2Exception:", "");
        if (S2ServletUtil.isAjaxRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        } else {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMessage", errorMessage);
            return mav;
        }
    }

    @ExceptionHandler(S2RuntimeException.class)
    public Object s2RuntimeExceptionHandler(HttpServletRequest request, S2RuntimeException e) {
        String errorMessage = e.getMessage().replace("S2RuntimeException:", "");
        if (S2ServletUtil.isAjaxRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        } else {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMessage", errorMessage);
            return mav;
        }
    }

}
