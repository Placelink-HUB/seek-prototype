package biz.placelink.seek.com.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 02. 08.      s2          최초생성
 * </pre>
 */
@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        String errorMessage = "오류가 발생했습니다.";

        if (exception instanceof ServletException) {
            errorMessage = "서블릿 오류가 발생했습니다.";
        } else {
            //errorMessage = exception.toString();
            errorMessage = "오류가 계속되면 관리자에서 문의 하세요";
        }

        model.addAttribute("status", status);
        model.addAttribute("errorMessage", errorMessage);

        logger.error("Exception 발생 : {}", exception);
        return "error/error";
    }

}