package biz.placelink.seek.com.advice;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import kr.s2.ext.exception.S2Exception;
import kr.s2.ext.exception.S2RuntimeException;
import kr.s2.ext.util.S2ServletUtil;
import kr.s2.ext.util.S2Util;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class S2ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(S2ExceptionHandler.class);

    @ExceptionHandler(S2Exception.class)
    public Object s2ExceptionHandler(HttpServletRequest request, S2Exception e) {
        String errorMessage = e.getMessage().replace("S2Exception:", "");
        if (S2ServletUtil.isAjaxRequest(request)) {
            MediaType mediaType = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(mediaType).body(errorMessage);
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
            MediaType mediaType = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(mediaType).body(errorMessage);
        } else {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMessage", errorMessage);
            return mav;
        }
    }

    @ExceptionHandler(Exception.class)
    public Object exceptionHandler(HttpServletRequest request, Exception e) {
        String errorMessage = getMessage("", e);
        if (S2ServletUtil.isAjaxRequest(request, ".*\\.ar$")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("S2Message:" + errorMessage);
        } else {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMessage", errorMessage);
            return mav;
        }
    }

    private String getMessage(String prefix, Exception e) {
        String errorMessage = String.format("%s[Code: %d]", S2Util.isNotEmpty(prefix) ? e.getMessage().replace(prefix, "") : e.getMessage(), Thread.currentThread().threadId());
        String userId = "";
        logger.error("###ERROR : {}|{}", userId, errorMessage, e);
        return errorMessage;
    }

}
