package biz.placelink.seek.com.serviceworker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import biz.placelink.seek.com.serviceworker.service.ServiceWorkerService;
import biz.placelink.seek.com.serviceworker.vo.SubscriptionVO;
import jakarta.servlet.http.HttpServletRequest;
import kr.s2.ext.util.S2ServletUtil;

/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자        수정내용
 *
 *  ------------       --------    ---------------------------
 *
 *  2025. 04. 29.      s2          최초생성
 * </pre>
 */
@Controller
public class ServiceWorkerController {

    private final ServiceWorkerService serviceWorkerService;

    public ServiceWorkerController(ServiceWorkerService serviceWorkerService) {
        this.serviceWorkerService = serviceWorkerService;
    }

    @PostMapping("/public/serviceworker/subscribe.ar")
    public String subscribe(@RequestBody SubscriptionVO subscriptionVO, HttpServletRequest request, Model model) {
        // !!s2!! 사용자 정보가 없어서 임시로 요청 IP를 사용하여 사용자 ID를 설정한다. (추후에는 삭제하고 ServiceWorkerService.subscribe 에서 사용자 정보를 사용하자)
        subscriptionVO.setUserId(S2ServletUtil.getClientIp(request));
        serviceWorkerService.subscribe(subscriptionVO);
        return "jsonView";
    }

    @PostMapping("/public/serviceworker/unsubscribe.ar")
    public String unsubscribe(@RequestBody String endpoint, Model model) {
        // serviceWorkerService.unsubscribe(SessionHelper.getUserId());
        // model.addAttribute(Constants.RESULT_CODE, 1);
        return "jsonView";
    }

}
