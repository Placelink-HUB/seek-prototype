package biz.placelink.seek.com.advice;

import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "biz.placelink.seek")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {Exception.class})
    public String handleCustomException(Exception e, Model model) {
        String errorMessage = "오류가 발생했습니다.";

        if (e instanceof ServletException) {
            errorMessage = "서블릿 오류가 발생했습니다.";
        } else {
            //errorMessage = e.toString();
            errorMessage = "오류가 계속되면 관리자에서 문의 하세요";
        }

        model.addAttribute("errorMessage", errorMessage);

        logger.error("Exception 발생 : {}", e, e);
        return "error/custom-error";
    }

}
